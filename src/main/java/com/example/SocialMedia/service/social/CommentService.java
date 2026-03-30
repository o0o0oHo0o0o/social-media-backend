package com.example.SocialMedia.service.social;

import com.example.SocialMedia.dto.request.CommentRequest;
import com.example.SocialMedia.dto.response.CommentResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    List<CommentResponse> getCommentsByPostId(Integer id, Pageable pageable);
    List<CommentResponse> getCommentsByUserName(String userName, Pageable pageable);
    List<CommentResponse> getCommentsByParentCommentId(Integer parentCommentId, Pageable pageable);

    // Cập nhật dòng này: thêm username
    CommentResponse createComment(String username, CommentRequest commentRequest);

    CommentResponse deleteComment(Integer commentId);

    List<CommentResponse> getCommentsByKeyword(String keyword, Pageable pageable);
}
