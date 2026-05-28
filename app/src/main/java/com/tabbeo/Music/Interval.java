package com.tabbeo.Music;


public class Interval {
    private int halfTones = 0;

    public static final Interval zero = new Interval(0);
    public static final Interval m2 = new Interval(1);
    public static final Interval M2 = new Interval(2);
    public static final Interval m3 = new Interval(3);
    public static final Interval M3 = new Interval(4);
    public static final Interval p4 = new Interval(5);
    public static final Interval a4 = new Interval(6);
    //public static final Interval d5 = new Interval(6);
    public static final Interval p5 = new Interval(7);
    public static final Interval m6 = new Interval(8);
    public static final Interval M6 = new Interval(9);
    public static final Interval m7 = new Interval(10);
    public static final Interval M7 = new Interval(11);

    Interval(int halfTones) {
        this.halfTones = halfTones;
    }

    // Normalizes it within an octave
    public void normalize() {
        while (halfTones < 0) halfTones += Music.HALFTONES_PER_OCTAVE;
        while (halfTones >= Music.HALFTONES_PER_OCTAVE) halfTones -= Music.HALFTONES_PER_OCTAVE;
    }

    public int getHalfTones() {
        return halfTones;
    }

    // Compares if it is the same grade, even though they might be in different octaves
    public boolean equalsNormalized(Object o) {
        Interval i = (Interval) o;
        return (halfTones % Music.HALFTONES_PER_OCTAVE) == (i.halfTones % Music.HALFTONES_PER_OCTAVE);
    }

    public static Interval getRandom(Scale.Mode mode, boolean includeRootNote) {
        Interval[] scale = Scale.getScale(mode);
        int offset = 0;
        if (!includeRootNote) offset = 1;

        return scale[(int) (Math.random() * (scale.length - offset) + offset)];
    }

    public String toString() {
        return getName();
    }

    public boolean equals(Object o) {
        if(!(o instanceof Interval)) return false;
        Interval i = (Interval) o;
        return halfTones == i.halfTones;
    }

    public String getName() {
        switch (halfTones) {
            case 0:
                return "R";
            case 1:
                return "m2";
            case 2:
                return "2";
            case 3:
                return "m3";
            case 4:
                return "3";
            case 5:
                return "4";
            case 6:
                return "4a";
            case 7:
                return "5";
            case 8:
                return "m6";
            case 9:
                return "6";
            case 10:
                return "m7";
            case 11:
                return "7";
            case 12:
                return "8";
        }
        throw new RuntimeException("Unknown interval. Halftones: " + halfTones);
    }

    public boolean isRoot() {
        return equalsNormalized(zero);
    }

    public boolean isMajor() {
        return equalsNormalized(M2) || equalsNormalized(M3) || equalsNormalized(M6) || equalsNormalized(M7);
    }

    public boolean isMinor() {
        return equalsNormalized(m2) || equalsNormalized(m3) || equalsNormalized(m6) || equalsNormalized(m7);
    }

    public boolean isPerfect() {
        return equalsNormalized(p4) || equalsNormalized(p5);
    }

    public boolean isAugmented() {
        return equalsNormalized(a4);
    }

    //public boolean isDiminished() {
    //  return equalsNormalized(d5);
    //}

}
