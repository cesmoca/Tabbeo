package com.tabbeo.detectorAnalyser.Analyser;

import android.os.SystemClock;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ProgressTracker{
    private final static String LOG_TAG = "ProgressTracker";
    private final static int INTERVAL = 2000; /*ms*/
    private int _totalTasks;
    private int _tasksDone;
    private long _startTime;
    private long _lastTimePrinted = 0;
    private DecimalFormat _dF;

    public ProgressTracker(int totalTasks, long startTime) {
        _totalTasks = totalTasks;
        _tasksDone = 0;
        _startTime = startTime;
        _dF = new DecimalFormat();
        _dF.setMinimumFractionDigits(2);
        _dF.setMaximumFractionDigits(2);
        _dF.setMinimumIntegerDigits(2);
    }

    public void addTask() {
        _tasksDone++;
    }

    public void printProgressDelayed() {
        long now = SystemClock.elapsedRealtime();
        long elapsedTimeSinceLastPrinted = now - _lastTimePrinted;
        if(elapsedTimeSinceLastPrinted < INTERVAL) return;

        // Percentage completed statistics
        long elapsedTime = (SystemClock.elapsedRealtime() - _startTime);
        float percentageCompleted = (_tasksDone / (float) _totalTasks) * 100;

        // Approximation of remaining time
        long remainingTime = (long)(100 * elapsedTime / percentageCompleted - elapsedTime);

        String strElapsedTime =  String.format(Locale.US, "%d'%02d''",
                TimeUnit.MILLISECONDS.toMinutes(elapsedTime),
                TimeUnit.MILLISECONDS.toSeconds(elapsedTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedTime))
        );

        String strRemainingTime =  String.format(Locale.US, "%d'%02d''",
                TimeUnit.MILLISECONDS.toMinutes(remainingTime),
                TimeUnit.MILLISECONDS.toSeconds(remainingTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime))
        );

        Log.d(LOG_TAG, "[" + _dF.format(percentageCompleted) + "% - " + strElapsedTime + " - Remaining: " + strRemainingTime + "]");

        _lastTimePrinted = now;
    }
}
