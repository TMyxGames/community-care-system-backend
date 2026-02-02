package com.tmyx.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmyx.backend.entity.StaffConfigDto;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.handler.LocationHandler;
import com.tmyx.backend.mapper.ServiceAreaMapper;
import com.tmyx.backend.mapper.StaffWorkMapper;
import com.tmyx.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StaffWorkMapper staffWorkMapper;
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisLocationService redisLocationService;

    // 根据关键词搜索用户（id、用户名、真实姓名)
    public List<User> searchUsers(String keyword, Integer currentUserId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return userMapper.searchUsers(keyword, currentUserId);
    }

    // 获取带有服务的人员列表
    public List<User> getStaffList() {
        List<User> staffList = userMapper.findAllStaff();

        for (User staff : staffList) {
            List<Integer> sIds = staffWorkMapper.findServiceIdsByStaffId(staff.getId());
            staff.setServiceIds(sIds);

            // 如果服务人员的服务区域id不为空
            if (staff.getServiceAreaId() != null) {
                // 将服务区域id传给serviceAreaMapper，根据服务区域id查询服务区域信息
                var area = serviceAreaMapper.findById(staff.getServiceAreaId());
                // 如果服务区域存在则获取它的名称
                if (area != null) {
                    staff.setAreaName(area.getAreaName());
                }
            } else {
                staff.setAreaName("暂无");
            }
        }
        return staffList;
    }

    //保存服务人员配置
    @Transactional
    public void updateStaffConfig(StaffConfigDto dto) {
        // 更新用户表的服务区域
        userMapper.updateServiceArea(dto.getUserId(), dto.getAreaId());
        // 更新关联服务（先全部删了再增加）
        staffWorkMapper.deleteByStaffId(dto.getUserId());
        if (dto.getServiceIds() != null && !dto.getServiceIds().isEmpty()) {
            staffWorkMapper.batchInsert(dto.getUserId(), dto.getServiceIds());
        }
    }

//    @Scheduled(fixedRate = 5000)
//    public void simulateMovement() {
//        // 1. 获取所有需要定位的人员（建议从数据库查出初始点）
//        List<User> staffList = userMapper.findAllStaff();
//
//        for (User staff : staffList) {
//            // 2. 计算模拟的新坐标（在原有坐标基础上随机偏移）
//            double currentLng = staff.getLng() == null ? 108.514819 : staff.getLng();
//            double currentLat = staff.getLat() == null ? 22.796636 : staff.getLat();
//
//            double newLng = currentLng + (Math.random() - 0.5) * 0.002;
//            double newLat = currentLat + (Math.random() - 0.5) * 0.002;
//
//            // 3. 写入 Redis
//            redisLocationService.updateLocation("staff", staff.getId(), newLng, newLat);
//
//            // 可选：如果想在 MySQL 里也实时看到变化，可以取消下面注释（不建议高频执行）
//            // userMapper.updateLocation(staff.getId(), newLng, newLat);
//        }
//    }

    // 发送服务人员位置信息
//    @Scheduled(fixedRate = 5000)
//    public void broadcastStaffLocations() {
//        Map<Integer, Map<String, Double>> locations = redisLocationService.getLocationsByType("staff");
//        broadcastGeneric("staff", locations); // 标识
//    }

    // 发送用户位置信息
//    @Scheduled(fixedRate = 5000)
//    public void broadcastUserLocations() {
//        Map<Integer, Map<String, Double>> locations = redisLocationService.getLocationsByType("user");
//        broadcastGeneric("user", locations); // 标识
//    }

    // 通用广播
//    private void broadcastGeneric(String type, Object data) {
//        Map<String, Object> message = new HashMap<>();
//        message.put("type", type);
//        message.put("data", data);
//        try {
//            LocationHandler.broadcastLocation(objectMapper.writeValueAsString(message));
//        } catch (Exception e) { e.printStackTrace(); }
//    }


}
