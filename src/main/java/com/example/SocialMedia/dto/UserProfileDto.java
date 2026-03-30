package com.example.SocialMedia.dto;

import com.example.SocialMedia.constant.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private int id;
    private String email;
    private String phone;
    private String username;
    private String name;
    private String avatar;
    private AuthProvider provider;
    private LocalDateTime createdAt;
}
