package com.tmyx.backend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageHandler extends TextWebSocketHandler {
    private static final Map<Integer, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 建立连接时从url中获取用户id
        Integer userId = getUserId(session);
        if (userId != null) {
            sessions.put(userId, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Integer userId = getUserId(session);
        if (userId != null) sessions.remove(userId);
    }

    // 提供一个静态方法给 Service 调用
    public static void sendToUser(Integer userId, Object message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Integer getUserId(WebSocketSession session) {
        String query = session.getUri().getQuery();
        // 简单的解析逻辑，实际建议从 Token 中解析
        return Integer.parseInt(query.split("=")[1]);
    }
}
