//
// Created by Kimminchan on 2016. 11. 18..
//

#include <com_mju_hps_withme_jni_WithMeJni.h>
#include <string.h>

JNIEXPORT jstring JNICALL Java_com_mju_hps_withme_jni_WithMeJni_getJNIString (JNIEnv *env, jobject obj) {

    return env->NewStringUTF("Message from jniMain");
}

JNIEXPORT jint JNICALL Java_com_mju_hps_withme_jni_WithMeJni_isValidMail (JNIEnv *env, jobject obj, jstring mail) {
    jint i;
//    char buf[128];

    const char *mail_temp = env->GetStringUTFChars(mail, 0);
    char *result;

    result = strstr(mail_temp, "@");

    env->ReleaseStringUTFChars(mail, mail_temp);
    if(result) {
        i = 1;
        return i;
    }
    else {
        i = 0;
        return i;
    }
}

JNIEXPORT jint JNICALL Java_com_mju_hps_withme_jni_WithMeJni_isSamePassword
        (JNIEnv *env, jobject obj, jstring password1, jstring password2) {
    jint i;
    const char *pw1 = env->GetStringUTFChars(password1, 0);
    const char *pw2 = env->GetStringUTFChars(password2, 0);

    int result = strcmp(pw1, pw2);

    env->ReleaseStringUTFChars(password1, pw1);
    env->ReleaseStringUTFChars(password2, pw2);

    if(result == 0){
        i = 1;
        return i;
    }
    else {
        i = 0;
        return i;
    }

}

JNIEXPORT jint JNICALL Java_com_mju_hps_withme_jni_WithMeJni_isValidPhone
        (JNIEnv *env, jobject obj, jstring phone) {
    jint i;
    const char *phone_tmp = env->GetStringUTFChars(phone, 0);
    int length = strlen(phone_tmp);
    if(length == 11 || length == 10) {
        i = 1;
        return i;
    }
    else {
        i = 0;
        return i;
    }
}