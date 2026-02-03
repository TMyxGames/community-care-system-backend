package com.tmyx.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Date;

public class Message {
    private Integer id;
    private Integer fromId;
    private Integer toId;
    private Integer type; // 0: 系统消息 1: 用户绑定请求
    private Integer status; // 0: 未处理 1: 已同意 2: 已拒绝 该字段仅在用户绑定请求中使用
    private String contentUrl; // 该字段仅在系统消息中使用
    private Date sendTime;

    @TableField(exist = false)
    private User fromUser;
    @TableField(exist = false)
    private User toUser;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", type=" + type +
                ", status=" + status +
                ", contentUrl='" + contentUrl + '\'' +
                ", sendTime=" + sendTime +
                '}';
    }

}
