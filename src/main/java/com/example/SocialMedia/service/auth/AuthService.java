package com.example.SocialMedia.service.auth;

import com.example.SocialMedia.constant.OtpChannel;
import com.example.SocialMedia.dto.auth.LoginRequest;
import com.example.SocialMedia.dto.auth.RegisterRequest;
import com.example.SocialMedia.model.coredata_model.User;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;


public interface AuthService {
    User register(RegisterRequest cachedData);

    User login(LoginRequest loginRequest);

    void saveRefreshToken(int userID, String refreshToken);

    void generateAndSetCookie(HttpServletResponse response, User user);

    User findOrCreateUserByPhone(String phoneNumber);

    Optional<User> findUserByIdentifier(String identifier);

    Optional<User> findUserByIdentifier(String identifier, OtpChannel channel);

    void cacheRegistrationData(RegisterRequest registerRequest, String prefix, int expiryMinutes) throws Exception;

    String refreshAccessToken(String refreshToken);

    void revokeRefreshToken (String refreshToken);

    void revokeAllRefreshTokens(int userID);

    void activeUserAccount(User user);
}
