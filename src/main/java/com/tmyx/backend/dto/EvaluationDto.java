package com.tmyx.backend.dto;

import com.tmyx.backend.entity.Evaluation;

import java.util.Date;

public class EvaluationDto {
    private Integer id;
    private Integer userId;
    private Integer staffId;
    private Integer serviceId;

    private String content;
    private Integer serviceRate;
    private Integer staffRate;
    private Date createTime;

    private String userName;
    private String avatarUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getServiceRate() {
        return serviceRate;
    }

    public void setServiceRate(Integer serviceRate) {
        this.serviceRate = serviceRate;
    }

    public Integer getStaffRate() {
        return staffRate;
    }

    public void setStaffRate(Integer staffRate) {
        this.staffRate = staffRate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "EvaluationDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", staffId=" + staffId +
                ", serviceId=" + serviceId +
                ", content='" + content + '\'' +
                ", serviceRate=" + serviceRate +
                ", staffRate=" + staffRate +
                ", createTime=" + createTime +
                ", userName='" + userName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
