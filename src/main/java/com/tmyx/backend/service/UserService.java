package com.tmyx.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmyx.backend.dto.StaffConfigDto;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.dto.UserBindDto;
import com.tmyx.backend.mapper.AreaMapper;
import com.tmyx.backend.mapper.StaffWorkMapper;
import com.tmyx.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StaffWorkMapper staffWorkMapper;
    @Autowired
    private AreaMapper areaMapper;
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

    // 查询绑定请求发送者的信息（用于接收方会话渲染）
    public UserBindDto getUserBindDto(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return null;
        }

        UserBindDto dto = new UserBindDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setAvatarUrl(user.getAvatarUrl());

        return dto;
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
                var area = areaMapper.findServiceAreaById(staff.getServiceAreaId());
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



}
