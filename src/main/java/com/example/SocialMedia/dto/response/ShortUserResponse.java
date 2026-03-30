package com.example.SocialMedia.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortUserResponse {
    private int id;
    private String name;
    private String username;
    private String avatarUrl;
    private LocalDateTime createdAt;
}
