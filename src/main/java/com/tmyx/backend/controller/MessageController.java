package com.tmyx.backend.controller;


import com.tmyx.backend.entity.Message;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.mapper.MessageMapper;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.service.MessageService;
import com.tmyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
@CrossOrigin
public class MessageController {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserMapper userMapper;

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

}
