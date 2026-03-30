package com.example.SocialMedia.dto.auth;

import com.example.SocialMedia.constant.OtpChannel;
import lombok.Data;

@Data
public class VerifyResponse {
    private String message;
    private String identifier;
    private OtpChannel channel;
}
