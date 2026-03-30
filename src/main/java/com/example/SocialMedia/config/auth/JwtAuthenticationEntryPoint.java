package com.example.SocialMedia.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {
        String uri = request.getRequestURI();
        String specificClientMessage;
        if (authException instanceof BadCredentialsException) {
            specificClientMessage = "Sai email hoặc mật khẩu. Vui lòng thử lại.";
            logger.warn("Lỗi đăng nhập (BadCredentials) cho {}: {}", uri, authException.getMessage());

        } else if (authException instanceof LockedException) {
            specificClientMessage = "Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email/SMS xác thực.";
            logger.warn("Lỗi đăng nhập (Tài khoản chưa kích hoạt/Locked) cho {}: {}", uri, authException.getMessage());

        } else if (authException instanceof DisabledException) {
            specificClientMessage = "Tài khoản của bạn đã bị vô hiệu hóa.";
            logger.warn("Lỗi đăng nhập (Tài khoản bị xóa/Disabled) cho {}: {}", uri, authException.getMessage());

        } else {
            specificClientMessage = "Yêu cầu xác thực không hợp lệ.";
            logger.error("Lỗi xác thực (Chưa xác thực) cho {}: {}", uri, authException.getMessage());
        }
        log.error("Authentication error: {} for URI: {}", authException.getMessage(), request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("message", specificClientMessage);
        errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("path", request.getRequestURI());
        response.getWriter().write(mapper.writeValueAsString(errorDetails));
    }
}
