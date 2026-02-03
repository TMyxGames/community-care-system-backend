package com.tmyx.backend.controller;


import com.tmyx.backend.entity.Message;
import com.tmyx.backend.mapper.MessageMapper;
import com.tmyx.backend.service.MessageService;
import com.tmyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@CrossOrigin
public class MessageController {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MessageService messageService;

    // 获取系统消息列表（0：系统消息）
//    @GetMapping("/all/{type}")
//    public Result getMsgList(@PathVariable Integer type, @RequestAttribute Integer userId) {
//        List<Message> messages = messageService.getMsgByType(type, userId);
//        return Result.success(messages);
//    }

    // 获取当前用户的绑定请求列表（1：绑定请求）
    @GetMapping("/all/1")
    public List<Message> getBindMsgList() {
        return messageMapper.findAllBindMsg();
    }

    // 获取安全提醒列表（2：安全提醒）
    @GetMapping("/all/2")
    public List<Message> getSafeMsgList() {
        return messageMapper.findAllSafeMsg();
    }


    // 发送绑定请求
    @PostMapping("/send/bind")
    public Result sendBindRequest(@RequestParam Integer userId, @RequestParam Integer toId) {
        messageService.sendBindingRequest(userId, toId);
        return Result.success();
    }


}
