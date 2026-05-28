package com.tabbeo.CourseLibrary;

import com.tabbeo.Music.Music;
import com.tabbeo.Music.Music.Duration;
import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.Music.Scale;
import com.tabbeo.Music.Track;
import com.tabbeo.Music.Track.TrackPlayable;

public class SongsLibrary {
    public static Track firstAMajor;
    public static Track firstDMajor;
    public static Track firstEMajor;
    public static Track upAndDownAMajor;
    public static Track firstSwitchingChords;
    public static Track aMinorPentatonicPos1Ex1;

    // Legacy tracks, during development
    public static Track tutorialMelody;
    public static Track odeToJoy;
    public static Track lozana; // Chords

    static {
        // Fist A Major chord
        firstAMajor = new Track(new Music.BeatsPerMeasure(1, 4));
        firstAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));

        // First D Major chord
        firstDMajor = new Track(new Music.BeatsPerMeasure(1, 4));
        firstDMajor.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstDMajor.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstDMajor.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstDMajor.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));

        // First E Major chord
        firstEMajor = new Track(new Music.BeatsPerMeasure(1, 4));
        firstEMajor.addMeasure(new TrackPlayable(Chord.EMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstEMajor.addMeasure(new TrackPlayable(Chord.EMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstEMajor.addMeasure(new TrackPlayable(Chord.EMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstEMajor.addMeasure(new TrackPlayable(Chord.EMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));

        // Up and Down A Major chord
        upAndDownAMajor = new Track(new Music.BeatsPerMeasure(1, 4));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        upAndDownAMajor.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        // First time switching chords
        firstSwitchingChords = new Track(new Music.BeatsPerMeasure(1, 4));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));


        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.EMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.EMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.EMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.EMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.EMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.EMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.AMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));
        firstSwitchingChords.addMeasure(new TrackPlayable(Chord.DMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));

        // Tutorial Melody
        tutorialMelody = new Track(new Music.BeatsPerMeasure(1, 4));
        tutorialMelody.addMeasure(new TrackPlayable(Note.E4, Duration.quarter));
        tutorialMelody.addMeasure(new TrackPlayable(Note.F4, Duration.quarter));
        tutorialMelody.addMeasure(new TrackPlayable(Note.B3, Duration.quarter));
        tutorialMelody.addMeasure(new TrackPlayable(Note.C4, Duration.quarter));

        // Legacy tracks, during development
        // Ode to joy
        odeToJoy = new Track(new Music.BeatsPerMeasure(4, 4));

        odeToJoy.addMeasure(new TrackPlayable(Note.E4, Duration.quarter),
                new TrackPlayable(Note.E4, Duration.quarter),
                new TrackPlayable(Note.F4, Duration.quarter),
                new TrackPlayable(Note.G4, Duration.quarter));
        odeToJoy.addMeasure(new TrackPlayable(Note.G4, Duration.quarter),
                new TrackPlayable(Note.F4, Duration.quarter),
                new TrackPlayable(Note.E4, Duration.quarter),
                new TrackPlayable(Note.D4, Duration.quarter));
        odeToJoy.addMeasure(new TrackPlayable(Note.C4, Duration.quarter),
                new TrackPlayable(Note.C4, Duration.quarter),
                new TrackPlayable(Note.D4, Duration.quarter),
                new TrackPlayable(Note.E4, Duration.quarter));
        odeToJoy.addMeasure(new TrackPlayable(Note.E4, Duration.quarter_dotted),
                new TrackPlayable(Note.D4, Duration.eight),
                new TrackPlayable(Note.D4, Duration.half));

        odeToJoy.addMeasure(new TrackPlayable(Note.E4, Duration.quarter),
                new TrackPlayable(Note.E4, Duration.quarter),
                new TrackPlayable(Note.F4, Duration.quarter),
                new TrackPlayable(Note.G4, Duration.quarter));
        odeToJoy.addMeasure(new TrackPlayable(Note.G4, Duration.quarter),
                new TrackPlayable(Note.F4, Duration.quarter),
                new TrackPlayable(Note.E4, Duration.quarter),
                new TrackPlayable(Note.D4, Duration.quarter));
        odeToJoy.addMeasure(new TrackPlayable(Note.C4, Duration.quarter),
                new TrackPlayable(Note.C4, Duration.quarter),
                new TrackPlayable(Note.D4, Duration.quarter),
                new TrackPlayable(Note.E4, Duration.quarter));
        odeToJoy.addMeasure(new TrackPlayable(Note.D4, Duration.quarter_dotted),
                new TrackPlayable(Note.C4, Duration.eight),
                new TrackPlayable(Note.C4, Duration.half));

        // Lozana (rhythmic song)
        lozana = new Track(new Music.BeatsPerMeasure(2, 4));
        lozana.addMeasure(new TrackPlayable(Chord.AMinor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE),
                new TrackPlayable(Chord.AMinor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));
        lozana.addMeasure(new TrackPlayable(Chord.GMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE),
                new TrackPlayable(Chord.GMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));
        lozana.addMeasure(new TrackPlayable(Chord.FMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE),
                new TrackPlayable(Chord.GMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_UP_STROKE));
        lozana.addMeasure(new TrackPlayable(Chord.FMajor, Duration.eight, Chord.ChordStrokePattern.PICK_DOWN_STROKE),
                new TrackPlayable(Chord.FMajor, Duration.eight, Chord.ChordStrokePattern.PICK_UP_STROKE),
                new TrackPlayable(Chord.EMajor, Duration.quarter, Chord.ChordStrokePattern.PICK_DOWN_STROKE));

        aMinorPentatonicPos1Ex1 = new Track(new Music.BeatsPerMeasure(1, 4), Scale.PENTATONIC_A_MINOR_POS_1);

        // Going up the scale
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.A2, Duration.quarter)); // Sixth string
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.C3, Duration.quarter));
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.D3, Duration.quarter)); // Fifth string
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.E3, Duration.quarter));
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.G3, Duration.quarter)); // Fourth string
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.A3, Duration.quarter));
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.C4, Duration.quarter)); // Third string
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.D4, Duration.quarter));
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.E4, Duration.quarter)); // Second string
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.G4, Duration.quarter));
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.A4, Duration.quarter)); // First string
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.C5, Duration.quarter));

        // Going down the scale
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.C5, Duration.quarter)); // First string
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.A4, Duration.quarter));
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.G4, Duration.quarter)); // Second string
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.E4, Duration.quarter));
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.D4, Duration.quarter)); // Third string
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.C4, Duration.quarter));
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.A3, Duration.quarter)); // Fourth string
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.G3, Duration.quarter));
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.E3, Duration.quarter)); // Fifth string
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.D3, Duration.quarter));
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.C3, Duration.quarter)); // Sixth string
        aMinorPentatonicPos1Ex1.addMeasure(new TrackPlayable(Note.A2, Duration.quarter));
    }
}
