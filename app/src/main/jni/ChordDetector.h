#ifndef CLAM_H_
#define CLAM_H_

#include <jniUtils.h>

#define CHORD_CLASS_NAME "com/tabbeo/Detector/ChordDetector"

namespace ChordDetector{

JNIEXPORT void Java_com_tabbeo_Detector_ChordDetector_ChordInit(JNIEnv* env, jobject thiz, jint sample_rate, jint bins_per_octave, jdouble min_frequency,
							     jdouble filter_inertia, jboolean tunning_enabled, jboolean peak_windowing_enabled,
								jdouble instant_tunning_estimator_inertia, jdouble sparse_constant_qkernel_threshold, jint correlation_threshold,
								jdouble component_threshold, jdouble intensity_threshold);
JNIEXPORT jint Java_com_tabbeo_Detector_ChordDetector_ChordGetSamplesBufferSize(JNIEnv* env, jobject thiz);
JNIEXPORT jintArray Java_com_tabbeo_Detector_ChordDetector_ChordDetect(JNIEnv* env, jobject thiz, jfloatArray data);
JNIEXPORT void Java_com_tabbeo_Detector_ChordDetector_ChordDeinit(JNIEnv* env, jobject thiz);

// This is used in the tests
JNIEXPORT jint Java_com_tabbeo_Detector_ChordDetector_getNModes(JNIEnv* env, jobject thiz);

static std::vector<JNINativeMethod> getChordMethods(){
  static std::vector<JNINativeMethod> methods = 
    {
      {"ChordInit", "(IIDDZZDDIDD)V", (void *)Java_com_tabbeo_Detector_ChordDetector_ChordInit},
      {"ChordGetSamplesBufferSize", "()I", (jint *)Java_com_tabbeo_Detector_ChordDetector_ChordGetSamplesBufferSize},
      {"ChordDetect", "([F)[I", (jint *)Java_com_tabbeo_Detector_ChordDetector_ChordDetect},
      {"ChordDeinit", "()V", (void *)Java_com_tabbeo_Detector_ChordDetector_ChordDeinit},
      
      // This is used in the tests
      {"getNModes", "()I", (void *)Java_com_tabbeo_Detector_ChordDetector_getNModes}
    };
  
  return methods;
};
  
}

#endif /* CLAM_H_ */
