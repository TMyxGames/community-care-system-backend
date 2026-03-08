package com.tmyx.backend.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.tmyx.backend.dto.LocationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RedisLocationService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    // 基础前缀
    private static final String BASE_KEY = "location:cache:";

    private static final String ELDER_BIND_KEY = "elder:areas:";
    private static final String AREA_DATA_KEY = "area:cache:safe";

    // 缓存位置信息
    public void updateLocation(String type, Integer userId, LocationDto dto) {
        String key = BASE_KEY + type;
        String jsonValue = JSON.toJSONString(dto, SerializerFeature.WriteNonStringKeyAsString);
        redisTemplate.opsForHash().put(key, userId.toString(), jsonValue);
    }

    // 获取位置信息
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

    // 缓存区域信息
    public void updateSafeArea(Integer elderId, Integer areaId, String wkt) {
        // 存入安全区域数据
        redisTemplate.opsForHash().put(AREA_DATA_KEY, areaId.toString(), wkt.trim());
        // 存入老人与安全区域的绑定关系
        redisTemplate.opsForSet().add(ELDER_BIND_KEY + elderId, areaId.toString());
    }

    // 获取老人所有安全区域
    public List<String> getElderAllSafeAreaWkt(Integer elderId) {
        // 获取老人相关的所有安全区域id
        Set<String> areaIds = redisTemplate.opsForSet().members(ELDER_BIND_KEY + elderId);
        if (areaIds == null || areaIds.isEmpty()) return new ArrayList<>();

        // 批量获取安全区域wkt
        List<Object> wktObjects = redisTemplate.opsForHash().multiGet(AREA_DATA_KEY, new ArrayList<>(areaIds));

        return wktObjects.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
