package com.tmyx.backend.entity;

public class SafeArea {
    private Integer id;
    private Integer userId;
    private String areaName;
    private String scopePath;
    private Double centerLng;
    private Double centerLat;

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

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getScopePath() {
        return scopePath;
    }

    public void setScopePath(String scopePath) {
        this.scopePath = scopePath;
    }

    public Double getCenterLng() {
        return centerLng;
    }

    public void setCenterLng(Double centerLng) {
        this.centerLng = centerLng;
    }

    public Double getCenterLat() {
        return centerLat;
    }

    public void setCenterLat(Double centerLat) {
        this.centerLat = centerLat;
    }

    @Override
    public String toString() {
        return "SafeArea{" +
                "id=" + id +
                ", userId=" + userId +
                ", areaName='" + areaName + '\'' +
                ", scopePath='" + scopePath + '\'' +
                ", centerLng=" + centerLng +
                ", centerLat=" + centerLat +
                '}';
    }



}
