package com.example.SocialMedia.dto.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PresenceState {
    private Integer userId;
    private String username;
    private boolean online;
    private String lastActiveAt; // ISO string
}