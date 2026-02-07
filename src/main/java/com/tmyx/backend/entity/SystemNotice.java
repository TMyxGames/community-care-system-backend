package com.tmyx.backend.entity;

import java.time.LocalDateTime;
import java.util.Date;

public class SystemNotice {
    private Integer id;
    private Integer adminId;
    private String title;
    private String contentUrl;
    private LocalDateTime createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SystemNotice{" +
                "id=" + id +
                ", adminId=" + adminId +
                ", title='" + title + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
