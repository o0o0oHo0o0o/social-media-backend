package com.example.SocialMedia.controller;

import com.example.SocialMedia.dto.*;
import com.example.SocialMedia.dto.auth.LoginRequest;
import com.example.SocialMedia.dto.auth.LoginResponse;
import com.example.SocialMedia.dto.auth.RegisterRequest;
import com.example.SocialMedia.dto.auth.RegisterResponse;
import com.example.SocialMedia.exception.ResourceNotFound.UserNotFoundException;
import com.example.SocialMedia.mapper.UserMapper;
import com.example.SocialMedia.model.coredata_model.RefreshToken;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.service.auth.AuthService;
import com.example.SocialMedia.service.auth.JwtService;
import com.example.SocialMedia.service.auth.OtpService;
import com.example.SocialMedia.service.auth.RecaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;
    private final RecaptchaService recaptchaService;
    private final JwtService jwtService;
    private final com.example.SocialMedia.service.auth.CookieService cookieService;
    private final com.example.SocialMedia.repository.RefreshTokenRepository refreshTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final String SIGNUP_KEY_PREFIX = "signup:";
    private static final int SIGNUP_EXPIRY_MINUTES = 10;
    @Value("${jwt.refresh.token.cookie.name}")
    private String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final UserMapper userMapper;
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest registerRequest) {
        String recaptchaToken = registerRequest.getRecaptchaToken();
        if (!recaptchaService.verifyRecaptcha(recaptchaToken)) {
            RegisterResponse bad = new RegisterResponse();
            bad.setMessage("Recaptcha verification failed.");
            return new ResponseEntity<>(bad, HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<User> existingUserOpt = authService.findUserByIdentifier(
                    registerRequest.getIdentifier(),
                    registerRequest.getChannel()
            );
            if (existingUserOpt.isPresent() && existingUserOpt.get().isVerified()) {
                throw new IllegalArgumentException("Email hoặc SĐT này đã được sử dụng.");
            }
            if (registerRequest.getChannel() == com.example.SocialMedia.constant.OtpChannel.EMAIL) {
                otpService.sendOtp(
                        registerRequest.getIdentifier(),
                        registerRequest.getChannel()
                );
            }
            authService.cacheRegistrationData(registerRequest, SIGNUP_KEY_PREFIX, SIGNUP_EXPIRY_MINUTES);
            RegisterResponse registerResponse = getRegisterResponse(registerRequest);
            return new ResponseEntity<>(registerResponse, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            RegisterResponse err = new RegisterResponse();
            err.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        } catch (Exception ex) {
            logger.error("Lỗi nghiêm trọng khi đăng ký: {}", ex.getMessage(), ex);

            RegisterResponse err = new RegisterResponse();
            err.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    private static RegisterResponse getRegisterResponse(RegisterRequest registerRequest) {
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setMessage("Successfully registered! Please verify your email.");
        if (registerRequest.getChannel() == com.example.SocialMedia.constant.OtpChannel.EMAIL) {
            registerResponse.setEmail(registerRequest.getIdentifier());
        } else if (registerRequest.getChannel() == com.example.SocialMedia.constant.OtpChannel.SMS) {
            registerResponse.setPhoneNumber(registerRequest.getIdentifier());
        }
        return registerResponse;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response){
        User user = authService.login(loginRequest);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setMessage("Successfully logged in!");
        authService.generateAndSetCookie(response, user);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser(
            @AuthenticationPrincipal User currentUser
    ) {
        if(currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = authService.findUserByIdentifier(currentUser.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + currentUser.getUsername()));

        return ResponseEntity.ok(userMapper.toUserProfileDto(user));
    }
    @PostMapping("/refresh")
    @Transactional
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;
        if (request.getCookies() != null) {
            refresh = Arrays.stream(request.getCookies())
                    .filter(c -> REFRESH_TOKEN_COOKIE.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        if (refresh == null || refresh.isBlank()) {
            return ResponseEntity.status(401).body("Missing refresh token");
        }

        Optional<RefreshToken> opt = refreshTokenRepository.findByToken(refresh);
        if (opt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
        RefreshToken rt = opt.get();

        // Giả định entity có trường expiryDate (theo log Hibernate bạn gửi)
        if (rt.getExpiryDate() != null && !rt.getExpiryDate().isAfter(LocalDateTime.now())) {
            return ResponseEntity.status(401).body("Refresh token expired");
        }

        User user = rt.getUser();
        if (user == null) {
            return ResponseEntity.status(401).body("Invalid refresh token (no user)");
        }

        // Phát hành access token mới và set cookie
        String newAccess = jwtService.generateAccessToken(user);
        cookieService.addAccessTokenCookie(response, newAccess);

        return ResponseEntity.ok("{\"ok\":true}");
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Xoá cookies auth
        cookieService.clearAllAuthCookie(response);
        return ResponseEntity.ok().build();
    }
}