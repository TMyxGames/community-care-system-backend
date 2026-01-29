package com.tmyx.backend.entity;

public class User {
    private Integer id;
    private String username;
    private String sex;
    private String password;
    private String email;
    private String avatarUrl;
    private Integer role; // 0: 用户, 1: 管理员, 2: 服务人员
    private Integer serviceStatus;
    private Integer serviceAreaId;

    public Integer getId() { return id; }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() { return sex; }

    public void setSex(String sex) { this.sex = sex; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getAvatarUrl() { return avatarUrl; }

    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Integer getRole() { return role; }

    public void setRole(Integer role) { this.role = role; }

    public Integer getServiceStatus() { return serviceStatus; }

    public void setServiceStatus(Integer serviceStatus) { this.serviceStatus = serviceStatus; }

    public Integer getServiceAreaId() { return serviceAreaId; }

    public void setServiceAreaId(Integer serviceAreaId) { this.serviceAreaId = serviceAreaId; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", sex='" + sex + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", role='" + role + '\'' +
                ", serviceStatus='" + serviceStatus + '\'' +
                ", serviceAreaId='" + serviceAreaId + '\'' +
                '}';
    }

}
