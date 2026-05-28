package com.tabbeo.unittests;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.AttributeSet;

import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.ExerciseLibraryTest;
import com.tabbeo.Widgets.ChordStrokesStreamWidget;

class ChordStrokesStreamWidgetTest extends ChordStrokesStreamWidget {
    public ChordStrokesStreamWidgetTest(Context context, AttributeSet attr) {
        super(context, attr);
    }
}

public class ChordStrokesStreamWidgetTests extends InstrumentationTestCase {
    private ChordStrokesStreamWidgetTest _chordStrokesStreamWidget;
    private Exercise _exercise = ExerciseLibraryTest.twoChordsExerciseTimed;
    private IMetronomeTimeRefTest _metronomeTimeRefTest;

    @Override
    protected void setUp(){
        _metronomeTimeRefTest = new IMetronomeTimeRefTest(_exercise.getTrack(), 50);

        _chordStrokesStreamWidget = new ChordStrokesStreamWidgetTest(getInstrumentation().getContext(), null);
        _chordStrokesStreamWidget.init(_exercise, _metronomeTimeRefTest);
        _chordStrokesStreamWidget.layout(0, 0, 150, 150);
    }
}