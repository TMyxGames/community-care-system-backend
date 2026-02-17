package com.tmyx.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.util.List;

public class User {
    private Integer id;
    private String username;
    private String realName;
    private String sex;
    private String password;
    private String email;
    private String avatarUrl;
    private Integer role; // 0: 家属（用户）, 1: 管理员, 2: 服务人员, 3: 老人（用户）
    private Integer serviceStatus; // 0: 离线, 1: 空闲中, 2: 已接单, 3: 活动中
    private Integer serviceAreaId;

    @TableField(exist = false)
    private String areaName;
    private List<Integer> serviceIds;
    private Double lng;
    private Double lat;

    public Integer getId() { return id; }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() { return realName; }

    public void setRealName(String realName) { this.realName = realName; }

    public String getSex() { return sex; }

    public void setSex(String sex) { this.sex = sex; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getAvatarUrl() { return avatarUrl; }

    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Integer getRole() { return role; }

    public void setRole(Integer role) { this.role = role; }

    public Integer getServiceStatus() { return serviceStatus; }

    public void setServiceStatus(Integer serviceStatus) { this.serviceStatus = serviceStatus; }

    public Integer getServiceAreaId() { return serviceAreaId; }

    public void setServiceAreaId(Integer serviceAreaId) { this.serviceAreaId = serviceAreaId; }

    public String getAreaName() { return areaName; }

    public void setAreaName(String areaName) { this.areaName = areaName; }

    public List<Integer> getServiceIds() { return serviceIds; }

    public void setServiceIds(List<Integer> serviceIds) { this.serviceIds = serviceIds; }

    public Double getLng() { return lng; }

    public void setLng(Double lng) { this.lng = lng; }

    public Double getLat() { return lat; }

    public void setLat(Double lat) { this.lat = lat; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", sex='" + sex + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", role='" + role + '\'' +
                ", serviceStatus='" + serviceStatus + '\'' +
                ", serviceAreaId='" + serviceAreaId + '\'' +
                '}';
    }

}
