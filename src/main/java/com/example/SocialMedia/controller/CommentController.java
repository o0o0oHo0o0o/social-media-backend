package com.example.SocialMedia.controller;

import com.example.SocialMedia.dto.request.CommentRequest;
import com.example.SocialMedia.dto.response.CommentResponse;
import com.example.SocialMedia.service.social.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity; // Nên dùng ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // Lấy comment của 1 bài Post (Comment gốc)
    @GetMapping("/post/{postId}") // Sửa đường dẫn cho rõ nghĩa hơn
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(
            @PathVariable int postId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId, pageable));
    }

    // Lấy comment của user
    @GetMapping("/user/{userName}")
    public ResponseEntity<List<CommentResponse>> getUserComments(
            @PathVariable String userName,
            Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getCommentsByUserName(userName, pageable));
    }

    // Lấy replies của 1 comment
    @GetMapping("/replies/{commentId}") // Sửa đường dẫn cho rõ nghĩa
    public ResponseEntity<List<CommentResponse>> getReplies(
            @PathVariable int commentId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getCommentsByParentCommentId(commentId, pageable));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @RequestBody CommentRequest commentRequest,
            @AuthenticationPrincipal UserDetails userDetails // <--- Bảo mật
    ) {
        commentRequest.setCreatedAt(LocalDateTime.now());
        // Truyền username xuống service
        return ResponseEntity.ok(commentService.createComment(userDetails.getUsername(), commentRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommentResponse> deleteComment(@PathVariable int id) {
        return ResponseEntity.ok(commentService.deleteComment(id));
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<CommentResponse>> getCommentByKeyword(
            @PathVariable String keyword,
            Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getCommentsByKeyword(keyword, pageable));
    }
}