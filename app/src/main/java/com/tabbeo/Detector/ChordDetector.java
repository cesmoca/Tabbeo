package com.tabbeo.Detector;

import android.support.annotation.Keep;

import com.tabbeo.Detector.AudioSource.IAudioSource;
import com.tabbeo.Detector.AudioSource.MicAudioSource;
import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.Music.Playable.Silence;
import com.tabbeo.Trainer.ITrainer;
import com.tabbeo.Trainer.Trainer;

public class ChordDetector implements IDetector<int[]> {
    protected static Config config;
    private ITrainer _trainer;

    protected static class Config{
        public int BINS_PER_OCTAVE; // Former 12. It has a lot of impact. The smaller, the fastest but less accurate
        public double MIN_FREQUENCY; // Former 96. It has a huge impact on the quality of detection
        public double FILTER_INERTIA; // Former 0.7.
        public boolean TUNNING_ENABLED; // Former true. This two booleans have some impact, not much
        public boolean PEAK_WINDOWING_ENABLED; // Former true
        public double INSTANT_TUNNING_ESTIMATOR_INERTIA; // Former 1.0. Only affects if TUNNING is enabled. Not much impact
        public double SPARSE_CONSTANT_QKERNEL_THRESHOLD; // Former 0.0054. Virtually no impact.

        // The new ChordCorrelator parameters ;)
        public int CORRELATION_THRESHOLD; // Range [0, 120] of ints. Minimun correlation for a chord to be considered as detecteed. (Recommended 80-110)
        public double COMPONENT_THRESHOLD; // Range [0.0, 1.0] of doubles. Minimun for a pitch component in the pcp to be considered (Recommended 0.1-0.25)
        public double INTENSITY_THRESHOLD; // Minimun intensity of sounds detected. To avoid background noise. (Recommended 80-120)
    }

    // We create the config statically
    static{
        // We do not take into account second candidate
        config = new Config();
        config.BINS_PER_OCTAVE = 12;
        config.MIN_FREQUENCY = 99;
        config.FILTER_INERTIA = 0.6;
        config.TUNNING_ENABLED = true;
        config.PEAK_WINDOWING_ENABLED = true;
        config.INSTANT_TUNNING_ESTIMATOR_INERTIA = 2.0;
        config.SPARSE_CONSTANT_QKERNEL_THRESHOLD = 0.0054;

        config.CORRELATION_THRESHOLD = 105;
        config.INTENSITY_THRESHOLD = 100;
        config.COMPONENT_THRESHOLD = 0.15;
    }

    // --- Native Interface ---
    // They have to be public in order to be found by the ndk
    public static native void ChordInit(int sample_rate, int bins_per_octave, double min_frequency, double filter_inertia, boolean tunning_enabled,
                                           boolean peak_windowing_enabled, double instant_tunning_estimator_inertia, double sparse_constant_qkernel_threshold,
                                        int correlation_threshold, double component_threshold, double intensity_threshold);
    public static native int ChordGetSamplesBufferSize();
    public static native int[] ChordDetect(float[] data);
    @Keep // So that minify does not strip it out because this is used only in test code
    public static native void ChordDeinit();
    @Keep // So that minify does not strip it out because this is used only in test code
    public static native int getNModes();

    protected static void initNativeResources() {
        Config currentConfig = config;

        ChordInit(MicAudioSource.getSampleRate(), currentConfig.BINS_PER_OCTAVE, currentConfig.MIN_FREQUENCY, currentConfig.FILTER_INERTIA,
                currentConfig.TUNNING_ENABLED, currentConfig.PEAK_WINDOWING_ENABLED, currentConfig.INSTANT_TUNNING_ESTIMATOR_INERTIA,
                currentConfig.SPARSE_CONSTANT_QKERNEL_THRESHOLD, currentConfig.CORRELATION_THRESHOLD, currentConfig.COMPONENT_THRESHOLD, currentConfig.INTENSITY_THRESHOLD);
    }

    protected static void uninitNativeResources(){
        ChordDeinit();
    }

    public ChordDetector(ITrainer trainer){
        _trainer = trainer;
    }

    @Override
    public int[] detect(float[] audioData) {
        return ChordDetect(audioData);
    }

    @Override
    public Playable getPlayableFromDetectionResult(int[] detectionResult) {
        if(detectionResult.length == 0) return Silence.SILENCE; // We did not detect anything

        Playable expectedPlayable = _trainer.getExpectedPlayable();

        Chord maxCorrelationChord = new Chord(detectionResult[0]);
        if(expectedPlayable == null){
            return maxCorrelationChord; // If nothing is expected, then let's return the one with max correlation
        }else{ // Let's find if the expected playable is amongst the proposed chord candidates
            for(int encodedChordCandidateIndex = 1; encodedChordCandidateIndex < detectionResult.length; ++encodedChordCandidateIndex){
                int encodedChordCandidate = detectionResult[encodedChordCandidateIndex];
                Chord candidate = new Chord(encodedChordCandidate);

                if(expectedPlayable.equals(candidate)) {
                    return candidate;
                }
            }
            // It's a mistmatch, let's return the max correlation one
            return maxCorrelationChord;
        }
    }

    @Override
    public int getBufferSize() {
        return ChordGetSamplesBufferSize();
    }
}
