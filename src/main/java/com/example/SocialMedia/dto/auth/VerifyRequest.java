package com.example.SocialMedia.dto.auth;

import com.example.SocialMedia.constant.OtpChannel;
import lombok.Data;

@Data
public class VerifyRequest {
    private String otp;
    private String identifier;
    private OtpChannel channel;
}
