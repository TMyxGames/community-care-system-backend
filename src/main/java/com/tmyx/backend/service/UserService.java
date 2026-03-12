package com.tmyx.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmyx.backend.dto.StaffConfigDto;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.dto.UserBindDto;
import com.tmyx.backend.mapper.AreaMapper;
import com.tmyx.backend.mapper.StaffWorkMapper;
import com.tmyx.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Value("${file.upload-path}")
    private String baseUploadPath;

    @Autowired
    private ResourceLoader resourceLoader;

    // 给新用户设置默认头像
    public void setDefaultAvatar(Integer userId) {
        try {
            Resource resource = resourceLoader.getResource("classpath:static/images/兔兔.jpg");

            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    this.saveAvatar(is, "兔兔.jpg", userId);
                }
            } else {
                System.err.println("警告：默认头像文件不存在");
            }
        } catch (IOException e) {
            System.err.println("设置默认头像失败：" + e.getMessage());
        }
    }



    // 保存默认头像方法
    private void saveAvatar(InputStream is, String originalName, Integer userId) throws IOException {
        // 确定物理存储路径
        File baseDir = new File(baseUploadPath).getAbsoluteFile();
        File uploadDir = new File(baseDir, "user/avatars/");
        if (!uploadDir.exists()) uploadDir.mkdirs();

        // 生成文件名
        String fileName = UUID.randomUUID() + "_" + originalName;
        File dest = new File(uploadDir, fileName);

        // 拷贝到目标文件
        Files.copy(is, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // 更新数据库
        String relativePath = "/files/user/avatars/" + fileName;
        userMapper.updateAvatar(userId, relativePath);
        System.out.println("默认头像初始化成功: " + relativePath);
    }


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
