package com.example.SocialMedia.dto.response;

import com.example.SocialMedia.constant.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionResponse {
    private Integer userId;
    private String username;
    private String fullName;
    private String avatarUrl; // URL ảnh đại diện
    private ReactionType reactionType; // Enum: LIKE, LOVE, etc.

    public ReactionResponse(int reactionId, ShortUserResponse shortUserResponse, ReactionType reactionType, int interactableItemId) {
    }

    public ReactionResponse(Integer o, Object shortUserResponse, Object reactionType, Object o1) {
    }
}