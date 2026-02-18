package com.tmyx.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

public class Article {
    @TableId(type = IdType.INPUT)
    private String id;
    private Integer up_id;
    private String title;
    private String contentUrl;
    private Integer status;
    private LocalDateTime uploadTime;

    @TableField(exist = false)
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUpId() {
        return up_id;
    }

    public void setUpId(Integer up_id) {
        this.up_id = up_id;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", up_id=" + up_id +
                ", title='" + title + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                ", status=" + status +
                ", uploadTime='" + uploadTime + '\'' +
                '}';
    }
}
