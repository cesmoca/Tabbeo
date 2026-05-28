package com.tabbeo.unittests;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.tabbeo.Music.GuitarNote;
import com.tabbeo.Music.Music;

public class GuitarNoteTests extends InstrumentationTestCase {
    public GuitarNoteTests() {
        super();
    }


    @SmallTest
    public void testGuitarNoteCtr_PossibleFretValues() {
        new GuitarNote(Music.GuitarString.First, 1); // Ok

        try{
            new GuitarNote(Music.GuitarString.First, -1); // Not Ok
            assertTrue(false);
        }catch(RuntimeException ignore){}

        try{
            new GuitarNote(Music.GuitarString.First, 100); // Not Ok
            assertTrue(false);
        }catch(RuntimeException ignore){}

        new GuitarNote(Music.GuitarString.First, 0); // Ok
    }

    @SmallTest
    public void testGuitarNoteCtr_FingerAndStringCantBeNull() {
        try{
            new GuitarNote(null, 1, Music.Finger.Finger1); // Not ok
            assertTrue(false);
        }catch (RuntimeException ignore){}

        try{
            new GuitarNote(Music.GuitarString.First, 0, null); // Not ok
            assertTrue(false);
        }catch (RuntimeException ignore){}
    }

    @SmallTest
    public void testGuitarNoteCtr_CantPutFingerIfOpenString() {
        try{
            new GuitarNote(null, 0, Music.Finger.Finger1); // Not ok
            assertTrue(false);
        }catch (RuntimeException ignore){}

        new GuitarNote(Music.GuitarString.First, 0, Music.Finger.NoFingerInfo); // Ok
        new GuitarNote(Music.GuitarString.First, 0, Music.Finger.OpenString); // Ok
    }
}
