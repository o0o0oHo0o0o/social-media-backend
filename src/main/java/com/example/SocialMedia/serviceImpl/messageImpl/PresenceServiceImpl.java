package com.example.SocialMedia.serviceImpl.messageImpl;

import com.example.SocialMedia.dto.message.PresenceState;
import com.example.SocialMedia.service.message.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PresenceServiceImpl implements PresenceService {

    private final StringRedisTemplate redis;
    private static final long TTL_SECONDS = 300; // 5 phút giữ key sống
    private static final String PREFIX_LAST_ACTIVE = "presence:lastActive:";
    private static final String PREFIX_ONLINE = "presence:online:";

    @Override
    public void touch(Integer userId, String username) {
        String now = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        redis.opsForValue().set(PREFIX_LAST_ACTIVE + userId, now, TTL_SECONDS, TimeUnit.SECONDS);
        redis.opsForValue().set(PREFIX_ONLINE + userId, "1", TTL_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void setOffline(Integer userId, String username) {
        String now = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        redis.opsForValue().set(PREFIX_LAST_ACTIVE + userId, now, TTL_SECONDS, TimeUnit.SECONDS);
        redis.delete(PREFIX_ONLINE + userId);
    }

    @Override
    public PresenceState getState(Integer userId, String username) {
        String lastActive = redis.opsForValue().get(PREFIX_LAST_ACTIVE + userId);
        boolean online = redis.hasKey(PREFIX_ONLINE + userId) != null && Boolean.TRUE.equals(redis.hasKey(PREFIX_ONLINE + userId));
        return PresenceState.builder()
                .userId(userId)
                .username(username)
                .online(online)
                .lastActiveAt(lastActive)
                .build();
    }

    @Override
    public boolean isOnlineWithin(Integer userId, String username, long withinSeconds) {
        String lastActive = redis.opsForValue().get(PREFIX_LAST_ACTIVE + userId);
        if (lastActive == null) return false;
        Instant last = Instant.parse(lastActive);
        return Instant.now().minusSeconds(withinSeconds).isBefore(last);
    }
}