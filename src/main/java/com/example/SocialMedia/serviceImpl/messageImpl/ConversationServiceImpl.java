package com.example.SocialMedia.serviceImpl.messageImpl;

import com.example.SocialMedia.constant.MessageType;
import com.example.SocialMedia.constant.TargetType;
import com.example.SocialMedia.dto.message.FileUploadResponse;
import com.example.SocialMedia.dto.response.ReactionResponse;
import com.example.SocialMedia.dto.response.SocketResponse;
import com.example.SocialMedia.model.coredata_model.InteractableItems;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.model.messaging_model.Conversation;
import com.example.SocialMedia.model.messaging_model.ConversationMember;
import com.example.SocialMedia.model.messaging_model.Messages;
import com.example.SocialMedia.repository.InteractableItemRepository;
import com.example.SocialMedia.repository.message.ConversationMemberRepository;
import com.example.SocialMedia.repository.message.ConversationRepository;
import com.example.SocialMedia.repository.message.MessageBodyRepository;
import com.example.SocialMedia.repository.message.MessageRepository;
import com.example.SocialMedia.service.IMinioService;
import com.example.SocialMedia.service.message.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationServiceImpl implements ConversationService {

    private final ConversationMemberRepository conversationMemberRepo;
    private final ConversationRepository conversationRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final InteractableItemRepository interactableItemRepository;
    private final MessageRepository messageRepo;
    private final MessageBodyRepository messageBodyRepo;
    private final IMinioService minioService;

    @Override
    @Transactional
    public void updateMemberNickname(Integer conversationId, Integer targetUserId, String nickname, String requesterUsername) {
        // 1. Validate & Lấy người yêu cầu (Actor)
        ConversationMember requesterMember = getRequesterMember(conversationId, requesterUsername);
        User actor = requesterMember.getUser();

        // 2. Lấy thành viên cần đổi tên (Target)
        ConversationMember targetMember = conversationMemberRepo
                .findByConversation_ConversationIdAndUser_Id(conversationId, targetUserId)
                .orElseThrow(() -> new RuntimeException("Thành viên cần đổi tên không tồn tại trong nhóm"));
        User targetUser = targetMember.getUser();

        // 3. Cập nhật DB
        targetMember.setNickname(nickname);
        conversationMemberRepo.save(targetMember);

        // 4. Tạo thông báo hệ thống
        String notificationContent = (actor.getId() == targetUser.getId())
                ? actor.getFullName() + " đã đổi biệt danh của mình thành " + nickname
                : actor.getFullName() + " đã đặt biệt danh cho " + targetUser.getFullName() + " là " + nickname;

        createSystemMessage(requesterMember.getConversation(), actor, notificationContent);

        // 5. Bắn Socket
        Map<String, Object> payload = Map.of(
                "type", "NICKNAME_UPDATE",
                "conversationId", conversationId,
                "userId", targetUserId,
                "newNickname", nickname,
                "updatedBy", requesterUsername,
                "message", notificationContent
        );

        sendSocketEvent(conversationId, payload);
    }

    @Override
    @Transactional
    public void updateConversationName(int conversationId, String newName, String requesterUsername) {
        // 1. Validate & Lấy người yêu cầu
        ConversationMember requesterMember = getRequesterMember(conversationId, requesterUsername);
        Conversation conversation = requesterMember.getConversation();
        User actor = requesterMember.getUser();

        // 2. Cập nhật DB
        conversation.setConversationName(newName);
        conversationRepo.save(conversation);

        // 3. Tạo thông báo
        String notificationContent = actor.getFullName() + " đã đổi tên nhóm thành " + newName;
        createSystemMessage(conversation, actor, notificationContent);

        // 4. Bắn Socket
        Map<String, Object> payload = Map.of(
                "type", "NAME_UPDATE",
                "conversationId", conversationId,
                "newName", newName,
                "updatedBy", requesterUsername
        );

        sendSocketEvent(conversationId, payload);
    }

    @Override
    @Transactional
    public String updateConversationAvatar(int conversationId, MultipartFile file, String requesterUsername) {
        // 1. Validate & Lấy người yêu cầu
        ConversationMember requesterMember = getRequesterMember(conversationId, requesterUsername);
        Conversation conversation = requesterMember.getConversation();
        User actor = requesterMember.getUser();

        // 2. Check Group Chat
        if (!Boolean.TRUE.equals(conversation.isGroupChat())) {
            throw new RuntimeException("Không thể đổi ảnh đại diện cho cuộc trò chuyện 1-1");
        }

        // 3. Upload file (MinIO)
        FileUploadResponse uploadResponse = minioService.uploadFile(file);
        String fileName = uploadResponse.getFileName();

        // 4. Update DB
        conversation.setGroupImageFile(fileName);
        conversationRepo.save(conversation);

        // 5. Tạo thông báo
        String notificationContent = actor.getFullName() + " đã thay đổi ảnh nhóm.";
        createSystemMessage(conversation, actor, notificationContent);

        String presignedUrl = uploadResponse.getMediaUrl();

        // 6. Bắn Socket
        Map<String, Object> payload = Map.of(
                "type", "AVATAR_UPDATE",
                "conversationId", conversationId,
                "newAvatar", presignedUrl,
                "updatedBy", requesterUsername
        );

        sendSocketEvent(conversationId, payload);

        return presignedUrl;
    }

    @Override
    @Transactional(readOnly = true) // Quan trọng để fetch Lazy list
    public List<ReactionResponse> getReactionDetails(Long messageId) {
        // 1. Tìm tin nhắn
        Messages message = messageRepo.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        // 2. Lấy InteractableItem
        InteractableItems item = message.getInteractableItem();

        // Check null an toàn
        if (item == null) {
            return List.of(); // Trả về list rỗng nếu chưa có tương tác
        }

        // 3. Map từ Entity sang DTO
        return item.getReactions().stream()
                .map(reaction -> ReactionResponse.builder()
                        .userId(reaction.getUser().getId())
                        // Lưu ý: Kiểm tra getter trong User entity của bạn là getUsername() hay getUserName()
                        .username(reaction.getUser().getUsername())
                        .fullName(reaction.getUser().getFullName())
                        .avatarUrl(reaction.getUser().getProfilePictureURL())
                        .reactionType(reaction.getReactionType()) // Enum
                        .build())
                .toList();
    }

    // ================= HELPER METHODS =================

    /**
     * Helper: Lấy thông tin thành viên đang thực hiện request.
     */
    private ConversationMember getRequesterMember(int conversationId, String username) {
        return conversationMemberRepo
                .findByConversation_ConversationIdAndUser_UserName(conversationId, username)
                .orElseThrow(() -> new RuntimeException("Bạn không phải thành viên nhóm hoặc nhóm không tồn tại!"));
    }

    /**
     * Helper: Tạo tin nhắn hệ thống
     */
    private void createSystemMessage(Conversation conversation, User actor, String content) {
        InteractableItems item = new InteractableItems();
        item.setItemType(TargetType.MESSAGE);
        item.setCreatedAt(LocalDateTime.now());
        item = interactableItemRepository.save(item);

        long nextSequence = 1;
        if (conversation.getLastMessageID() > 0) {
            nextSequence = conversation.getLastMessageID() + 1;
        }

        Messages message = new Messages();
        message.setConversation(conversation);
        message.setSender(actor);
        message.setInteractableItem(item);
        message.setMessageType(MessageType.SYSTEM);
        message.setSentAt(LocalDateTime.now());
        message.setSequenceNumber(nextSequence);
        message.setDeleted(false);

        Messages savedMsg = messageRepo.save(message);
        messageBodyRepo.insertBody(savedMsg.getMessageId(), content);

        conversation.setLastMessageID(savedMsg.getMessageId());
        conversationRepo.save(conversation);
    }

    /**
     * Helper: Bắn Socket Event
     */
    private void sendSocketEvent(int conversationId, Object payload) {
        SocketResponse<Object> socketEvent = SocketResponse.builder()
                .type("CONVERSATION_UPDATE")
                .payload(payload)
                .build();
        messagingTemplate.convertAndSend("/topic/chat." + conversationId, socketEvent);
    }
}