/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_mju_hps_withme_jni_WithMeJni */

#ifndef _Included_com_mju_hps_withme_jni_WithMeJni
#define _Included_com_mju_hps_withme_jni_WithMeJni
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_mju_hps_withme_jni_WithMeJni
 * Method:    getJNIString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_mju_hps_withme_jni_WithMeJni_getJNIString
  (JNIEnv *, jobject);

/*
 * Class:     com_mju_hps_withme_jni_WithMeJni
 * Method:    isValidMail
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_mju_hps_withme_jni_WithMeJni_isValidMail
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_mju_hps_withme_jni_WithMeJni
 * Method:    isSamePassword
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_mju_hps_withme_jni_WithMeJni_isSamePassword
  (JNIEnv *, jobject, jstring, jstring);

#ifdef __cplusplus
}
#endif
#endif