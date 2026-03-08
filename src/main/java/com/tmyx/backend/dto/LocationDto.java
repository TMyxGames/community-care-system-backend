package com.tmyx.backend.dto;

public class LocationDto {
    private Integer userId;
    private Double lng;
    private Double lat;
    private String avatarUrl;

    public LocationDto(Integer userId, Double lng, Double lat, String avatarUrl) {
        this.userId = userId;
        this.lng = lng;
        this.lat = lat;
        this.avatarUrl = avatarUrl;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "LocationDto{" +
                "userId=" + userId +
                ", lng=" + lng +
                ", lat=" + lat +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
