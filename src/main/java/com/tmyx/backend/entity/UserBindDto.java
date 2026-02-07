package com.tmyx.backend.entity;

public class UserBindDto {
    private Integer id; // 对方用户id
    private String username; // 对方用户名
    private String realName; // 对方真实姓名
    private String avatarUrl; // 对方头像
    private String remark; // 绑定关系备注

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getRealName() { return realName; }

    public void setRealName(String realName) { this.realName = realName; }

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
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
