package com.mju.hps.withme;

/**
 * Created by KMC on 2016. 11. 30..
 */

public class WaitingItem {
    private String id;
    private String name;
    private String birth;

    public WaitingItem(String id, String name, String birth) {
        this.id = id;
        this.name = name;
        this.birth = birth;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}