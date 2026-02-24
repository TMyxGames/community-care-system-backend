package com.tmyx.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Date;

public class HealthDataDto {
    private Integer userId;
    private String type;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime recordDate;
    // 血压
    private Integer systolic;
    private Integer diastolic;
    private Integer heartRate;
    // bmi
    private Double height;
    private Double weight;
    private Double bmi;
    // 血糖
    private Double bloodSugar;
    private Integer mealStatus;
    // 预留数值
    private Double value;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
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

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
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

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "HealthDataDto{" +
                "userId=" + userId +
                ", type='" + type + '\'' +
                ", recordDate=" + recordDate +
                ", systolic=" + systolic +
                ", diastolic=" + diastolic +
                ", heartRate=" + heartRate +
                ", height=" + height +
                ", weight=" + weight +
                ", bmi=" + bmi +
                ", bloodSugar=" + bloodSugar +
                ", mealStatus=" + mealStatus +
                ", value=" + value +
                '}';
    }
}
