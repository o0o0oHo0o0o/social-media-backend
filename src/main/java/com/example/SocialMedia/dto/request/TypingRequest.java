package com.example.SocialMedia.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingRequest {
    private Integer conversationId;
    private Integer userId;
    private String username;
    private String avatarUrl;
    @JsonProperty("isTyping")
    private boolean isTyping;
    private LocalDateTime timestamp;
}