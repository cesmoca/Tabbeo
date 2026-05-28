package com.tabbeo.unittests;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.Detector.ChordDetector;
import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.Trainer.Trainer;
import com.tabbeo.Music.Music.Root;
import com.tabbeo.Music.Music.Duration;
import com.tabbeo.Music.Music;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.Music.Track;
import com.tabbeo.Widgets.FretboardWidget;

class NoteTest extends Note{

    public NoteTest(double frequency) {
        super(frequency);
    }

    protected static long mod(long x, long y) {
        return Note.mod(x, y);
    }
    protected static int getHalfTonesFromA4(double frequency) { return Note.getHalfTonesFromA4(frequency); }

}

public class MusicTests extends InstrumentationTestCase {
    public MusicTests() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        System.loadLibrary("Detector");
    }

    @Override
    protected void tearDown() throws Exception {
    }

    @SmallTest
    public void testModesInJavaHaveTheSameLengthThanInNDK() {
        assertEquals(ChordDetector.getNModes(), Chord.Mode.values().length);
    }

    @SmallTest
    public void testCreateChord_A_Minor() {
        Chord chord = Chord.AMinor;
        assertEquals("The root should be A", chord.getRoot(), Root.A);
        assertEquals("The mode should me minor", chord.getMode(), Chord.Mode.Minor);
    }

    @SmallTest
    public void testRoot() {
        assertEquals("There should be 12 notes. No more. No less.", Root.values().length, 12);
        assertEquals("It should start with C", Root.values()[0], Root.C);
        assertEquals("The index should be contiguous", Root.values()[11].getIndex(), 11);
    }

    @SmallTest
    public void testDuration() {
        assertEquals("Whole = 1", Duration.whole.divisor, 1.0f);
        assertEquals("Whole_dotted is 2/3 of Whole", Duration.whole_dotted.divisor, 2.0f / 3.0f * Duration.whole.divisor);
    }

    @SmallTest
    public void testGuitarStrings() {
        assertEquals("There should be 6 guitar strings", Music.GuitarString.values().length, 6);
        assertEquals(Music.GuitarString.First.getOpenNote(), Note.E4);
        assertEquals(Music.GuitarString.Second.getOpenNote(), Note.B3);
        assertEquals(Music.GuitarString.Third.getOpenNote(), Note.G3);
        assertEquals(Music.GuitarString.Fourth.getOpenNote(), Note.D3);
        assertEquals(Music.GuitarString.Fifth.getOpenNote(), Note.A2);
        assertEquals(Music.GuitarString.Sixth.getOpenNote(), Note.E2);

        assertEquals(Music.GuitarString.First.getStringNumber(), 0);
        assertEquals(Music.GuitarString.Sixth.getStringNumber(), 5);
    }

    @SmallTest
    public void testMusicConstants() {
        assertEquals(Music.HALFTONES_PER_OCTAVE, 12);
        assertEquals(Music.HALFTONES_FROM_C3_TO_A4, 9);
        assertEquals(Music.MAX_FRETS_GUITAR, 24);
    }

    @SmallTest
    public void testNote_FrequencyCtr() throws Exception {
        Note note = new Note(440); // Perfect A4
        assertEquals(note, Note.A4);

        note = new Note(82.41); // E2
        assertEquals(note, Note.E2);

        note = new Note(1318.51); // E6
        assertEquals(note, Note.E6);
    }

    @SmallTest
    public void testNote_CompareTo() {
        Note noteString1 = Music.GuitarString.First.getOpenNote();
        Note noteString6 = Music.GuitarString.Sixth.getOpenNote();

        assertTrue(noteString1.compareTo(noteString6) > 0);
        assertTrue(noteString1.compareTo(noteString1) == 0);
        assertTrue(noteString6.compareTo(noteString1) < 0);
    }

    @SmallTest
    public void testNote_Equals() {
        Note note1 = Note.A4;
        Note note2 = Note.A4;

        assertTrue(note1.equals(note2));

        assertTrue(note1.equals(note2));
    }

    @SmallTest
    public void testExercise_EmptryTrack() {
        Track track = new Track(new Music.BeatsPerMeasure(1, 4));
        Exercise exercise = new Exercise("Ex", Exercise.Type.Melody, Trainer.Type.Interactive, track, 0, 5, null /*descriptionId*/, FretboardWidget.NOTE_REPRESENTATION.SCALE_ANTICIPATION_CIRCLE);

        assertEquals(0, exercise.getTrack().getNPlayables());
        assertEquals(0, exercise.getMaxPoints());
    }

    @SmallTest
    public void testExercise_TrackWithOneMeasure() {
        Track track = new Track(new Music.BeatsPerMeasure(1, 4));
        track.addMeasure(new Track.TrackPlayable(Note.A4, Duration.quarter));

        Exercise exercise = new Exercise("Ex", Exercise.Type.Melody, Trainer.Type.Interactive, track, 0, 5, null /*descriptionId*/, FretboardWidget.NOTE_REPRESENTATION.SCALE_ANTICIPATION_CIRCLE);

        assertEquals(1, track.getNPlayables());
        assertEquals(Duration.quarter, track.getDuration(0));
        assertEquals(Trainer.POINTS_HIT, exercise.getMaxPoints());
    }

    @SmallTest
    public void testExercise_TrackWithTwoMeasure() {
        Track track = new Track(new Music.BeatsPerMeasure(1, 4));
        track.addMeasure(new Track.TrackPlayable(Note.A4, Duration.quarter));
        track.addMeasure(new Track.TrackPlayable(Note.A4,Duration.quarter));

        Exercise exercise = new Exercise("Ex", Exercise.Type.Melody, Trainer.Type.Interactive, track, 0, 5, null /*descriptionId*/, FretboardWidget.NOTE_REPRESENTATION.SCALE_ANTICIPATION_CIRCLE);

        assertEquals(2, track.getNPlayables());
        assertEquals(2 * Trainer.POINTS_HIT, exercise.getMaxPoints());
        assertEquals(Duration.quarter, track.getDuration(1));
    }

    @SmallTest
    public void testTrack_addMeasure_InvalidMesures() {
        Track track = null;
        try{
            // Add invalid measure, no playables added
            track = new Track(new Music.BeatsPerMeasure(2, 4));
            track.addMeasure(new Track.TrackPlayable(Note.A4, Duration.quarter));
            assertTrue(false);
        }catch(RuntimeException e){
            assertEquals(0, track.getNPlayables());
        }
    }

    @SmallTest
    public void testModWithNegativeResults() {
        assertEquals(0, NoteTest.mod(0, 4));
        assertEquals(0, NoteTest.mod(4, 4));
        assertEquals(0, NoteTest.mod(8, 4));
        assertEquals(1, NoteTest.mod(1, 4));
        assertEquals(1, NoteTest.mod(5, 4));
        assertEquals(0, NoteTest.mod(-4, 4));
        assertEquals(1, NoteTest.mod(-3, 4));
        assertEquals(3, NoteTest.mod(-1, 4));
        assertEquals(0, NoteTest.mod(-8, 4));
    }

    @SmallTest
    public void testGetHalfTonesFromA4() {
        assertEquals(0, NoteTest.getHalfTonesFromA4(440)); // A4
        assertEquals(1, NoteTest.getHalfTonesFromA4(466)); // As4
        assertEquals(3, NoteTest.getHalfTonesFromA4(523)); // C5
        assertEquals(10, NoteTest.getHalfTonesFromA4(784)); // G5
        assertEquals(12, NoteTest.getHalfTonesFromA4(880)); // A5
    }

    @SmallTest
    public void testNote_CheckOrderStaticNotes() {
        Note prevNote = Note.notes[0];
        for(int i = 1; i < Note.notes.length; ++i){
            Note currentNote = Note.notes[i];
            assertEquals(-1, prevNote.compareTo(currentNote)); // There is a half step of difference between all the notes
            prevNote = currentNote;
        }
    }

    @SmallTest
    public void testGuitarStrings_HaveRightNoteIndexes() {
        assertEquals(Note.E4, Music.GuitarString.First.getOpenNote());
        assertEquals(Note.B3, Music.GuitarString.Second.getOpenNote());
        assertEquals(Note.G3, Music.GuitarString.Third.getOpenNote());
        assertEquals(Note.D3, Music.GuitarString.Fourth.getOpenNote());
        assertEquals(Note.A2, Music.GuitarString.Fifth.getOpenNote());
        assertEquals(Note.E2, Music.GuitarString.Sixth.getOpenNote());
    }

    @SmallTest
    public void testGuitarStrings_getNote_CheckRanges() {
        try{
            Music.GuitarString.First.getNote(-1);
            assertTrue(false);
        }catch(RuntimeException ignore){}

        try{
            Music.GuitarString.First.getNote(Music.MAX_FRETS_GUITAR+1);
            assertTrue(false);
        }catch(RuntimeException ignore){}

        // But these should work
        Music.GuitarString.First.getNote(Music.MAX_FRETS_GUITAR);
        Music.GuitarString.Second.getNote(Music.MAX_FRETS_GUITAR);
        Music.GuitarString.Third.getNote(Music.MAX_FRETS_GUITAR);
        Music.GuitarString.Fourth.getNote(Music.MAX_FRETS_GUITAR);
        Music.GuitarString.Fifth.getNote(Music.MAX_FRETS_GUITAR);
        Music.GuitarString.Sixth.getNote(Music.MAX_FRETS_GUITAR);
    }
}
