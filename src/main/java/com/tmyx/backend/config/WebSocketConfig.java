package com.tmyx.backend.config;

import com.tmyx.backend.handler.LocationHandler;
import com.tmyx.backend.handler.MessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 将地址映射为 ws://localhost:8081/ws/location
        registry.addHandler(new LocationHandler(), "/ws/location")
                .setAllowedOrigins("*");

        registry.addHandler(messageHandler(), "/ws/message")
                .setAllowedOrigins("*");
    }

    @Bean
    public MessageHandler messageHandler() {
        return new MessageHandler();
    }
}
