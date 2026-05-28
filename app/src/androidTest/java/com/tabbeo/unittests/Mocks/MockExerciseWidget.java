package com.tabbeo.unittests.Mocks;

import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.Music.IMetronomeTimeRef;
import com.tabbeo.Widgets.IExerciseWidget;

public class MockExerciseWidget implements IExerciseWidget {
    public boolean hasCalledReset;

    public MockExerciseWidget(){
        reset();
    }

    @Override
    public void setAlpha(float alpha) {

    }

    @Override
    public void loadExercise() {

    }

    @Override
    public void init(Exercise exercise, IMetronomeTimeRef metronomeTimeRef) {

    }

    public void reset() {
        hasCalledReset = false;
    }

}
