package com.tmyx.backend.service;

import com.tmyx.backend.common.Result;
import com.tmyx.backend.dto.UserInfoDto;
import com.tmyx.backend.dto.WebSocketResult;
import com.tmyx.backend.entity.EmergencyCall;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.handler.MessageHandler;
import com.tmyx.backend.mapper.EmergencyCallMapper;
import com.tmyx.backend.mapper.UserMapper;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SecurityService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageHandler messageHandler;
    @Autowired
    private MailService mailService;
    @Autowired
    private EmergencyCallMapper emergencyCallMapper;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    // 检查位置是否在范围内
    public boolean checkInArea(double lng, double lat, String wktPolygon) {
        try {
            WKTReader reader = new WKTReader(geometryFactory);
            // 将字符串转换为多边形对象
            Polygon polygon = (Polygon) reader.read(wktPolygon);
            // 创建当前位置点
            Point point = geometryFactory.createPoint(new Coordinate(lng, lat));
            // 执行运算
            return polygon.contains(point);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 获取告警状态
    public Map<String, Object> getAlarmStatus(Integer elderId) {
        Map<String, Object> status = new HashMap<>();
        // 检查是否在静默期
        String silenceKey = "silence_mode:" + elderId;
        Boolean isSilenced = redisTemplate.hasKey(silenceKey);
        status.put("isSilenced", isSilenced);
        // 获取当前的告警阶段
        String countKey = "alarm_count:" + elderId;
        Object countObj = redisTemplate.opsForValue().get(countKey);
        int stage = 0;
        if (countObj != null) {
            stage = Integer.parseInt(countObj.toString());
        }
        status.put("stage", stage);
        // 是否处于活跃告警状态
        // 如果 stage > 0 且没有被静默，则认为前端需要恢复告警 UI
        status.put("isAlarming", stage > 0 && !isSilenced);
        // 获取剩余静默时间（用于前端显示倒计时）
        if (isSilenced) {
            Long expire = redisTemplate.getExpire(silenceKey, TimeUnit.MINUTES);
            status.put("silenceMinutesLeft", expire);
        }

        return status;
    }

    // 处理安全告警
    public Map<String, Object> processSafetyAlarm(Integer userId) {
        // 检查是否在静默期
        String silenceKey = "silence_mode:" + userId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(silenceKey))) {
            return null;
        }
        // 定义redis告警计数key
        String countKey = "alarm_count:" + userId;
        // 定义告警时间key
        String lastTimeKey = "last_alarm_time:" + userId;
        // 获取上一次告警时间
        Long lastTime = (Long) redisTemplate.opsForValue().get(lastTimeKey);
        long currentTime = System.currentTimeMillis();
        // 如果距离上一次告警不足5分钟则直接拦截请求
        if (lastTime != null && (currentTime - lastTime) < 5 * 60 * 1000) {
            return null;
        }
        // 增加计数
        Long count = redisTemplate.opsForValue().increment(countKey);
        // 更新本次告警的时间，并设置过期时间
        redisTemplate.opsForValue().set(lastTimeKey, currentTime, 15, TimeUnit.MINUTES);
        // 逻辑判断
        Map<String, Object> wsData = new HashMap<>();
        wsData.put("stage", count.intValue());
        wsData.put("elderId", userId);
        wsData.put("elderName", userMapper.findById(userId).getRealName());
        if (count == 1) {
            // 发送告警邮件
            sendAlarmEmails(userId);
        } else if (count == 3) {
            // 触发紧急呼叫
            saveEmergencyCall(userId, "三次告警触发紧急呼叫");
        }
        pushAlarmToWebsocket(userId, wsData);

        return wsData;
    }

    // 发送告警邮件方法
    private void sendAlarmEmails(Integer userId) {
        User elder = userMapper.findById(userId);
        // 找到所有关注该老人的家属
        List<UserInfoDto> followers = userMapper.findFollowersInfoByElderId(userId);
        // 循环发送邮件给每个家属
        for (UserInfoDto follower : followers) {
            if (follower.getEmail() != null) {
                mailService.sendAlarmMail(follower.getEmail(), elder.getRealName());
            }
        }
    }

    // 推送ws告警消息
    private void pushAlarmToWebsocket(Integer userId, Map<String, Object> wsData) {
        WebSocketResult<Map<String, Object>> wsMessage = WebSocketResult.build("safety_alarm", wsData);
        // 推送给老人
        messageHandler.sendMessageToUser(userId, wsMessage);
        // 推送给家属
        List<UserInfoDto> followers = userMapper.findFollowersInfoByElderId(userId);
        for (UserInfoDto follower : followers) {
            messageHandler.sendMessageToUser(follower.getId(), wsMessage);
        }
    }

    // 保存紧急呼叫记录
    private void saveEmergencyCall(Integer userId, String remark) {
        EmergencyCall call = new EmergencyCall();
        call.setUserId(userId);
        call.setType(1);
        call.setRemark(remark);
        emergencyCallMapper.insertEmergencyCall(call);
    }

    // 通用的推送清理消息方法
    private void sendClearAlarmSocket(Integer elderId, String actionType, Integer minutes) {
        // 构造ws消息
        Map<String, Object> wsData = new HashMap<>();
        wsData.put("elderId", elderId);
        wsData.put("action", actionType); // "leave" 或 "hangup"
        wsData.put("minutes", minutes);   // 仅在leave时有效
        // 封装ws消息
        WebSocketResult<Map<String, Object>> wsMessage = WebSocketResult.build("clear_alarm", wsData);
        // 推送给老人
        messageHandler.sendMessageToUser(elderId, wsMessage);
        // 推送给家属
        List<UserInfoDto> followers = userMapper.findFollowersInfoByElderId(elderId);
        for (UserInfoDto follower : followers) {
            messageHandler.sendMessageToUser(follower.getId(), wsMessage);
        }
    }

    // 设置临时离开时间
    public void setTemporaryLeave(Integer elderId, Integer minutes) {
        // 清理当前的告警计数和时间记录
        redisTemplate.delete("alarm_count:" + elderId);
        redisTemplate.delete("last_alarm_time:" + elderId);
        // 设置静默期key，有效期为家属设置的时间
        String silenceKey = "silence_mode:" + elderId;
        redisTemplate.opsForValue().set(silenceKey, true, minutes, TimeUnit.MINUTES);

        sendClearAlarmSocket(elderId, "leave", minutes);
    }

    // 挂断紧急通话后重置告警状态
    public void clearAllAlarmStatus(Integer elderId) {
        // 删除redis里所有相关的key
        redisTemplate.delete("alarm_count:" + elderId);
        redisTemplate.delete("last_alarm_time:" + elderId);

        sendClearAlarmSocket(elderId, "hangup", 0);
    }

}
