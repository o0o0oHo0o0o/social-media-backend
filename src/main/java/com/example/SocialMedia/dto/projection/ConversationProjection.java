package com.example.SocialMedia.dto.projection;

import java.time.LocalDateTime;

public interface ConversationProjection {
    Integer getConversationID();
    String getConversationName();
    String getGroupImageURL();
    Boolean getIsGroupChat();
    LocalDateTime getCreatedAt();
    Long getLastMessageID();
    String getLastMessageContent();
    LocalDateTime getLastMessageSentAt();
    String getLastMessageSender(); // Username người gửi cuối
    Long getLastReadMessageID();
    LocalDateTime getMutedUntil();
    Long getUnreadCount();
    String getOtherUserFullName();      // Tên người kia (cho chat 1-1)
    String getOtherUserAvatar();
    Integer getOtherUserId();
    String getOtherUserUsername();
    String getInboxType(); // PRIMARY, GENERAL, REQUEST

}
