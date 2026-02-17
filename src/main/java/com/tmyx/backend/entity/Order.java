package com.tmyx.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private Integer id;
    private String orderSn;
    private Integer userId;
    private Integer staffId;
    private Integer serviceId;
    private String serviceTitle;
    private String serviceImg;
    private BigDecimal servicePrice;
    private Double lng;
    private Double lat;
    private String addressShot;
    private LocalDateTime createTime;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;
    private Integer state; // 0: 待接单 1: 待服务 2: 进行中 3: 已完成 4: 已评价 5: 已取消

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
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

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public String getServiceImg() {
        return serviceImg;
    }

    public void setServiceImg(String serviceImg) {
        this.serviceImg = serviceImg;
    }

    public BigDecimal getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(BigDecimal servicePrice) {
        this.servicePrice = servicePrice;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getAddressShot() {
        return addressShot;
    }

    public void setAddressShot(String addressShot) {
        this.addressShot = addressShot;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(LocalDateTime completeTime) {
        this.completeTime = completeTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderSn='" + orderSn + '\'' +
                ", userId=" + userId +
                ", staffId=" + staffId +
                ", serviceId=" + serviceId +
                ", serviceTitle='" + serviceTitle + '\'' +
                ", serviceImg='" + serviceImg + '\'' +
                ", servicePrice=" + servicePrice +
                ", lng=" + lng +
                ", lat=" + lat +
                ", addressShot='" + addressShot + '\'' +
                ", createTime=" + createTime +
                ", startTime=" + startTime +
                ", completeTime=" + completeTime +
                ", state=" + state +
                '}';
    }
}
