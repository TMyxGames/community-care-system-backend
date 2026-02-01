package com.tmyx.backend.entity;

public class StaffWork {
    private Integer id;
    private Integer staffId;
    private Integer serviceId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "StaffWork{" +
                "id=" + id +
                ", staffId=" + staffId +
                ", serviceId=" + serviceId +
                '}';
    }
}
