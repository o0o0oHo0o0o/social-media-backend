package com.example.SocialMedia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TypingPayload {
    private Integer conversationId;
    private String username;
    private String avatarUrl;
    private int userId;
    @JsonProperty("isTyping")
    private boolean typing;
}
