package com.tabbeo.unittests.Mocks;

import com.tabbeo.Activities.Exercises.ActivityExerciseOnOffButton;
import com.tabbeo.Music.Playable.Playable;

public class MockActivityExercise extends ActivityExerciseOnOffButton {
    public boolean updateProgressCalled;
    public boolean playedWell;
    public int points;

    public boolean trainerLoadedCalled;
    public boolean endOfTrainingCalled;
    public boolean showNextPlayableCalled;

    public boolean updateProgressTestCalled = false;

    public MockActivityExercise(){
        reset();
    }

    public void reset(){
        updateProgressCalled = false;
        playedWell = false;
        points = 0;
        trainerLoadedCalled = false;
        endOfTrainingCalled = false;
        showNextPlayableCalled = false;
        updateProgressTestCalled = false;
    }

    @Override
    public void updateProgress(boolean playedWell, int points) {
        updateProgressCalled = true;
        this.playedWell = playedWell;
        this.points = points;
    }

    @Override
    public void endOfTrainingShowSummary() {
        endOfTrainingCalled = true;
    }

    @Override
    public void updateProgressTest(final long virtualTimestamp, final Playable detectedPlayable) {
        updateProgressTestCalled = true;
    }
}
