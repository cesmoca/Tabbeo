package com.tabbeo.Detector;

import android.support.annotation.Keep;

import com.tabbeo.Detector.AudioSource.IAudioSource;
import com.tabbeo.Detector.AudioSource.MicAudioSource;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.Music.Playable.Silence;

public class PitchDetector implements  IDetector<Float> {
    protected static Config config;
    protected PitchFilter _filter = new PitchFilter();

    // DO NOT MODIFY Copied from aubio pitchdetection.h
    public enum DetectionType {
        aubio_pitch_yin,     /**< YIN algorithm */
        aubio_pitch_mcomb,   /**< Multi-comb filter */
        aubio_pitch_schmitt, /**< Schmitt trigger */
        aubio_pitch_fcomb,   /**< Fast comb filter */
        aubio_pitch_yinfft   /**< Spectral YIN */
    }

    protected static class Config {
        public int BUFFER_SIZE;
        public int OVERLAP_SIZE_DIVISOR;
        public DetectionType DETECTION_TYPE;
        public double YINFFT_THRESHOLD;
    }

    // We create the configs statically
    static{
        config = new Config();
        config.BUFFER_SIZE = 1200; // Formerly 2048
        config.OVERLAP_SIZE_DIVISOR = 10; // Formerly 2
        config.DETECTION_TYPE = DetectionType.aubio_pitch_yinfft;
        config.YINFFT_THRESHOLD = 0.6;
    }

    // --- Native interface ---
    // They have to be public in order to be found by the ndk
    public static native void PitchInit(int sample_rate, int buffer_size, int overlap_size, int detectionType, double yin_threshold);
    public static native double PitchDetect(float[] data);
    @Keep // So that minify does not strip it out because this is used only in test code
    public static native void PitchDeinit();


    // --------- OnsetStuff --------
    private static final double PITCH_ONSET_THRESHOLD = 0.8; // default= 0.3 / The bigger, the pickier / Used by the ndk
    private static final double PITCH_ONSET_SILENCE = 10.0; // default= -0.9 / The bigger, the pickier / Used by the ndk
    //public static native boolean PitchIsOnset(float[] data); // Commented in the ndk too
    //Aubio Specific Algorithms and settings
    //private String type_onset = "kl"
    //private String type_onset2 = "complex";


    protected static void initNativeResources() {
        int overlapSize = config.BUFFER_SIZE / config.OVERLAP_SIZE_DIVISOR;
        PitchInit(MicAudioSource.getSampleRate(), config.BUFFER_SIZE, overlapSize, config.DETECTION_TYPE.ordinal(), config.YINFFT_THRESHOLD);
    }

    protected static void uninitNativeResources(){
        PitchDeinit();
    }

    @Override
    public Float detect(float[] audioData) {
        return (float) PitchDetect(audioData);
    }

    @Override
    public Playable getPlayableFromDetectionResult(Float detectionResult) {
        Playable detectedPlayable;
        if(detectionResult == 0) detectedPlayable = Silence.SILENCE; // Nothing detected
        else detectedPlayable = new Note(detectionResult);

        return _filter.filter(detectedPlayable);

    }

    @Override
    public int getBufferSize() {
        return config.BUFFER_SIZE;
    }
}
