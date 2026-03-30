package com.example.SocialMedia.config.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@Slf4j
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("✅ Một kết nối socket mới vừa được thiết lập!");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.warn("❌ Một kết nối socket vừa bị ngắt (User thoát hoặc mất mạng)");
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        log.info("\uD83D\uDCE1 Có người vừa Subscribe vào đường dẫn: {}", event.getMessage().getHeaders().get("simpDestination"));
    }
}