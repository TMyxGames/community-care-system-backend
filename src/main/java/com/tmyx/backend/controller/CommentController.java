package com.tmyx.backend.controller;


import com.tmyx.backend.entity.Comment;
import com.tmyx.backend.mapper.CommentMapper;
import com.tmyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@CrossOrigin
public class CommentController {
    @Autowired
    private CommentMapper commentMapper;

    // 添加评论
    @PostMapping("/add")
    public Result addComment(@RequestAttribute Integer userId, @RequestBody Comment comment) {
        comment.setUserId(userId);
        commentMapper.insert(comment);
        return Result.success(comment);
    }

    // 获取服务评论
    @GetMapping("/all/{id}")
    public Result getComment(@RequestParam Integer serviceId) {
        List<Comment> comments = commentMapper.findByServiceId(serviceId);
        return Result.success(comments);
    }
}
