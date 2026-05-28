#include "jniUtils.h"

int jniRegisterNativeMethods(JNIEnv* env, const char* className,
			     const std::vector <JNINativeMethod>& gMethods)
{
    jclass clazz;
    int numMethods = gMethods.size();
    
    LOGV("Registering %d natives:",numMethods);
    LOGV("Class name: %s",className);
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        LOGE("Native registration unable to find class:");
        LOGE("%s",className);
        return -1;
    }
		   
    if (env->RegisterNatives(clazz, &gMethods[0], numMethods) < 0) {
        LOGE("RegisterNatives failed for:");
        LOGE("%s",className);
        return -1;
    }
    LOGV("Successfully registered natives.");
    return 0;
}
