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

    // 构造提示词
    public String generateAiPrompt(Integer targetId) {
        UserHealthVo summary = this.getAllHealthData(targetId);

        StringBuilder sb = new StringBuilder();
        // 设定身份和背景
        sb.append("你是一位资深的养老健康管理专家。请根据以下老人的健康数据进行深度分析：\n\n");
        // 注入基本信息
        sb.append(String.format("【基本信息】性别：%s，年龄：%d岁\n",
                "1".equals(summary.getSex()) ? "男" : "女", summary.getAge()));
        // 注入bmi数据
        sb.append("【当前指标】\n");
        if (summary.getLatestBmi() != null) {
            sb.append(String.format("- BMI指数: %.1f (属于%s)\n",
                    summary.getLatestBmi().getBmi(),
                    getBmiStatus(summary.getLatestBmi().getBmi())));
        }
        // 注入血压数据
        if (summary.getLatestBp() != null) {
            sb.append(String.format("- 最新血压: %d/%d mmHg\n",
                    summary.getLatestBp().getSystolic(),
                    summary.getLatestBp().getDiastolic()));
        }
        // 注入血糖数据
        if (summary.getLatestFastingBs() != null) {
            sb.append(String.format("- 最新空腹血糖: %.1f mmol/L\n",
                    summary.getLatestFastingBs().getBloodSugar()));
        }
        if (summary.getLatestPostprandialBs() != null) {
            sb.append(String.format("- 最新餐后血糖: %.1f mmol/L\n",
                    summary.getLatestPostprandialBs().getBloodSugar()));
        }
        // 根据老年人生理特点进行预测
        sb.append("\n【任务要求】\n");
        sb.append("1. 分析现状：结合老年人标准（注意：老年人血压血糖标准较中青年稍宽），评价当前指标是否健康。\n");
        sb.append("2. 健康预测：若保持当前生活习惯，预测未来 1-3 个月可能存在的健康风险（如心血管、糖尿病风险等）。\n");
        sb.append("3. 个性化建议：给出针对性的饮食、运动和日常起居建议。\n");
        sb.append("\n要求：语气亲切尊重，多用‘您’，避免生硬的医学术语，字数 250 字左右。");

        return sb.toString();
    }

    // 计算bmi状态
    private String getBmiStatus(double bmi) {
        if (bmi < 18.5) return "偏瘦";
        if (bmi < 24) return "正常";
        if (bmi < 28) return "偏胖";
        return "肥胖";
    }
}
