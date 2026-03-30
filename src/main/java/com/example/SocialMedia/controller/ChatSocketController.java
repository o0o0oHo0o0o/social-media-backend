package com.example.SocialMedia.controller;

import com.example.SocialMedia.dto.TypingPayload;
import com.example.SocialMedia.dto.response.SocketResponse;
import com.example.SocialMedia.dto.request.TypingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    // Client gửi socket tới: /app/chat.typing
    @MessageMapping("/chat.typing")
    public void handleTypingEvent(@Payload TypingRequest request, Authentication authentication) {
        System.out.println("[TYPING DEBUG] Full Request: " + request);
        System.out.println("[TYPING DEBUG] isTyping value: " + request.isTyping());
        System.out.println("[TYPING DEBUG] conversationId: " + request.getConversationId());

        var typingPayload = new TypingPayload(
                request.getConversationId(),
                authentication.getName(),
                request.getAvatarUrl(),
                request.getUserId(),
                request.isTyping()
        );
        System.out.println("[TYPING DEBUG] TypingPayload created: " + typingPayload);

        SocketResponse<Object> event = SocketResponse.builder()
                .type("TYPING")
                .payload(typingPayload)
                .build();


        messagingTemplate.convertAndSend("/topic/chat." + request.getConversationId(), event);
    }
}