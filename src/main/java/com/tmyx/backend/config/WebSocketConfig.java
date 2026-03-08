package com.tmyx.backend.config;

import com.tmyx.backend.handler.LocationHandler;
import com.tmyx.backend.handler.MessageHandler;
import com.tmyx.backend.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        LocationHandler locationHandler = applicationContext.getBean(LocationHandler.class);
        SecurityService securityService = applicationContext.getBean(SecurityService.class);
        locationHandler.setSecurityService(securityService);

        // 将地址映射为 ws://localhost:8081/ws/location
        registry.addHandler(locationHandler, "/ws/location")
                .setAllowedOrigins("*");

        // 将地址映射为 ws://localhost:8081/ws/message
        MessageHandler messageHandler = applicationContext.getBean(MessageHandler.class);
        registry.addHandler(messageHandler, "/ws/message")
                .setAllowedOrigins("*");
    }

    @Bean
    public MessageHandler messageHandler() {
        return new MessageHandler();
    }
}
