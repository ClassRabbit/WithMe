//
// Created by Kimminchan on 2016. 11. 18..
//

#include <com_mju_hps_withme_jni_WithMeJni.h>

JNIEXPORT jstring JNICALL Java_com_mju_hps_withme_jni_WithMeJni_getJNIString (JNIEnv *env, jobject obj) {

    return env->NewStringUTF("Message from jniMain");
}
