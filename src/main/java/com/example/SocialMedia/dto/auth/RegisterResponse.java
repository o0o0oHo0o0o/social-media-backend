package com.example.SocialMedia.dto.auth;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RegisterResponse {
    private String email;
    private String phoneNumber;
    private String message;
}
