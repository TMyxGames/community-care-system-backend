package com.tmyx.backend.controller;

import com.tmyx.backend.entity.HealthData;
import com.tmyx.backend.mapper.HealthDataMapper;
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

    // 上传健康数据
    @PostMapping("/upload")
    public String upload(@RequestBody HealthData data) {
        healthDataMapper.insert(data);
        return "数据上传成功";
    }

    // 获取健康数据
    @GetMapping("/getData")
    public List<HealthData> getList(@RequestParam("userId") Integer userId) {
        return healthDataMapper.findByUserId(userId);
    }

}
