package com.example.SocialMedia.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMemberDTO {
    private Integer userId;
    private String username;
    private String profilePictureUrl;
    private String nickname;
    private String role;
    private boolean isOnline;
}