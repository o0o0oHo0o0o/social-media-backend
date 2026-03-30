package com.example.SocialMedia.mapper;

import com.example.SocialMedia.dto.response.CommentResponse;
import com.example.SocialMedia.dto.response.ReactionStat;
import com.example.SocialMedia.dto.response.ShortUserResponse;
import com.example.SocialMedia.model.coredata_model.Comment;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.repository.CommentRepository;
import com.example.SocialMedia.repository.ReactionRepository;
import com.example.SocialMedia.service.IMinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
    private final IMinioService minioService;

    public CommentResponse toCommentResponse(Comment comment) {
        if (comment == null) return null;

        // 1. XỬ LÝ AVATAR QUA MINIO
        User author = comment.getUser();
        String rawAvatar = author.getProfilePictureURL();
        String finalAvatarUrl = null;

        if (rawAvatar != null && !rawAvatar.isBlank()) {
            if (rawAvatar.startsWith("http")) {
                finalAvatarUrl = rawAvatar;
            } else {
                try {
                    finalAvatarUrl = minioService.getFileUrl(rawAvatar);
                } catch (Exception e) {
                    System.err.println(">>> [CommentMapper] Lỗi tạo link Avatar: " + e.getMessage());
                }
            }
        }

        ShortUserResponse authorResponse = new ShortUserResponse(
                author.getId(),
                author.getFullName(),
                author.getUsername(),
                finalAvatarUrl,
                author.getCreatedLocalDateTime()
        );

        // 2. XỬ LÝ REACTION STATS
        List<ReactionStat> stats = reactionRepository.countReactionsByInteractableItemId(
                comment.getOwnInteractableItem().getInteractableItemId()
        );
        Map<String, Long> reactionMap = stats.stream()
                .collect(Collectors.toMap(
                        stat -> stat.getReactionType().name(),
                        ReactionStat::getReactionCount
                ));

        // 3. BUILD RESPONSE
        return CommentResponse.builder()
                .id(comment.getCommentId())
                .content(comment.getContent())
                .user(authorResponse)
                .interactableItemId(comment.getOwnInteractableItem().getInteractableItemId())
                .targetInteractableItemId(comment.getPost().getInteractableItem().getInteractableItemId())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
                .replied(commentRepository.existsByParentComment_CommentId(comment.getCommentId()))
                .reactionCounts(reactionMap)
                .createdAt(comment.getCreatedLocalDateTime())
                .build();
    }
}