package com.tmyx.backend.controller;

import com.tmyx.backend.dto.BloodPressureDto;
import com.tmyx.backend.dto.BloodSugarDto;
import com.tmyx.backend.dto.BmiDto;
import com.tmyx.backend.dto.HealthDataDto;
import com.tmyx.backend.entity.HealthDataBMI;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.mapper.HealthDataMapper;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.service.HealthAiService;
import com.tmyx.backend.service.HealthDataService;
import com.tmyx.backend.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    @PostMapping("/add")
    public Result addHealthData(@RequestBody HealthDataDto request) {
        if (request.getUserId() == null || request.getType() == null) {
            return Result.error("参数不全");
        }
        // 默认记录时间
        if (request.getRecordDate() == null) {
            request.setRecordDate(LocalDateTime.now());
        }
        try {
            switch (request.getType()) {
                case "blood_pressure":
                    // 组装并调用血压插入
                    BloodPressureDto bpDto = new BloodPressureDto();
                    bpDto.setUserId(request.getUserId());
                    bpDto.setSystolic(request.getSystolic());
                    bpDto.setDiastolic(request.getDiastolic());
                    bpDto.setHeartRate(request.getHeartRate());
                    bpDto.setRecordDate(request.getRecordDate());
                    healthDataMapper.insertBP(bpDto);
                    break;

                case "blood_sugar":
                    // 组装并调用血糖插入
                    BloodSugarDto bsDto = new BloodSugarDto();
                    bsDto.setUserId(request.getUserId());
                    bsDto.setBloodSugar(request.getBloodSugar());
                    bsDto.setMealStatus(request.getMealStatus());
                    bsDto.setRecordDate(request.getRecordDate());
                    healthDataMapper.insertBS(bsDto);
                    break;

                case "bmi":
                    // 组装并调用bmi插入
                    BmiDto bmiDto = new BmiDto();
                    bmiDto.setUserId(request.getUserId());
                    bmiDto.setHeight(request.getHeight());
                    bmiDto.setWeight(request.getWeight());
                    bmiDto.setRecordDate(request.getRecordDate());
                    // 根据dto里的身高和体重计算bmi
                    if (request.getHeight() != null && request.getWeight() != null && request.getHeight() > 0) {
                        double heightInMeters = request.getHeight() / 100.0;
                        double bmiValue = request.getWeight() / (heightInMeters * heightInMeters);
                        // 保留两位小数
                        bmiValue = Math.round(bmiValue * 100.0) / 100.0;
                        bmiDto.setBmi(bmiValue);
                    } else {
                        return Result.error("身高或体重数据异常，无法计算");
                    }
                    healthDataMapper.insertBMI(bmiDto);
                    break;

                default:
                    return Result.error("未知的健康数据类型: " + request.getType());
            }
            return Result.success("记录成功", null);
        } catch (Exception e) {
            return Result.error("数据库写入失败: " + e.getMessage());
        }
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

    // 健康分析
    @GetMapping("/ai/advice")
    public Result getAiAdvice(@RequestParam("userId") Integer targetId,
                              @RequestAttribute Integer userId) {
        // 判断用户权限
        healthDataService.checkHealthDataPermission(userId, targetId);
        try {
            // 构造提示词
            String prompt = healthDataService.generateAiPrompt(targetId);
            // 调用服务
            String advice = healthAiService.getHealthAdvice(prompt);
            return Result.success(advice);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "AI暂时掉线了，请稍后再试");
        }
    }

}
