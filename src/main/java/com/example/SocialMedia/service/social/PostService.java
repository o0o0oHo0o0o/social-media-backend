package com.example.SocialMedia.service.social;

import com.example.SocialMedia.dto.request.PostRequest;
import com.example.SocialMedia.dto.response.PostResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    PostResponse getPostById(Integer id);

    PostResponse createPost(String username, PostRequest postRequest, MultipartFile[] files);

    List<PostResponse> getPostByUserName(String userName, Pageable pageable);

    PostResponse updatePost(String username, PostRequest postRequest);

    PostResponse deletePost(int postId);

    List<PostResponse> getPostsByKeyword(String keyword, Pageable pageable);
}