package com.tmyx.backend.dto;

public class UserBindDto {
    private Integer id; // 对方用户id
    private String username; // 对方用户名
    private String realName; // 对方真实姓名
    private String sex; // 对方性别
    private String avatarUrl; // 对方头像
    private Integer relation; // 关系（0: 家属, 1: 保姆, ...）
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

    public String getSex() { return sex; }

    public void setSex(String sex) { this.sex = sex; }

    public String getAvatarUrl() { return avatarUrl; }

    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Integer getRelation() {
        return relation;
    }

    public void setRelation(Integer relation) {
        this.relation = relation;
    }

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
                ", sex='" + sex + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", relation=" + relation +
                ", remark='" + remark + '\'' +
                '}';
    }
}
