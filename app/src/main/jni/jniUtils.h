#ifndef JNI_UTILS
#define JNI_UTILS

#include <stddef.h>
#include <jni.h>
#include <android/log.h>
#include <vector>

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "Detector" , __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "Detector", __VA_ARGS__)

#define RECORDER_CLASS_NAME "com/tabbeo/Detector/AudioSource/IAudioSource"

jint JNI_OnLoad(JavaVM* vm, void* reserved);
int jniRegisterNativeMethods(JNIEnv* env, const char* className, const std::vector<JNINativeMethod>& gMethods);

#endif /* JNI_UTILS */
