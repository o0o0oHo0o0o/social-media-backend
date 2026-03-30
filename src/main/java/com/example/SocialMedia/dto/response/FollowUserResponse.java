package com.example.SocialMedia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowUserResponse {
    private Integer userId;
    private String username;
    private String fullName;
    private String avatarUrl;
    private boolean mutualFollow; // Cờ kiểm tra bạn bè (follow 2 chiều)
}