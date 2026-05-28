package com.tabbeo.Music;


import com.tabbeo.Music.Playable.Note;

public class Scale {
    public static GuitarNote[] PENTATONIC_A_MINOR_POS_1 = new GuitarNote[]{
            new GuitarNote(Music.GuitarString.First, 5), new GuitarNote(Music.GuitarString.First, 8),
            new GuitarNote(Music.GuitarString.Second, 5), new GuitarNote(Music.GuitarString.Second, 8),
            new GuitarNote(Music.GuitarString.Third, 5), new GuitarNote(Music.GuitarString.Third, 7),
            new GuitarNote(Music.GuitarString.Fourth, 5), new GuitarNote(Music.GuitarString.Fourth, 7),
            new GuitarNote(Music.GuitarString.Fifth, 5), new GuitarNote(Music.GuitarString.Fifth, 7),
            new GuitarNote(Music.GuitarString.Sixth, 5), new GuitarNote(Music.GuitarString.Sixth, 8),
    };

    public enum Mode {
        IONIAN, MAJOR,
        DORIAN,
        PHRYGIAN,
        LYDIAN,
        MIXOLYDIAN,
        AEOLIAN, MINOR,
        LOCRIAN,
        CHROMATIC
    }

    public static final Interval ionianIntervals[] = {Interval.zero, Interval.M2, Interval.M3, Interval.p4, Interval.p5, Interval.M6, Interval.M7};
    public static final Interval dorianIntervals[] = {};
    public static final Interval phrygianIntervals[] = {};
    public static final Interval lydianIntervals[] = {};
    public static final Interval mixolydianIntervals[] = {};
    public static final Interval aeolianIntervals[] = {};
    public static final Interval locrianIntervals[] = {};
    public static final Interval chromaticIntervals[] = {Interval.zero, Interval.m2, Interval.M2, Interval.m3, Interval.M3, Interval.p4, Interval.a4, Interval.p5, Interval.m6, Interval.M6, Interval.m7, Interval.M7};


    Note key;
    Mode mode;

    // Builds it from the key note
   /* public Scale(Note key, Mode mode) {
        this(key, Interval.zero, Mode.MAJOR);

    }*/

    /*public Scale(Note note, Interval grade, Mode mode) {
        key = new Note(note);
        key.addInterval(HALFTONES_PER_OCTAVE - grade.getHalfTones());
        this.mode = mode;
    }

    public Interval getGrade(Note note) {
        Interval interval = key.getInterval(note);

        interval.normalize();

        return interval;
    }*/

    public static Interval[] getScale(Mode mode) {
        switch (mode) {
            case IONIAN:
            case MAJOR:
                return ionianIntervals;
            case DORIAN:
                return dorianIntervals;
            case PHRYGIAN:
                return phrygianIntervals;
            case LYDIAN:
                return lydianIntervals;
            case MIXOLYDIAN:
                return mixolydianIntervals;
            case AEOLIAN:
            case MINOR:
                return aeolianIntervals;
            case LOCRIAN:
                return locrianIntervals;
            case CHROMATIC:
                return chromaticIntervals;
        }
        return ionianIntervals;
    }

    public boolean belongs(Interval interval) {
        Interval scale[] = getScale(mode);
        for (Interval aScale : scale)
            if (aScale.equalsNormalized(interval))
                return true;
        return false;
    }

}
