package com.example.SocialMedia.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.TcpOperations;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import reactor.netty.tcp.TcpClient;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketTokenFilter webSocketTokenFilter;

    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;

    @Value("${spring.rabbitmq.username}")
    private String rabbitUser;

    @Value("${spring.rabbitmq.password}")
    private String rabbitPass;

    @Value("${spring.rabbitmq.stomp.port}")
    private int rabbitStompPort;

    @Value("${spring.rabbitmq.virtual-host:/}")
    private String rabbitVhost;

    @Value("${spring.rabbitmq.stomp.ssl-enabled:true}")
    private boolean rabbitStompSslEnabled;

    @Value("${app.stomp.relay.enabled:true}")
    private boolean stompRelayEnabled;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");

        registry.addEndpoint("/ws-sockjs")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        if (stompRelayEnabled) {
            TcpClient tcpClient = TcpClient.create()
                    .host(rabbitHost)
                    .port(rabbitStompPort);

            if (rabbitStompSslEnabled) {
                tcpClient = tcpClient.secure();
            }

            TcpOperations<byte[]> relayTcpClient = new ReactorNettyTcpClient<>(tcpClient, new StompReactorNettyCodec());

            registry.enableStompBrokerRelay("/topic", "/queue", "/exchange")
                    .setRelayHost(rabbitHost)
                    .setRelayPort(rabbitStompPort)
                    .setClientLogin(rabbitUser)
                    .setClientPasscode(rabbitPass)
                    .setSystemLogin(rabbitUser)
                    .setSystemPasscode(rabbitPass)
                    .setVirtualHost(rabbitVhost)
                    .setTcpClient(relayTcpClient)
                    .setSystemHeartbeatSendInterval(10000)
                    .setSystemHeartbeatReceiveInterval(10000);

            log.info(
                    "WebSocket broker mode: STOMP relay {}:{} (sslEnabled={}, vhost={})",
                    rabbitHost,
                    rabbitStompPort,
                    rabbitStompSslEnabled,
                    rabbitVhost);
        } else {
            registry.enableSimpleBroker("/topic", "/queue", "/exchange");
            log.warn("WebSocket broker mode: in-memory simple broker (STOMP relay disabled)");
        }

        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketTokenFilter);
    }
}
