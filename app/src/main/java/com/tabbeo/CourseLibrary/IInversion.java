package com.tabbeo.CourseLibrary;

import com.tabbeo.Music.GuitarNote;
import com.tabbeo.Music.Music;

public abstract class IInversion {
    protected GuitarNote[] _notes = new GuitarNote[6]; // Six strings. Null if deaf string
    protected boolean _fingerCapo = false;
    protected int _minFret = Music.MAX_FRETS_GUITAR;
    protected int _maxFret = 0;
    protected int _nPlayingStrings;

    public GuitarNote[] getGuitarNotes() {
        return _notes;
    }

    public boolean hasFingerCapo() {
        return _fingerCapo;
    }

    public int getMinFret() {
        return _minFret;
    }

    public int getMaxFret() {
        return _maxFret;
    }

    public int getNPlayingStrings(){ return _nPlayingStrings; }
}
