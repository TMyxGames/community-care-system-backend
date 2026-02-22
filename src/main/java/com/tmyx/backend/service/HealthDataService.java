package com.tmyx.backend.service;

import com.tmyx.backend.common.exception.CustomException;
import com.tmyx.backend.dto.BloodPressureDto;
import com.tmyx.backend.dto.BloodSugarDto;
import com.tmyx.backend.dto.BmiDto;
import com.tmyx.backend.entity.HealthDataBMI;
import com.tmyx.backend.entity.HealthDataBP;
import com.tmyx.backend.entity.HealthDataBS;
import com.tmyx.backend.entity.User;
import com.tmyx.backend.mapper.UserMapper;
import com.tmyx.backend.vo.UserHealthVo;
import com.tmyx.backend.mapper.HealthDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class HealthDataService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HealthDataMapper healthDataMapper;

    // 判断用户权限
    public void checkHealthDataPermission(Integer userId, Integer targetId) {
        // 获取当前用户信息
        User currentUser = userMapper.findById(userId);
        // 如果当前用户不存在
        if (currentUser == null) {
            throw new CustomException(401, "用户未登录");
        }
        // 如果当前用户身份为老人
        if (currentUser.getRole() == 3) {
            // 如果当前用户id与查看的目标id不一致
            if (!userId.equals(targetId)) {
                throw new CustomException(403, "您无权查看他人的健康数据");
            }
        } else {
            // 如果当前用户身份为家属，则判断当前用户是否与目标用户有绑定关系
            int count = userMapper.countBinding(userId, targetId);
            if (count == 0) {
                throw new CustomException(403, "您无权查看该用户的健康数据");
            }
        }
    }

    // 计算年龄
    private Integer calculateAge(Date birthday) {
        if (birthday == null) return null;
        Calendar now = Calendar.getInstance();
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthday);
        return now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
    }

    // 获取用户所有健康数据
    public UserHealthVo getAllHealthData(Integer userId) {
        UserHealthVo summary = new UserHealthVo();
        // 获取用户信息并计算年龄
        User user = userMapper.findById(userId);
        if (user != null) {
            summary.setUserId(userId);
            summary.setUsername(user.getUsername());
            summary.setRealName(user.getRealName());
            summary.setSex(user.getSex());
            summary.setAge(calculateAge(user.getBirthday()));
            summary.setAvatarUrl(user.getAvatarUrl());
        }
        // 分别获取三张表中的最新记录
        List<BmiDto> bmiList = healthDataMapper.findBMILatestData(userId, 1);
        List<BloodPressureDto> bpList = healthDataMapper.findBPLatestData(userId, 1);
        List<BloodSugarDto> bsList = healthDataMapper.findBSLatestData(userId);
        // 将数据组装至vo
        if (!bmiList.isEmpty()) summary.setLatestBmi(bmiList.get(0));
        if (!bpList.isEmpty()) summary.setLatestBp(bpList.get(0));
        for (BloodSugarDto bs : bsList) {
            if (bs.getMealStatus() == 0) {
                summary.setLatestFastingBs(bs);
            } else if (bs.getMealStatus() == 1) {
                summary.setLatestPostprandialBs(bs);
            }
        }

        return summary;
    }
}
