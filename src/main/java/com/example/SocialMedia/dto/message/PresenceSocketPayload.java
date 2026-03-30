package com.example.SocialMedia.dto.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PresenceSocketPayload {
    private Integer conversationId;
    private Integer userId;
    private String username;
    private Boolean isOnline;
    private String lastActiveAt;
}