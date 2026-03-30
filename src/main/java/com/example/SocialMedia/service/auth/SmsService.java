package com.example.SocialMedia.service.auth;

public interface SmsService {
    void sendOtpSms(String phoneNumber, String otp);
}
