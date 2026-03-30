package com.example.SocialMedia.mapper;

import com.example.SocialMedia.dto.response.PostMediaResponse;
import com.example.SocialMedia.dto.response.PostResponse;
import com.example.SocialMedia.dto.response.ReactionStat;
import com.example.SocialMedia.dto.response.ShortUserResponse;
import com.example.SocialMedia.model.coredata_model.Post;
import com.example.SocialMedia.model.coredata_model.PostMedia;
import com.example.SocialMedia.repository.CommentRepository;
import com.example.SocialMedia.repository.ReactionRepository;
import com.example.SocialMedia.repository.ShareRepository;
import com.example.SocialMedia.service.IMinioService;
import com.example.SocialMedia.service.social.PostMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final PostMediaService postMediaService;
    private final IMinioService minioService;
    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
    private final ShareRepository shareRepository;

    private static final int MAX_LOAD_MEDIA = 4;
    private static final Pageable MEDIA_PAGEABLE = PageRequest.of(0, MAX_LOAD_MEDIA);

    public PostResponse toPostResponse(Post post) {
        // 1. Xử lý Media & MinIO Link
        List<PostMedia> dbMedias = postMediaService.findByPost(post, MEDIA_PAGEABLE);

        List<PostMediaResponse> mediaResponses = dbMedias.stream().map(m -> {
            String freshUrl = minioService.getFileUrl(m.getFileName());
            return PostMediaResponse.builder()
                    .id(m.getId())
                    .mediaURL(freshUrl)
                    .fileName(m.getFileName())
                    .mediaType(m.getMediaType())
                    .sortOrder(m.getSortOrder())
                    .build();
        }).collect(Collectors.toList());

        // 2. Xử lý Reaction Map
        List<ReactionStat> stats = reactionRepository.countReactionsByInteractableItemId(
                post.getInteractableItem().getInteractableItemId()
        );
        Map<String, Long> reactionMap = stats.stream()
                .collect(Collectors.toMap(
                        stat -> stat.getReactionType().name(),
                        ReactionStat::getReactionCount
                ));

        // 3. Đếm Comment/Share
        int cmtCount = commentRepository.countCommentByPost(post);
        int shareCount = shareRepository.countShareByPost(post);

        // ==========================================
        // 4. XỬ LÝ AVATAR TÁC GIẢ BẰNG MINIO
        // ==========================================
        String rawAvatar = post.getUser().getProfilePictureURL();
        String finalAvatarUrl = minioService.resolvePublicUrl(rawAvatar);

        if (rawAvatar != null && !rawAvatar.isBlank()) {
            if (rawAvatar.startsWith("http")) {
                finalAvatarUrl = rawAvatar;
            } else {
                try {
                    // Biến tên file thành link thực tế cho Frontend
                    finalAvatarUrl = minioService.getFileUrl(rawAvatar);
                } catch (Exception e) {
                    System.err.println(">>> [PostMapper] Lỗi tạo link Avatar: " + e.getMessage());
                }
            }
        }

        ShortUserResponse authorResponse = new ShortUserResponse(
                post.getUser().getId(),
                post.getUser().getFullName(),
                post.getUser().getUsername(),
                finalAvatarUrl, // <--- THAY VÌ TRUYỀN RAW, ANH TRUYỀN LINK ĐÃ QUA MINIO VÀO ĐÂY
                post.getUser().getCreatedLocalDateTime()
        );

        // 5. Build
        return PostResponse.builder()
                .id(post.getPostId())
                .content(post.getContent())
                .postTopic(post.getPostTopic())
                .location(post.getLocation())
                .user(authorResponse) // <--- Gắn cục authorResponse xịn xò vào
                .interactableItemId(post.getInteractableItem().getInteractableItemId())
                .createdAt(post.getCreatedLocalDateTime())
                .updatedAt(post.getUpdatedLocalDateTime())
                .commentCount(cmtCount)
                .reactionCounts(reactionMap)
                .shareCount(shareCount)
                .medias(mediaResponses)
                .build();
    }
}