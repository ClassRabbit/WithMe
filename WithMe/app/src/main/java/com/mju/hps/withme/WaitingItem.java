package com.mju.hps.withme;

/**
 * Created by KMC on 2016. 11. 30..
 */

public class WaitingItem {
    private String id;
    private String mail;
    private String name;
    private String birth;
    private String gender;
    private String phone;

    public WaitingItem(String id, String mail, String name, String birth, String gender, String phone) {
        this.id = id;
        this.mail = mail;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}