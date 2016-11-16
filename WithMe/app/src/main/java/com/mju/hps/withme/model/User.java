package com.mju.hps.withme.model;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by KMC on 2016. 11. 16..
 */

public class User {
    private static User user;
    private String id;
    private String mail;
    private String password;
    private String token;

    private User(String id, String mail, String password, String token){
        this.id = id;
        this.mail = mail;
        this.password = password;
        this.token = token;
    }

    public static void setInstance(String id, String mail, String password, String token){
        if(user == null){
            user = new User(id, mail, password, token);
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
}
