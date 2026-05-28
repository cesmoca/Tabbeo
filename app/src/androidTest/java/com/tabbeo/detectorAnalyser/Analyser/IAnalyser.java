package com.tabbeo.detectorAnalyser.Analyser;

import android.app.Instrumentation;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.tabbeo.Detector.PitchFilter;
import com.tabbeo.Music.Playable.Playable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class IAnalyser<DetectionResultClass> {
    private static final String LOG_TAG="IAnalyser";

    public enum Mode{PRE_FILTER, POST_FILTER, PRE_AND_POST_FILTER }

    private Instrumentation _instrumentation;
    private Mode _mode;
    private PitchFilter _filter;

    // Abstract interface
    // Returns the buffer size
    protected abstract int init(HashMap<Enum, Number> params);
    protected abstract DetectionResultClass detect(float[] audioData);
    protected abstract void deinit();
    protected abstract Playable getPlayableFromDetectionResult(DetectionResultClass detectionResult, Playable expectedPlayable);
    protected abstract Enum[] getConfigParamEnum();
    protected abstract String getReportName();
    protected abstract List<RangeUtils.NamedParamRange> getParamsRanges();

    public IAnalyser(Instrumentation instrumentation, Mode mode){
        _instrumentation = instrumentation;
        _mode = mode;

        if(_mode == Mode.POST_FILTER || _mode == Mode.PRE_AND_POST_FILTER) {
            _filter = new PitchFilter();
        }
    }

    final public void analyzeWavs(LabeledWavAudio[] testWavs) throws IOException {
        List<RangeUtils.NamedParamRange> paramsRanges = getParamsRanges();

        FileOutputStream reportFile = _instrumentation.getTargetContext().openFileOutput(getReportName()+".txt", Context.MODE_PRIVATE);

        if(paramsRanges.size() != getConfigParamEnum().length)
            throw new AssertionError("This pitch init needs "+ getConfigParamEnum().length+" parameters, not "+paramsRanges.size());

        // How many configs do we have to test
        int nTotalConfigs = testWavs.length;
        for(RangeUtils.NamedParamRange configValues : paramsRanges){
            if(configValues.size() == 0) throw new AssertionError("There should be at least one divisor for this param range: "+configValues.getName());
            nTotalConfigs *= configValues.size();
        }

        ProgressTracker progressTracker = new ProgressTracker(nTotalConfigs, SystemClock.elapsedRealtime());

        for(LabeledWavAudio wav : testWavs){
            wav.load(_instrumentation.getContext().getResources(), false /*verbose*/);
            HashMap<Enum,Number> configValues = new HashMap<>();

            ArrayList<SingleConfigReport>  wavReport = new ArrayList<>();

            analyseWavAllConfigsRecursive(wav, wavReport, progressTracker, reportFile, configValues, paramsRanges, 0);

            writeToReport(wav, wavReport, reportFile);
        }

        Log.d(LOG_TAG, "Writing "+getReportName()+" to "+_instrumentation.getTargetContext().getFilesDir());
        reportFile.close();
    }

    final private void analyseWavAllConfigsRecursive(LabeledWavAudio wav, List<SingleConfigReport> wavReport, ProgressTracker progressTracker, FileOutputStream reportFile, HashMap<Enum, Number> configParams, List<RangeUtils.NamedParamRange> paramsRanges, int depth) throws IOException {

        RangeUtils.NamedParamRange paramRanges = paramsRanges.get(depth);
        for(Object o : paramRanges){
            Number param = (Number) o;
            configParams.put(paramsRanges.get(depth).getName(), param);

            if(depth == paramsRanges.size()-1){
                // Base case
                analyseWavSingleConfig(wav, wavReport, progressTracker, reportFile, configParams);
            }else{
                // Recursive case
                analyseWavAllConfigsRecursive(wav, wavReport, progressTracker,  reportFile, configParams, paramsRanges, depth+1);
            }
        }
    }

    final private void analyseWavSingleConfig(LabeledWavAudio wav, List<SingleConfigReport> wavReport, ProgressTracker progressTracker, FileOutputStream reportFile, HashMap<Enum, Number> configParams) throws IOException {

        int bufferSize = init(configParams);

        SingleConfigReport preFilterSingleConfigReport = null;
        SingleConfigReport postFilterSingleConfigReport = null;

        if(_mode == Mode.PRE_FILTER || _mode == Mode.PRE_AND_POST_FILTER ) {
            preFilterSingleConfigReport = new SingleConfigReport(wav, bufferSize, configParams, false /*postfilter*/);
        }

        if(_mode == Mode.POST_FILTER || _mode == Mode.PRE_AND_POST_FILTER ) {
            postFilterSingleConfigReport = new SingleConfigReport(wav, bufferSize, configParams, true /*postfilter*/);
        }


        wav.rewind(bufferSize);

        float[] audioData = new float[bufferSize];
        DetectionResultClass detectionResult;

        long startTime = SystemClock.elapsedRealtime();
        while(true) {
            if(!wav.getLatest(audioData)){
                break;
            }

            detectionResult = detect(audioData);

            Playable detectedPlayable = getPlayableFromDetectionResult(detectionResult, wav.getLabeledPlayable(wav.getElapsedTime()));

            if(preFilterSingleConfigReport != null) preFilterSingleConfigReport.addDetectionResult(wav.getElapsedTime(), detectedPlayable);

            if(postFilterSingleConfigReport != null ) {
                Playable postFilterdetectionResult = _filter.filter(detectedPlayable);
                postFilterSingleConfigReport.addDetectionResult(wav.getElapsedTime(), postFilterdetectionResult);
            }
        }

        long analysisDuration = SystemClock.elapsedRealtime() - startTime;

        if(preFilterSingleConfigReport != null)    preFilterSingleConfigReport.setAnalysisDuration(analysisDuration);
        if(postFilterSingleConfigReport != null)   postFilterSingleConfigReport.setAnalysisDuration(analysisDuration);

        deinit();

        // If they get a 0% score on notes, do not even add it to the report
        if(preFilterSingleConfigReport != null){
            //if(preFilterSingleConfigReport.calculateMatchRatioPlayableStartMatches() > 30)
                wavReport.add(preFilterSingleConfigReport);
        }
        if(postFilterSingleConfigReport != null) {
            //if(postFilterSingleConfigReport.calculateMatchRatioPlayableStartMatches() > 30)
                wavReport.add(postFilterSingleConfigReport);
        }


        progressTracker.addTask();
        progressTracker.printProgressDelayed();
    }

    private void writeToReport(LabeledWavAudio wav, List<SingleConfigReport> wavReport, FileOutputStream reportFile) throws IOException {
        Collections.sort(wavReport);
        writeLine(reportFile, "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n--------------------------------------------------------------------");
        writeLine(reportFile, "---- Report "+getReportName()+" for audio file "+wav+" ----");

        for(SingleConfigReport report : wavReport) {
            writeLine(reportFile, report.toString());

            long minBlink = Integer.MAX_VALUE;
            long maxBlink = Integer.MIN_VALUE;
            long accumBlink = 0;
            int nBlinks = 0;

            List<SingleConfigReport.Mismatch> mismatches = report.getMismatches();
            for(SingleConfigReport.Mismatch mismatch : mismatches){
                //if(mismatch.detectedPlayable != null) {
                //if(mismatch.duration > 185){
                    //writeLine(reportFile, mismatch.toString());

                    if(mismatch.duration < minBlink) minBlink = mismatch.duration;
                    if(mismatch.duration > maxBlink) maxBlink = mismatch.duration;
                    accumBlink += mismatch.duration;
                    nBlinks++;
                //}
            }

            for(LabeledWavAudio.Label label :wav.getLabels()){
                if(label.playable == null) continue; // Silences do not count.. There is so much to improve in this class, and in the architecture in general regarding Silences and Playables
                //writeLine(reportFile, ((report.getPlayableStartDetected().contains(label))?"Detected": "NOT Detected") + " "+label);
            }

            //writeLine(reportFile, "Min: "+minBlink+" Max "+maxBlink+" Avg "+(accumBlink/(float)nBlinks));
        }
    }

    private static void writeLine(FileOutputStream reportFile, String str) throws IOException {
        reportFile.write((str+'\n').getBytes());
    }
}
