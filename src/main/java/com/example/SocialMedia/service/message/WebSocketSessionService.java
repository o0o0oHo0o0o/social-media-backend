package com.example.SocialMedia.service.message;

import com.example.SocialMedia.dto.message.WebSocketTokenResponse;
import com.example.SocialMedia.model.coredata_model.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

public interface WebSocketSessionService {
    @Transactional
    WebSocketTokenResponse generateToken(User user);
    @Transactional(readOnly = true)
    public Integer validateTokenAndGetUserId(String token);
    @Transactional
    void invalidateToken(String token);

    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    void cleanupExpiredSessions();
}
