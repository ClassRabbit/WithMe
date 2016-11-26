package com.mju.hps.withme;

/**
 * Created by MinChan on 2016-11-26.
 */

public class RoomItem {
    private String id;
    private String title;
    private int limit;
    private String address;


    public RoomItem(String id, String title, int limit, String address) {
        this.title = title;
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
