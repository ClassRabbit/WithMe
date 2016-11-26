package com.mju.hps.withme.model;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mju.hps.withme.database.DatabaseLab;

/**
 * Created by KMC on 2016. 11. 16..
 */

public class User {
    private static User user;
    private String id;
    private String mail;
    private String password;
    private String token;
    private String name;
    private String birth;
    private String phone;
    private String gender;

    private Bitmap profileImage;

//    private User(String id, String mail, String password, String token){
//        this.id = id;
//        this.mail = mail;
//        this.password = password;
//        this.token = token;
//    }

    public static void setInstance(){
        if(user == null){
            user = new User();
            DatabaseLab.getInstance().setUser(null, null);
        }
    }

    public static User getInstance(){
        return user;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap image) {
        this.profileImage = image;
    }
}
