package com.tmyx.backend.service;

import com.tmyx.backend.entity.Session;
import com.tmyx.backend.mapper.SessionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SessionService {
    @Autowired
    private SessionMapper sessionMapper;

    // 获取用户会话列表
    public List<Session> getUserSessions(Integer userId) {
        List<Session> list = sessionMapper.findByUserId(userId);
        // 如果用户没有会话，则初始化默认会话
        if (list == null || list.isEmpty()) {
            initDefaultSessions(userId);
            list = sessionMapper.findByUserId(userId);
        }
        return list;
    }

    // 初始化默认会话
    private void initDefaultSessions(Integer userId) {
        int[] types = {0, 1, 2};
        String[] names = {"系统通知", "绑定请求", "安全提醒"};

        for (int i = 0; i < types.length; i++){
            Session session = new Session();
            session.setUserId(userId);
            session.setName(names[i]);
            session.setType(types[i]);
            session.setLastMsg("暂无消息");
            session.setUpdateTime(new Date());
            sessionMapper.insert(session);
        }

    }


}
