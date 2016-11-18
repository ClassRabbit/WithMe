package com.mju.hps.withme.jni;

/**
 * Created by KMC on 2016. 11. 18..
 */

public class WithMeJni {
    static {
        System.loadLibrary("jniExample");
    }

    public native String getJNIString();

    public String testRe(){
        return getJNIString();
    }
}
