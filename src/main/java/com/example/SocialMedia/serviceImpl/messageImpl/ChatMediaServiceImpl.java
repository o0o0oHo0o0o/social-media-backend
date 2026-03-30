package com.example.SocialMedia.serviceImpl.messageImpl;

import com.example.SocialMedia.dto.MemberDto;
import com.example.SocialMedia.dto.UserSummary;
import com.example.SocialMedia.dto.file.FileDto;
import com.example.SocialMedia.dto.file.PhotoDto;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.model.messaging_model.ConversationMember;
import com.example.SocialMedia.model.messaging_model.MessageMedia;
import com.example.SocialMedia.model.messaging_model.Messages;
import com.example.SocialMedia.repository.UserRepository;
import com.example.SocialMedia.repository.message.ConversationMemberRepository;
import com.example.SocialMedia.repository.message.ConversationRepository;
import com.example.SocialMedia.repository.message.MessageRepository;
import com.example.SocialMedia.service.IMinioService;
import com.example.SocialMedia.service.message.ChatMediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatMediaServiceImpl implements ChatMediaService {

    private final MessageRepository messageRepository;
    private final ConversationMemberRepository memberRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final IMinioService minioService;

    @Override
    public Page<PhotoDto> getPhotos(int conversationId, int page, int size) {
        log.info("Fetching photos for conversationId: {}, page: {}", conversationId, page);
        verifyConversationAccess(conversationId);

        Page<Messages> photos = messageRepository.findPhotos(conversationId, PageRequest.of(page, size));
        log.info("Found {} messages containing photos", photos.getNumberOfElements());

        return photos.map(this::convertToPhotoDto);
    }

    @Override
    public Page<FileDto> getFiles(int conversationId, int page, int size) {
        log.info("Fetching files for conversationId: {}, page: {}", conversationId, page);
        verifyConversationAccess(conversationId);

        Page<Messages> files = messageRepository.findFiles(conversationId, PageRequest.of(page, size));
        log.info("Found {} messages containing files", files.getNumberOfElements());

        return files.map(this::convertToFileDto);
    }

    @Override
    public List<MemberDto> getMembers(int conversationId) {
        verifyConversationAccess(conversationId);
        return memberRepository.findByConversation_ConversationId(conversationId).stream()
                .map(this::convertToMemberDto)
                .collect(Collectors.toList());
    }

    private PhotoDto convertToPhotoDto(Messages message) {
        try {
            // Lọc chính xác media là IMAGE
            List<MessageMedia> images = message.getMessageMedia().stream()
                    .filter(mm -> "IMAGE".equalsIgnoreCase(mm.getMediaType()))
                    .toList();

            List<String> urls = images.stream()
                    .map(mm -> minioService.getFileUrl(mm.getMediaName()))
                    .collect(Collectors.toList());

            List<String> fileNames = images.stream()
                    .map(MessageMedia::getFileName)
                    .collect(Collectors.toList());

            return new PhotoDto(
                    message.getMessageId(),
                    urls,
                    fileNames,
                    "IMAGE",
                    convertToUserSummary(message.getSender()),
                    message.getSentAt()
            );
        } catch (Exception e) {
            log.error("Error converting photo DTO for messageId: {}", message.getMessageId(), e);
            return null;
        }
    }

    private FileDto convertToFileDto(Messages message) {
        try {
            // Lấy các loại file không phải ảnh
            Set<String> targetTypes = Set.of("FILE", "VIDEO", "AUDIO");

            List<MessageMedia> mediaList = message.getMessageMedia().stream()
                    .filter(mm -> targetTypes.contains(mm.getMediaType().toUpperCase()))
                    .toList();

            List<String> urls = mediaList.stream()
                    .map(mm -> minioService.getFileUrl(mm.getMediaName()))
                    .collect(Collectors.toList());

            List<String> fileNames = mediaList.stream()
                    .map(MessageMedia::getFileName)
                    .collect(Collectors.toList());

            List<Long> fileSizes = mediaList.stream()
                    .map(mm -> {
                        if (mm.getFileSize() == null) {
                            return 0L; // Nếu null thì trả về 0
                        }
                        return mm.getFileSize().longValue(); // An toàn vì Integer nullable
                    })
                    .collect(Collectors.toList());

            String commonType = mediaList.isEmpty() ? "FILE" : mediaList.getFirst().getMediaType();

            return new FileDto(
                    message.getMessageId(),
                    urls,
                    fileNames,
                    fileSizes,
                    commonType,
                    convertToUserSummary(message.getSender()),
                    message.getSentAt()
            );
        } catch (Exception e) {
            log.error("Error converting file DTO for messageId: {}", message.getMessageId(), e);
            return null;
        }
    }

    private UserSummary convertToUserSummary(User user) {
        if (user == null) return null;
        return new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getProfilePictureURL()
        );
    }

    private MemberDto convertToMemberDto(ConversationMember member) {
        return new MemberDto(
                member.getUser().getId(),
                member.getUser().getUsername(),
                member.getUser().getFullName(),
                member.getUser().getProfilePictureURL(),
                member.getNickname(),
                member.getJoinedLocalDateTime()
        );
    }

    private void verifyConversationAccess(int conversationId) {
        User currentUser = getCurrentUser();
        boolean isMember = conversationRepository.existsConversationMember(conversationId, currentUser.getId());
        if (!isMember) {
            log.warn("Access denied: User {} tried to access conversation {}", currentUser.getUsername(), conversationId);
            throw new RuntimeException("Bạn không phải thành viên của cuộc trò chuyện này");
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hiện tại"));
    }
}