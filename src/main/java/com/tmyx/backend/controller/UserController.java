package com.tmyx.backend.controller;


import com.tmyx.backend.entity.User;
import com.tmyx.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserMapper userMapper;

    // 获取所有用户
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    // 获取所有服务人员
    @GetMapping("/staff/all")
    public List<User> getAllStaff() {
        return userMapper.findAllStaff();
    }

    // 更新服务人员工作区域
    @PutMapping("/staff/update/area")
    public String updateStaffArea(@RequestParam Integer userId, @RequestParam Integer areaId) {
        User staff = userMapper.findById(userId);
        if (staff == null) {
            return "用户不存在";
        }
        if (staff.getRole() != 2) {
            return "用户不是服务人员";
        }
        staff.setServiceAreaId(areaId);
        int result = userMapper.updateServiceArea(staff);
        if (result > 0) {
            return "更新成功";
        } else {
            return "更新失败";
        }
    }













}
