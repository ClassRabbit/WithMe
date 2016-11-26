package com.mju.hps.withme.model;

import java.util.Date;

/**
 * Created by MinChan on 2016-11-26.
 */

public class RoomData {
    String title;
    String content;
    int limit;
    String[] images;
    int latitude;
    int longitude;
    Date createAt;

    public RoomData(String title, String content, int limit, String[] images, int latitude, int longitude, Date createAt) {
        this.title = title;
        this.content = content;
        this.limit = limit;
        this.images = images;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createAt = createAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
