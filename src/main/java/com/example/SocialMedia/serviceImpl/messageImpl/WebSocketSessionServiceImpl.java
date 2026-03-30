package com.example.SocialMedia.serviceImpl.messageImpl;

import com.example.SocialMedia.dto.message.WebSocketTokenResponse;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.model.coredata_model.UserSession;
import com.example.SocialMedia.repository.message.UserSessionRepository;
import com.example.SocialMedia.service.message.WebSocketSessionService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketSessionServiceImpl implements WebSocketSessionService {
    @Value("${websocket.token.expiration-hours:1}")
    private int tokenExpirationHours;
    private final UserSessionRepository userSessionRepository;

    @Override
    @Transactional
    public WebSocketTokenResponse generateToken(User user) {
        userSessionRepository.deactivateUserSessions(user.getId());

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(tokenExpirationHours);

        UserSession session = new UserSession();
        session.setUser(user);
        session.setSessionToken(token);
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(expiresAt);
        session.setActive(true);

        userSessionRepository.save(session);

        log.info("Generated WebSocket token for user: {}", user.getId());

        return WebSocketTokenResponse.builder()
                .token(token)
                .expiresAt(expiresAt)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer validateTokenAndGetUserId(String token) {
        return userSessionRepository.findValidSessionByToken(token, LocalDateTime.now())
                .map(session -> session.getUser().getId())
                .orElse(null);
    }

    @Override
    @Transactional
    public void invalidateToken(String token) {
        int deactivatedCount = userSessionRepository.deactivateByToken(token);
        if (deactivatedCount > 0) {
            log.info("Invalidated {} WebSocket token(s): {}", deactivatedCount, token);
        } else {
            log.warn("Attempted to invalidate non-existent or inactive token: {}", token);
        }
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        int deactivatedCount = userSessionRepository.deactivateExpiredSessions(now);
        if (deactivatedCount > 0) {
            log.info("Cleaned up {} expired WebSocket sessions", deactivatedCount);
        }
    }
}