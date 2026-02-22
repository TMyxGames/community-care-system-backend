package com.tmyx.backend.entity;

import java.util.Date;

public class HealthDataBP {
    private Integer id;
    private Integer userId;
    private Integer heartRate;
    private Integer systolic;
    private Integer diastolic;
    private Date recordDate;

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

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getSystolic() {
        return systolic;
    }

    public void setSystolic(Integer systolic) {
        this.systolic = systolic;
    }

    public Integer getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(Integer diastolic) {
        this.diastolic = diastolic;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    @Override
    public String toString() {
        return "HealthDataBP{" +
                "id=" + id +
                ", userId=" + userId +
                ", heartRate=" + heartRate +
                ", systolic=" + systolic +
                ", diastolic=" + diastolic +
                ", recordDate=" + recordDate +
                '}';
    }
}
