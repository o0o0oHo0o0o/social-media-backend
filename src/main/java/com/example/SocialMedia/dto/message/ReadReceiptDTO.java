package com.example.SocialMedia.dto.message;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.SocialMedia.model.messaging_model.MessageStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadReceiptDTO {
    private Integer conversationId;
    private Long messageId;
    private Integer userId;
    private String username;
    private MessageStatus.MessageStatusEnum status;
    private LocalDateTime timestamp;
}