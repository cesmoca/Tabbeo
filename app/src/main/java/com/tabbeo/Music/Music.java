package com.tabbeo.Music;

import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.R;
import com.tabbeo.StudentProfile;

import java.util.ArrayList;
import java.util.List;

public class Music {

    public static final int HALFTONES_PER_OCTAVE = 12;
    // 440 Hz is A4 and there are 9 half-steps from C4 to A4 (C is the beginning of the octave)
    // for frequency references -> http://www.contrabass.com/pages/frequency.html
    public static final int HALFTONES_FROM_C3_TO_A4 = 9;

    public static final int MAX_FRETS_GUITAR = 24;

    private Music() { } // Just a namespace class

    // Root names
    public enum Root {
        // We rely on the fact that there are 12 notes. No more, no less.
        C(0, "C", "Do"),
        Cs(1, "C#", "Do#"),
        D(2, "D","Re"),
        Ds(3, "D#", "Re#"),
        E(4, "E", "Mi"),
        F(5, "F", "Fa"),
        Fs(6, "F#", "Fa#"),
        G(7, "G", "Sol"),
        Gs(8, "G#", "Sol#"),
        A(9, "A", "La"),
        As(10, "A#", "La#"),
        B(11, "B", "Si");

        private final int _index;
        private final String _abcNotationName;
        private final String _doReMiNotationName;

        Root(int index, String abcNotationName, String doReMiNotationName) {
            _index = index;
            _abcNotationName = abcNotationName;
            _doReMiNotationName = doReMiNotationName;
        }

        public int getIndex() {
            return _index;
        }

        public String toString() {
            StudentProfile.NotesNotation notesNotation = StudentProfile.getNotesNotation();
            switch(notesNotation){
                case ABC:
                    return _abcNotationName;
                case DOREMI:
                    return _doReMiNotationName;
            }

            throw new RuntimeException("Unsupported notes notation: "+notesNotation);
        }

        public int getColorId() {
            switch (this) {
                case A:
                    return R.color.note_a;
                case As:
                    return R.color.note_a_sharp;
                case B:
                    return R.color.note_b;
                case C:
                    return R.color.note_c;
                case Cs:
                    return R.color.note_c_sharp;
                case D:
                    return R.color.note_d;
                case Ds:
                    return R.color.note_d_sharp;
                case E:
                    return R.color.note_e;
                case F:
                    return R.color.note_f;
                case Fs:
                    return R.color.note_f_sharp;
                case G:
                    return R.color.note_g;
                case Gs:
                    return R.color.note_g_sharp;
            }

            throw new RuntimeException("We do not have a color for this root: " + this);
        }

    }

    // Lengths
    public enum Duration {
        whole(1.0f),
        whole_dotted(2.0f / 3.0f * whole.divisor),
        half(2.0f),
        half_dotted(2.0f / 3.0f * half.divisor),
        quarter(4.0f),
        quarter_dotted(2.0f / 3.0f * quarter.divisor),
        eight(8.0f),
        eight_dotted(2.0f / 3.0f * eight.divisor),
        sixteenth(16.f),
        sixteenth_dotted(2.0f / 3.0f * sixteenth.divisor);

        public float divisor;

        Duration(float v) {
            divisor = v;
        }
    }

    public enum GuitarString {
        // From higher to lower pitch
        First(0, 24),
        Second(1, 19),
        Third(2, 15),
        Fourth(3, 10),
        Fifth(4, 5),
        Sixth(5, 0);

        public Note getNote(int nFret){
            if(nFret < 0 || nFret > Music.MAX_FRETS_GUITAR) throw new RuntimeException("Invalid fret number: "+nFret);
            return Note.notes[_noteIndex + nFret];
        }
        public Note getOpenNote(){ return Note.notes[_noteIndex]; }
        public int getStringNumber(){ return _stringNumber; }

        private final int _noteIndex;
        private final int _stringNumber;

        GuitarString(int stringNumber, int noteIndex) {
            _stringNumber = stringNumber;
            _noteIndex = noteIndex;
        }
    }

    public enum Finger{
        Finger1("1", R.color.finger1),
        Finger2("2", R.color.finger2),
        Finger3("3", R.color.finger3),
        Finger4("4", R.color.finger4),
        OpenString("", 0),
        NoFingerInfo("",0);

        private final String _text;
        private final int _colorId;

        Finger(String text, int colorId) {
            _text = text;
            _colorId = colorId;
        }

        @Override
        public String toString(){ return _text; }

        public int getColorId(){ return _colorId; }
    }

    public static List<Root> getChordNotes(Root root, Chord.Mode mode){
        ArrayList<Root> notes = new ArrayList<>();
        notes.add(root); // 1st

        switch(mode){
            case Major: {
                int thirdIndex = (root.getIndex() + 4 /* two tones */) % Root.values().length;
                notes.add(Root.values()[thirdIndex]);
                break;
            }
            case Minor: {
                int thirdIndex = (root.getIndex() + 3 /* one tone and one halftone */) % Root.values().length;
                notes.add(Root.values()[thirdIndex]);
                break;
            }
            default: {
                return null;
            }
        }

        int fifthIndex = (root.getIndex() + 7 /* three tones and one halftone */) % Root.values().length; // Fifth
        notes.add(Root.values()[fifthIndex]);

        return notes;
    }

    public static class BeatsPerMeasure {
        public BeatsPerMeasure(int beats, int measure) {
            this.beats = beats;
            this.measure = measure;
        }

        public int beats;
        public int measure;
    }
}
