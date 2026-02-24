package com.tmyx.backend.entity;

import java.time.LocalDateTime;
import java.util.Date;

public class HealthDataBS {
    private Integer id;
    private Integer userId;
    private Double bloodSugar;
    private Integer mealStatus;
    private LocalDateTime recordDate;

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

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }

    @Override
    public String toString() {
        return "HealthDataBS{" +
                "id=" + id +
                ", userId=" + userId +
                ", bloodSugar=" + bloodSugar +
                ", mealStatus=" + mealStatus +
                ", recordDate=" + recordDate +
                '}';
    }
}
