package com.example.SocialMedia.controller;

import com.example.SocialMedia.dto.auth.RegisterRequest;
import com.example.SocialMedia.dto.auth.VerifyRequest;
import com.example.SocialMedia.dto.auth.VerifyResponse;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.service.auth.AuthService;
import com.example.SocialMedia.service.auth.OtpService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/verification")
public class VerificationController {

    private final OtpService otpService;

    private final AuthService authService;

    private static final String SIGNUP_KEY_PREFIX = "signup:";
    private final StringRedisTemplate redisTemplate;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

//    private final com.google.firebase.FirebaseApp firebaseApp;
    @PostMapping("/otp")
    public ResponseEntity<VerifyResponse> verifyOtp(@RequestBody VerifyRequest verifyRequest,
                                                    HttpServletResponse response) {
        VerifyResponse responseEntity = new VerifyResponse();
        try {
            Optional<User> exists = authService.findUserByIdentifier(verifyRequest.getIdentifier(), verifyRequest.getChannel());
            if (exists.isPresent() && exists.get().isVerified()) {
                responseEntity.setMessage("Identifier already in use.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity);
            }

            otpService.verifyOtp(verifyRequest.getIdentifier(), verifyRequest.getOtp(), verifyRequest.getChannel());
            String key = SIGNUP_KEY_PREFIX + verifyRequest.getIdentifier();
            registerCacheData(response, key);
            responseEntity.setMessage("OTP verified successfully.");
            responseEntity.setIdentifier(verifyRequest.getIdentifier());
            responseEntity.setChannel(verifyRequest.getChannel());
            return ResponseEntity.ok(responseEntity);
        } catch (Exception ex) {
            responseEntity.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity);
        }
    }

    private void registerCacheData(HttpServletResponse response, String key) throws com.fasterxml.jackson.core.JsonProcessingException {
        String cachedJson = redisTemplate.opsForValue().get(key);
        if (cachedJson == null) {
            throw new RuntimeException("Phiên đăng ký đã hết hạn. Vui lòng thử lại.");
        }
        RegisterRequest cachedData = objectMapper.readValue(cachedJson, RegisterRequest.class);
        User user = authService.register(cachedData);
        authService.generateAndSetCookie(response,user);
        redisTemplate.delete(key);
    }

//    @PostMapping("/firebase-otp")
//    public ResponseEntity<VerifyResponse> verifyFirebaseOtp(
//            @RequestBody FirebaseVerifyRequest firebaseRequest,
//            HttpServletResponse response) {
//
//        VerifyResponse responseEntity = new VerifyResponse();
//        try {
//            FirebaseToken decodedToken = FirebaseAuth.getInstance(firebaseApp)
//                    .verifyIdToken(firebaseRequest.getFirebaseToken());
//
//            String uid = decodedToken.getUid();
//            UserRecord userRecord = FirebaseAuth.getInstance(firebaseApp).getUser(uid);
//            String phoneNumber = userRecord.getPhoneNumber();
//            if (phoneNumber == null) {
//                throw new RuntimeException("Firebase token không hợp lệ (không có SĐT).");
//            }
//
//            String key = SIGNUP_KEY_PREFIX + phoneNumber;
//            registerCacheData(response, key);
//
//            responseEntity.setMessage("Xác thực SĐT thành công!");
//            responseEntity.setIdentifier(phoneNumber);
//            return ResponseEntity.ok(responseEntity);
//
//        } catch (Exception ex) {
//            responseEntity.setMessage(ex.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity);
//        }
//    }
    @PostMapping("/resend-otp")
    public ResponseEntity<VerifyResponse> resendOtp(@RequestBody VerifyRequest verifyRequest) {
        VerifyResponse responseEntity = new VerifyResponse();
        try {
            otpService.sendOtp(verifyRequest.getIdentifier(), verifyRequest.getChannel());
            responseEntity.setMessage("OTP resent successfully.");
            responseEntity.setIdentifier(verifyRequest.getIdentifier());
            responseEntity.setChannel(verifyRequest.getChannel());
            return ResponseEntity.ok(responseEntity);
        } catch (RuntimeException ex) {
            responseEntity.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity);
        }
    }
}
