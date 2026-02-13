package com.tmyx.backend.entity;

import java.util.Date;

public class Comment {
    private Integer id;
    private Integer userId;
    private Integer orderId;
    private Integer serviceId;
    private Integer staffId;
    private String content;
    private Integer serviceRate;
    private Integer staffRate;
    private Date createTime;

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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
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

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", orderId=" + orderId +
                ", serviceId=" + serviceId +
                ", staffId=" + staffId +
                ", content='" + content + '\'' +
                ", serviceRate=" + serviceRate +
                ", staffRate=" + staffRate +
                ", createTime=" + createTime +
                '}';
    }
}
