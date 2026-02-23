package com.tmyx.backend.controller;

import com.tmyx.backend.dto.BloodPressureDto;
import com.tmyx.backend.dto.BloodSugarDto;
import com.tmyx.backend.dto.BmiDto;
import com.tmyx.backend.entity.HealthDataBMI;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.mapper.HealthDataMapper;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.service.HealthAiService;
import com.tmyx.backend.service.HealthDataService;
import com.tmyx.backend.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/health")
@CrossOrigin
public class HealthController {
    @Autowired
    private HealthDataMapper healthDataMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HealthDataService healthDataService;
    @Autowired
    private HealthAiService healthAiService;

    // 上传健康数据
    @PostMapping("/upload")
    public String upload(@RequestBody HealthDataBMI data) {
        healthDataMapper.insert(data);
        return "数据上传成功";
    }

    // 获取用户最新健康数据
    @GetMapping("/get/summary")
    public Result getList(@RequestParam("userId") Integer targetId,
                          @RequestAttribute Integer userId) {
        // 获取当前用户信息
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

        return Result.success(healthDataService.getAllHealthData(targetId));
    }

    // 获取用户七天内bmi数据
    @GetMapping("/get/bmi/trend")
    public Result getBmiList(@RequestParam("userId") Integer targetId,
                             @RequestAttribute Integer userId) {
        // 判断用户权限
        healthDataService.checkHealthDataPermission(userId, targetId);
        List<BmiDto> trend = healthDataMapper.findBMISevenDays(targetId);
        return Result.success(trend);
    }

    // 获取用户七天内血压数据
    @GetMapping("/get/bp/trend")
    public Result getBpList(@RequestParam("userId") Integer targetId,
                             @RequestAttribute Integer userId) {
        // 判断用户权限
        healthDataService.checkHealthDataPermission(userId, targetId);
        List<BloodPressureDto> trend = healthDataMapper.findBPSevenDays(targetId);
        return Result.success(trend);
    }

    // 获取用户七天内血糖数据
    @GetMapping("/get/bs/trend")
    public Result getBsList(@RequestParam("userId") Integer targetId,
                             @RequestAttribute Integer userId) {
        // 判断用户权限
        healthDataService.checkHealthDataPermission(userId, targetId);
        List<BloodSugarDto> trend = healthDataMapper.findBSSevenDays(targetId);
        return Result.success(trend);
    }

    @GetMapping("/ai/advice")
    public Result getAiAdvice(@RequestParam("userId") Integer targetId,
                              @RequestAttribute Integer userId) {
        // 判断用户权限
        healthDataService.checkHealthDataPermission(userId, targetId);

        try {
            // 2. 构造 Prompt
            String prompt = healthDataService.generateAiPrompt(targetId);

            // 3. 调用 AI 服务
            String advice = healthAiService.getHealthAdvice(prompt);

            return Result.success(advice);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "AI 健康助手暂时掉线了，请稍后再试");
        }
    }
}
