package com.example.SocialMedia.dto.otp;

import com.example.SocialMedia.constant.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialAuthDto {
    private String accessToken;
    private AuthProvider provider;
}
