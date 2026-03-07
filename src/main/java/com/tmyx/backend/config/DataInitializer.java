package com.tmyx.backend.config;

import com.tmyx.backend.dto.LocationDto;
import com.tmyx.backend.entity.Location;
import com.tmyx.backend.entity.SafeArea;
import com.tmyx.backend.entity.ServiceArea;
import com.tmyx.backend.mapper.AreaMapper;
import com.tmyx.backend.mapper.LocationMapper;
import com.tmyx.backend.service.RedisLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private RedisLocationService redisLocationService;

    // 启动项目时将所有服务人员位置预热到redis
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("========== [数据预热] 启动位置信息初始化 ==========");

        try {
            // 从数据库查询所有服务人员的位置(role=2)
            List<LocationDto> staffLocations = locationMapper.findAllStaffLocations();
            preheatLocations("staff", staffLocations);

            List<LocationDto> userLocations = locationMapper.findAllUserLocations();
            preheatLocations("user", userLocations);

            List<SafeArea> safeAreas = areaMapper.findAllSafeArea();
            preheatSafeAreas("safe", safeAreas);
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

    private void preheatSafeAreas(String type, List<SafeArea> areas) {
        if (areas != null && !areas.isEmpty()) {
            for (SafeArea area : areas) {
                redisLocationService.updateArea(
                        type,
                        area.getId(),
                        area.getRegion());
            }
            System.out.println(">>> 成功预热围栏数据，共计：" + areas.size() + " 条");
        }
    }



}
