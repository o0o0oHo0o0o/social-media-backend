package com.example.SocialMedia.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationDTO {
    private String title;
    private String body;
    private String imageUrl;
    private Integer conversationId;
    private Long messageId;
    private Integer senderId;
    private String senderName;
}