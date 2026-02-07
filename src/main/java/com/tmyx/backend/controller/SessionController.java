package com.tmyx.backend.controller;


import com.tmyx.backend.entity.Session;
import com.tmyx.backend.mapper.SessionMapper;
import com.tmyx.backend.service.SessionService;
import com.tmyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/session")
@CrossOrigin
public class SessionController {
    @Autowired
    private SessionMapper sessionMapper;
    @Autowired
    private SessionService sessionService;

    // 获取用户的会话
    @GetMapping("/all")
    public Result getSessionList(@RequestAttribute Integer userId) {
        // 从数据库查询该用户的会话
        List<Session> sessions = sessionService.getUserSessions(userId);

        return Result.success(sessions);
    }

    // 更新未读数
//    @PutMapping("/read/{sessionId}")
//    public Result clearUnread(@PathVariable Integer sessionId, @RequestAttribute Integer userId) {
//
//    }

}
