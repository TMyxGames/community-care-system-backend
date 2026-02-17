package com.tmyx.backend.controller;


import com.tmyx.backend.dto.LocationDto;
import com.tmyx.backend.entity.Location;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.mapper.LocationMapper;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.service.RedisLocationService;
import com.tmyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/location")
@CrossOrigin
public class LocationController {
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisLocationService redisLocationService;

    // 手动控制模拟移动
    @PostMapping("/move")
    public Result manualMove(@RequestParam Integer userId, @RequestParam String type, @RequestParam String direction) {
        // 从数据库中获取位置信息
        LocationDto dto = redisLocationService.getLocationDto(type, userId);

        if (dto == null) {
            Location loc = locationMapper.findByUserId(userId);
            if (loc == null) return Result.error("用户不存在");

            dto = new LocationDto();
            dto.setUserId(userId);
            dto.setLng(loc.getLng());
            dto.setLat(loc.getLat());
            // 补全姓名和头像
            User user = userMapper.findById(userId);
            dto.setAvatarUrl(user.getAvatarUrl());
        }

        double step = 0.00001;
        // 根据方向更新位置信息
        switch (direction) {
            case "up":
                dto.setLat(dto.getLat() + step);
                break;
            case "down":
                dto.setLat(dto.getLat() - step);
                break;
            case "left":
                dto.setLng(dto.getLng() - step);
                break;
            case "right":
                dto.setLng(dto.getLng() + step);
                break;
        }
        // 写入redis和数据库
        redisLocationService.updateLocation(type, userId, dto);
//        String json = JSON.toJSONString(new LocationMessage("staff_update", Collections.singletonMap(userId, dto)));
//        LocationHandler.broadcastLocation(json);
//        locationMapper.updateLocation(userId, dto.getLng(), dto.getLat(), new java.util.Date());

        return Result.success();
    }

    // 获取单个服务人员位置
    @GetMapping("/staff/{id}")
    public Result getStaffLocation(@PathVariable Integer id) {
        // 从redis中获取位置信息
        Map<String, Double> redisLoc = redisLocationService.getLocationsByType("staff").get(id);
        if (redisLoc != null) {
            return Result.success(redisLoc);
        }
        // redis中没有，从数据库中获取
        Location loc = locationMapper.findByUserId(id);
        return Result.success(loc);
    }


}
