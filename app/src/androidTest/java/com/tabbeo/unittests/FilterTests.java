package com.tabbeo.unittests;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.tabbeo.Detector.PitchFilter;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.Music.Playable.Silence;
import com.tabbeo.TabbeoAppTest;

class PitchFilterTest extends PitchFilter {
    public int getIterationsToAdaptNewNote() {
        return N_ITERATIONS_TO_ADAPT_NEW_NOTE;
    }
    //public int getMaxNoiseIterationsBeforeGivingUp() {
    //    return N_NOISE_ITERATIONS_BEFORE_GIVING_UP;
    //}
}

public class FilterTests extends InstrumentationTestCase {
    private PitchFilterTest _filter;
    private Note _a4 = Note.A4;

    @Override
    public void setUp() {

        _filter = new PitchFilterTest();
        TabbeoAppTest.setContext(getInstrumentation().getTargetContext());
    }

    //@SmallTest
    //public void testNoiseTimeShouldBeHigherThanAdoptTime() {
    //    // Otherwise, when we are trying to adapt a new note, since the noise time is the same, we drop it as "noise" before considering it a new note.
    //    assertTrue(_filter.getMaxNoiseIterationsBeforeGivingUp() > _filter.getIterationsToAdaptNewNote());
    //}

    @SmallTest
    public void testFilter_NoDetecting_NewNoteLongEnough_ChangesToNewNote() {
        Playable detectedPlayable;

        detectedPlayable = _filter.filter(Silence.SILENCE);
        assertEquals(Silence.SILENCE, detectedPlayable);

        // We start to detect a new note!
        detectedPlayable = _filter.filter(_a4);
        assertEquals(Silence.SILENCE, detectedPlayable);

        // We keep it long enough
        detectedPlayable = sendConstantPlayable(_a4);
        assertEquals(_a4, detectedPlayable);
    }

//    @SmallTest
//    public void testDetectsNote_KeepNoiseLongEnough_StopsDetecting() {
//        Playable detectedPlayable;
//
//        // At first nothing is detected
//        detectedPlayable = _filter.filter(Silence.SILENCE);
//        assertEquals(Silence.SILENCE, detectedPlayable);
//
//        // We change to a note
//        detectedPlayable = sendConstantPlayable(_a4);
//        assertEquals(_a4, detectedPlayable);
//
//        // Keep noise for a long time
//        detectedPlayable = sendNoiseToMax();
//
//        assertEquals(Silence.SILENCE, detectedPlayable);
//    }

//    @SmallTest
//    public void testHaveSomeNoise_ThenNoteLongEnough_DetectsNote() {
//        Playable detectedPlayable;
//
//        // At first nothing is detected
//        detectedPlayable = _filter.filter(Silence.SILENCE);
//        assertEquals(Silence.SILENCE, detectedPlayable);
//
//        // We keep noise
//        detectedPlayable = sendNoise(_filter.getMaxNoiseIterationsBeforeGivingUp() / 2);
//        assertEquals(Silence.SILENCE, detectedPlayable);
//
//        // We change to a note
//        detectedPlayable = sendConstantPlayable(_a4);
//        assertEquals(_a4, detectedPlayable);
//    }

//    @SmallTest
//    public void testDetectNoise_LittleNoiseInBetween_NotEnoughToMakeItStopDetectTheNote() {
//        Playable detectedPlayable;
//
//        // At first nothing is detected
//        detectedPlayable = _filter.filter(Silence.SILENCE);
//        assertEquals(Silence.SILENCE, detectedPlayable);
//
//        // We change to a note
//        detectedPlayable = sendConstantPlayable(_a4);
//        assertEquals(_a4, detectedPlayable);
//
//        // A little of noise
//        detectedPlayable = sendNoise(_filter.getMaxNoiseIterationsBeforeGivingUp() / 2);
//        assertEquals(_a4, detectedPlayable);
//
//        // Enough to keep the note
//        detectedPlayable = _filter.filter(_a4);
//        assertEquals(_a4, detectedPlayable);
//
//        // A little of noise
//        detectedPlayable = sendNoise(_filter.getMaxNoiseIterationsBeforeGivingUp() / 2);
//        assertEquals(_a4, detectedPlayable);
//
//        // Enough to keep the note
//        detectedPlayable = _filter.filter(_a4);
//        assertEquals(_a4, detectedPlayable);
//    }

//    @SmallTest
//    public void testNote_Noise_NoDetect_NoteLongEnough_Detect() {
//        Playable detectedPlayable;
//
//        // At first nothing is detected
//        detectedPlayable = _filter.filter(Silence.SILENCE);
//        assertEquals(Silence.SILENCE, detectedPlayable);
//
//        // We change to a note
//        detectedPlayable = sendConstantPlayable(_a4);
//        assertEquals(_a4, detectedPlayable);
//
//        // Keep noise for a long time, and stop detecting
//        detectedPlayable = sendNoiseToMax();
//        assertEquals(Silence.SILENCE, detectedPlayable);
//
//        // We go back to detect a note
//        detectedPlayable = sendConstantPlayable(_a4);
//        assertEquals(_a4, detectedPlayable);
 //   }

    public Playable sendConstantPlayable(Playable playable) {
        Playable detectedPlayable;

        detectedPlayable = _filter.filter(playable);

        for(int i=0; i<=_filter.getIterationsToAdaptNewNote(); ++i) {
            detectedPlayable = _filter.filter(playable);
        }

        return detectedPlayable;
    }

//    public Playable sendNoiseToMax() {
//        return sendNoise(_filter.getMaxNoiseIterationsBeforeGivingUp()+1);
//    }

    public Playable sendNoise(int nIterations) {
        Playable detectedPlayable = Silence.SILENCE;
        Note note1 = Note.E2; // Noise
        Note note2 = Note.E6; // Noise

        Note note = note1;

        for(int i=0; i<nIterations; ++i) {
            detectedPlayable = _filter.filter(note);
            note = (note == note1) ? note2 : note1;
        }

        return detectedPlayable;
    }
}
