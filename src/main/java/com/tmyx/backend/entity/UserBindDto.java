package com.tmyx.backend.entity;

public class UserBindDto {
    private Integer id;
    private Integer userId;
    private Integer followerId;
    private Integer elderId;
    private String avatarUrl;
    private String remark;

    public UserBindDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() { return userId;}

    public void setUserId(Integer userId) { this.userId = userId;}

    public Integer getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Integer followerId) {
        this.followerId = followerId;
    }

    public Integer getElderId() {
        return elderId;
    }

    public void setElderId(Integer elderId) {
        this.elderId = elderId;
    }

    public String getAvatarUrl() { return avatarUrl; }

    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "UserBindingDto{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", followerId='" + followerId + '\'' +
                ", elderId='" + elderId + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
