package com.example.SocialMedia.controller;

import com.example.SocialMedia.dto.WebRtcMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebRtcController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Endpoint nhận tín hiệu WebRTC từ Client
     * Client gửi tới: /app/video-call
     */
    @MessageMapping("/video-call")
    public void handleWebRtcSignal(@Payload WebRtcMessage message, Principal principal) {
        // 1. Log nhận tin (Incoming)
        log.info("📥 WebRTC IN: Type={} | From={} | To={}",
                message.getType(), principal.getName(), message.getReceiver());

        message.setSender(principal.getName());

        if (message.getReceiver() == null || message.getReceiver().isEmpty()) {
            log.warn("⚠️ WebRTC Ignored: Receiver is null");
            return;
        }

        // 2. Log gửi tin (Outgoing) -> Để chắc chắn code chạy tới đây
        log.debug("📤 WebRTC OUT: Forwarding to User [{}]", message.getReceiver());

        try {
            messagingTemplate.convertAndSendToUser(
                    message.getReceiver(),
                    "/queue/video-call",
                    message
            );
        } catch (Exception e) {
            log.error("🔥 Error sending WebRTC message to user: {}", message.getReceiver(), e);
        }
    }
}