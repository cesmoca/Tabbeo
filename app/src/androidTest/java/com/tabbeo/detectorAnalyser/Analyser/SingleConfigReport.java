package com.tabbeo.detectorAnalyser.Analyser;

import android.support.annotation.NonNull;

import com.tabbeo.Music.Playable.Playable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SingleConfigReport<DetectionResultClass extends Playable> implements Comparable {

    public class Mismatch{
        long startTimestamp;
        long duration;
        Playable theoreticalDetection, detectedPlayable;

        public Mismatch(long startTimestamp, long duration, Playable theoreticalDetection, Playable detectedPlayable){
            this.startTimestamp = startTimestamp;
            this.duration = duration;
            this.theoreticalDetection = theoreticalDetection;
            this.detectedPlayable = detectedPlayable;
        }

        public boolean equals(Playable theoreticalDetection, Playable detectedPlayable){
            return this.theoreticalDetection.equals(theoreticalDetection)
                    && this.detectedPlayable.equals(detectedPlayable);
        }

        @Override
        public String toString(){
            return "["+startTimestamp+" - Duration: "+duration+"] "+detectedPlayable+" instead of "+theoreticalDetection;
        }
    }

    private DecimalFormat _dF;
    private LabeledWavAudio _wav;
    private String _config;

    // We do a general matching, if we are getting silences and playing notes right
    private int _totalDetectionResultsWithPlayableExpected = 0;
    private int _totalDetectionResultsWithSilenceExpected = 0;

    private List<LabeledWavAudio.Label> _detectedPlayables;
    private long _playableStartScore;

    private long _analysisDuration = 0;

    private List<Mismatch> _mismatches;
    private Mismatch _currentMismatch;

    public SingleConfigReport(LabeledWavAudio testWav, int sampleBufferSize, HashMap<Enum, Number> configParams, boolean postFilter) {
        _wav = testWav;
        _config = postFilter?"> Postfilter: " : "> Prefilter: ";
        _config += "BufferSize: " + sampleBufferSize +'\n';
        for (Map.Entry entry : configParams.entrySet()) {
            _config += entry.getKey() + ": " + entry.getValue()+"\n";
        }

        _mismatches = new ArrayList<>();

        _dF = new DecimalFormat();
        _dF.setMinimumFractionDigits(2);
        _dF.setMaximumFractionDigits(2);
        _dF.setMinimumIntegerDigits(2);

        _detectedPlayables = new ArrayList<>();


    }

    public List<Mismatch> getMismatches(){ return _mismatches; }
    public List<LabeledWavAudio.Label> getPlayableStartDetected(){ return _detectedPlayables; }

    public void setAnalysisDuration(long analysisDuration){ _analysisDuration = analysisDuration; }

    public void addDetectionResult(long timestamp, DetectionResultClass detectedPlayable) {
        storeNotesAndSilenceMatches(timestamp, detectedPlayable);
        storePlayableStartScore(timestamp, detectedPlayable);
    }

    // We only take into account of accurate and fast it can detect the playable start
    protected void storePlayableStartScore(long timestamp, Playable detectedPlayable){
        LabeledWavAudio.Label currentLabel = _wav.getLabel(timestamp);

        if(currentLabel.playable == null) return; // It is a silence, not biggie

        //If detected already, skipping
        if(_detectedPlayables.contains(currentLabel)) return;

        if(currentLabel.playable.equals(detectedPlayable)){
            _detectedPlayables.add(currentLabel);
            float score = (float)(timestamp - currentLabel.startTimestamp);
            _playableStartScore += score;
        }
    }

    protected void storeNotesAndSilenceMatches(long timeStamp, DetectionResultClass detectedPlayable) {
        Playable theoreticalPlayable = _wav.getLabeledPlayable(timeStamp);
        if (detectedPlayable.equals(_wav.getLabeledPlayable(timeStamp))) {
            // Let's close the current mismatch, if any
            if (_currentMismatch != null) {
                _mismatches.add(_currentMismatch);
                _currentMismatch = null;
            }
        } else {
            if (_currentMismatch == null){
                // New mismatch! Let's start it
                _currentMismatch = new Mismatch(timeStamp, (long) _wav.getMsPerBuffer(), theoreticalPlayable, detectedPlayable);
            }else{
                if (_currentMismatch.equals(theoreticalPlayable, detectedPlayable)) {
                    // If it is the same mistmatch, we extend it
                    _currentMismatch.duration += _wav.getMsPerBuffer();
                }else {
                    // Otherwise, we close it and start a new one
                    _mismatches.add(_currentMismatch);
                    _currentMismatch = new Mismatch(timeStamp,(long) _wav.getMsPerBuffer(), theoreticalPlayable, detectedPlayable);
                }
            }
        }

        if(theoreticalPlayable == null){ // Supposed to detect silence
            _totalDetectionResultsWithSilenceExpected++;
        }else { // Supposed to detect notes
            _totalDetectionResultsWithPlayableExpected++;
        }
    }

    // Sort orders: PlayableStart - How fast it is detected - SilenceRatio - PlayableRatio
    public int compareToPlayableStart(Object another){
        SingleConfigReport anotherReport = (SingleConfigReport) another;
        float diff = calculateMatchRatioPlayableStartMatches() - anotherReport.calculateMatchRatioPlayableStartMatches();

        if (diff < 0) return -1;
        else if (diff > 0) return 1;
        else {
            diff = anotherReport._playableStartScore - _playableStartScore; // This score is inversely proportional

            if (diff < 0) return -1;
            else if (diff > 0) return 1;
            else {
                diff = calculateMatchRatioSilence() - ((SingleConfigReport) another).calculateMatchRatioSilence();

                if (diff < 0) return -1;
                else if (diff > 0) return 1;
                else {
                    diff = calculateMatchRatioPlayables() - ((SingleConfigReport) another).calculateMatchRatioPlayables();

                    if (diff < 0) return -1;
                    else if (diff > 0) return 1;
                    else return 0;
                }
            }
        }
    }


    public float calculateMatchRatioPlayableStartMatches(){
        return _detectedPlayables.size()*100/(float)_wav.getNPlayables();
    }

    public float calculateMatchRatioPlayables(){
        int nPlayableMistmaches = 0;
        for(Mismatch mismatch : _mismatches){
            if(mismatch.theoreticalDetection != null) nPlayableMistmaches++;
        }

        return 100*(1- nPlayableMistmaches/(float)_totalDetectionResultsWithPlayableExpected);
    }

    public float calculateMatchRatioSilence(){
        int nSilenceMistmaches = 0;
        for(Mismatch mismatch : _mismatches){
            if(mismatch.theoreticalDetection == null) nSilenceMistmaches++;
        }

        return 100 * (1 - nSilenceMistmaches/(float)_totalDetectionResultsWithSilenceExpected);
    }

    @Override
    public int compareTo(@NonNull Object another) {
        return compareToPlayableStart(another);
    }

    @Override
    public String toString() {
        String strAnalysisDuration =  String.format(Locale.US, "%d'%02d''",
                TimeUnit.MILLISECONDS.toMinutes(_analysisDuration),
                TimeUnit.MILLISECONDS.toSeconds(_analysisDuration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(_analysisDuration)));

        return "Duration: "+strAnalysisDuration+"\n "+_dF.format(calculateMatchRatioPlayableStartMatches())+"% (" + _dF.format(_playableStartScore/(float)_wav.getNPlayables())+" delay avg)\n"+_dF.format(calculateMatchRatioPlayables())+" playable ratio - "+_dF.format(calculateMatchRatioSilence())+" silence ratio\n" + _config;
    }
}


