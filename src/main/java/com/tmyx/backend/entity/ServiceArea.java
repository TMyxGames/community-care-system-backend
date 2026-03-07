package com.tmyx.backend.entity;

import org.locationtech.jts.geom.Polygon;

public class ServiceArea {
    private Integer id;
    private Integer adminId;
    private String areaName;
    private String scopePath;
    private String region;
    private Double centerLng;
    private Double centerLat;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getAdminId() { return adminId; }

    public void setAdminId(Integer adminId) { this.adminId = adminId; }

    public String getAreaName() { return areaName; }

    public void setAreaName(String areaName) { this.areaName = areaName; }

    public String getScopePath() { return scopePath; }

    public void setScopePath(String scopePath) { this.scopePath = scopePath; }

    public String getRegion() { return region; }

    public void setRegion(String region) { this.region = region; }

    public Double getCenterLng() { return centerLng; }

    public void setCenterLng(Double centerLng) { this.centerLng = centerLng; }

    public Double getCenterLat() { return centerLat; }

    public void setCenterLat(Double centerLat) { this.centerLat = centerLat; }

    @Override
    public String toString() {
        return "ServiceArea{" +
                "id=" + id +
                ", adminId=" + adminId +
                ", areaName='" + areaName + '\'' +
                ", scopePath='" + scopePath + '\'' +
                ", region=" + region +
                ", centerLng=" + centerLng +
                ", centerLat=" + centerLat +
                '}';
    }

}
