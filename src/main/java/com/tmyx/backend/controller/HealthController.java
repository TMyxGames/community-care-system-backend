package com.tmyx.backend.controller;

import com.tmyx.backend.entity.HealthData;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.mapper.HealthDataMapper;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/health")
@CrossOrigin
public class HealthController {
    @Autowired
    private HealthDataMapper healthDataMapper;
    @Autowired
    private UserMapper userMapper;

    // 上传健康数据
    @PostMapping("/upload")
    public String upload(@RequestBody HealthData data) {
        healthDataMapper.insert(data);
        return "数据上传成功";
    }

    // 获取健康数据
    @GetMapping("/get")
    public Result getList(@RequestParam("userId") Integer targetId, @RequestAttribute Integer userId) {
        User currentUser = userMapper.findById(userId);
        // 如果当前用户身份为老人
        if (currentUser.getRole() == 3) {
            // 如果当前用户id与查看的目标id不一致
            if (!userId.equals(targetId)) {
                return Result.error(403, "您无权查看他人的健康数据");
            }
        } else {
            // 如果当前用户身份为家属，则判断当前用户是否与目标用户有绑定关系
            int count = userMapper.countBinding(userId, targetId);
            if (count == 0) {
                return Result.error(403, "您无权查看他人的健康数据");
            }
        }

        return Result.success(healthDataMapper.findByUserId(targetId));
    }

}
