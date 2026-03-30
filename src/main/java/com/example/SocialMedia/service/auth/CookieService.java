package com.example.SocialMedia.service.auth;

import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
    void addAccessTokenCookie(HttpServletResponse response, String token);

    void addRefreshTokenCookie(HttpServletResponse response, String token);

    void clearCookie(HttpServletResponse response, String name);

    void clearAllAuthCookie(HttpServletResponse response);
}
