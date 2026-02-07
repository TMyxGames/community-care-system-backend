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
    private static final Map<Integer, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 建立连接时将用户id与session进行绑定
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 建立连接时从url中获取用户id
        Integer userId = getUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
            System.out.println("WebSocket 已连接: 用户ID = " + userId);
        }
    }

    // 断开连接时移除session
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer userId = getUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
            System.out.println("WebSocket 已断开: 用户ID = " + userId);
        }
    }

    // 提供一个方法给Service调用
    public void sendMessageToUser(Integer userId, Object message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                System.out.println("准备推送给用户: " + userId + "，当前在线用户集: " + userSessions.keySet());
                String json = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 从连接url中获取用户id
    private Integer getUserId(WebSocketSession session) {
        try {
            String query = session.getUri().getQuery();
            if (query != null && query.contains("userId=")) {
                return Integer.parseInt(query.split("userId=")[1]);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
