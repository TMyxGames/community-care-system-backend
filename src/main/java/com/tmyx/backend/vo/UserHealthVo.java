package com.tmyx.backend.vo;

import com.tmyx.backend.dto.BloodPressureDto;
import com.tmyx.backend.dto.BloodSugarDto;
import com.tmyx.backend.dto.BmiDto;
import com.tmyx.backend.entity.HealthDataBMI;
import com.tmyx.backend.entity.HealthDataBP;
import com.tmyx.backend.entity.HealthDataBS;

public class UserHealthVo {
    private Integer userId;
    private String username;
    private String realName;
    private Integer age;
    private String sex;
    private String avatarUrl;

    private BmiDto latestBmi;
    private BloodPressureDto latestBp;
    private BloodSugarDto latestFastingBs;
    private BloodSugarDto latestPostprandialBs;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public BmiDto getLatestBmi() {
        return latestBmi;
    }

    public void setLatestBmi(BmiDto latestBmi) {
        this.latestBmi = latestBmi;
    }

    public BloodPressureDto getLatestBp() {
        return latestBp;
    }

    public void setLatestBp(BloodPressureDto latestBp) {
        this.latestBp = latestBp;
    }

    public BloodSugarDto getLatestFastingBs() {
        return latestFastingBs;
    }

    public void setLatestFastingBs(BloodSugarDto latestFastingBs) {
        this.latestFastingBs = latestFastingBs;
    }

    public BloodSugarDto getLatestPostprandialBs() {
        return latestPostprandialBs;
    }

    public void setLatestPostprandialBs(BloodSugarDto latestPostprandialBs) {
        this.latestPostprandialBs = latestPostprandialBs;
    }

    @Override
    public String toString() {
        return "UserHealthDto{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", latestBmi=" + latestBmi +
                ", latestBp=" + latestBp +
                ", latestBs=" + latestFastingBs +
                ", latestPostprandialBs=" + latestPostprandialBs +
                '}';
    }
}
