package com.tmyx.backend.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tmyx.backend.dto.LocationDto;
import com.tmyx.backend.service.LocationSimulationService;
import com.tmyx.backend.service.RedisLocationService;
import com.tmyx.backend.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class LocationHandler extends TextWebSocketHandler {
    @Autowired
    private SecurityService securityService;
    @Autowired
    private RedisLocationService redisLocationService;

    // 添加一个setter方法给WebSocketConfig调用
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    // 建立连接时触发
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        sessions.add(session);
        System.out.println("连接成功，当前连接数：" + sessions.size());
    }

    // 关闭连接时触发
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("连接已关闭");
    }

    // 处理前端发来的位置信息
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 解析前端发来的消息
        String payload = message.getPayload();
        JSONObject json = JSON.parseObject(payload);
        String type = json.getString("type");
        // 检测消息类型
        if ("report_location".equals(type)) {
            Integer userId = json.getInteger("userId");
            Double lng = json.getDouble("lng");
            Double lat = json.getDouble("lat");
            String avatarUrl = json.getString("avatarUrl");

            System.out.println("用户 " + userId + " 的位置：" + lng + ", " + lat);

            // 缓存位置信息到redis
            LocationDto dto = new LocationDto(userId, lng, lat, avatarUrl);
            redisLocationService.updateLocation("user", userId, dto);
            // 调用监控方法
//            securityService.monitorElderLocation(userId, lng, lat);

            // 广播数据
//            LocationSimulationService.pushUserLocations();
        }
    }

    // 广播模拟位置信息
    public static void broadcastLocation(String json) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(json));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
