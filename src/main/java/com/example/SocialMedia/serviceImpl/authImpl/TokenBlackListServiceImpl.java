package com.example.SocialMedia.serviceImpl.authImpl;

import com.example.SocialMedia.service.auth.JwtService;
import com.example.SocialMedia.service.auth.TokenBlackListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenBlackListServiceImpl implements TokenBlackListService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;
    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    @Override
    public void blackList(String token) {
        Date expiration = jwtService.extractExpiration(token);
        long ttl = expiration.getTime() - System.currentTimeMillis();
        if (ttl > 0) {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(
                    key,
                    "blacklist",
                    Duration.ofMillis(ttl)
            );
        }
    }
}
