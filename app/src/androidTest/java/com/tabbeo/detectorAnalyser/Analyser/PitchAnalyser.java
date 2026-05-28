package com.tabbeo.detectorAnalyser.Analyser;

import android.app.Instrumentation;

import com.tabbeo.Detector.AudioSource.IAudioSource;
import com.tabbeo.Detector.AudioSource.MicAudioSource;
import com.tabbeo.Detector.PitchDetector;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.Music.Playable.Silence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PitchAnalyser extends IAnalyser<Double> {
    private enum ParamNames {BUFFER_SIZE, OVERLAP_SIZE_DIVISOR, YIN_THRESHOLD}

    public interface Config{
        int MIN_BUFFER_SIZE = 1200;                          int MAX_BUFFER_SIZE = 1200;                   int BUFFER_SIZE_STEP = 500;
        int MIN_OVERLAP_SIZE_DIVISOR = 10;        int MAX_OVERLAP_SIZE_DIVISOR = 10; int OVERLAP_SIZE_STEP = 1;
        double MIN_YINFFT_THRESHOLD = 0.6;             double MAX_YINFFT_THRESHOLD = 0.6;      double YINFFT_THRESHOLD_STEP = 0.025f;
        //double MIN_YIN_THRESHOLD = 0.15f;       double MAX_YIN_THRESHOLD = 0.15f;                 double YIN_THRESHOLD_STEP = 0.05f;
    }

    // Class to access the native methods
    private static class PitchDetectorAccessor extends PitchDetector{
        public static void Accessor_PitchInit(int sample_rate, int buffer_size, int overlap_size, int detectionType, double yinfft_threshold) {
            PitchInit(sample_rate, buffer_size, overlap_size, detectionType, yinfft_threshold);
        }

        public static double Accessor_PitchDetect(float[] data) { return PitchDetect(data); }
        public static void Accessor_PitchDeinit(){ PitchDeinit(); }
    }

    private PitchDetector.DetectionType _detectionType;

    public PitchAnalyser(Instrumentation instrumentation, Mode mode, PitchDetector.DetectionType detectionType) {
        super(instrumentation, mode);
        _detectionType = detectionType;
    }

    @Override
    protected int init(HashMap<Enum, Number> params) {
        int bufferSize = (int) params.get(ParamNames.BUFFER_SIZE);
        int overlapSizeDivisor = (int) params.get(ParamNames.OVERLAP_SIZE_DIVISOR);
        double yinThreshold = (double) params.get(ParamNames.YIN_THRESHOLD);

        PitchDetectorAccessor.Accessor_PitchInit(MicAudioSource.getSampleRate(), bufferSize, bufferSize/overlapSizeDivisor, _detectionType.ordinal(), yinThreshold);

        return bufferSize;
    }

    @Override
    protected Double detect(float[] audioData) {
        return  PitchDetectorAccessor.Accessor_PitchDetect(audioData);
    }

    @Override
    protected void deinit() {
        PitchDetectorAccessor.Accessor_PitchDeinit();
    }

    @Override
    protected Playable getPlayableFromDetectionResult(Double detectionResult, Playable expectedPlayable) {
        if(detectionResult == 0) return Silence.SILENCE;
        else return new Note(detectionResult);
    }

    @Override
    protected List<RangeUtils.NamedParamRange> getParamsRanges() {
        // The parameters will be different dependidng on the detection type
        List<RangeUtils.NamedParamRange> paramsRanges = new ArrayList<>();

        paramsRanges.add(new RangeUtils.IntNamedParamRange(ParamNames.BUFFER_SIZE, Config.MIN_BUFFER_SIZE, Config.MAX_BUFFER_SIZE, Config.BUFFER_SIZE_STEP));
        paramsRanges.add(new RangeUtils.IntNamedParamRange(ParamNames.OVERLAP_SIZE_DIVISOR, Config.MIN_OVERLAP_SIZE_DIVISOR, Config.MAX_OVERLAP_SIZE_DIVISOR, Config.OVERLAP_SIZE_STEP));

        if(_detectionType == PitchDetector.DetectionType.aubio_pitch_yin) {
            //paramsRanges.add(new RangeUtils.DoubleNamedParamRange(ParamNames.YIN_THRESHOLD, Config.MIN_YIN_THRESHOLD, Config.MAX_YIN_THRESHOLD, Config.YIN_THRESHOLD_STEP));
            throw new RuntimeException("We have discarded YIN method. Do not even bother.");
        }else if(_detectionType == PitchDetector.DetectionType.aubio_pitch_yinfft){
            paramsRanges.add(new RangeUtils.DoubleNamedParamRange(ParamNames.YIN_THRESHOLD, Config.MIN_YINFFT_THRESHOLD, Config.MAX_YINFFT_THRESHOLD, Config.YINFFT_THRESHOLD_STEP));
        }else if (_detectionType == PitchDetector.DetectionType.aubio_pitch_fcomb || _detectionType == PitchDetector.DetectionType.aubio_pitch_mcomb){
            throw new RuntimeException("We have discarded methods FComb and MComb.");
        }else{
            paramsRanges.add(new RangeUtils.DoubleNamedParamRange(ParamNames.YIN_THRESHOLD, 0.0, 0.0, 1.0));// This will just be ignored for these detection methods
        }

        return paramsRanges;
    }

    @Override
    protected Enum[] getConfigParamEnum() {
        return ParamNames.values();
    }

    @Override
    protected String getReportName() {
        return "PitchReport_"+_detectionType;
    }
}
