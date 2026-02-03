package com.tmyx.backend.service;

import com.tmyx.backend.entity.Message;
import com.tmyx.backend.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    // 根据类型获取消息
//    public Message[] getMsgByType(Integer type) {
//
//    }

    // 发送绑定请求
    public void sendBindingRequest(Integer fromId, Integer toId) {
        if (fromId.equals(toId)) {
            throw new RuntimeException("不能绑定自己");
        }

        Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setType(1); // 1: 绑定请求
        message.setStatus(0); // 0: 未处理
        message.setSendTime(new Date());

        messageMapper.insert(message);
    }


}
