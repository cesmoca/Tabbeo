package com.tabbeo.Music.Playable;

import com.tabbeo.Music.Music;
import com.tabbeo.Music.Music.Root;

public class Note implements Playable {
    private final int _octave;
    private final Root _root;

    // Do not use this guy, use the instances down there
    private Note(Root noteRoot, int octave) {
        _root = noteRoot;
        _octave = octave;
    }

    public Note(double frequency) {
        if(frequency == 0) throw new RuntimeException("The frequency is 0. We can't make a note out of that");

        long halfTonesFromA4 = getHalfTonesFromA4(frequency);
        _root = Root.values()[(int) mod(halfTonesFromA4 + Music.HALFTONES_FROM_C3_TO_A4, Music.HALFTONES_PER_OCTAVE)];
        _octave = 4 + (int) Math.floor((Music.HALFTONES_FROM_C3_TO_A4 + (int) halfTonesFromA4) / (float) Music.HALFTONES_PER_OCTAVE);
    }

    public Root getRoot() {
        return _root;
    }

    @Override
    public String toString() {
        return _root.toString() + _octave;
    }

    public int compareTo(Object o) {
        Note note = (Note) o;

        return (_root.getIndex() + _octave * Music.HALFTONES_PER_OCTAVE)
                - (note._root.getIndex() + note._octave * Music.HALFTONES_PER_OCTAVE);
    }

    public boolean equals(Object o) {
        if (o instanceof Note) {
            Note note = (Note) o;
            return _root == note._root && _octave == note._octave;
        }
        return false;
    }

    // Modulus without negative results
    protected static long mod(long x, long y) {
        long result = x % y;
        if (result < 0)
            result += y;
        return result;
    }

    // Closest approximation
    protected static int getHalfTonesFromA4(double frequency) {
        return (int) Math.round(getAccurateHalfTonesFromA4(frequency));
    }

    protected static double getAccurateHalfTonesFromA4(double frequency) {
        return Math.log(frequency / 440) * 12 / Math.log(2);
    }

    // Starting here, we show the public notes available to use :)
    // The numeration starts in C
    ////////////////////////////////////////////////////////////////////////////////////
    public final static Note E2 = new Note(Root.E, 2); // Lowest note in standard guitar
    public final static Note F2 = new Note(Root.F, 2);
    public final static Note Fs2 = new Note(Root.Fs, 2);
    public final static Note G2 = new Note(Root.G, 2);
    public final static Note Gs2 = new Note(Root.Gs, 2);
    public final static Note A2 = new Note(Root.A, 2);
    public final static Note As2 = new Note(Root.As, 2);
    public final static Note B2 = new Note(Root.B, 2);

    public final static Note C3 = new Note(Root.C, 3);
    public final static Note Cs3 = new Note(Root.Cs, 3);
    public final static Note D3 = new Note(Root.D, 3);
    public final static Note Ds3 = new Note(Root.Ds, 3);
    public final static Note E3 = new Note(Root.E, 3);
    public final static Note F3 = new Note(Root.F, 3);
    public final static Note Fs3 = new Note(Root.Fs, 3);
    public final static Note G3 = new Note(Root.G, 3);
    public final static Note Gs3 = new Note(Root.Gs, 3);
    public final static Note A3 = new Note(Root.A, 3);
    public final static Note As3 = new Note(Root.As, 3);
    public final static Note B3 = new Note(Root.B, 3);

    public final static Note C4 = new Note(Root.C, 4);
    public final static Note Cs4 = new Note(Root.Cs, 4);
    public final static Note D4 = new Note(Root.D, 4);
    public final static Note Ds4 = new Note(Root.Ds, 4);
    public final static Note E4 = new Note(Root.E, 4);
    public final static Note F4 = new Note(Root.F, 4);
    public final static Note Fs4 = new Note(Root.Fs, 4);
    public final static Note G4 = new Note(Root.G, 4);
    public final static Note Gs4 = new Note(Root.Gs, 4);
    public final static Note A4 = new Note(Root.A, 4);
    public final static Note As4 = new Note(Root.As, 4);
    public final static Note B4 = new Note(Root.B, 4);

    public final static Note C5 = new Note(Root.C, 5);
    public final static Note Cs5 = new Note(Root.Cs, 5);
    public final static Note D5 = new Note(Root.D, 5);
    public final static Note Ds5 = new Note(Root.Ds, 5);
    public final static Note E5 = new Note(Root.E, 5);
    public final static Note F5 = new Note(Root.F, 5);
    public final static Note Fs5 = new Note(Root.Fs, 5);
    public final static Note G5 = new Note(Root.G, 5);
    public final static Note Gs5 = new Note(Root.Gs, 5);
    public final static Note A5 = new Note(Root.A, 5);
    public final static Note As5 = new Note(Root.As, 5);
    public final static Note B5 = new Note(Root.B, 5);

    public final static Note C6 = new Note(Root.C, 6);
    public final static Note Cs6 = new Note(Root.Cs, 6);
    public final static Note D6 = new Note(Root.D, 6);
    public final static Note Ds6 = new Note(Root.Ds, 6);
    public final static Note E6 = new Note(Root.E, 6); // Highest note in standard guitar
    ////////////////////////////////////////////////////////////////////////////////////

    public final static Note notes[] = {
                              E2, F2, Fs2, G2, Gs2, A2, As2, B2,
            C3, Cs3, D3, Ds3, E3, F3, Fs3, G3, Gs3, A3, As3, B3,
            C4, Cs4, D4, Ds4, E4, F4, Fs4, G4, Gs4, A4, As4, B4,
            C5, Cs5, D5, Ds5, E5, F5, Fs5, G5, Gs5, A5, As5, B5,
            C6, Cs6, D6, Ds6, E6
    };
}
