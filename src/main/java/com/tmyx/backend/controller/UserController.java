package com.tmyx.backend.controller;


import com.tmyx.backend.entity.StaffConfigDto;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tmyx.backend.util.Result;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    // 获取所有用户
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    // 根据关键词搜索用户（id、用户名、真实姓名)
    @GetMapping("/search")
    public Result search(@RequestParam String keyword, @RequestParam Integer userId) {
        List<User> users = userService.searchUsers(keyword, userId);
        return Result.success(users);
    }

    // 获取所有服务人员
    @GetMapping("/staff/all")
    public List<User> getAllStaff() {
        return userService.getStaffList();
    }

    // 更新服务人员配置（服务区域、服务项目）
    @PutMapping("/staff/config")
    public String updateStaffConfig(@RequestBody StaffConfigDto dto) {
        userService.updateStaffConfig(dto);
        return "更新成功";
    }











}
