package com.tabbeo.Widgets;

import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.Music.IMetronomeTimeRef;

public interface IExerciseWidget {
    void setAlpha(float alpha);
    void loadExercise();
    void init(Exercise exercise, IMetronomeTimeRef metronomeTimeRef);
}
