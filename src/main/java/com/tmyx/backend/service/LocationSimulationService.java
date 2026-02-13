package com.tmyx.backend.service;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.tmyx.backend.dto.LocationDto;
import com.tmyx.backend.entity.Location;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.handler.LocationHandler;
import com.tmyx.backend.mapper.LocationMapper;
import com.tmyx.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LocationSimulationService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private RedisLocationService redisLocationService;

    // 查询所有服务人员位置
    public List<Location> getLocation() {
        List<User> staffList = userMapper.findAllStaff();
        if (staffList.isEmpty()) return new ArrayList<>();

        List<Integer> ids = staffList.stream()
                                     .map(User::getId)
                                     .collect(Collectors.toList());

        return locationMapper.findLocationsByUserIds(ids);
    }

//  @Scheduled(fixedRate = 5000)
    public void simulateMovement() {
        // 检查开关状态
        String status = redisTemplate.opsForValue().get("system:simulation:status");
        if (!"on".equals(status)) return;

        List<LocationDto> staffLocations = locationMapper.findAllStaffLocations();

        for (LocationDto loc : staffLocations) {
            // 计算模拟的新坐标（在原有坐标基础上随机偏移）
            double currentLng = loc.getLng() == null ? 108.514819 : loc.getLng();
            double currentLat = loc.getLat() == null ? 22.796636 : loc.getLat();
            // 随机偏移
            double newLng = currentLng + (Math.random() - 0.5) * 0.0005;
            double newLat = currentLat + (Math.random() - 0.5) * 0.0005;
            // 写入 Redis
            redisLocationService.updateLocation("staff", loc.getUserId(), loc);

            // 可选：如果想在 MySQL 里也实时看到变化，可以取消下面注释（不建议高频执行）
//            locationMapper.updateLocation(loc.getUserId(), newLng, newLat, new java.util.Date());
        }
    }

    // 每 5 秒执行一次推送服务人员位置到redis
    @Scheduled(fixedRate = 5000)
    public void pushStaffLocations() {
        // 从redis获取所有服务人员的实时位置
        String key = "location:cache:staff";
        Map<Object, Object> rawData = redisTemplate.opsForHash().entries(key);
        Map<String, LocationDto> formattedData = new HashMap<>();

        rawData.forEach((k, v) -> {
            LocationDto dto = JSON.parseObject(v.toString(), LocationDto.class);
            formattedData.put(k.toString(), dto);
        });

        if (!formattedData.isEmpty()) {
            // 封装成统一的消息格式
            Map<String, Object> message = new HashMap<>();
            message.put("type", "staff_update");
            message.put("data", formattedData);

            // 序列化并调用 Handler 进行广播
            String jsonString = JSON.toJSONString(message, SerializerFeature.WriteNonStringKeyAsString);
            LocationHandler.broadcastLocation(jsonString);

            // System.out.println(">>> 已推送最新位置数据至前端");
        }
    }

    @Scheduled(fixedRate = 5000)
    public void pushUserLocations() {
        // 从redis获取所有用户的实时位置
        String key = "location:cache:user";
        Map<Object, Object> rawData = redisTemplate.opsForHash().entries(key);
        Map<String, LocationDto> formattedData = new HashMap<>();

        rawData.forEach((k, v) -> {
            LocationDto dto = JSON.parseObject(v.toString(), LocationDto.class);
            formattedData.put(k.toString(), dto);
        });

        if (!formattedData.isEmpty()) {
            // 封装成统一的消息格式
            Map<String, Object> message = new HashMap<>();
            message.put("type", "user_update");
            message.put("data", formattedData);

            // 序列化并调用 Handler 进行广播
            String jsonString = JSON.toJSONString(message, SerializerFeature.WriteNonStringKeyAsString);
            LocationHandler.broadcastLocation(jsonString);

            // System.out.println(">>> 已推送最新位置数据至前端");
        }
    }


}
