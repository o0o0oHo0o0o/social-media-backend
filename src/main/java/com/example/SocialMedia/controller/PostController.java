package com.example.SocialMedia.controller;

import com.example.SocialMedia.dto.request.PostRequest;
import com.example.SocialMedia.dto.response.PostResponse;
import com.example.SocialMedia.exception.FileTooLargeException;
import com.example.SocialMedia.exception.TooManyFileException;
// QUAN TRỌNG: Import đúng Interface PostService trong package social
import com.example.SocialMedia.service.social.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final int MAX_FILE = 10;

    // --- VALIDATION HELPER ---
    private void checkUploadedFile(MultipartFile[] files) {
        if (files == null || files.length == 0) return;

        if (files.length > MAX_FILE) {
            throw new TooManyFileException("File exceeds maximum number of 10");
        }
        for (MultipartFile f : files) {
            if (f.getSize() > MAX_FILE_SIZE) {
                throw new FileTooLargeException("File exceeds maximum size limit of 10MB");
            }
        }
    }

    // --- ENDPOINTS ---

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Integer id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // CREATE: Multipart Form (Json + Files)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @RequestParam(value = "mediaFile", required = false) MultipartFile[] files,
            @RequestPart("postRequest") PostRequest postRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        checkUploadedFile(files);

        // Set thời gian Server (Source of Truth)
        postRequest.setCreatedAt(LocalDateTime.now());

        // Gọi Service (Truyền Username + DTO + Files riêng lẻ)
        return ResponseEntity.ok(postService.createPost(userDetails.getUsername(), postRequest, files));
    }

    @GetMapping("/user/{name}")
    public ResponseEntity<List<PostResponse>> getPostByUserName(@PathVariable String name, Pageable pageable) {
        return ResponseEntity.ok(postService.getPostByUserName(name, pageable));
    }

    // UPDATE: Multipart Form (Json + Files mới + IDs file xóa)
    @PostMapping("/{id}") // Có thể dùng @PutMapping nếu client hỗ trợ tốt
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Integer id,
            @RequestParam(value = "mediaFile", required = false) MultipartFile[] files,
            @RequestParam(value = "deleteFile", required = false) int[] deleteFile,
            @RequestPart(value = "postRequest", required = false) PostRequest postRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        checkUploadedFile(files);

        // --- MAP DỮ LIỆU TỪ PARAM VÀO DTO ---
        // Vì Service updatePost chỉ nhận (username, postRequest),
        // nên ta phải nhét file và id xóa vào trong postRequest tại đây.

        if (files != null && files.length > 0) {
            postRequest.setMedias(files);
        }
        if (deleteFile != null && deleteFile.length > 0) {
            postRequest.setDeleteMedia(deleteFile);
        }

        postRequest.setPostId(id);
        postRequest.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(postService.updatePost(userDetails.getUsername(), postRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PostResponse> deletePost(@PathVariable Integer id) {
        return ResponseEntity.ok(postService.deletePost(id));
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<PostResponse>> getPostsByKeyword(@PathVariable String keyword, Pageable pageable) {
        return  ResponseEntity.ok(postService.getPostsByKeyword(keyword, pageable));
    }
}