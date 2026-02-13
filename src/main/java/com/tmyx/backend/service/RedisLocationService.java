package com.tmyx.backend.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.tmyx.backend.dto.LocationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RedisLocationService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    // 基础前缀
    private static final String BASE_KEY = "location:cache:";

    // 动态传入类型
    public void updateLocation(String type, Integer userId, LocationDto dto) {
        String key = BASE_KEY + type;
        String jsonValue = JSON.toJSONString(dto, SerializerFeature.WriteNonStringKeyAsString);
        redisTemplate.opsForHash().put(key, userId.toString(), jsonValue);
    }

    public LocationDto getLocationDto (String type, Integer userId) {
        String key = BASE_KEY + type;
        Object json = redisTemplate.opsForHash().get(key, userId.toString());
        if (json == null) return null;
        return JSON.parseObject(json.toString(), LocationDto.class);
    }

    // 获取指定类型的所有位置信息
    public Map<Integer, Map<String, Double>> getLocationsByType(String type) {
        String key = BASE_KEY + type;
        Map<Object, Object> rawData = redisTemplate.opsForHash().entries(key);
        Map<Integer, Map<String, Double>> result = new HashMap<>();

        rawData.forEach((idStr, coordStr) -> {
            try {
                String[] coords = ((String) coordStr).split(",");
                Map<String, Double> pos = new HashMap<>();
                pos.put("lng", Double.parseDouble(coords[0]));
                pos.put("lat", Double.parseDouble(coords[1]));
                result.put(Integer.parseInt((String) idStr), pos);
            } catch (Exception e) {
                // 容错处理
                System.out.println("Error parsing location data: " + e.getMessage());
            }
        });
        return result;
    }

    // 清除某个类别的位置信息
    public void clearType(String type) {
        redisTemplate.delete(BASE_KEY + type);
    }
}
