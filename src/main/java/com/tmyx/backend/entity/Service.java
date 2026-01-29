package com.tmyx.backend.entity;

import java.math.BigDecimal;

public class Service {
    private int id;
    private int sortOrder;
    private String title;
    private String type;
    private String introduce;
    private String contentUrl;
    private String provider;
    private String workTime;
    private String location;
    private BigDecimal price;
    private int total;
    private String imgUrl;
    private String link;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public String getLocation() { return location; }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getTotal() { return total; }

    public void setTotal(int total) { this.total = total; }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
                ", provider='" + provider + '\'' +
                ", workTime='" + workTime + '\'' +
                ", location='" + location + '\'' +
                ", price='" + price + '\'' +
                ", total='" + total + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
