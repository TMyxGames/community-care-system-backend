package com.tmyx.backend.entity;

import java.time.LocalDateTime;
import java.util.Date;

public class Session {
    private Integer id;
    private Integer userId;
    private Integer targetId;
    private String name;
    private Integer type; // 0: 系统消息 1: 绑定请求 2: 安全提醒 3: 用户私信
    private String lastMsg;
    private Integer unreadCount;
    private Date updateTime;

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

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() { return type; }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "session{" +
                "id=" + id +
                ", userId=" + userId +
                ", targetId=" + targetId +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", lastMsg='" + lastMsg + '\'' +
                ", unreadCount=" + unreadCount +
                ", updateTime=" + updateTime +
                '}';
    }

}
