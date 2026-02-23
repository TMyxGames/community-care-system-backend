package com.tmyx.backend.entity;

import java.time.LocalDateTime;

public class EmergencyCall {
    private Integer id;
    private Integer userId;
    private Integer type; // 0: 手动 1: 自动
    private LocalDateTime callTime;
    private String remark;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public LocalDateTime getCallTime() {
        return callTime;
    }

    public void setCallTime(LocalDateTime callTime) {
        this.callTime = callTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "EmergencyCall{" +
                "id=" + id +
                ", userId=" + userId +
                ", type=" + type +
                ", callTime=" + callTime +
                ", remark='" + remark + '\'' +
                '}';
    }
}
