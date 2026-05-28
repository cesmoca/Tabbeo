package com.tabbeo.detectorAnalyser.Analyser;

import android.app.Instrumentation;

import com.tabbeo.Detector.AudioSource.IAudioSource;

import com.tabbeo.Detector.AudioSource.MicAudioSource;
import com.tabbeo.Detector.ChordDetector;
import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.Music.Playable.Silence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChordAnalyser extends IAnalyser<int[]> {
    protected enum ConfigParam {
        BINS_PER_OCTAVE, MIN_FREQUENCY, FILTER_INERTIA, TUNNING_ENABLED,
        PEAK_WINDOWING_ENABLED, INSTANT_TUNNING_ESTIMATOR_INERTIA,
        SPARSE_CONSTANT_QKERNEL_THRESHOLD, CORRELATION_THRESHOLD,
        COMPONENT_THRESHOLD, INTENSITY_THRESHOLD
    }

    public interface Config{
        int MIN_BINS_PER_OCTAVE = 12;               int MAX_BINS_PER_OCTAVE = 12;            int BINS_PER_OCTAVE_STEP = 0;
        double MIN_MIN_FREQUENCY =99;              double MAX_MIN_FREQUENCY =99;          double MIN_FREQUENCY_STEP = 1;

        int MIN_TUNNING_ENABLED = 1;                int MAX_TUNNING_ENABLED = 1;
        double MIN_INSTANT_TUNNING_ESTIMATOR_INERTIA = 2;     double MAX_INSTANT_TUNNING_ESTIMATOR_INERTIA = 2;          double INSTANT_TUNNING_ESTIMATOR_INERTIA_STEP = 0.5;

        int MIN_PEAK_WINDOWING_ENABLED = 1;         int MAX_PEAK_WINDOWING_ENABLED = 1;

        double MIN_SPARSE_CONSTANT_QKERNEL_THERSHOLD = 0.0054;     double MAX_SPARSE_CONSTANT_QKERNEL_THERSHOLD = 0.0054;        double SPARSE_CONSTANT_QKERNEL_THERSHOLD_STEP = 0.01;

        double MIN_FILTER_INERTIA = 0.6;              double MAX_FILTER_INERTIA = 0.6;     double FILTER_INERTIA_STEP = 0.1;
        int MIN_CORRELATION_THERSHOLD = 105;     int MAX_CORRELATION_THRESHOLD = 105;        int CORRELATION_STEP = 5;
        double MIN_COMPONENT_THRESHOLD = 0.25;     double MAX_COMPONENT_THRESHOLD = 0.25;        double COMPONENT_THRESHOLD_STEP = 0.05;
        double MIN_INTENSITY_THRESHOLD = 20;     double MAX_INTENSITY_THRESHOLD = 20;        double INTENSITY_THRESHOLD_STEP = 10;

    }

    // Class to access the native methods
    private static class ChordDetectorAccessor extends ChordDetector {

        private ChordDetectorAccessor() { // Just an accessor to the static methods
            super(null);
        }

        public static void Accessor_ChordInit(int sample_rate, int bins_per_octave, double min_frequency, double filter_inertia, boolean tunning_enabled,
                                              boolean peak_windowing_enabled, double instant_tunning_estimator_inertia, double sparse_constant_qkernel_threshold,
                                              int correlation_threshold, double component_threshold, double intensity_threshold) {
            ChordInit(sample_rate, bins_per_octave, min_frequency, filter_inertia, tunning_enabled,
                    peak_windowing_enabled, instant_tunning_estimator_inertia, sparse_constant_qkernel_threshold, correlation_threshold, component_threshold, intensity_threshold);
        }

        public static int[] Accessor_ChordDetect(float[] data) { return ChordDetect(data); }
        public static int Accessor_ChordGetSamplesBufferSize(){ return ChordGetSamplesBufferSize(); }
        public static void Accessor_ChordDeinit(){ ChordDeinit(); }
    }

    public ChordAnalyser(Instrumentation instrumentation, Mode mode) {
        super(instrumentation, mode);
    }

    @Override
    protected int init(HashMap<Enum, Number> params) {
        int binsPerOctave = (int) params.get(ConfigParam.BINS_PER_OCTAVE);
        double minFrequency = (double) params.get(ConfigParam.MIN_FREQUENCY);
        double filter_inertia = (double) params.get(ConfigParam.FILTER_INERTIA);
        boolean tunning_enabled = ((int)params.get(ConfigParam.TUNNING_ENABLED)) == 1;
        boolean peak_windowing_enabled = ((int)params.get(ConfigParam.PEAK_WINDOWING_ENABLED)) == 1;
        double instant_tunning_estimator_inertia = (double) params.get(ConfigParam.INSTANT_TUNNING_ESTIMATOR_INERTIA);
        double sparse_constant_qkernel_threshold = (double) params.get(ConfigParam.SPARSE_CONSTANT_QKERNEL_THRESHOLD);
        int correlation_threshold = (int) params.get(ConfigParam.CORRELATION_THRESHOLD);
        double component_threshold = (double) params.get(ConfigParam.COMPONENT_THRESHOLD);
        double intensity_threshold = (double) params.get(ConfigParam.INTENSITY_THRESHOLD);


        ChordDetectorAccessor.Accessor_ChordInit(MicAudioSource.getSampleRate(), binsPerOctave, minFrequency, filter_inertia, tunning_enabled,
                peak_windowing_enabled, instant_tunning_estimator_inertia, sparse_constant_qkernel_threshold, correlation_threshold, component_threshold, intensity_threshold);

        return ChordDetectorAccessor.Accessor_ChordGetSamplesBufferSize();
    }

    @Override
    protected int[] detect(float[] audioData) {
        return ChordDetectorAccessor.Accessor_ChordDetect(audioData);
    }

    @Override
    protected void deinit() {
        ChordDetectorAccessor.Accessor_ChordDeinit();
    }

    protected Playable getPlayableFromDetectionResult(int[] detectionResult, Playable expectedPlayable) {
        if(detectionResult.length == 0) return Silence.SILENCE; // We did not detect anything
        if(expectedPlayable == null) return new Chord(detectionResult[0]); // If nothing is expected, then let's return the first one
        else{ // Let's find if the expected playable is amongst the proposed chord candidates
            Chord candidate = null;
            for(int encodedChordCandidate : detectionResult){
                candidate = new Chord(encodedChordCandidate);
                if(expectedPlayable.equals(candidate))
                    return candidate;
            }
            // It's a mistmatch, let's return whatever
            return candidate;
        }
    }

    @Override
    protected List<RangeUtils.NamedParamRange> getParamsRanges() {
        List<RangeUtils.NamedParamRange> paramsRanges = new ArrayList<>();

        paramsRanges.add(new RangeUtils.IntNamedParamRange(ConfigParam.BINS_PER_OCTAVE, Config.MIN_BINS_PER_OCTAVE, Config.MAX_BINS_PER_OCTAVE, Config.BINS_PER_OCTAVE_STEP));
        paramsRanges.add(new RangeUtils.DoubleNamedParamRange(ConfigParam.MIN_FREQUENCY, Config.MIN_MIN_FREQUENCY, Config.MAX_MIN_FREQUENCY, Config.MIN_FREQUENCY_STEP));

        paramsRanges.add(new RangeUtils.DoubleNamedParamRange(ConfigParam.FILTER_INERTIA, Config.MIN_FILTER_INERTIA, Config.MAX_FILTER_INERTIA, Config.FILTER_INERTIA_STEP));

        // These are boolean, but to make things easy, we keep them as integers with values 0 and 1
        paramsRanges.add(new RangeUtils.IntNamedParamRange(ConfigParam.TUNNING_ENABLED, Config.MIN_TUNNING_ENABLED, Config.MAX_TUNNING_ENABLED, 1));
        paramsRanges.add(new RangeUtils.IntNamedParamRange(ConfigParam.PEAK_WINDOWING_ENABLED, Config.MIN_PEAK_WINDOWING_ENABLED, Config.MAX_PEAK_WINDOWING_ENABLED, 1));

        paramsRanges.add(new RangeUtils.DoubleNamedParamRange(ConfigParam.INSTANT_TUNNING_ESTIMATOR_INERTIA, Config.MIN_INSTANT_TUNNING_ESTIMATOR_INERTIA, Config.MAX_INSTANT_TUNNING_ESTIMATOR_INERTIA, Config.INSTANT_TUNNING_ESTIMATOR_INERTIA_STEP));
        paramsRanges.add(new RangeUtils.DoubleNamedParamRange(ConfigParam.SPARSE_CONSTANT_QKERNEL_THRESHOLD, Config.MIN_SPARSE_CONSTANT_QKERNEL_THERSHOLD, Config.MAX_SPARSE_CONSTANT_QKERNEL_THERSHOLD, Config.SPARSE_CONSTANT_QKERNEL_THERSHOLD_STEP));
        paramsRanges.add(new RangeUtils.IntNamedParamRange(ConfigParam.CORRELATION_THRESHOLD, Config.MIN_CORRELATION_THERSHOLD, Config.MAX_CORRELATION_THRESHOLD, Config.CORRELATION_STEP));
        paramsRanges.add(new RangeUtils.DoubleNamedParamRange(ConfigParam.COMPONENT_THRESHOLD, Config.MIN_COMPONENT_THRESHOLD, Config.MAX_COMPONENT_THRESHOLD, Config.COMPONENT_THRESHOLD_STEP));
        paramsRanges.add(new RangeUtils.DoubleNamedParamRange(ConfigParam.INTENSITY_THRESHOLD, Config.MIN_INTENSITY_THRESHOLD, Config.MAX_INTENSITY_THRESHOLD, Config.INTENSITY_THRESHOLD_STEP));

        return paramsRanges;
    }

    @Override
    protected Enum[] getConfigParamEnum() {
        return ConfigParam.values();
    }

    @Override
    protected String getReportName() {
        return "ChordReport";
    }
}
