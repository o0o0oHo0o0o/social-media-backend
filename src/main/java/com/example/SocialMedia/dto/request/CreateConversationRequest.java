package com.example.SocialMedia.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CreateConversationRequest {
    private String name;
    private boolean isGroup;
    private List<Integer> memberIds;
}