package com.tabbeo.Music.Playable;
import com.tabbeo.Music.Music;
import com.tabbeo.Music.Music.Root;

public class Chord implements Playable {
    public enum ChordStrokePattern implements StrokePattern{ // How to play it. Maybe in which string to start
        PICK_DOWN_STROKE,
        PICK_UP_STROKE,
    }

    // Copying the same structure used in CLAM (ChordCorrelator.hxx)
    public enum Mode {
        //Fifth("5"), // We are removing them for now, they tend to be detected instead of Majors or Minors. They are not that important anyway.
        Major("M"),
        Minor("m"),
        Suspended2("sus2"),
        Suspended4("sus4"),
        Augmented("Augmented"),
        Diminished("Diminished"),
        Sixth("Sixth"),
        Minor6("Minor6"),
        Seventh("Seventh"),
        Minor7("Minor7"),
        Major7("Major7"),
        MinorMajor7("MinorMajor7"),
        Diminished7("Diminished7")
    ;
        private final String _str;

        Mode(String str) {
            _str = str;
        }

        public String toString() {
            return _str;
        }
    }

    private final Root _root;
    private final Mode _mode;

    // Do not use this guy, use the static instances of the chords down there
    private Chord(Root root, Mode mode) {
        _root = root;
        _mode = mode;
    }

    public Chord(int encodedChord){
        this(Music.Root.values()[(encodedChord & 0x0F0) >> 4], Chord.Mode.values()[(encodedChord & 0x000F)]);
    }

    public Root getRoot() {
        return _root;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Chord) {
            Chord chord = (Chord) o;
            return _root == chord._root && _mode == chord._mode;
        }

        return false;
    }

    public Mode getMode() {
        return _mode;
    }

    @Override
    public String toString() {
        return _root + " " + _mode;
    }


    // From here, we have the available chords
    public final static Chord AMajor = new Chord(Root.A, Mode.Major);

    public final static Chord DMajor = new Chord(Root.D, Mode.Major);
    public final static Chord EMajor = new Chord(Root.E, Mode.Major);
    public final static Chord FMajor = new Chord(Root.F, Mode.Major);
    public final static Chord GMajor = new Chord(Root.G, Mode.Major);


    public final static Chord AMinor = new Chord(Root.A, Mode.Minor);

    public final static Chord EMinor = new Chord(Root.E, Mode.Minor);


}
