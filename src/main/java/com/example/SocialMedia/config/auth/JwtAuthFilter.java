package com.example.SocialMedia.config.auth;

import com.example.SocialMedia.service.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Value("${jwt.access.token.cookie.name}")
    private String ACCESS_TOKEN_COOKIE = "access_token";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean skip = path.startsWith("/auth/login") || path.startsWith("/auth/logout");
        log.info("[JwtAuthFilter] Path: {}, Skip: {}", path, skip);
        return skip;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws IOException, ServletException {
        String path = request.getRequestURI();
        log.info("[JwtAuthFilter] doFilterInternal - Path: {}", path);

        try {
            String jwt = extractTokenFromCookie(request);
            log.info("[JwtAuthFilter] Token from cookie: {}", jwt != null ? "FOUND" : "NOT FOUND");

            if (jwt == null) {
                jwt = extractTokenFromHeader(request);
                log.info("[JwtAuthFilter] Token from header: {}", jwt != null ? "FOUND" : "NOT FOUND");
            }

            if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("[JwtAuthFilter] Processing JWT token");
                String username = jwtService.extractUsername(jwt);
                log.info("[JwtAuthFilter] Extracted username: {}", username);

                if (username != null) {
                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        log.info("[JwtAuthFilter] Loaded user: {}, Authorities: {}", username, userDetails.getAuthorities());

                        if (jwtService.isTokenValid(jwt, userDetails)){
                            log.info("[JwtAuthFilter] Token valid, setting authentication for: {}", username);
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            log.info("[JwtAuthFilter] Authentication set successfully");
                        } else {
                            log.warn("[JwtAuthFilter] Token validation failed");
                        }
                    } catch (UsernameNotFoundException e) {
                        log.error("[JwtAuthFilter] User not found: {}", username, e);
                    }
                }
            } else {
                log.info("[JwtAuthFilter] No JWT token or authentication already set");
            }
        } catch (Exception e) {
            log.error("[JwtAuthFilter] Error: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return
                Arrays.stream(request.getCookies())
                        .filter(cookie -> cookie.getName().equals(ACCESS_TOKEN_COOKIE))
                        .map(Cookie::getValue).findFirst().orElse(null);
    }
}