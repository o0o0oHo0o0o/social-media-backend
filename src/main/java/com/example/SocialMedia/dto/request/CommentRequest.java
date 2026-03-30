package com.example.SocialMedia.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentRequest {
    private Integer id;
    private int userId;
    private int targetInteractableItemID;
    private Integer parentCommentId;
    private String content;
    private LocalDateTime createdAt;
}
