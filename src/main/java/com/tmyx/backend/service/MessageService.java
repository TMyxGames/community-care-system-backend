package com.tmyx.backend.service;

import com.tmyx.backend.entity.Message;
import com.tmyx.backend.entity.Session;
import com.tmyx.backend.mapper.MessageMapper;
import com.tmyx.backend.mapper.SessionMapper;
import com.tmyx.backend.mapper.SystemNoticeMapper;
import com.tmyx.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SessionMapper sessionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SystemNoticeMapper systemNoticeMapper;

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
        Message message = new Message();
        message.setFromSessionId(fromSession.getId()); // 发送者会话id
        message.setToSessionId(toSession.getId()); // 接收者会话id
        message.setFromId(fromId); // 发送者id
        message.setToId(toId); // 接收者id
        message.setType(1); // 1: 绑定请求
        message.setStatus(0); // 0: 未处理
        message.setSendTime(new Date());

        messageMapper.insert(message);
    }

    // 处理绑定请求
    @Transactional
    public void handleBindRequest(Integer messageId, Integer status, Integer currentUserId) {
        // 查询消息并校验
        Message msg = messageMapper.findById(messageId);
        if (msg == null || !msg.getToId().equals(currentUserId)) {
            throw new RuntimeException("消息不存在或无权访问");
        }
        if (msg.getStatus() != 0) {
            throw new RuntimeException("该请求已处理");
        }
        // 更新消息状态（0: 未处理, 1: 同意, 2: 拒绝)
        messageMapper.updateStatus(messageId, status);
        // 如果同意，则调用UserMapper进行绑定
        if (status == 1) {
            if (userMapper.countBinding(msg.getFromId(), msg.getToId()) == 0) {
                userMapper.insertBinding(msg.getFromId(), msg.getToId(), "");
                userMapper.insertBinding(msg.getToId(), msg.getFromId(), "");
            }
        }
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
    }


}
