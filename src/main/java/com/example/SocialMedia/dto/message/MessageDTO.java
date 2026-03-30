package com.example.SocialMedia.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long messageId;
    private Integer conversationId;
    private Integer senderId;
    private String senderUsername;
    private String senderAvatar;
    private String content;
    private String messageType;
    private LocalDateTime sentAt;
    private Long sequenceNumber;
    private Long replyToMessageId;
    private boolean isDeleted;
    private List<MessageMediaDTO> media;
    private MessageStatusSummary statusSummary;
}