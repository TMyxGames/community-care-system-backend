package com.tmyx.backend.service;

import com.tmyx.backend.entity.Message;
import com.tmyx.backend.entity.Session;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.entity.UserBindDto;
import com.tmyx.backend.handler.MessageHandler;
import com.tmyx.backend.mapper.MessageMapper;
import com.tmyx.backend.mapper.SessionMapper;
import com.tmyx.backend.mapper.SystemNoticeMapper;
import com.tmyx.backend.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SessionMapper sessionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private SystemNoticeMapper systemNoticeMapper;
    @Autowired
    private MessageHandler messageHandler;

    // 获取会话详情
    public List<?> getMessagesBySession(Integer sessionId, Integer userId) {
        Session session = sessionMapper.findById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("会话不存在或无权访问");
        }

        if (session.getType() == 0) {
            return systemNoticeMapper.findAllOrderByTime();
        } else {
            return messageMapper.findBySessionId(sessionId, userId);
        }
    }

    // 发送绑定请求
    public void sendBindingRequest(Integer fromId, Integer toId) {
        if (fromId.equals(toId)) {
            throw new RuntimeException("不能绑定自己");
        }
        // 获取发送者的绑定请求会话
        Session fromSession = sessionMapper.findByUserAndType(fromId, 1);
        // 获取接收者的绑定请求会话
        Session toSession = sessionMapper.findByUserAndType(toId, 1);
        // 构造消息并关联会话
        Message msg = new Message();
        msg.setFromSessionId(fromSession.getId()); // 发送者会话id
        msg.setToSessionId(toSession.getId()); // 接收者会话id
        msg.setFromId(fromId); // 发送者id
        msg.setToId(toId); // 接收者id
        msg.setType(1); // 1: 绑定请求
        msg.setStatus(0); // 0: 未处理
        msg.setSendTime(new Date());
        // 插入消息到数据库
        messageMapper.insert(msg);
        System.out.println("插入后的消息ID: " + msg.getId());
        // 获取发送者的信息
        User senderInfo = userMapper.findById(fromId);
        msg.setOtherUser(senderInfo);
        // 给接收者实时提醒
        messageHandler.sendMessageToUser(toId, msg);
    }

    // 处理绑定请求
    @Transactional
    public void handleBindRequest(Integer messageId, Integer status, Integer currentUserId) {
        // 查询消息并校验
        Message msg = messageMapper.findById(messageId);
        System.out.println(msg);
        if (msg == null || !msg.getToId().equals(currentUserId)) {
            throw new RuntimeException("消息不存在或无权访问");
        }
        if (msg.getStatus() != 0) {
            throw new RuntimeException("该请求已处理");
        }
        // 更新消息状态（0: 未处理, 1: 同意, 2: 拒绝)
        messageMapper.updateStatus(messageId, status);
        msg.setStatus(status);
        // 如果同意，则调用UserMapper进行绑定
        if (status == 1) {
            if (userMapper.countBinding(msg.getFromId(), msg.getToId()) == 0) {
                userMapper.insertBinding(msg.getFromId(), msg.getToId(), "");
                userMapper.insertBinding(msg.getToId(), msg.getFromId(), "");
            }
        }
        // 获取双方的身份信息
        User userA = userMapper.findById(msg.getFromId()); // 发送者
        User userB = userMapper.findById(msg.getToId()); // 接收者
        // 实时推送消息给发送者
        Message toA = cloneMessage(msg);
        toA.setOtherUser(userB);
        messageHandler.sendMessageToUser(msg.getFromId(), toA);
        // 实时推送消息给接受者
        Message toB = cloneMessage(msg);
        toB.setOtherUser(userA);
        messageHandler.sendMessageToUser(msg.getToId(), toB);
    }

    // 拷贝消息（给处理绑定请求方法使用）
    private Message cloneMessage(Message origin) {
        Message copy = new Message();
        BeanUtils.copyProperties(origin, copy);
        return copy;
    }

    // 处理解绑
    @Transactional
    public void handleUnbind(Integer currentUserId, Integer targetId) {
        // 获取双方的绑定请求会话id（type=1 绑定请求）
        Session fromSession = sessionMapper.findByUserAndType(currentUserId, 1);
        Session toSession = sessionMapper.findByUserAndType(targetId, 1);
        // 验证会话是否存在
        if (fromSession == null || toSession == null) {
            throw new RuntimeException("会话异常");
        }
        // 删除binding表中的双向记录
        userMapper.deleteBinding(currentUserId, targetId);
        userMapper.deleteBinding(targetId, currentUserId);
        // 插入一条解绑消息（status=3 解绑）
        Message unbindMsg = new Message();
        unbindMsg.setFromSessionId(fromSession.getId()); // 发送者会话id
        unbindMsg.setToSessionId(toSession.getId()); // 接收者会话id
        unbindMsg.setFromId(currentUserId); // 发送者id
        unbindMsg.setToId(targetId); // 接收者id
        unbindMsg.setType(1); // 1: 绑定请求
        unbindMsg.setStatus(3); // 3: 解绑
        unbindMsg.setSendTime(new Date());

        messageMapper.insert(unbindMsg);
        // 获取双方的身份信息
        User userA = userMapper.findById(unbindMsg.getFromId()); // 发送者
        User userB = userMapper.findById(unbindMsg.getToId()); // 接收者
        // 实时推送消息给发送者
        Message toA = cloneMessage(unbindMsg);
        toA.setOtherUser(userB);
        messageHandler.sendMessageToUser(unbindMsg.getFromId(), toA);
        // 实时推送消息给接受者
        Message toB = cloneMessage(unbindMsg);
        toB.setOtherUser(userA);
        messageHandler.sendMessageToUser(unbindMsg.getToId(), toB);
    }


}
