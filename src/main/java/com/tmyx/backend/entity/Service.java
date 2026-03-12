package com.tmyx.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.math.BigDecimal;

public class Service {
    private Integer id;
    private Integer sortOrder;
    private String title;
    private String type;
    private String introduce;
    private String contentUrl;
    private String workTime;
    private BigDecimal price;
    private String imgUrl;

    @TableField(exist = false)
    private Integer total;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getContentUrl() { return contentUrl; }

    public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getTotal() { return total; }

    public void setTotal(Integer total) { this.total = total; }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", sortOrder=" + sortOrder +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", introduce='" + introduce + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                ", workTime='" + workTime + '\'' +
                ", price='" + price + '\'' +
                ", total='" + total + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
