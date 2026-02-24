package com.tmyx.backend.dto;

import java.time.LocalDateTime;
import java.util.Date;

public class BloodPressureDto {
    private Integer userId;
    private Integer heartRate;
    private Integer systolic;
    private Integer diastolic;
    private LocalDateTime recordDate;

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

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }

    @Override
    public String toString() {
        return "BloodPressureDto{" +
                "userId=" + userId +
                ", heartRate=" + heartRate +
                ", systolic=" + systolic +
                ", diastolic=" + diastolic +
                ", recordDate=" + recordDate +
                '}';
    }
}
