package com.tmyx.backend.entity;

import java.util.Date;

public class Location {
    private Integer userId;
    private Double lng;
    private Double lat;
    private Date updateTime;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Location{" +
                "userId=" + userId +
                ", lng=" + lng +
                ", lat=" + lat +
                ", updateTime=" + updateTime +
                '}';
    }
}
