/** Copyright (C) 2009 by Aleksey Surkov.
 **
 ** Permission to use, copy, modify, and distribute this software and its
 ** documentation for any purpose and without fee is hereby granted, provided
 ** that the above copyright notice appear in all copies and that both that
 ** copyright notice and this permission notice appear in supporting
 ** documentation.  This software is provided "as is" without express or
 ** implied warranty.
 */

#ifndef FFT_H_
#define FFT_H_

#include <jniUtils.h>

#define PITCH_CLASS_NAME "com/tabbeo/Detector/PitchDetector"

namespace PitchDetector{

JNIEXPORT void Java_com_tabbeo_Detector_PitchDetector_PitchInit(JNIEnv* env, jobject thiz, jint sample_rate, jint buffer_size, jint overlap_size, jint detection_type, jdouble yin_threshold);
JNIEXPORT jdouble Java_com_tabbeo_Detector_PitchDetector_PitchDetect(JNIEnv* env, jobject thiz, jfloatArray data);
JNIEXPORT void Java_com_tabbeo_Detector_PitchDetector_PitchDeinit(JNIEnv* env, jobject thiz);
//JNIEXPORT jboolean Java_com_tabbeo_Detector_PitchDetector_PitchIsOnset(JNIEnv* env, jobject thiz, jfloatArray data);

 static std::vector<JNINativeMethod> getPitchMethods(){
   static std::vector<JNINativeMethod> methods = 
     {
       {"PitchInit", "(IIIID)V", (void *)Java_com_tabbeo_Detector_PitchDetector_PitchInit},
       {"PitchDetect", "([F)D", (jdouble *)Java_com_tabbeo_Detector_PitchDetector_PitchDetect},
       //{"PitchIsOnset", "([F)Z", (void *)Java_com_tabbeo_Detector_PitchDetector_PitchIsOnset},
       {"PitchDeinit", "()V", (void *)Java_com_tabbeo_Detector_PitchDetector_PitchDeinit},
     };
   
   return methods;
};

}
#endif /* FFT_H_ */
