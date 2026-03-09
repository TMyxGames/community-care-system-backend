package com.tmyx.backend.dto;

import java.time.LocalDateTime;

public class CallDto {
    private Integer id; // 呼叫记录id
    private Integer userId; // 呼叫用户id
    private Integer type; // 0: 手动 1: 自动
    private LocalDateTime callTime; // 呼叫时间

    private String username;
    private String realName;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "CallDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", type=" + type +
                ", callTime=" + callTime +
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
