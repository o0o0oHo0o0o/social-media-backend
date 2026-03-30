package com.example.SocialMedia.service.auth;

import com.example.SocialMedia.constant.OtpChannel;

public interface OtpService {
    String generateOtp();
    void sendOtp(String identifier, OtpChannel channel);

    boolean verifyOtp(String identifier, String otp, OtpChannel channel);
    long getOtpExpiryTime(String identifier);
}
