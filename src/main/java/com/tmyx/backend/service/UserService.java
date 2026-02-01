package com.tmyx.backend.service;

import com.tmyx.backend.entity.StaffConfigDto;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.mapper.ServiceAreaMapper;
import com.tmyx.backend.mapper.StaffWorkMapper;
import com.tmyx.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StaffWorkMapper staffWorkMapper;
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

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


}
