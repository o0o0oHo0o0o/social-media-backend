package com.example.SocialMedia.serviceImpl.social;

import com.example.SocialMedia.dto.message.FileUploadResponse;
import com.example.SocialMedia.dto.request.PostRequest;
import com.example.SocialMedia.dto.response.PostResponse;
import com.example.SocialMedia.exception.ResourceNotFound.PostNotFoundException;
import com.example.SocialMedia.exception.ResourceNotFound.UserNotFoundException;
import com.example.SocialMedia.mapper.PostMapper;
import com.example.SocialMedia.model.coredata_model.*;
import com.example.SocialMedia.repository.*;
import com.example.SocialMedia.service.IMinioService;
import com.example.SocialMedia.service.social.PostService; // Import đúng Interface
import com.example.SocialMedia.service.social.InteractableItemService;
import com.example.SocialMedia.service.social.PostMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final InteractableItemService interactableItemService;
    private final PostMediaService postMediaService;
    private final IMinioService minioService;
    private final PostMapper postMapper;
    // --- HELPER METHODS ---


    private void handleUploadMedia(MultipartFile[] files, Post post) {
        if (files == null) return;

        for (MultipartFile file : files) {
            // 1. Upload MinIO -> Lấy được FileName
            FileUploadResponse uploadRes = minioService.uploadFile(file);

            // 2. Lưu Metadata vào DB (Chỉ truyền Post, FileName, Type)
            postMediaService.createPostMedia(
                    post,
                    uploadRes.getFileName(),
                    uploadRes.getMediaType()
            );
        }
    }

    // --- MAIN METHODS ---

    @Override
    public PostResponse getPostById(Integer id) {
        Post post = postRepository.findByInteractableItem_InteractableItemId(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
        return postMapper.toPostResponse(post);
    }

    @Override
    @Transactional
    public PostResponse createPost(String username, PostRequest postRequest, MultipartFile[] files) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        InteractableItems item = interactableItemService.createInteractableItems("POST", postRequest.getCreatedAt());

        Post post = new Post();
        post.setContent(postRequest.getContent());
        post.setPostTopic(postRequest.getPostTopic());
        post.setLocation(postRequest.getLocation());
        post.setUser(user);
        post.setInteractableItem(item);
        post.setCreatedLocalDateTime(postRequest.getCreatedAt());

        post = postRepository.save(post);

        // Upload Media
        handleUploadMedia(files, post);

        return postMapper.toPostResponse(post);
    }

    @Override
    public List<PostResponse> getPostByUserName(String userName, Pageable pageable) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userName));

        Page<Post> posts = postRepository.findByUserAndIsDeletedIsFalse(user, pageable);

        return posts.getContent().stream()
                .map(postMapper::toPostResponse) // Gọi sang class Mapper
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostResponse updatePost(String username, PostRequest postRequest) {
        Post post = postRepository.findByPostId(postRequest.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền sửa bài viết này");
        }

        if (postRequest.getContent() != null) post.setContent(postRequest.getContent());
        if (postRequest.getPostTopic() != null) post.setPostTopic(postRequest.getPostTopic());
        if (postRequest.getLocation() != null) post.setLocation(postRequest.getLocation());

        post.setUpdatedLocalDateTime(LocalDateTime.now());
        post = postRepository.save(post);

        // Upload thêm media mới
        handleUploadMedia(postRequest.getMedias(), post);

        // Xóa media cũ
        if (postRequest.getDeleteMedia() != null) {
            for (int mediaId : postRequest.getDeleteMedia()) {
                postMediaService.deletePostMedia(mediaId);
            }
        }

        return postMapper.toPostResponse(post);
    }

    @Override
    public PostResponse deletePost(int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
        post.setDeleted(true);
        return postMapper.toPostResponse(post);
    }

    @Override
    public List<PostResponse> getPostsByKeyword(String keyword, Pageable pageable) {
        Page<Post> posts = postRepository.findByContentContainingIgnoreCase(keyword, pageable);
        return posts.getContent().stream()
                .map(postMapper::toPostResponse) // Gọi sang class Mapper
                .collect(Collectors.toList());
    }
}