package com.mju.hps.withme;

/**
 * Created by KMC on 2016. 11. 30..
 */

public class ConstitutorItem {
    private String id;
    private String mail;
    private String name;
    private String gender;
    private String birth;


    public ConstitutorItem(String id, String mail, String name, String gender, String birth) {
        this.id = id;
        this.mail = mail;
        this.name = name;
        this.gender = gender;
        this.birth = birth;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
