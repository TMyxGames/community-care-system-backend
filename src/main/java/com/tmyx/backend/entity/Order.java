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
    private Integer addressId;
    private String addressShot;
    private LocalDateTime createTime;
    private LocalDateTime scheduledTime;
    private LocalDateTime completeTime;
    private Integer state;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getOrderSn() { return orderSn; }

    public void setOrderSn(String orderSn) { this.orderSn = orderSn; }

    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getStaffId() { return staffId; }

    public void setStaffId(Integer staffId) { this.staffId = staffId; }

    public Integer getServiceId() { return serviceId; }

    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }

    public String getServiceTitle() { return serviceTitle; }

    public void setServiceTitle(String serviceTitle) { this.serviceTitle = serviceTitle; }

    public String getServiceImg() { return serviceImg; }

    public void setServiceImg(String serviceImg) { this.serviceImg = serviceImg; }

    public BigDecimal getServicePrice() { return servicePrice; }

    public void setServicePrice(BigDecimal servicePrice) { this.servicePrice = servicePrice; }

    public Integer getAddressId() { return addressId; }

    public void setAddressId(Integer addressId) { this.addressId = addressId; }

    public String getAddressShot() { return addressShot; }

    public void setAddressShot(String addressShot) { this.addressShot = addressShot; }

    public LocalDateTime getCreateTime() { return createTime; }

    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }

    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public LocalDateTime getCompleteTime() { return completeTime; }

    public void setCompleteTime(LocalDateTime completeTime) { this.completeTime = completeTime; }

    public Integer getState() { return state; }

    public void setState(Integer state) { this.state = state; }

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
                ", addressId=" + addressId +
                ", addressShot='" + addressShot + '\'' +
                ", createTime=" + createTime +
                ", scheduledTime=" + scheduledTime +
                ", completeTime=" + completeTime +
                ", state=" + state +
                '}';
    }

}
