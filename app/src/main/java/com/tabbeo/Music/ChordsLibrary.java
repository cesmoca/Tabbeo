package com.tabbeo.Music;

import com.tabbeo.CourseLibrary.IInversion;
import com.tabbeo.Music.Music.Root;
import com.tabbeo.Music.Playable.Chord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This class is initialized statically
// We are not going to put any runtime checks
// Unit tests will be in charge of that
// NOTE: This should be static knowledge. Probably loaded from a text file
public class ChordsLibrary {
    private static final Map<Root, HashMap<Chord.Mode, List<IInversion>>> _chordsLibrary;

    private static class HowStringIsPlayed{}
    private static class DeafString extends HowStringIsPlayed{}
    private static class OpenString extends HowStringIsPlayed{}
    private static class FingerInString extends HowStringIsPlayed{
        int fret;
        Music.Finger finger;
        FingerInString(Music.Finger finger, int fret){
            this.finger = finger;
            this.fret = fret;
        }
    }

    private static class Inversion extends IInversion {
        // From lowest-pitched to highest-pitched string
        public Inversion(HowStringIsPlayed str6, HowStringIsPlayed str5, HowStringIsPlayed str4, HowStringIsPlayed str3, HowStringIsPlayed str2, HowStringIsPlayed str1){
            setString(Music.GuitarString.Sixth, str6);
            setString(Music.GuitarString.Fifth, str5);
            setString(Music.GuitarString.Fourth, str4);
            setString(Music.GuitarString.Third, str3);
            setString(Music.GuitarString.Second, str2);
            setString(Music.GuitarString.First, str1);
        }

        private void setString(Music.GuitarString string, HowStringIsPlayed howStringIsPlayed){
            if(howStringIsPlayed instanceof DeafString) return; // Nothing, really

            _nPlayingStrings++;

            if(howStringIsPlayed instanceof OpenString) {
                _notes[string.getStringNumber()] = new GuitarNote(string, 0, Music.Finger.OpenString);
            }

            if(howStringIsPlayed instanceof FingerInString){
                FingerInString fingerInString = (FingerInString) howStringIsPlayed;

                // Are we using a capo?
                if(fingerInString.finger == Music.Finger.Finger1 && !_fingerCapo){
                    for(GuitarNote note : _notes){
                        if(note != null && note.getFinger() == Music.Finger.Finger1){
                            _fingerCapo = true;
                        }
                    }
                }

                _notes[string.getStringNumber()] = new GuitarNote(string, fingerInString.fret, fingerInString.finger);
            }

            if(_notes[string.getStringNumber()].getFret() < _minFret) _minFret = _notes[string.getStringNumber()].getFret();
            if(_notes[string.getStringNumber()].getFret() > _maxFret) _maxFret = _notes[string.getStringNumber()].getFret();
        }
    }

    private ChordsLibrary(){} // This class is initialized statically. Can't be instantiated

    public static List<IInversion> getInversions(Root root, Chord.Mode mode){
        HashMap<Chord.Mode, List<IInversion>> modes = _chordsLibrary.get(root);
        if(modes == null) return null;

        List<IInversion> inversions = modes.get(mode);
        if(inversions == null) return null;

        return inversions;
    }

    public static List<IInversion> getInversions(Chord chord){
        return getInversions(chord.getRoot(), chord.getMode());
    }

    private static void addInversion(IInversion inversion, Root root, Chord.Mode mode){
        HashMap<Chord.Mode, List<IInversion>> modes = _chordsLibrary.get(root);
        if(modes == null){
            modes = new HashMap<>();
            _chordsLibrary.put(root, modes);
        }

        List<IInversion> inversions = modes.get(mode);
        if(inversions == null){
            inversions = new ArrayList<>();
            modes.put(mode, inversions);
        }

        inversions.add(inversion);
    }

