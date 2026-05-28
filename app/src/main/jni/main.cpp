#include <jni.h>
#include "PitchDetector.h"
#include "ChordDetector.h"

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }

    jniRegisterNativeMethods(env, PITCH_CLASS_NAME, PitchDetector::getPitchMethods());
    jniRegisterNativeMethods(env, CHORD_CLASS_NAME, ChordDetector::getChordMethods());
    
    return JNI_VERSION_1_4;
}
