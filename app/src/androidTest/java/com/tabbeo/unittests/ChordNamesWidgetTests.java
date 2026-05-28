package com.tabbeo.unittests;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.AttributeSet;

import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.ExerciseLibraryTest;
import com.tabbeo.Widgets.ChordNamesWidget;


class ChordNamesWidgetTest extends ChordNamesWidget{
    public ChordNamesWidgetTest(Context context, AttributeSet attr) {
        super(context, attr);
    }
}

public class ChordNamesWidgetTests extends InstrumentationTestCase {
    private ChordNamesWidgetTest _chordNamesWidget;
    private Exercise _exercise = ExerciseLibraryTest.twoChordsExerciseTimed;
    private IMetronomeTimeRefTest _metronomeTimeRefTest;

    @Override
    protected void setUp(){
        _metronomeTimeRefTest = new IMetronomeTimeRefTest(_exercise.getTrack(), 50);

        _chordNamesWidget = new ChordNamesWidgetTest(getInstrumentation().getTargetContext(), null);
        _chordNamesWidget.init(_exercise, _metronomeTimeRefTest);
        _chordNamesWidget.layout(0, 0, 150, 150);
    }
}