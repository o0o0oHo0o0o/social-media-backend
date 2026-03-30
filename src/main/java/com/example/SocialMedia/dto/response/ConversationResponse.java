package com.example.SocialMedia.dto.response;

import com.example.SocialMedia.constant.InboxType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationResponse {
    private Long conversationId;
    private String conversationName;
    private String avatarUrl;
    private boolean isGroup;

    // Last Message Info
    private String lastMessageContent;
    private String lastMessageTime;
    private String lastMessageType; // TEXT, IMAGE...
    private SenderDto lastSender;

    // Status
    private long unreadCount;
    private boolean isMuted;
    private InboxType inboxType;

    private String createdAt;
    private Long lastMessageId;
    private String lastMessageSender;
    private Long lastReadMessageId;
    private String mutedUntil;

    private Integer otherUserId;
    private String otherUserUsername;
    private Boolean otherUserOnline;
    private String otherUserLastActiveAt;
}