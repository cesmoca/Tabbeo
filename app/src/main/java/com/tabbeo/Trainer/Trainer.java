package com.tabbeo.Trainer;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.tabbeo.Activities.Exercises.ActivityExerciseOnOffButton;
import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.Detector.DetectorManager;
import com.tabbeo.Detector.IDetectorManager;
import com.tabbeo.Music.IMetronomeTicker;
import com.tabbeo.Music.IMetronomeTimeRef;
import com.tabbeo.Music.MetronomeTicker;
import com.tabbeo.Music.MetronomeTimeRefInteractive;
import com.tabbeo.Music.MetronomeTimeRefTimed;
import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.Music.Track;
import com.tabbeo.Widgets.IOnOffButton;

public class Trainer implements ITrainer{
    // How late, according to the note's duration, can the note be played and still be good.
    // It was 0.75. We are rising to 1 because if it is pretty fast, the detection goes slow
    protected static final float MAX_RATIO_DELAY = 1.0f;

    public static int POINTS_HIT = 5;

    protected IDetectorManager _detector;
    protected ActivityExerciseOnOffButton _exerciseActivity;
    protected IOnOffButton _onOffButton;
    protected Exercise _exercise;
    protected IMetronomeTimeRef _metronomeTimeRef;
    protected IMetronomeTicker _ticker;

    protected int _currentlyDetectingPlayableIndex;
    protected boolean _playablePlayed = false;

    public Trainer(Type type, ActivityExerciseOnOffButton exerciseActivity, Exercise exercise, IOnOffButton onOffButton, int tempo) {
        _detector = new DetectorManager(this, exerciseActivity);
        _exerciseActivity = exerciseActivity;
        _onOffButton = onOffButton;
        _exercise = exercise;

        if(type == Type.Timed) { // We only start the ticker in timed mode
            _ticker = new MetronomeTicker();
        }

        _metronomeTimeRef =  createMetronomeTimeRef(type, exercise.getTrack(), tempo);
    }

    @CallSuper
    @Override
    public void startTraining() {
        _metronomeTimeRef.start(SystemClock.elapsedRealtime());

        if(_ticker != null) _ticker.start(_metronomeTimeRef.getMsPerBeat());

        _playablePlayed = false;
        _currentlyDetectingPlayableIndex = 0;

        _onOffButton.startPlayingAnim(_metronomeTimeRef);

        _detector.start();
    }

    @UiThread
    @Override
    public void stopTraining() {
        _onOffButton.stopPlayingAnim();

        if(_ticker != null) _ticker.stop();

        _metronomeTimeRef.stop();
        _detector.stop();
    }

    @Override
    final public void pauseDetecting(){
        _detector.stop();
    }

    @Override
    final public void resumeDetecting(){
        _detector.start(); // It is supposed to be initialized already, so it has de trainer already
    }

    protected IMetronomeTimeRef createMetronomeTimeRef(Type type, Track track, int tempo){
        switch(type){
            case Timed:
                return new MetronomeTimeRefTimed(track, tempo);
            case Interactive:
                return new MetronomeTimeRefInteractive(track, tempo);
        }

        throw new RuntimeException("Unknown trainer type: "+type);
    }

    @Override
    public IMetronomeTimeRef getMetronomeTimeRef() {
        return _metronomeTimeRef;
    }

    @NonNull
    @Override
    public Playable getExpectedPlayable() {
        // If we are still in the countdown, le't send the first one
        if( _currentlyDetectingPlayableIndex == -1)
            return _exercise.getTrack().getPlayable(0);

        // If already played, let's send the incoming one (if there is any, not end of the song)
        if(_playablePlayed && (_currentlyDetectingPlayableIndex + 1) < _exercise.getTrack().getNPlayables())
            return _exercise.getTrack().getPlayable(_currentlyDetectingPlayableIndex + 1);

        // Let's send the current one
        return _exercise.getTrack().getPlayable(_currentlyDetectingPlayableIndex);
    }

    protected static boolean tooLateToPlayRight(long expectedPlayableDuration, long elapsedTime) {
        double ratioDelay = elapsedTime / (double) expectedPlayableDuration;

        return ratioDelay > MAX_RATIO_DELAY;
    }

    protected boolean isPlayableDetected(long virtualTimestamp, Playable detectedPlayable) {
        // Has the correct note been played? We add the points :)
        long deltaTime = virtualTimestamp - _metronomeTimeRef.getPlayableStartTime(_currentlyDetectingPlayableIndex);

        return _exercise.getTrack().getPlayable( _currentlyDetectingPlayableIndex).equals(detectedPlayable) && !tooLateToPlayRight(_metronomeTimeRef.getCurrentPlayableDuration(), deltaTime);
    }

    @Override
    public ContinueDetecting onAnalysis(long realTimestamp, @NonNull  Playable detectedPlayable) {

        long virtualTimestamp = _metronomeTimeRef.getTimestamp(realTimestamp);

        _exerciseActivity.updateProgressTest(virtualTimestamp, detectedPlayable);

        if(virtualTimestamp < 0) { // We are still in the countdown
            return ContinueDetecting.Yes;
        }

        // If we did not make it ont time, let's move on and notify the activity of the miss
        if(_currentlyDetectingPlayableIndex != _metronomeTimeRef.getCurrentPlayableIndex()){
            if(!_playablePlayed){
                _exerciseActivity.updateProgress(false, 0);
            }

            _playablePlayed = false;
            _currentlyDetectingPlayableIndex = _metronomeTimeRef.getCurrentPlayableIndex();
        }

        // Is the end of training?
        if (_metronomeTimeRef.isEndOfExercise()) {
            if(!_playablePlayed){
                _exerciseActivity.updateProgress(false, 0);
            }

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    stopTraining();
                }
            });

            _exerciseActivity.endOfTrainingShowSummary();

            return ContinueDetecting.No;
        }

        // If the user hasn't played the playable already, we need to check
        if(!_playablePlayed) {
           boolean playableWell = isPlayableDetected(virtualTimestamp, detectedPlayable);

            if(playableWell){
                _playablePlayed = true;
                _exerciseActivity.updateProgress(true, POINTS_HIT);
                _metronomeTimeRef.advanceToNextPlayable(realTimestamp); // For the interactive, let's get to the next playable
            }
        }

        return ContinueDetecting.Yes;
    }
}
