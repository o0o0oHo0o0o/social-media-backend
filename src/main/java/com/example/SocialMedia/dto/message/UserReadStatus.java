package com.example.SocialMedia.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.example.SocialMedia.model.messaging_model.MessageStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReadStatus {
    private Integer userId;
    private String username;
    private MessageStatus.MessageStatusEnum status;
    private LocalDateTime readAt;
}