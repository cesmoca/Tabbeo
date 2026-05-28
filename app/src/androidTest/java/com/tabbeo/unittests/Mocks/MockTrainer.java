package com.tabbeo.unittests.Mocks;

import android.support.annotation.NonNull;

import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.Music.IMetronomeTimeRef;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.Trainer.ITrainer;
import com.tabbeo.Music.Playable.Playable;

public class MockTrainer implements ITrainer {
    public ITrainer.ContinueDetecting continueDetecting = ITrainer.ContinueDetecting.Yes;
    public Playable detectedPlayable;
    public Playable expectedPlayable = Note.A4; //This can't be null in production code, so setting a default value

    private final Object _lock = new Object();

    public MockTrainer() {}

    @Override
    public void startTraining() { }

    public void stopTraining() { }

    @NonNull
    @Override
    public Playable getExpectedPlayable() {
        return expectedPlayable;
    }

    @Override
    public ITrainer.ContinueDetecting onAnalysis(long realTimestamp, @NonNull Playable detectedPlayable) {
        synchronized (_lock) {
            this.detectedPlayable = detectedPlayable;

            return continueDetecting;
        }
    }

    @Override
    public IMetronomeTimeRef getMetronomeTimeRef() {
        return null;
    }

    @Override
    public void pauseDetecting() {

    }

    @Override
    public void resumeDetecting() {

    }

    public Playable getDetectedPlayable() {
        synchronized (_lock) {
            if (detectedPlayable != null) return detectedPlayable;
        }
        return null;
    }
}
