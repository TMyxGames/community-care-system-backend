package com.tmyx.backend.controller;


import com.tmyx.backend.dto.UserInfoDto;
import com.tmyx.backend.entity.Message;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.mapper.MessageMapper;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.service.MailService;
import com.tmyx.backend.service.MessageService;
import com.tmyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/message")
@CrossOrigin
public class MessageController {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    // 根据会话获取消息列表
    @GetMapping("/list/{sessionId}")
    public Result getMessagesBySession(@PathVariable Integer sessionId, @RequestAttribute Integer userId) {
        List<?> list = messageService.getMessagesBySession(sessionId, userId);
        return Result.success(list);
    }

    // 发送绑定请求
    @PostMapping("/bind/send")
    public Result sendBindRequest(@RequestAttribute Integer userId, @RequestParam Integer toId, @RequestParam Integer relation) {
        User currentUser = userMapper.findById(userId); // 发送者
        User targetUser = userMapper.findById(toId); // 接收者
        // 不能绑定自己
        if (userId.equals(toId)) {
            return Result.error(400, "您不能与自己绑定");
        }
        // 如果发送者身份为老人
        if (currentUser.getRole() == 3) {
            return Result.error(403, "您无权发送绑定请求");
        }
        // 如果接收者身份不为老人
        if (targetUser.getRole() != 3) {
            return Result.error(400, "您只能向老人发送绑定请求");
        }
        // 如果用户已经绑定过了
        if (userMapper.countBinding(userId, toId) > 0) {
            return Result.error(400, "您已经和该用户绑定过了");
        }
        messageService.sendBindingRequest(userId, toId, relation);

        return Result.success();
    }

    // 处理绑定请求
    @PutMapping("/bind/handle")
    public Result handleBind(@RequestBody Map<String, Object> body, @RequestAttribute Integer userId) {
        Integer messageId = (Integer) body.get("messageId");
        Integer status = (Integer) body.get("status");
        messageService.handleBindRequest(messageId, status, userId);
        return Result.success();
    }

    // 处理解绑
    @PostMapping("/unbind")
    public Result unbind(@RequestBody Map<String, Integer> params, @RequestAttribute Integer userId) {
        Integer targetId = params.get("targetId");
        if (targetId == null) {
            return Result.error("参数错误：缺少目标用户id");
        }

        messageService.handleUnbind(userId, targetId);
        return Result.success();
    }

    // 发送安全告警邮件（离开安全区域）
    @PostMapping("/safety-alarm")
    public Result safetyAlarm(@RequestBody Map<String, Integer> params) {
        // 获取老人id
        Integer userId = params.get("userId");
        // 定义redis冷却时间key
        String lockKey = "alarm_lock:" + userId;
        // 尝试写入锁
        Boolean isLocked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "locked", 5, TimeUnit.MINUTES);
        // 如果获取锁失败，则冷却未到
        if (Boolean.FALSE.equals(isLocked)) {
            return Result.error("该用户处于告警冷却期，暂不发送告警邮件");
        }

        // 获取老人的信息
        User elder = userMapper.findById(userId);
        // 找到所有关注该老人的家属
        List<UserInfoDto> followers = userMapper.findFollowersInfoByElderId(userId);
        // 循环发送邮件给每个家属
        for (UserInfoDto follower : followers) {
            if (follower.getEmail() != null) {
                try {
                    mailService.sendAlarmMail(follower.getEmail(), elder.getRealName());
                } catch (Exception e) {
                    // 记录错误但不阻塞其他人的邮件发送
                    System.err.println("发送给 " + follower.getEmail() + " 的告警邮件失败");
                }
            }
        }

        return Result.success("告警通知已下发");
    }

    // 发送返回邮件（返回安全区域）
    @PostMapping("/back-to-safety")
    public Result backToSafety(@RequestBody Map<String, Integer> body) {
        // 获取老人id
        Integer elderId = body.get("userId");
        // 删除redis冷却锁
        String lockKey = "alarm_lock:" + elderId;
        redisTemplate.delete(lockKey);
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

}
