package com.tabbeo.unittests;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.tabbeo.CourseLibrary.IInversion;
import com.tabbeo.Music.ChordsLibrary;
import com.tabbeo.Music.Music.Root;
import com.tabbeo.Music.GuitarNote;
import com.tabbeo.Music.Music;
import com.tabbeo.Music.Playable.Chord;

import java.util.ArrayList;
import java.util.List;

public class ChordsLibraryTests extends InstrumentationTestCase {
    public ChordsLibraryTests() {
        super();
    }

    @SmallTest
    public void testCheckChordsNotes() {
        for(int chordIndex = 0; chordIndex < Root.values().length; ++chordIndex){
            Root chordRoot = Root.values()[chordIndex];
            for(int modeIndex = 0; modeIndex < Chord.Mode.values().length; ++modeIndex){
                Chord.Mode chordMode = Chord.Mode.values()[modeIndex];
                List<IInversion> inversions = ChordsLibrary.getInversions(chordRoot, chordMode);
                if(inversions == null) continue;

                for(IInversion inversion : inversions){
                    List<Root> chordNotes = Music.getChordNotes(chordRoot, chordMode);

                    for(GuitarNote guitarNote : inversion.getGuitarNotes()){
                        if(guitarNote == null) continue; // Deaf strings
                        assertTrue(chordNotes.contains(guitarNote.getNote().getRoot()));
                    }
                }
            }
        }
    }

    @SmallTest
    public void testNotRepeatedFingers() {
        for(int chordIndex = 0; chordIndex < Root.values().length; ++chordIndex){
            Root chordRoot = Root.values()[chordIndex];
            for(int modeIndex = 0; modeIndex < Chord.Mode.values().length; ++modeIndex){
                Chord.Mode chordMode = Chord.Mode.values()[modeIndex];
                List<IInversion> inversions = ChordsLibrary.getInversions(chordRoot, chordMode);
                if(inversions == null) continue;

                for(IInversion inversion : inversions){
                    ArrayList<Music.Finger> fingersInChord = new ArrayList<>();

                    for(GuitarNote guitarNote : inversion.getGuitarNotes()){
                        if(guitarNote == null) continue; // Deaf strings

                        Music.Finger finger = guitarNote.getFinger();
                        assertNotNull(finger);
                        if(finger == Music.Finger.NoFingerInfo) assertTrue(false); // We are supposed to always have finger info here
                        if(finger == Music.Finger.OpenString) continue;
                        if(inversion.hasFingerCapo() && finger == Music.Finger.Finger1) continue; // If we have a capo, it SHOULD be repeated

                        assertFalse(fingersInChord.contains(finger));
                        fingersInChord.add(finger);
                    }
                }
            }
        }
    }

    @SmallTest
    public void testIfThereIsCapo_ThereIsNoFingerLowerThanTheCapo() {
        for(int chordIndex = 0; chordIndex < Root.values().length; ++chordIndex){
            Root chordRoot = Root.values()[chordIndex];
            for(int modeIndex = 0; modeIndex < Chord.Mode.values().length; ++modeIndex){
                Chord.Mode chordMode = Chord.Mode.values()[modeIndex];
                List<IInversion> inversions = ChordsLibrary.getInversions(chordRoot, chordMode);
                if(inversions == null) continue;

                for(IInversion inversion : inversions){
                    if(!inversion.hasFingerCapo()) continue;

                    // Where is the finger1?
                    int capoFret = 0;
                    for(GuitarNote note : inversion.getGuitarNotes()){
                        if(note == null) continue; // Deaf strings
                        if(note.getFinger() == Music.Finger.Finger1){
                            capoFret = note.getFret();
                            break;
                        }
                    }
                    assertTrue(capoFret > 0); // If it says there is a capo, there should be a fret for it

                    // Let's check all the fingers are below the capoFret
                    for(GuitarNote note : inversion.getGuitarNotes()){
                        if(note == null) continue; // Deaf strings
                        if(note.getFinger() != Music.Finger.Finger1){
                            assertTrue(note.getFret() > capoFret);
                            break;
                        }
                    }
                }
            }
        }
    }

