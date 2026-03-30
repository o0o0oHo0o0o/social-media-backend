package com.example.SocialMedia.serviceImpl.messageImpl;

import com.example.SocialMedia.dto.request.MarkReadRequest;
import com.example.SocialMedia.dto.response.SenderDto;
import com.example.SocialMedia.dto.response.SocketResponse;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.model.messaging_model.ConversationMember;
import com.example.SocialMedia.repository.UserRepository;
import com.example.SocialMedia.repository.message.ConversationMemberRepository;
import com.example.SocialMedia.repository.message.MessageRepository;
import com.example.SocialMedia.repository.message.MessageStatusRepository;
import com.example.SocialMedia.service.message.MessageStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageStatusServiceImpl implements MessageStatusService {

    private final ConversationMemberRepository conversationMemberRepo;
    private final MessageStatusRepository messageStatusRepo;
    private final UserRepository userRepo;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void markAsRead(String username, MarkReadRequest request) {
        try {
            // Validate request
            if (request == null || request.getConversationId() == null || request.getConversationId() <= 0) {
                log.warn("[MessageStatus] Invalid request - conversationId: {}", request == null ? null : request.getConversationId());
                return; // Silent fail
            }

            User user = userRepo.findByUserName(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            // 1. Cập nhật "Con trỏ" đọc tin nhắn (ConversationMember)
            ConversationMember member = conversationMemberRepo
                    .findByConversation_ConversationIdAndUser_Id(request.getConversationId(), user.getId())
                    .orElse(null);

            if (member == null) {
                log.warn("[MessageStatus] Member not found - conversationId: {}, userId: {}", request.getConversationId(), user.getId());
                return; // Silent fail
            }

            // 2. TRY TO UPDATE lastReadMessage - handle case where message doesn't exist
            if (request.getLastMessageId() != null && request.getLastMessageId() > 0) {
                var messageOpt = messageRepository.findById(request.getLastMessageId());

                if (messageOpt.isEmpty()) {
                    log.warn("[MessageStatus] Message not found with id: {}, but continuing to update read status", request.getLastMessageId());
                    // Don't throw error - just skip updating lastReadMessage
                    // Continue to mark other messages as read
                } else {
                    var message = messageOpt.get();
                    if (member.getLastReadMessage() == null || request.getLastMessageId() > member.getLastReadMessage().getMessageId()) {
                        member.setLastReadMessage(message);
                        conversationMemberRepo.save(member);
                        log.info("[MessageStatus] Updated lastReadMessage for user {} to message {}", user.getId(), request.getLastMessageId());
                    }
                }
            }

            // 3. Cập nhật trạng thái chi tiết (MessageStatus Table)
            // Mark all messages <= messageId as READ in this conversation
            try {
                messageStatusRepo.markAsRead(
                        request.getConversationId(),
                        user.getId(),
                        request.getLastMessageId()
                );
                log.info("[MessageStatus] Marked messages as read - conversationId: {}, userId: {}, lastMessageId: {}",
                        request.getConversationId(), user.getId(), request.getLastMessageId());
            } catch (Exception e) {
                log.error("[MessageStatus] Error marking messages as read: {}", e.getMessage());
                // Continue anyway - don't fail the entire operation
            }

            // 4. PUSH SOCKET EVENT - only if we have a valid message ID
            try {
                if (request.getLastMessageId() != null && request.getLastMessageId() > 0) {
                    Map<String, Object> readPayload = Map.of(
                            "conversationId", request.getConversationId(),
                            "messageId", request.getLastMessageId(),
                            "user", SenderDto.fromUser(user, member.getNickname()),
                            "readAt", LocalDateTime.now().toString()
                    );

                    SocketResponse<Object> socketEvent = SocketResponse.builder()
                            .type("READ_RECEIPT")
                            .payload(readPayload)
                            .build();

                    messagingTemplate.convertAndSend("/topic/chat." + request.getConversationId(), socketEvent);
                    log.info("[MessageStatus] Sent READ_RECEIPT socket event for conversation {}", request.getConversationId());
                }
            } catch (Exception e) {
                log.error("[MessageStatus] Error sending socket event: {}", e.getMessage());
                // Don't fail the entire operation if socket send fails
            }

        } catch (RuntimeException e) {
            log.error("[MessageStatus] RuntimeException in markAsRead: {}", e.getMessage(), e);
            throw e; // Re-throw for user-related errors
        } catch (Exception e) {
            log.error("[MessageStatus] Unexpected error in markAsRead: {}", e.getMessage(), e);
            // Don't crash the entire operation for unexpected errors
        }
    }
}