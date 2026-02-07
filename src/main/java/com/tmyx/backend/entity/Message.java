package com.tmyx.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Date;

public class Message {
    private Integer id;
    private Integer fromSessionId;
    private Integer toSessionId;
    private Integer fromId;
    private Integer toId;
    private String content;
    private Integer type; // 0: 系统通知 1: 绑定请求 2: 安全提醒 3: 用户私信
    private Integer status; // 0: 未处理 1: 已同意 2: 已拒绝 该字段仅在用户绑定请求中使用
    private Date sendTime;

    @TableField(exist = false)
    private User otherUser;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromSessionId() { return fromSessionId; }

    public void setFromSessionId(Integer fromSessionId) { this.fromSessionId = fromSessionId; }

    public Integer getToSessionId() { return toSessionId; }

    public void setToSessionId(Integer toSessionId) { this.toSessionId = toSessionId; }

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

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public Integer getType() { return type; }

    public void setType(Integer type) { this.type = type; }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public User getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", fromSessionId=" + fromSessionId +
                ", toSessionId=" + toSessionId +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", sendTime=" + sendTime +
                '}';
    }

}
