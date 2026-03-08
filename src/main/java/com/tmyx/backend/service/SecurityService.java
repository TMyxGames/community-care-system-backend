package com.tmyx.backend.service;

import com.tmyx.backend.common.Result;
import com.tmyx.backend.dto.UserInfoDto;
import com.tmyx.backend.dto.WebSocketResult;
import com.tmyx.backend.entity.EmergencyCall;
import com.tmyx.backend.entity.SafeArea;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.handler.MessageHandler;
import com.tmyx.backend.mapper.AreaMapper;
import com.tmyx.backend.mapper.EmergencyCallMapper;
import com.tmyx.backend.mapper.UserMapper;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SecurityService {
    @Autowired
    private UserMapper userMapper;
    @Lazy
    @Autowired
    private MessageHandler messageHandler;
    @Autowired
    private MailService mailService;
    @Autowired
    private EmergencyCallMapper emergencyCallMapper;
    @Autowired
    private RedisLocationService redisLocationService;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    private AreaMapper areaMapper;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    // 安全总控方法
    public void monitorElderLocation(Integer userId, double lng, double lat) {
        List<String> wkts = redisLocationService.getElderAllSafeAreaWkt(userId);
        if (wkts.isEmpty()) return;
        // 检查老人位置是否位于某个关联的安全范围内
        boolean isSafe = false;
        for (String wkt : wkts) {
            if (checkInArea(lng, lat, wkt)) {
                isSafe = true;
                break;
            }
        }

        // 定义离开范围计次key、告警阶段key、上一次告警时间key、静默状态key
        String outCountKey = "out_count:" + userId;
        String alarmCountKey = "alarm_count:" + userId;
        String lastTimeKey = "last_alarm_time:" + userId;
        String silenceKey = "silence_mode:" + userId;

        // 判断安全
        if (isSafe) {
            // 无论是否处于静默状态，只要存在告警阶段且已回到安全区域，则发送平安邮件
            if (Boolean.TRUE.equals(redisTemplate.hasKey(alarmCountKey)) ||
                Boolean.TRUE.equals(redisTemplate.hasKey(silenceKey))) {
                System.out.println("老人[" + userId + "]已返回安全区域，清除告警状态");
                handleBackToSafety(userId);
            }
            // 在安全区域内则立即清除计次
            redisTemplate.delete(outCountKey);
            return;
        }

        // 检查是否处于静默状态
        if (Boolean.TRUE.equals(redisTemplate.hasKey(silenceKey))) {
            return;
        }

        // 检查是否处于冷却时间（距离上一次告警未达到5分钟）
        Long lastTime = (Long) redisTemplate.opsForValue().get(lastTimeKey);
        boolean isCoolDown = lastTime != null && (System.currentTimeMillis() - lastTime) < 5 * 60 * 1000;
        // 如果已处于告警状态或者静默状态，则不再累加计数
        if (isCoolDown) {
            return;
        }

        // 检查是否处于告警状态（是否存在告警阶段）
        if (Boolean.TRUE.equals(redisTemplate.hasKey(alarmCountKey))) {
            // 若处于告警状态，则直接触发安全告警，不再走下面的累加计数流程
            processSafetyAlarm(userId);
            return;
        }

        // 不在安全区域时累加计数
        Long currentCount = redisTemplate.opsForValue().increment(outCountKey);
        // 设置过期时间，如果30秒内没收到新位置，则中断计次，清除计次
        redisTemplate.expire(outCountKey, 30, TimeUnit.SECONDS);

        System.out.println("老人[" + userId + "]离开安全区域，当前计数：" + currentCount);
        // 当计次达到3次时，触发告警
        if (currentCount != null && currentCount == 6) {
            // 触发处理安全告警方法
            processSafetyAlarm(userId);
        }

    }


    // 检查位置是否在范围内
    public boolean checkInArea(double lng, double lat, String wktPolygon) {
        if (wktPolygon == null || wktPolygon.isEmpty()) return true;

        try {
            WKTReader reader = new WKTReader(geometryFactory);
            Polygon polygon = (Polygon) reader.read(wktPolygon);
            Point point = geometryFactory.createPoint(new Coordinate(lng, lat));

            return polygon.contains(point);
        } catch (Exception e) {
            return true; // 发生异常时默认不告警，防止误报
        }
    }

    // 处理安全告警
    public Map<String, Object> processSafetyAlarm(Integer userId) {
        // 更新上一次告警时间
        long currentTime = System.currentTimeMillis();
        String lastTimeKey = "last_alarm_time:" + userId;
        redisTemplate.opsForValue().set(lastTimeKey, currentTime, 15, TimeUnit.MINUTES);

        // 定义告警阶段计数key，增加告警阶段计数
        String alarmCountKey = "alarm_count:" + userId;
        Long count = redisTemplate.opsForValue().increment(alarmCountKey);

        // 构造ws消息
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

    // 处理返回安全区域
    public void handleBackToSafety(Integer userId) {
        // 清除所有相关状态（离开区域计次、告警阶段、冷却时间、静默时间）
        redisTemplate.delete("out_count:" + userId);
        redisTemplate.delete("alarm_count:" + userId);
        redisTemplate.delete("last_alarm_time:" + userId);
        redisTemplate.delete("silence_mode:" + userId);

        // 发送平安邮件
        sendSafetyEmails(userId);
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

    // 发送平安邮件方法
    private void sendSafetyEmails(Integer userId) {
        User elder = userMapper.findById(userId);
        // 找到所有关注该老人的家属
        List<UserInfoDto> followers = userMapper.findFollowersInfoByElderId(userId);
        // 循环发送邮件给每个家属
        for (UserInfoDto follower : followers) {
            if (follower.getEmail() != null) {
                mailService.sendBackToSafetyMail(follower.getEmail(), elder.getRealName());
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
        // 清除部分相关状态（离开区域计次、冷却时间），保留告警阶段计次
        redisTemplate.delete("out_count:" + elderId);
        redisTemplate.delete("last_alarm_time:" + elderId);
        // 将告警阶段计次设为1
        String alarmCountKey = "alarm_count:" + elderId;
        redisTemplate.opsForValue().set(alarmCountKey, "1");
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
