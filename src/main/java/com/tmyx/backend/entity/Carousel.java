package com.tmyx.backend.entity;

public class Carousel {
    private int id;
    private int sortOrder;
    private String title;
    private String imgUrl;
    private String link;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSortOrder() { return sortOrder; }

    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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
        return "Carousel{" +
                "id=" + id +
                ", sortOrder=" + sortOrder +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
