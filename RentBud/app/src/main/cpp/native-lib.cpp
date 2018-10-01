//
// Created by Cody on 9/16/2018.
//
#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_com_rentbud_activities_LoginActivity_getEmailString(
        JNIEnv *env,
        jobject /* this */) {
    std::string string = "ax9!4rkpl?";
    return env->NewStringUTF(string.c_str());
}