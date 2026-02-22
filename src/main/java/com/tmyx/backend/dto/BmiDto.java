package com.tmyx.backend.dto;

import java.util.Date;

public class BmiDto {
    private Integer userId;
    private Double height;
    private Double weight;
    private Date recordDate;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    @Override
    public String toString() {
        return "BmiDto{" +
                "userId=" + userId +
                ", height=" + height +
                ", weight=" + weight +
                ", recordDate=" + recordDate +
                '}';
    }
}
