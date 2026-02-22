package com.tmyx.backend.dto;

import java.util.Date;

public class BloodSugarDto {
    private Integer userId;
    private Double bloodSugar;
    private Integer mealStatus;
    private Date recordDate;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(Double bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public Integer getMealStatus() {
        return mealStatus;
    }

    public void setMealStatus(Integer mealStatus) {
        this.mealStatus = mealStatus;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    @Override
    public String toString() {
        return "BloodSugarDto{" +
                "userId=" + userId +
                ", bloodSugar=" + bloodSugar +
                ", mealStatus=" + mealStatus +
                ", recordDate=" + recordDate +
                '}';
    }
}
