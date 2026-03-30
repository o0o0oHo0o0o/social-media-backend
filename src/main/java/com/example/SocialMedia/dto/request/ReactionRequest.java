package com.example.SocialMedia.dto.request;


import com.example.SocialMedia.constant.ReactionType;
import com.example.SocialMedia.constant.TargetType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReactionRequest {
    private int targetId; // ID of the item being reacted to
    private TargetType targetType; // e.g., MESSAGE, POST, COMMENT, SHARE
    private ReactionType reactionType; // e.g., LIKE, LOVE, LAUGH, SAD,
    private LocalDateTime reactedAt;
}
