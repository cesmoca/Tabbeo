package com.tabbeo.unittests;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.AttributeSet;

import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.CourseLibrary.ExercisesLibrary;
import com.tabbeo.ExerciseLibraryTest;
import com.tabbeo.Music.GuitarNote;
import com.tabbeo.Music.Music;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.Widgets.FretboardWidget;
import com.tabbeo.Widgets.Sprites.StringSprite;

class StringSpriteTest extends StringSprite{

    public StringSpriteTest(Context context, Music.GuitarString string, long startTime, long duration, int minFret, int maxFret) {
        super(context, string, startTime, duration, minFret, maxFret);
    }

    public long getStartTime(){ return _startTime; }
    public long getEndTime(){ return _endTime; }
}

class FretboardWidgetTest extends FretboardWidget {
    public FretboardWidgetTest(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public static GuitarNote accessor_findGuitarNote(Note note, int minFret, int maxFret){
        return FretboardWidget.findGuitarNote(note, minFret, maxFret);
    }
}

public class FretboardWidgetTests extends InstrumentationTestCase {
    private FretboardWidgetTest _fretboardWidget;
    private IMetronomeTimeRefTest _metronomeTimeRefTest;

    @Override
    protected void setUp() throws Exception {
        _metronomeTimeRefTest = new IMetronomeTimeRefTest(ExerciseLibraryTest.oneNoteTrack, 50 /*tempo*/);

        Exercise exercise = ExerciseLibraryTest.twoNotesExerciseInteractive;

        _fretboardWidget = new FretboardWidgetTest(getInstrumentation().getTargetContext(), null);
        _fretboardWidget.init(exercise, _metronomeTimeRefTest);
    }

    @SmallTest
    public void testNoteMelodiesInExercisesAreRepresentableInFretboard() {
        noteMelodiesInExercisesAreRepresentableInFretboard(ExercisesLibrary.courseExercises);
        noteMelodiesInExercisesAreRepresentableInFretboard(ExercisesLibrary.testExercises);
    }

    private void noteMelodiesInExercisesAreRepresentableInFretboard(Exercise[] exercises){
        // If something is wrong, it will throw a RuntimeException
        for (Exercise exercise : exercises) {
            if (exercise.getTrack() == null) continue; // Some exercises do not have tracks

            for (int j = 0; j < exercise.getTrack().getNPlayables(); ++j) {
                try {
                    Playable p = exercise.getTrack().getPlayable(j);
                    if (p instanceof Note) {
                        FretboardWidgetTest.accessor_findGuitarNote((Note) p, exercise.getMinFret(), exercise.getMaxFret());
                    }
                } catch (RuntimeException ex) {
                    throw new RuntimeException("Exercise: " + exercise + ". " + ex.toString());
                }
            }
        }
    }

    @SmallTest
    public void testFindGuitarNote_FretsDoNotMakeSense() {
        int frets [][] = new int[][]{{5, 0}, {-1, 5}, {5, -6}, {Music.MAX_FRETS_GUITAR + 1, 5}, {0, Music.MAX_FRETS_GUITAR + 1}};

        for (int[] fret : frets) {
            try {
                FretboardWidgetTest.accessor_findGuitarNote(Note.A4, fret[0], fret[1]);
                assertTrue(false);
            } catch (RuntimeException ignored) {}
        }
    }

    @SmallTest
    public void testFindGuitarNote_NoteInGuitarButNotWithinFretRange() {
        try{
            FretboardWidgetTest.accessor_findGuitarNote(Note.A4, 0, 1);
            assertTrue(false);
        }catch(RuntimeException ignored){}
    }

    @SmallTest
    public void testFindGuitarNote() {
        {
            // Note in guitar, and within frets range, first string
            GuitarNote gn = FretboardWidgetTest.accessor_findGuitarNote(Note.A4, 3, 6);
            assertNotNull(gn);
            assertEquals(gn.getString(), Music.GuitarString.First);
            assertEquals(gn.getFret(), 5);
            assertEquals(gn.getNote(), Note.A4);
        }

        {
            // Note in guitar, and within frets range, fifth string
            GuitarNote gn = FretboardWidgetTest.accessor_findGuitarNote(Note.A2, 0, 5);
            assertNotNull(gn);
            assertEquals(gn.getString(), Music.GuitarString.Fifth);
            assertEquals(gn.getFret(), 0);
            assertEquals(gn.getNote(), Note.A2);
        }
    }

    // This is to avoid overlaps at the very first millisecond of the playable
    // which is a problem for interactive exercises
    @SmallTest
    public void testStringSprite_StartsOneMsAfterPlayableStart() {
        long startTime = 1234;
        long duration = 10;

        StringSpriteTest sp = new StringSpriteTest(getInstrumentation().getTargetContext(), Music.GuitarString.First, startTime, duration, 0 /*minFret*/, 5 /*maxFret*/);

        assertEquals(startTime+1, sp.getStartTime());
        assertEquals(startTime+duration, sp.getEndTime());
    }
}