    @SmallTest
    public void testDistanceBetweenFingersNotBiggerThanFourFrets() {
        for(int chordIndex = 0; chordIndex < Root.values().length; ++chordIndex){
            Root chordRoot = Root.values()[chordIndex];
            for(int modeIndex = 0; modeIndex < Chord.Mode.values().length; ++modeIndex){
                Chord.Mode chordMode = Chord.Mode.values()[modeIndex];
                List<IInversion> inversions = ChordsLibrary.getInversions(chordRoot, chordMode);
                if(inversions == null) continue;

                for(IInversion inversion : inversions){
                    int minFret = Music.MAX_FRETS_GUITAR;
                    int maxFret = 0;

                    for(GuitarNote note : inversion.getGuitarNotes()){
                        if(note == null) continue; // Deaf strings
                        if(note.getFret() < minFret) minFret = note.getFret();
                        if(note.getFret() > maxFret) maxFret = note.getFret();
                    }

                    assertEquals(minFret, inversion.getMinFret());
                    assertEquals(maxFret, inversion.getMaxFret());

                    assertTrue((maxFret - minFret + 1) <= 4); // A span of 4 frets max
                }
            }
        }
    }

    @SmallTest
    public void testThereIsFingerCapo_Finger1ShouldBeRepeated() {
        for(int chordIndex = 0; chordIndex < Root.values().length; ++chordIndex){
            Root chordRoot = Root.values()[chordIndex];
            for(int modeIndex = 0; modeIndex < Chord.Mode.values().length; ++modeIndex){
                Chord.Mode chordMode = Chord.Mode.values()[modeIndex];
                List<IInversion> inversions = ChordsLibrary.getInversions(chordRoot, chordMode);
                if(inversions == null) continue;

                for(IInversion inversion : inversions){
                    if(!inversion.hasFingerCapo()) continue;
                    int nFinger1 = 0;

                    for(GuitarNote note : inversion.getGuitarNotes()){
                        if(note == null) continue; // Deaf string
                        if(note.getFinger() == Music.Finger.Finger1) nFinger1++;
                    }

                    assertTrue(nFinger1 > 1); // If there is a capo, we should see finger1 several times
                }
            }
        }
    }

    @SmallTest
    public void testTheOrderOfFingersShouldBeUpToDownLeftToRight() {
        for(int chordIndex = 0; chordIndex < Root.values().length; ++chordIndex){
            Root chordRoot = Root.values()[chordIndex];
            for(int modeIndex = 0; modeIndex < Chord.Mode.values().length; ++modeIndex){
                Chord.Mode chordMode = Chord.Mode.values()[modeIndex];
                List<IInversion> inversions = ChordsLibrary.getInversions(chordRoot, chordMode);
                if(inversions == null) continue;

                for(IInversion inversion : inversions){
                    // Get the min and max fret
                    int minFret = Music.MAX_FRETS_GUITAR;
                    int maxFret = 0;

                    for(GuitarNote note : inversion.getGuitarNotes()){
                        if(note == null) continue; // Deaf strings
                        if(note.getFret() < minFret) minFret = note.getFret();
                        if(note.getFret() > maxFret) maxFret = note.getFret();
                    }

                    assertEquals(minFret, inversion.getMinFret());
                    assertEquals(maxFret, inversion.getMaxFret());

                    Music.Finger currentFinger = null;
                    for(int i=minFret; i<=maxFret;++i){
                        for(int j = Music.GuitarString.Sixth.getStringNumber(); j>=Music.GuitarString.First.getStringNumber(); j--){ // From the lowest to the hightest pitch
                            GuitarNote note = inversion.getGuitarNotes()[j];
                            if(note == null) continue; // Deaf notes
                            if(note.getFret() == i){
                                Music.Finger finger = note.getFinger();
                                assertNotNull(finger);
                                if(finger == Music.Finger.NoFingerInfo) assertTrue(false); // We are supposed to always have finger info here
                                if(finger == Music.Finger.OpenString) continue; // This is an open string
                                if(currentFinger == null){
                                    currentFinger = finger;
                                }else{
                                    assertTrue(finger.ordinal() >= currentFinger.ordinal()); // The capo is repeated, so it could be the same one
                                    currentFinger = finger;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
