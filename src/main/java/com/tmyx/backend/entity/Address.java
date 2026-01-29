package com.tmyx.backend.entity;

public class Address {
    private Integer id;
    private Integer userId;
    private String contact;
    private String phone;
    private String area;
    private String detail;
    private Integer isDefault;
    private Double lng;
    private Double lat;
    private String adcode;
    private Boolean isVerified;

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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
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

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", userId=" + userId +
                ", contact='" + contact + '\'' +
                ", phone='" + phone + '\'' +
                ", area='" + area + '\'' +
                ", detail='" + detail + '\'' +
                ", isDefault=" + isDefault +
                ", lng=" + lng +
                ", lat=" + lat +
                ", adcode='" + adcode + '\'' +
                ", isVerified=" + isVerified +
                '}';
    }
}
