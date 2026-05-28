package com.tabbeo.unittests.Mocks;

import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.Music.IMetronomeTimeRef;
import com.tabbeo.Trainer.Trainer;

public class MockExercise extends Exercise{
    public IMetronomeTimeRef metronomeTimeRef;

    public MockExercise() {
        super("MockExercise", Type.ChordsRhythm, Trainer.Type.Interactive, null, 0, 5, 0, null);
    }
}

