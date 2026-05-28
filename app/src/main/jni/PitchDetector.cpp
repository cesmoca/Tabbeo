/** Copyright (C) 2009 by Aleksey Surkov.
 **
 ** Permission to use, copy, modify, and distribute this software and its
 ** documentation for any purpose and without fee is hereby granted, provided
 ** that the above copyright notice appear in all copies and that both that
 ** copyright notice and this permission notice appear in supporting
 ** documentation.  This software is provided "as is" without express or
 ** implied warranty.
 */

#include <math.h>
#include "PitchDetector.h"
#include <aubio.h>

//#define VERBOSE

namespace PitchDetector{

/* pitch objects */
fvec_t ibuf;
aubio_pitchdetection_t* pitchdet = NULL;
  //aubio_pvoc_t* phase_vocoder;
  //cvec_t* fftgrain;
  //aubio_onsetdetection_t* onset;
  //fvec_t* onset_output;
  //aubio_pickpeak_t* peak_picker;

// Configuration given from the JVM
struct PitchDetectorConfig{
  aubio_pitchdetection_type detection_type;
  //aubio_onsetdetection_type type_onset;
  //aubio_onsetdetection_type type_onset2 = aubio_onset_complex;
  uint_t sample_rate;
  uint_t buffer_size;
  uint_t overlap_size; // Typically, half of buffer size
  double yin_threshold;
  //smpl_t onset_threshold;
  //smpl_t onset_silence;
}pitch_detector_conf;
  
// Constants
uint_t N_CHANNELS = 1;

  void Java_com_tabbeo_Detector_PitchDetector_PitchInit(JNIEnv* env, jobject thiz, jint sample_rate, jint buffer_size, jint overlap_size, jint detection_type, jdouble yin_threshold)
{
  //LOGV("Initing PitchDetector");
  
  if(pitchdet != NULL){
    LOGE("ChordDetector has been already initialized. Release it first");
    throw "PitchDetector has been already initialized. Release it first";
  }

  pitch_detector_conf = PitchDetectorConfig();
  
  // Pitch detection type
  pitch_detector_conf.detection_type = (aubio_pitchdetection_type) detection_type;// aubio_pitch_yinfft / aubio_pitch_mcomb...  
   
  // Sample rate
  pitch_detector_conf.sample_rate = sample_rate;

  // Buffer size
  pitch_detector_conf.buffer_size = buffer_size;

  // Overlap size
  pitch_detector_conf.overlap_size = overlap_size;
  
  // Yin threshold
  pitch_detector_conf.yin_threshold = yin_threshold;

#ifdef VERBOSE
  LOGV("Setting detection type: %d", pitch_detector_conf.detection_type);
  LOGV("Setting sample_rate: %d", pitch_detector_conf.sample_rate);
  LOGV("Setting buffer_size: %d",pitch_detector_conf.buffer_size);
  LOGV("Setting overlap_size: %d",pitch_detector_conf.overlap_size);
  LOGV("Setting Aubio yin threshold: %f", pitch_detector_conf.yin_threshold);
#endif
  
  // OnSet type
  //pitch_detector_conf.type_onset = aubio_onset_kl;
  //pitch_detector_conf.type_onset2 = aubio_onset_complex;
  // Onset threshold
  //pitch_detector_conf.onset_threshold = onset_threshold;
  //LOGV("Setting onset_threshold: %f", pitch_detector_conf.onset_threshold);
  // Onset silence
  //pitch_detector_conf.onset_silence = onset_silence;
  //LOGV("Setting onset_silence: %f", pitch_detector_conf.onset_silence);
   // Peak Picker
  //peak_picker = new_aubio_peakpicker(pitch_detector_conf.onset_threshold);
  // Onset detection
  //onset = new_aubio_onsetdetection(pitch_detector_conf.type_onset, pitch_detector_conf.buffer_size, N_CHANNELS);
  //onset_output = new_fvec(1, N_CHANNELS);
  // Phase vocoder
  //phase_vocoder = new_aubio_pvoc(pitch_detector_conf.buffer_size, pitch_detector_conf.overlap_size, N_CHANNELS);
  //fftgrain = new_cvec(pitch_detector_conf.buffer_size, N_CHANNELS);

  // Pitch detector
  pitchdet = new_aubio_pitchdetection(pitch_detector_conf.buffer_size, pitch_detector_conf.overlap_size, N_CHANNELS, pitch_detector_conf.sample_rate, pitch_detector_conf.detection_type, aubio_pitchm_freq);

  // Setting the yin threshold
  if(pitch_detector_conf.detection_type == aubio_pitch_yin || pitch_detector_conf.detection_type == aubio_pitch_yinfft){ 
    aubio_pitchdetection_set_yinthresh(pitchdet, pitch_detector_conf.yin_threshold);
  }
 
  ibuf.length = buffer_size;
  ibuf.channels = N_CHANNELS;
}
  
jdouble Java_com_tabbeo_Detector_PitchDetector_PitchDetect(JNIEnv* env, jobject thiz, jfloatArray data)
{
  jdouble pitch;
  jfloat* source_data = env->GetFloatArrayElements(data, JNI_FALSE);

  ibuf.data = &source_data;

  pitch = aubio_pitchdetection(pitchdet, &ibuf);

  env->ReleaseFloatArrayElements(data, source_data, 0);
  return pitch;
}

void Java_com_tabbeo_Detector_PitchDetector_PitchDeinit(JNIEnv* env, jobject thiz)
{
  //LOGV("Deiniting PitchDetector");
  //del_aubio_onsetdetection(onset);
  //del_aubio_peakpicker(peak_picker);
  //del_aubio_pvoc(phase_vocoder);
  del_aubio_pitchdetection(pitchdet);
  aubio_cleanup();

  pitchdet = NULL;
}

  //jboolean Java_com_tabbeo_Detector_PitchDetector_PitchIsOnset(JNIEnv* env, jobject thiz, jfloatArray data)
  //{
  // jfloat* source_data = env->GetFloatArrayElements(data, JNI_FALSE);
  // bool isOnset = false;

  // ibuf.data = &source_data;

  // /* block loop */
  // aubio_pvoc_do(phase_vocoder, &ibuf, fftgrain);
  // aubio_onsetdetection(onset, fftgrain, onset_output);

  // isOnset = aubio_peakpick_pimrt(onset_output, peak_picker);
  
  // if(isOnset){
  //   smpl_t curLevel = aubio_level_detection(&ibuf, pitch_detector_conf.onset_silence);
  //   ////LOGV("Current level:");
  //   ////LOGV("%f", curLevel);
  //   // If it is below silence level, it returns 1
  //   if(curLevel == 1.)
  //     isOnset = 0;
  // }

  // env->ReleaseFloatArrayElements(data, source_data, 0);
  // return isOnset;

  //throw "OnSet is not supported yet. It will be investigated and implemented as a separate module";
  //return false;
  //}

}
