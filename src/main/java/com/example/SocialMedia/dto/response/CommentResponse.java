package com.example.SocialMedia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private int id;
    private String content;
    private ShortUserResponse user;
    private Integer interactableItemId;
    private Integer targetInteractableItemId;
    private Integer parentCommentId;
    private boolean replied; 

    private Map<String, Long> reactionCounts;

    private LocalDateTime createdAt;
}