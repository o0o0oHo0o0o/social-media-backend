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
public class ConversationDTO {
    private Integer conversationId;
    private String conversationName;
    private String groupImageUrl;
    private boolean isGroupChat;
    private LocalDateTime createdAt;
    private MessageDTO lastMessage;
    private Integer unreadCount;
    private LocalDateTime mutedUntil;
    private List<String> membersAvatars;
}