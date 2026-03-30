package com.example.SocialMedia.serviceImpl.social;

import com.example.SocialMedia.dto.request.CommentRequest;
import com.example.SocialMedia.dto.response.CommentResponse;
import com.example.SocialMedia.exception.ResourceNotFound.CommentNotFoundException;
import com.example.SocialMedia.exception.ResourceNotFound.PostNotFoundException;
import com.example.SocialMedia.exception.ResourceNotFound.UserNotFoundException;
import com.example.SocialMedia.mapper.CommentMapper; // Import thêm cái này
import com.example.SocialMedia.model.coredata_model.*;
import com.example.SocialMedia.repository.*;
import com.example.SocialMedia.service.social.CommentService;
import com.example.SocialMedia.service.social.InteractableItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final InteractableItemService interactableItemService;
    private final CommentMapper commentMapper; // Inject Mapper vào đây

    @Override
    public List<CommentResponse> getCommentsByPostId(Integer id, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByPost_InteractableItem_InteractableItemId_AndParentCommentIsNull(id, pageable);
        return comments.getContent().stream()
                .map(commentMapper::toCommentResponse) // Dùng Mapper
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByUserName(String userName, Pageable pageable) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userName));
        Page<Comment> comments = commentRepository.findByUserAndIsDeletedIsFalse(user, pageable);
        return comments.getContent().stream()
                .map(commentMapper::toCommentResponse) // Dùng Mapper
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByParentCommentId(Integer parentCommentId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByParentComment_CommentId(parentCommentId, pageable);
        return comments.getContent().stream()
                .map(commentMapper::toCommentResponse) // Dùng Mapper
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponse createComment(String username, CommentRequest request) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        Post post = postRepository.findByInteractableItem_InteractableItemId(request.getTargetInteractableItemID())
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(request.getContent());
        comment.setCreatedLocalDateTime(request.getCreatedAt());

        if (request.getParentCommentId() != null){
            Comment parent = commentRepository.findByCommentId(request.getParentCommentId())
                    .orElseThrow(() -> new CommentNotFoundException("Parent comment not found"));
            comment.setParentComment(parent);
        }

        comment.setOwnInteractableItem(
                interactableItemService.createInteractableItems("COMMENT", request.getCreatedAt())
        );

        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment); // Dùng Mapper
    }

    @Override
    public CommentResponse deleteComment(Integer commentId) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
        comment.setDeleted(true);
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment); // Dùng Mapper
    }

    @Override
    public List<CommentResponse> getCommentsByKeyword(String keyword, Pageable pageable){
        Page<Comment> comments = commentRepository.findByContentContainingIgnoreCase(keyword, pageable);
        return comments.getContent().stream()
                .map(commentMapper::toCommentResponse) // Dùng Mapper
                .collect(Collectors.toList());
    }
}   