    // Initialize the chords library itself
    static{
        _chordsLibrary=new HashMap<>();

        Inversion aMajor = new Inversion(
                new DeafString(),
                new OpenString(),
                new FingerInString(Music.Finger.Finger1, 2),
                new FingerInString(Music.Finger.Finger2, 2),
                new FingerInString(Music.Finger.Finger3, 2),
                new OpenString()
        );
        addInversion(aMajor, Root.A, Chord.Mode.Major);

        Inversion aMinor = new Inversion(
                new DeafString(),
                new OpenString(),
                new FingerInString(Music.Finger.Finger2, 2),
                new FingerInString(Music.Finger.Finger3, 2),
                new FingerInString(Music.Finger.Finger1, 1),
                new OpenString()
        );
        addInversion(aMinor, Root.A, Chord.Mode.Minor);

        Inversion bMajor = new Inversion(
                new DeafString(),
                new FingerInString(Music.Finger.Finger1, 2),
                new FingerInString(Music.Finger.Finger2, 4),
                new FingerInString(Music.Finger.Finger3, 4),
                new FingerInString(Music.Finger.Finger4, 4),
                new FingerInString(Music.Finger.Finger1, 2)
        );
        addInversion(bMajor, Root.B, Chord.Mode.Major);

        Inversion bMinor = new Inversion(
                new DeafString(),
                new FingerInString(Music.Finger.Finger1, 2),
                new FingerInString(Music.Finger.Finger3, 4),
                new FingerInString(Music.Finger.Finger4, 4),
                new FingerInString(Music.Finger.Finger2, 3),
                new FingerInString(Music.Finger.Finger1, 2)
        );
        addInversion(bMinor, Root.B, Chord.Mode.Minor);

        Inversion cMajor = new Inversion(
                new DeafString(),
                new FingerInString(Music.Finger.Finger3, 3),
                new FingerInString(Music.Finger.Finger2, 2),
                new OpenString(),
                new FingerInString(Music.Finger.Finger1, 1),
                new OpenString()
        );
        addInversion(cMajor, Root.C, Chord.Mode.Major);

        Inversion dMajor = new Inversion(
                new DeafString(),
                new DeafString(),
                new OpenString(),
                new FingerInString(Music.Finger.Finger1, 2),
                new FingerInString(Music.Finger.Finger3, 3),
                new FingerInString(Music.Finger.Finger2, 2)
        );
        addInversion(dMajor, Root.D, Chord.Mode.Major);

        Inversion dMinor = new Inversion(
                new DeafString(),
                new DeafString(),
                new OpenString(),
                new FingerInString(Music.Finger.Finger2, 2),
                new FingerInString(Music.Finger.Finger3, 3),
                new FingerInString(Music.Finger.Finger1, 1)
        );
        addInversion(dMinor, Root.D, Chord.Mode.Minor);

        Inversion eMajor = new Inversion(
                new OpenString(),
                new FingerInString(Music.Finger.Finger2, 2),
                new FingerInString(Music.Finger.Finger3, 2),
                new FingerInString(Music.Finger.Finger1, 1),
                new OpenString(),
                new OpenString()
        );
        addInversion(eMajor, Root.E, Chord.Mode.Major);

        Inversion eMinor = new Inversion(
                new OpenString(),
                new FingerInString(Music.Finger.Finger2, 2),
                new FingerInString(Music.Finger.Finger3, 2),
                new OpenString(),
                new OpenString(),
                new OpenString()
        );
        addInversion(eMinor, Root.E, Chord.Mode.Minor);

        Inversion fMajor = new Inversion(
                new FingerInString(Music.Finger.Finger1, 1),
                new FingerInString(Music.Finger.Finger3, 3),
                new FingerInString(Music.Finger.Finger4, 3),
                new FingerInString(Music.Finger.Finger2, 2),
                new FingerInString(Music.Finger.Finger1, 1),
                new FingerInString(Music.Finger.Finger1, 1)
        );
        addInversion(fMajor, Root.F, Chord.Mode.Major);

        Inversion fMinor = new Inversion(
                new FingerInString(Music.Finger.Finger1, 1),
                new FingerInString(Music.Finger.Finger3, 3),
                new FingerInString(Music.Finger.Finger4, 3),
                new FingerInString(Music.Finger.Finger1, 1),
                new FingerInString(Music.Finger.Finger1, 1),
                new FingerInString(Music.Finger.Finger1, 1)
        );
        addInversion(fMinor, Root.F, Chord.Mode.Minor);

        Inversion gMajor = new Inversion(
                new FingerInString(Music.Finger.Finger2, 3),
                new FingerInString(Music.Finger.Finger1, 2),
                new OpenString(),
                new OpenString(),
                new OpenString(),
                new FingerInString(Music.Finger.Finger3, 3)
        );
        addInversion(gMajor, Root.G, Chord.Mode.Major);
    }
}
