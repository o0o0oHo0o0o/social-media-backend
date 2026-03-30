package com.example.SocialMedia.config;

import com.example.SocialMedia.repository.UserRepository;
import com.example.SocialMedia.service.message.WebSocketSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketTokenFilter implements ChannelInterceptor {

    private final WebSocketSessionService webSocketSessionService;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String wsToken = accessor.getFirstNativeHeader("X-WS-TOKEN");
            if (wsToken == null || wsToken.isBlank()) {
                log.warn("Missing X-WS-TOKEN on CONNECT");
                return message; // sẽ là anonymous => controller nên null-guard
            }
            Integer userId = webSocketSessionService.validateTokenAndGetUserId(wsToken);
            if (userId == null) {
                log.warn("Invalid WS token");
                return message;
            }
            var user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                log.warn("User not found for WS token");
                return message;
            }
            var auth = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), null, List.of() // TODO: add roles nếu cần
            );
            accessor.setUser(auth);
        }
        return message;
    }
}