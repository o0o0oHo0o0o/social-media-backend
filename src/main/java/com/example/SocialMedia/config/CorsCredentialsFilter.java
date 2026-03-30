package com.example.SocialMedia.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class CorsCredentialsFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        
        // Log origin
        if (origin != null) {
            log.info("[CorsCredentialsFilter] Origin: {}", origin);
        }
        
        // Cho phép credentials header cho tất cả origins (Spring CORS sẽ handle origin validation)
        response.setHeader("Access-Control-Allow-Credentials", "true");
        
        log.info("[CorsCredentialsFilter] Set Access-Control-Allow-Credentials: true");
        
        filterChain.doFilter(request, response);
    }
}
