
package com.tabbeo.Detector;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.tabbeo.Activities.Exercises.IActivityExerciseFrame;
import com.tabbeo.Detector.AudioSource.IAudioSource;
import com.tabbeo.Detector.AudioSource.MicAudioSource;
import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.Trainer.ITrainer;
import com.tabbeo.Trainer.Trainer;

public class DetectorManager implements IDetectorManager, Runnable {
    private final static String LOG_TAG = "DetectorManager";

    protected ITrainer _trainer = null;
    protected static boolean _resourcesLoaded = false;
    protected static final Object _resourcesLock = new Object();

    protected IDetector _pitchDetector;
    protected IDetector _chordDetector;
    protected IActivityExerciseFrame _activityExercise;
    final protected IAudioSource _audioSource;
    protected Thread _detectorThread;

    protected IDetector _currentDetector;

    public static void loadResoucesInBackground() {
        AsyncTask<Void, Void, Void> loadingTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                loadResources();
                return null;
            }
        };

        loadingTask.execute();
    }

    public static void loadResources() {
        if (_resourcesLoaded) return;
        synchronized (_resourcesLock) {
            System.loadLibrary("Detector");

            PitchDetector.initNativeResources();
            ChordDetector.initNativeResources();

            _resourcesLoaded = true;
        }
    }

    public DetectorManager(ITrainer trainer, IActivityExerciseFrame activityExercise) {
        _trainer = trainer;
        _activityExercise = activityExercise;
        _pitchDetector = new PitchDetector();
        _chordDetector = new ChordDetector(trainer);

        _currentDetector = null;

        _audioSource = createAudioSource(_activityExercise);
    }

    // We do not want to release it, we keep it in memory for the next exercise_frame
    // We only use it in DetectorAnalyser, so that we initialise several times for testing purposes
    protected void release() {
        Log.d(LOG_TAG, "[Detector] Releasing FFT");
        stop(); // Make sure it is stopped

        PitchDetector.uninitNativeResources();
        ChordDetector.uninitNativeResources();

        _resourcesLoaded = false;
    }

    public void start() {
        if (_detectorThread != null) {
            try {
                _detectorThread.interrupt();
                _detectorThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException("We ourselves got interrupted while joining. ");
            }
        }
        _detectorThread = new Thread(this);
        _detectorThread.start();
    }

    @Override
    final public void stop() {
        if (Thread.currentThread().equals(_detectorThread))
            throw new RuntimeException("We are trying to stop ourselves. Do not call this API from the same thread");

        // If it is already stopped, that's ok :)
        if(_detectorThread != null){
            _detectorThread.interrupt();
        }
    }

    protected IAudioSource createAudioSource(Activity activityExercise) {
        return new MicAudioSource(activityExercise);
    }

    protected void setRightTypeOfDetector() {
        Playable expectedPlayable = _trainer.getExpectedPlayable();

        if (expectedPlayable instanceof Note) {
            if (_currentDetector != _pitchDetector) {
                startDetector(_pitchDetector);
            }
        } else if (expectedPlayable instanceof Chord) {
            if (_currentDetector != _chordDetector) {
                startDetector(_chordDetector);
            }
        } else {
            throw new RuntimeException("We received a null or unknown expectedPlayable: " + expectedPlayable);
        }
    }

    protected void startDetector(IDetector detector) {
        _currentDetector = detector;

        _audioSource.stop();
        try {
            _audioSource.start(_currentDetector.getBufferSize());
        } catch (InterruptedException e) {
            // We have been stopped before we could even initialize the audio source.
            // All right, let's stop...
        }
    }

    public void run() {

        float[] audioBuffer;
        Playable detectedPlayable;

        // We want it to at least execute one loop. This will help with the tests, that will guarantee they will have at least one loop executed :)
        do {
            setRightTypeOfDetector(); // We prepare the audio source and set the right type of detector, if needed to change

            audioBuffer = _audioSource.getLatest();

            if(audioBuffer == null) break; // We couldn't even initialize the AudioRecord, because we were stopped

            Object detectionResult = _currentDetector.detect(audioBuffer);
            detectedPlayable = _currentDetector.getPlayableFromDetectionResult(detectionResult);

            if (_trainer.onAnalysis(SystemClock.elapsedRealtime(), detectedPlayable) == ITrainer.ContinueDetecting.No)
                break;
        } while (!Thread.interrupted());

        _audioSource.stop();
        _currentDetector = null;

        Log.d(LOG_TAG, "[Detector] stopped.");
    }
}