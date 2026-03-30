package com.example.SocialMedia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private int userId;
    private String username;
    private String fullName;
    private String avatar;
    private String nickname;
    private LocalDateTime joinedAt;
}