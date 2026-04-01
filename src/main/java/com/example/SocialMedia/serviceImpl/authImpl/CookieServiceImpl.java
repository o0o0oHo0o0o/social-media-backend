package com.example.SocialMedia.serviceImpl.authImpl;

import com.example.SocialMedia.service.auth.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CookieServiceImpl implements CookieService {

    private static final String COOKIE_PATH = "/";

    @Value("${jwt.access.token.cookie.name}")
    private String ACCESS_TOKEN_COOKIE = "access_token";

    @Value("${jwt.refresh.token.cookie.name}")
    private String REFRESH_TOKEN_COOKIE = "refresh_token";

    @Value("${jwt.access.token.ttl}")
    private int accessExpiration;

    @Value("${jwt.refresh.token.ttl}")
    private int refreshExpiration;

    @Value("${app.cookie.secure:true}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site:None}")
    private String cookieSameSite;

    @Override
    public void addAccessTokenCookie(HttpServletResponse response, String token) {
        addSecureCookie(response, ACCESS_TOKEN_COOKIE, token, accessExpiration / 1000);
    }

    @Override
    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        addSecureCookie(response, REFRESH_TOKEN_COOKIE, token, refreshExpiration / 1000);

    }

    @Override
    public void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        applyCookiePolicy(cookie);
        response.addCookie(cookie);
    }

    @Override
    public void clearAllAuthCookie(HttpServletResponse response) {
        clearCookie(response, ACCESS_TOKEN_COOKIE);
        clearCookie(response, REFRESH_TOKEN_COOKIE);
    }

    private void addSecureCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAgeInSeconds);
        applyCookiePolicy(cookie);
        response.addCookie(cookie);
    }

    private void applyCookiePolicy(Cookie cookie) {
        cookie.setPath(COOKIE_PATH);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setAttribute("SameSite", cookieSameSite);
    }
}
