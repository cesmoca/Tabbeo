package com.tabbeo.unittests;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.AttributeSet;

import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.CourseLibrary.ExercisesLibrary;
import com.tabbeo.ExerciseLibraryTest;
import com.tabbeo.Widgets.ExerciseWidget;
import com.tabbeo.Widgets.ICanvas;
import com.tabbeo.Widgets.Sprites.ISprite;
import com.tabbeo.unittests.Mocks.MockCanvas;

class ExerciseWidgetTest extends ExerciseWidget{
    public boolean callDrawBackground;
    public boolean callDrawForeground;
    public boolean callOnPlayableChanged;

    public ExerciseWidgetTest(Context context, AttributeSet attrs) {
        super(context, attrs);
        reset();
    }

    @Override
    protected void drawBackground(ICanvas canvas) {
        callDrawBackground = true;
    }

    @Override
    protected void drawForeground(ICanvas canvas, long virtualTimestamp) {
        callDrawForeground = true;
    }

    @Override
    public void setAlpha(float alpha) {

    }

    @Override
    public void loadWidgetExercise() {

    }

    public void accessor_draw(ICanvas canvas, long realTimestamp){
        draw(canvas, realTimestamp);
    }

    public void reset() {
        callDrawBackground = false;
        callDrawForeground = false;
        callOnPlayableChanged = false;
    }

    public long accessor_getAnticipationDuration(int playableIndex){
        return getAnticipationDuration(playableIndex);
    }

    }
public class ExerciseWidgetTests extends InstrumentationTestCase {
    private ExerciseWidgetTest _exerciseWidgetTest;
    private MetronomeTimeRefTimedTest _metronomeTimeRefTimed;
    private MockCanvas _mockCanvas;
    private Exercise _exercise = ExerciseLibraryTest.twoChordsExerciseInteractive;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _metronomeTimeRefTimed = new MetronomeTimeRefTimedTest(_exercise.getTrack(), 50 /*tempo*/);
        _mockCanvas = new MockCanvas();
        _exerciseWidgetTest = new ExerciseWidgetTest(getInstrumentation().getContext(), null);
        _exerciseWidgetTest.init(_exercise, _metronomeTimeRefTimed);
    }

    @SmallTest
    public void testISprite_Interpolate() {

        float val;

        // Ascending interpolation
        val = ISprite.interpolate(1, 5, 5, 7, 0);
        assertEquals(1.0f, val);
        val = ISprite.interpolate(1, 5, 5, 7, 4);
        assertEquals(1.0f, val);
        val = ISprite.interpolate(1, 5, 5, 7, 5);
        assertEquals(1.0f, val);
        val = ISprite.interpolate(1, 5, 5, 7, 6);
        assertEquals(3.0f, val);
        val = ISprite.interpolate(1, 5, 5, 7, 7);
        assertEquals(5.0f, val);
        val = ISprite.interpolate(1, 5, 5, 7, 9);
        assertEquals(5.0f, val);

        // Descending interpolation
        val = ISprite.interpolate(5, 1, 5, 7, 0);
        assertEquals(5.0f, val);
        val = ISprite.interpolate(5, 1, 5, 7, 4);
        assertEquals(5.0f, val);
        val = ISprite.interpolate(5, 1, 5, 7, 5);
        assertEquals(5.0f, val);
        val = ISprite.interpolate(5, 1, 5, 7, 6);
        assertEquals(3.0f, val);
        val = ISprite.interpolate(5, 1, 5, 7, 7);
        assertEquals(1.0f, val);
        val = ISprite.interpolate(5, 1, 5, 7, 9);
        assertEquals(1.0f, val);
    }

    @SmallTest
    public void testOnDraw_OnlyDrawsForeground_IfMetronomePlaying() {
        assertFalse(_metronomeTimeRefTimed.isPlaying());
        assertFalse(_exerciseWidgetTest.callDrawBackground);
        assertFalse(_exerciseWidgetTest.callDrawForeground);

        _exerciseWidgetTest.accessor_draw(_mockCanvas, 0);

        assertFalse(_metronomeTimeRefTimed.isPlaying());
        assertTrue(_exerciseWidgetTest.callDrawBackground);
        assertFalse(_exerciseWidgetTest.callDrawForeground);

        _exerciseWidgetTest.reset();

        _metronomeTimeRefTimed.start(0);
        _exerciseWidgetTest.accessor_draw(_mockCanvas, 1);

        assertTrue(_metronomeTimeRefTimed.isPlaying());
        assertTrue(_exerciseWidgetTest.callDrawBackground);
        assertTrue(_exerciseWidgetTest.callDrawForeground);
    }


    // This is so that there is no overlap in the very first millisecond of the playable
    // which will be a problem in interactive exercises
    @SmallTest
    public void testGetAnticipationDuration_IsOneMillisecondShorterThanActualPlayableDuration() {
        assertEquals(_metronomeTimeRefTimed.getCountdownDuration(), _exerciseWidgetTest.accessor_getAnticipationDuration(0));
        assertEquals(_metronomeTimeRefTimed.getPlayableDurationInMs(0) - 1, _exerciseWidgetTest.accessor_getAnticipationDuration(1));
    }

}
