package com.tmyx.backend.config;

import com.tmyx.backend.dto.LocationDto;
import com.tmyx.backend.entity.Location;
import com.tmyx.backend.entity.SafeArea;
import com.tmyx.backend.entity.ServiceArea;
import com.tmyx.backend.mapper.AreaMapper;
import com.tmyx.backend.mapper.LocationMapper;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.service.RedisLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private RedisLocationService redisLocationService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    // 启动项目时将所有服务人员位置预热到redis
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("========== [数据预热] 启动位置信息初始化 ==========");

        try {
            // 从数据库查询所有服务人员的位置(role=2)
            List<LocationDto> staffLocations = locationMapper.findAllStaffLocations();
            preheatLocations("staff", staffLocations);

//            List<LocationDto> userLocations = locationMapper.findAllUserLocations();
//            preheatLocations("user", userLocations);
//            LocationDto userLocation = locationMapper.findLocationByUserId(37);
//            preheatLocation("user", userLocation);

            preheatSafeAreas();
        } catch (Exception e) {
            System.err.println(">>> 数据预热失败，请检查数据库连接或 Redis 状态: " + e.getMessage());
        }

    }

    private void preheatLocations(String type, List<LocationDto> locations) {
        if (locations != null && !locations.isEmpty()) {
            for (LocationDto loc : locations) {
                // 调用RedisLocationService
                redisLocationService.updateLocation(
                        type,
                        loc.getUserId(),
                        loc
                );
            }
            System.out.println(">>> 成功预热 " + type + " 位置数据，共计：" + locations.size() + " 条");
        }
    }

    private void preheatLocation(String type, LocationDto location) {
        // 调用RedisLocationService
        redisLocationService.updateLocation(
                type,
                location.getUserId(),
                location
        );
        System.out.println(">>> 成功预热 " + type + " 位置数据，共计：" + 1 + " 条");
    }

    private void preheatSafeAreas() {
        // 获取系统中所有老人id
        List<Integer> allElderIds = userMapper.findAllElderIds();

        for (Integer elderId : allElderIds) {
            // 根据老人id获取相关的所有安全区域
            List<SafeArea> areas = areaMapper.findSafeAreaByElderId(elderId);
            for (SafeArea area : areas) {
                // 将areaId加入到该老人的Set中
                redisTemplate.opsForSet().add("elder:areas:" + elderId, area.getId().toString());
                // 同时确保围栏池里有这个数据
                redisTemplate.opsForHash().put("area:cache:safe", area.getId().toString(), area.getRegion());
            }
        }
        System.out.println(">>> 成功预热围栏数据");
    }



}
