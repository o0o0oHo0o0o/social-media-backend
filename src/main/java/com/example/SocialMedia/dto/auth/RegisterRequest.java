package com.example.SocialMedia.dto.auth;

import com.example.SocialMedia.constant.AuthProvider;
import com.example.SocialMedia.constant.OtpChannel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RegisterRequest {
    private String fullName;
    private String password;
    private String identifier;
    private OtpChannel channel;
    private String email;
    private AuthProvider authProvider;
    private String recaptchaToken;
}
