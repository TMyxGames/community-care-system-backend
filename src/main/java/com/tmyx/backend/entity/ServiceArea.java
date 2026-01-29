package com.tmyx.backend.entity;

public class ServiceArea {
    private Integer id;
    private Integer staffId;
    private String areaName;
    private String scopePath;
    private Double centerLng;
    private Double centerLat;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getStaffId() { return staffId; }

    public void setStaffId(Integer staffId) { this.staffId = staffId; }

    public String getAreaName() { return areaName; }

    public void setAreaName(String areaName) { this.areaName = areaName; }

    public String getScopePath() { return scopePath; }

    public void setScopePath(String scopePath) { this.scopePath = scopePath; }

    public Double getCenterLng() { return centerLng; }

    public void setCenterLng(Double centerLng) { this.centerLng = centerLng; }

    public Double getCenterLat() { return centerLat; }

    public void setCenterLat(Double centerLat) { this.centerLat = centerLat; }

    @Override
    public String toString() {
        return "ServiceArea{" +
                "id=" + id +
                ", staffId=" + staffId +
                ", areaName='" + areaName + '\'' +
                ", scopePath='" + scopePath + '\'' +
                ", centerLng=" + centerLng +
                ", centerLat=" + centerLat +
                '}';
    }

}
