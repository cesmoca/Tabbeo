#include "ChordDetector.h"
#include "clam/ChordExtractor.h"

using namespace Simac;

//#define VERBOSE

namespace ChordDetector{

  ChordExtractor* pEstimator = NULL;
  double intensityThreshold;
  
  JNIEXPORT void Java_com_tabbeo_Detector_ChordDetector_ChordInit(JNIEnv* env, jobject thiz, jint sample_rate,  jint bins_per_octave, jdouble min_frequency,
								  jdouble filter_inertia, jboolean tunning_enabled, jboolean peak_windowing_enabled,
								  jdouble instant_tunning_estimator_inertia, jdouble sparse_constant_qkernel_threshold, jint correlation_threshold,
								  jdouble component_threshold, jdouble intensity_threshold)
  {
    if(pEstimator != NULL){
      LOGE("ChordDetector has been already initialized. Release it first");
      throw "ChordDetector has been already initialized. Release it first";
    }

    intensityThreshold = intensity_threshold;
    
#ifdef VERBOSE
    LOGV("Creating ChordExtractor");
#endif
    
    pEstimator = new ChordExtractor(sample_rate, min_frequency, bins_per_octave, instant_tunning_estimator_inertia, sparse_constant_qkernel_threshold, correlation_threshold, component_threshold);
    pEstimator->setFilterInertia(filter_inertia);
    pEstimator->setEnableTunning(tunning_enabled);
    pEstimator->setEnablePeakWindowing(peak_windowing_enabled);
    
#ifdef VERBOSE
    LOGV("Initing ChordDetector");
    LOGV("Buffer size required: %d", pEstimator->getFrameSize());
    LOGV("Setting sample_rate: %d", sample_rate);
    LOGV("Setting bins_per_octave: %d", bins_per_octave);
    LOGV("Setting min frequency: %f", min_frequency);
    LOGV("Setting filter_intertia: %f",filter_inertia);
    LOGV("Setting tunning_enabled: %d", tunning_enabled);
    LOGV("Setting peak_windowing_enabled: %d", peak_windowing_enabled);
    LOGV("Setting instant_tunning_estimator_inertia: %f", instant_tunning_estimator_inertia);
    LOGV("Setting sparse_constant_qkernel_threshold: %f", sparse_constant_qkernel_threshold);
    LOGV("Setting correlation_threshold: %d", correlation_threshold);
    LOGV("Setting component_threshold: %f", component_threshold);
    LOGV("Setting intensity_threshold: %f", intensity_threshold);
#endif
  }

  JNIEXPORT jint Java_com_tabbeo_Detector_ChordDetector_ChordGetSamplesBufferSize(JNIEnv* env, jobject thiz)
  {
    if(pEstimator == NULL)
      throw "Can't return the samples buffer size. The chord detector hasn't been initialized";
    
    return pEstimator->getFrameSize();
  }
  
      
  JNIEXPORT void Java_com_tabbeo_Detector_ChordDetector_ChordDeinit(JNIEnv* env, jobject thiz)
  {
#ifdef VERBOSE    
    LOGV("Deiniting ChordDetector");
#endif
    delete pEstimator;
    pEstimator = NULL;
  }

 
  JNIEXPORT jintArray Java_com_tabbeo_Detector_ChordDetector_ChordDetect(JNIEnv* env, jobject thiz, jfloatArray data)
  {
    jfloat* source_data = env->GetFloatArrayElements(data, JNI_FALSE);
    
    if(!source_data) return 0;
    
    pEstimator->doIt(source_data);

    std::vector<int> chordCandidates;
    pEstimator->createCandidateChordsArray(chordCandidates, intensityThreshold);

    jintArray jArrayChordCandidates;
    jArrayChordCandidates = env->NewIntArray(chordCandidates.size());
	  
    // move from the temp structure to the java structure
    env->SetIntArrayRegion(jArrayChordCandidates, 0, chordCandidates.size(), &chordCandidates[0]);
    
    return jArrayChordCandidates;
  }


  JNIEXPORT jint Java_com_tabbeo_Detector_ChordDetector_getNModes(JNIEnv* env, jobject thiz)
  {
    return ChordCorrelator::Diminished7 + 1;
  }
  
}
