package com.example.SocialMedia.dto.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReplyMessageDto {
    private Long messageId;
    private String content;
    private String senderName;
    private boolean hasMedia;
    private String mediaType;
}
