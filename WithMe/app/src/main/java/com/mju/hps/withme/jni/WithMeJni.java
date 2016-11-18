package com.mju.hps.withme.jni;

/**
 * Created by KMC on 2016. 11. 18..
 */

public class WithMeJni {
    static {
        System.loadLibrary("jniExample");
    }

    public native String getJNIString();
    public native int isValidMail(String mail);
    public native int isSamePassword(String password1, String password2);

    public String testRe(){
        return getJNIString();
    }
}
