package com.example.SocialMedia.serviceImpl.authImpl;

import com.example.SocialMedia.constant.OtpChannel;
import com.example.SocialMedia.dto.auth.LoginRequest;
import com.example.SocialMedia.dto.auth.RegisterRequest;
import com.example.SocialMedia.model.coredata_model.RefreshToken;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.repository.RefreshTokenRepository;
import com.example.SocialMedia.service.*;
import com.example.SocialMedia.service.auth.AuthService;
import com.example.SocialMedia.service.auth.CookieService;
import com.example.SocialMedia.service.auth.EmailService;
import com.example.SocialMedia.service.auth.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.SocialMedia.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    @Value("${jwt.refresh.token.ttl}")
    private long expiration;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final EmailService emailService;
    private final UserService userService;
    private final StringRedisTemplate redisTemplate;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$";

    @Override
    public User register(RegisterRequest cachedData) {
        User user = new User();
        user.setUserName(userService.generateUniqueUsername(
                cachedData.getIdentifier(),
                cachedData.getChannel()
        ));
        if (cachedData.getChannel() == OtpChannel.EMAIL) {
            user.setEmail(cachedData.getIdentifier());
        } else if (cachedData.getChannel() == OtpChannel.SMS) {
            user.setPhoneNumber(cachedData.getIdentifier());
            user.setEmail(cachedData.getIdentifier() + "@phone.local");
        }
        user.setPassword(cachedData.getPassword());
        user.setAuthProvider(cachedData.getAuthProvider());
        user.setFullName(cachedData.getFullName());
        user.setCreatedLocalDateTime(LocalDateTime.now());
        user.setVerified(true);
        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }
    @Override
    public void cacheRegistrationData(RegisterRequest registerRequest, String prefix, int expiryMinutes) throws Exception {
        String identifier = registerRequest.getIdentifier();

        if (registerRequest.getChannel() == OtpChannel.SMS) {
            identifier = formatPhoneNumber(identifier);
        }
        registerRequest.setIdentifier(identifier);
        String key = prefix + registerRequest.getIdentifier();
        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
        registerRequest.setPassword(hashedPassword);

        String requestJson = objectMapper.writeValueAsString(registerRequest);

        redisTemplate.opsForValue().set(key, requestJson, Duration.ofMinutes(expiryMinutes));
    }
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        if (phoneNumber.startsWith("+")) {
            return phoneNumber;
        }
        if (phoneNumber.startsWith("0")) {
            return "+84" + phoneNumber.substring(1);
        }
        if (phoneNumber.matches("\\d{9}")) {
            return "+84" + phoneNumber;
        }
        return phoneNumber;
    }
    @Override
    public User login(LoginRequest loginRequest) {
        User user = findUserByIdentifier(loginRequest.getIdentifier())
                .orElseThrow(() -> new BadCredentialsException("Sai thông tin đăng nhập"));
        String actualUsername = user.getUsername();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        actualUsername,
                        loginRequest.getPassword()
                )
        );
        User authenticationUser = (User) authentication.getPrincipal();
        authenticationUser.setLastLogin(LocalDateTime.now());
        userRepository.save(authenticationUser);
        return authenticationUser;
    }
    @Override
    public Optional<User> findUserByIdentifier(String identifier) {
        if (identifier.matches(EMAIL_REGEX)) {
            return userRepository.findByEmail(identifier);
        } else {
            return userRepository.findByPhoneNumber(identifier);
        }
    }
    @Override
    public Optional<User> findUserByIdentifier(String identifier, OtpChannel channel) {
        if (channel == OtpChannel.EMAIL) {
            return userRepository.findByEmail(identifier);

        } else if (channel == OtpChannel.SMS) {
            return userRepository.findByPhoneNumber(identifier);
        } else {
            throw new IllegalArgumentException("Kênh xác thực không hợp lệ: " + channel);
        }
    }

    private String generateAccessToken(User user) {
        return jwtService.generateAccessToken(user);
    }

    private String generateRefreshToken() {
        return jwtService.generateRefreshToken();
    }

    @Override
    public void saveRefreshToken(int userID, String refreshToken) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("Error: User not found with id " + userID));
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setCreatedAt(LocalDateTime.now());
        refreshTokenEntity.setExpiryDate(LocalDateTime.now().plusSeconds(expiration));
        refreshTokenRepository.save(refreshTokenEntity);
    }

    @Override
    public void generateAndSetCookie(HttpServletResponse response, User user) {
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken();

        saveRefreshToken(user.getId(), refreshToken);
        cookieService.addAccessTokenCookie(response, accessToken);
        cookieService.addRefreshTokenCookie(response, refreshToken);
    }

    @Override
    public User findOrCreateUserByPhone(String phoneNumber) {
        return null;
    }

    @Override
    public void activeUserAccount(User user) {
        user.setVerified(true);
        userRepository.save(user);
        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken).orElseThrow(
                () -> new RuntimeException("Error: Refresh token not found")
        );
        if(token.getExpiryDate().isBefore(LocalDateTime.now())){
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token is expired");
        }
        User user = token.getUser();
        return generateAccessToken(user);
    }

    @Override
    public void revokeRefreshToken(String refreshToken) {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);
        token.ifPresent(refreshTokenRepository::delete);
    }
    @Override
    public void revokeAllRefreshTokens(int userID){
        List<RefreshToken> refreshTokens = refreshTokenRepository.findByUserId(userID);
        refreshTokenRepository.deleteAll(refreshTokens);
    }
}
