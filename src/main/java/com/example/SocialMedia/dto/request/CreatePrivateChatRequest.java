package com.example.SocialMedia.dto.request;

import lombok.Data;

@Data
public class CreatePrivateChatRequest {
    private int targetUserId;
}