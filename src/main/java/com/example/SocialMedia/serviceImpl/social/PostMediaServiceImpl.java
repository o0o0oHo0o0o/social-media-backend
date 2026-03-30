package com.example.SocialMedia.serviceImpl.social;

import com.example.SocialMedia.model.coredata_model.Post;
import com.example.SocialMedia.model.coredata_model.PostMedia;
import com.example.SocialMedia.repository.PostMediaRepository;
import com.example.SocialMedia.service.social.InteractableItemService;
import com.example.SocialMedia.service.social.PostMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostMediaServiceImpl implements PostMediaService {

    private final PostMediaRepository postMediaRepository;
    private final InteractableItemService interactableItemService;
    // Bỏ StorageService vì đã dùng MinIO ở tầng PostService

    @Override
    public List<PostMedia> findByPost(Post post, Pageable pageable) {
        // Trả về List Entity gốc
        return postMediaRepository.findByPost(post, pageable).getContent();
    }


    @Override
    @Transactional
    public void createPostMedia(Post post, String fileName, String mediaType) {
        PostMedia postMedia = new PostMedia();
        postMedia.setPost(post);
        postMedia.setFileName(fileName); // Lưu fileName từ MinIO trả về
        postMedia.setMediaType(mediaType);
        postMedia.setSortOrder(0);

        // Tạo InteractableItem nếu logic DB yêu cầu (Media có thể like/comment riêng)
        postMedia.setInteractableItem(
                interactableItemService.createInteractableItems("MEDIA", LocalDateTime.now())
        );

        postMediaRepository.save(postMedia);
    }

    @Override
    public void deletePostMedia(int id) {
        PostMedia postMedia = postMediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        // TODO: Nếu cần xóa file trên MinIO, gọi MinioService.delete(postMedia.getFileName()) ở đây
        // Hiện tại chỉ xóa DB
        postMediaRepository.delete(postMedia);
    }
}