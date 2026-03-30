package com.example.SocialMedia.service.auth;

public interface EmailService {
    void sendEmail(String to, String otp);
    String buildOtpEmailBody(String otp);
    void sendWelcomeEmail(String to, String name);
}
