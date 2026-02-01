package com.tmyx.backend.entity;

import java.util.List;

public class StaffConfigDto {
    private Integer userId;
    private Integer areaId;
    private List<Integer> serviceIds;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public List<Integer> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Integer> serviceIds) {
        this.serviceIds = serviceIds;
    }

    @Override
    public String toString() {
        return "StaffConfigDto{" +
                "userId=" + userId +
                ", areaId=" + areaId +
                ", serviceIds=" + serviceIds +
                '}';
    }
}
