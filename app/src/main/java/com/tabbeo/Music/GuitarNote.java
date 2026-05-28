package com.tabbeo.Music;

import com.tabbeo.Music.Playable.Note;
import com.tabbeo.Music.Music.Finger;

public class GuitarNote {
    private final Music.GuitarString _string;
    private final int _fret;
    private final Finger _finger;

    public GuitarNote(Music.GuitarString guitarString, int fret) {
        this(guitarString, fret, Finger.NoFingerInfo);
    }

    public GuitarNote(Music.GuitarString guitarString, int fret, Finger finger) {
        if(guitarString == null) throw new RuntimeException("GuitarString is null");
        if(finger == null) throw new RuntimeException("Finger is null");

        if (fret < 0 || fret > Music.MAX_FRETS_GUITAR)
            throw new RuntimeException("Invalid fret in GuitarNote ctr");

        if (fret == 0) {
            if (finger != Finger.OpenString && finger != Finger.NoFingerInfo)
                throw new RuntimeException("This string is played open string. No finger is needed");
        }

        _fret = fret;
        _string = guitarString;
        _finger = finger;
    }

    public Music.GuitarString getString() { return _string; }
    public int getFret() { return _fret; }
    public Note getNote() { return _string.getNote(_fret); }
    public Music.Finger getFinger(){ return _finger; }

    @Override
    public String toString(){
        return getNote().toString();
    }
}
