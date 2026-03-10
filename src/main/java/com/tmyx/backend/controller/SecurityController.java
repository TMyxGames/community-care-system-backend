package com.tmyx.backend.controller;


import com.tmyx.backend.common.Result;
import com.tmyx.backend.dto.CallDto;
import com.tmyx.backend.dto.UserBindDto;
import com.tmyx.backend.dto.UserInfoDto;
import com.tmyx.backend.dto.WebSocketResult;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.handler.MessageHandler;
import com.tmyx.backend.mapper.EmergencyCallMapper;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.service.MailService;
import com.tmyx.backend.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/security")
@CrossOrigin
public class SecurityController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageHandler messageHandler;
    @Autowired
    private MailService mailService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    private EmergencyCallMapper emergencyCallMapper;

    // 获取告警状态
    @GetMapping("/alarm-status/{elderId}")
    public Result getAlarmStatus(@PathVariable Integer elderId) {
        Map<String, Object> status = securityService.getAlarmStatus(elderId);
        return Result.success(status);
    }

    // 发送安全告警邮件（离开安全区域）
    @PostMapping("/safety-alarm")
    public Result safetyAlarm(@RequestBody Map<String, Integer> params) {
        // 获取老人id
        Integer userId = params.get("userId");
        Map<String, Object> result = securityService.processSafetyAlarm(userId);

        if (result == null) {
            return Result.success("处于告警冷却期内，跳过处理");
        }
        return Result.success(result);
    }

    // 发送返回邮件（返回安全区域）
    @PostMapping("/back-to-safety")
    public Result backToSafety(@RequestBody Map<String, Integer> body) {
        // 获取老人id
        Integer elderId = body.get("userId");
        System.out.println("用户返回安全区域：" + elderId);
        // 删除redis告警计数key和告警时间key
        redisTemplate.delete("alarm_count:" + elderId);
        redisTemplate.delete("last_alarm_time:" + elderId);
        // 发送报返回邮件
        User elder = userMapper.findById(elderId);
        List<UserInfoDto> followers = userMapper.findFollowersInfoByElderId(elderId);
        // 循环发送邮件给每个家属
        for (UserInfoDto follower : followers) {
            if (follower.getEmail() != null) {
                mailService.sendBackToSafetyMail(follower.getEmail(), elder.getRealName());
            }
        }
        return Result.success("平安消息已发送，告警锁定已解除");
    }

    // 临时离开
    @PostMapping("/temporary-leave")
    public Result setTemporaryLeave(@RequestBody Map<String, Integer> params) {
        Integer elderId = params.get("elderId");
        Integer minutes = params.get("minutes");

        if (elderId == null || minutes == null) {
            return Result.error("参数缺失");
        }
        // 设置临时离开时间
        securityService.setTemporaryLeave(elderId, minutes);
        // 构造ws消息
        Map<String, Object> wsData = new HashMap<>();
        wsData.put("elderId", elderId);
        wsData.put("minutes", minutes);
        // 封装ws消息
        WebSocketResult<Map<String, Object>> wsMessage = WebSocketResult.build("clear_alarm", wsData);
        // 推送给老人
        messageHandler.sendMessageToUser(elderId, wsMessage);
        // 推送给家属
        List<UserInfoDto> followers = userMapper.findFollowersInfoByElderId(elderId);
        for (UserInfoDto follower : followers) {
            messageHandler.sendMessageToUser(follower.getId(), wsMessage);
        }
        messageHandler.sendMessageToUser(elderId, wsMessage);

        return Result.success("已设置临时离开模式");
    }

    // 清除告警状态（挂断紧急呼叫后）
    @PostMapping("/clear-alarm")
    public Result clearAlarm(@RequestBody Map<String, Integer> params) {
        Integer elderId = params.get("elderId");
        if (elderId == null) return Result.error("缺少老人ID");

        securityService.clearAllAlarmStatus(elderId);
        return Result.success("告警状态已重置");
    }

    // 获取紧急呼叫记录
    @GetMapping("/call/all")
    public Result getAllCalls(@RequestParam Integer role, @RequestAttribute Integer userId) {
        if (role == 3) {
            List<CallDto> calls = emergencyCallMapper.findEmergencyCallsByElderId(userId);
            return Result.success(calls);
        } else {
            List<CallDto> calls = emergencyCallMapper.findEmergencyCallsByUserId(userId);
            return Result.success(calls);
        }
    }
}
