package com.example.SocialMedia.service.auth;

import com.example.SocialMedia.model.coredata_model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;

public interface JwtService {

    String extractUsername(String token);
    Date extractExpiration(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenExpired(String token);
    String generateToken(Map<String, Object> extraClaims , UserDetails userDetails, long expiration);
    String generateAccessToken(User userDetails);
    String generateRefreshToken();
}
