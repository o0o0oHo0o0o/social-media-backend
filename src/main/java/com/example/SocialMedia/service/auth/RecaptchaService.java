package com.example.SocialMedia.service.auth;

public interface RecaptchaService {
    boolean verifyRecaptcha(String recaptchaToken);

}
