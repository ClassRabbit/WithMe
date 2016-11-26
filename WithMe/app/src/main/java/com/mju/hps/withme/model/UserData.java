package com.mju.hps.withme.model;

/**
 * Created by MinChan on 2016-11-26.
 */

public class UserData {
    private String id;
    private String mail;
    private String password;
    private String token;
    private String name;
    private String birth;
    private String phone;
    private String gender;

    public UserData(String id, String mail, String password, String token, String name, String birth, String phone, String gender) {
        this.id = id;
        this.mail = mail;
        this.password = password;
        this.token = token;
        this.name = name;
        this.birth = birth;
        this.phone = phone;
        this.gender = gender;
    }




    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }




}
