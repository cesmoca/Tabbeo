package com.tabbeo.detectorAnalyser.Analyser;

import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.Music.Playable.Silence;

public class TestAudioLibrary {
    // ----------- BOTH --------------------
    public static LabeledWavAudio background_noise = new LabeledWavAudio(com.tabbeo.test.R.raw.background_noise,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
            });

    public static LabeledWavAudio intense_noise = new LabeledWavAudio(com.tabbeo.test.R.raw.intense_noise,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
            });

    public static LabeledWavAudio silence = new LabeledWavAudio(com.tabbeo.test.R.raw.silence,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
            });

    // ----------- PITCH ONLY --------------------
    public static LabeledWavAudio e_bad_quality = new LabeledWavAudio(com.tabbeo.test.R.raw.e_bad_quality,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(1284, Note.E4),
                    new LabeledWavAudio.Label(6137, Silence.SILENCE),
            });

    public static LabeledWavAudio e_intermittent = new LabeledWavAudio(com.tabbeo.test.R.raw.e_intermittent,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(1858, Note.E4),
                    new LabeledWavAudio.Label(2798, Silence.SILENCE),
                    new LabeledWavAudio.Label(3513, Note.E4),
                    new LabeledWavAudio.Label(4420, Silence.SILENCE),
                    new LabeledWavAudio.Label(5093, Note.E4),
                    new LabeledWavAudio.Label(6129, Silence.SILENCE),
                    new LabeledWavAudio.Label(6855, Note.E4),
                    new LabeledWavAudio.Label(7805, Silence.SILENCE),
                    new LabeledWavAudio.Label(8360, Note.E4),
                    new LabeledWavAudio.Label(9108, Silence.SILENCE),
                    new LabeledWavAudio.Label(9514, Note.E4),
                    new LabeledWavAudio.Label(10112, Silence.SILENCE),
                    new LabeledWavAudio.Label(10496, Note.E4),
                    new LabeledWavAudio.Label(10965, Silence.SILENCE),
                    new LabeledWavAudio.Label(11382, Note.E4),
                    new LabeledWavAudio.Label(11565, Silence.SILENCE),
                    new LabeledWavAudio.Label(12375, Note.E4),
                    new LabeledWavAudio.Label(12700, Silence.SILENCE),
                    new LabeledWavAudio.Label(13443, Note.E4),
                    new LabeledWavAudio.Label(13700, Silence.SILENCE),
                    new LabeledWavAudio.Label(14521, Note.E4),
                    new LabeledWavAudio.Label(14938, Silence.SILENCE),
                    new LabeledWavAudio.Label(15077, Note.E4),
                    new LabeledWavAudio.Label(15472, Silence.SILENCE),
                    new LabeledWavAudio.Label(15643, Note.E4),
                    new LabeledWavAudio.Label(15900, Silence.SILENCE),
                    new LabeledWavAudio.Label(16219, Note.E4),
                    new LabeledWavAudio.Label(16646, Silence.SILENCE),
                    new LabeledWavAudio.Label(16892, Note.E4),
                    new LabeledWavAudio.Label(17200, Silence.SILENCE),
            });

    public static LabeledWavAudio e_noisy_background = new LabeledWavAudio(com.tabbeo.test.R.raw.e_noisy_background,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(2685, Note.E4),
                    new LabeledWavAudio.Label(4366, Silence.SILENCE)
            });

    public static LabeledWavAudio e_one_note = new LabeledWavAudio(com.tabbeo.test.R.raw.e_one_note,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(3305, Note.E4),
                    new LabeledWavAudio.Label(5398, Silence.SILENCE),
            });

    public static LabeledWavAudio e_poor_quality = new LabeledWavAudio(com.tabbeo.test.R.raw.e_poor_quality,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(1315, Note.E4),
                    new LabeledWavAudio.Label(8121, Silence.SILENCE),
            });

    public static LabeledWavAudio three_notes_fast_pace = new LabeledWavAudio(com.tabbeo.test.R.raw.three_notes_fast_pace,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(2023, Note.E4),
                    new LabeledWavAudio.Label(2124, Note.F4),
                    new LabeledWavAudio.Label(2317, Note.G4),
                    new LabeledWavAudio.Label(2463, Note.F4),
                    new LabeledWavAudio.Label(2664, Note.E4),
                    new LabeledWavAudio.Label(3192, Silence.SILENCE),
            });

    public static LabeledWavAudio three_notes_normal_pace = new LabeledWavAudio(com.tabbeo.test.R.raw.three_notes_normal_pace,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(2435, Note.E4),
                    new LabeledWavAudio.Label(2984, Note.F4),
                    new LabeledWavAudio.Label(3497, Note.G4),
                    new LabeledWavAudio.Label(4074, Note.F4),
                    new LabeledWavAudio.Label(4647, Note.E4),
                    new LabeledWavAudio.Label(5588, Silence.SILENCE),
            });

    public static LabeledWavAudio three_notes_slow_pace = new LabeledWavAudio(com.tabbeo.test.R.raw.three_notes_slow_pace,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(2127, Note.E4),
                    new LabeledWavAudio.Label(3178, Note.F4),
                    new LabeledWavAudio.Label(4308, Note.G4),
                    new LabeledWavAudio.Label(5398, Note.F4),
                    new LabeledWavAudio.Label(6479, Note.E4),
                    new LabeledWavAudio.Label(7928, Silence.SILENCE),
            });

    public static LabeledWavAudio three_notes_very_fast_pace = new LabeledWavAudio(com.tabbeo.test.R.raw.three_notes_very_fast_pace,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(2428, Note.E4),
                    new LabeledWavAudio.Label(2527, Note.F4),
                    new LabeledWavAudio.Label(2662, Note.G4),
                    new LabeledWavAudio.Label(2788, Note.F4),
                    new LabeledWavAudio.Label(2935, Note.E4),
                    new LabeledWavAudio.Label(3198, Silence.SILENCE),
            });

    // ----------- CHORD ONLY --------------------
    public static LabeledWavAudio a_major_noisy_background = new LabeledWavAudio(com.tabbeo.test.R.raw.a_major_noisy_background,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(4028, Chord.AMajor),
                    new LabeledWavAudio.Label(8093, Silence.SILENCE),
            });

    public static LabeledWavAudio e_major_intermittent = new LabeledWavAudio(com.tabbeo.test.R.raw.e_major_intermittent,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(2061, Chord.EMajor),
                    new LabeledWavAudio.Label(3370, Silence.SILENCE),
                    new LabeledWavAudio.Label(4602, Chord.EMajor),
                    new LabeledWavAudio.Label(6080, Silence.SILENCE),
                    new LabeledWavAudio.Label(7026, Chord.EMajor),
                    new LabeledWavAudio.Label(8724, Silence.SILENCE),
                    new LabeledWavAudio.Label(9800, Chord.EMajor),
                    new LabeledWavAudio.Label(11342, Silence.SILENCE),
                    new LabeledWavAudio.Label(12276, Chord.EMajor),
                    new LabeledWavAudio.Label(13196, Silence.SILENCE),
                    new LabeledWavAudio.Label(13689, Chord.EMajor),
                    new LabeledWavAudio.Label(14544, Silence.SILENCE),
                    new LabeledWavAudio.Label(14907, Chord.EMajor),
                    new LabeledWavAudio.Label(15763, Silence.SILENCE),
                    new LabeledWavAudio.Label(16152, Chord.EMajor),
                    new LabeledWavAudio.Label(16903, Silence.SILENCE),
                    new LabeledWavAudio.Label(17227, Chord.EMajor),
                    new LabeledWavAudio.Label(17785, Silence.SILENCE),
                    new LabeledWavAudio.Label(17914, Chord.EMajor),
                    new LabeledWavAudio.Label(18446, Silence.SILENCE),
                    new LabeledWavAudio.Label(18640, Chord.EMajor),
                    new LabeledWavAudio.Label(19211, Silence.SILENCE),
                    new LabeledWavAudio.Label(19353, Chord.EMajor),
                    new LabeledWavAudio.Label(19963, Silence.SILENCE),
                    new LabeledWavAudio.Label(20157, Chord.EMajor),
                    new LabeledWavAudio.Label(20831, Silence.SILENCE),
            });

    public static LabeledWavAudio e_major_one_chord = new LabeledWavAudio(com.tabbeo.test.R.raw.e_major_one_chord,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(3599, Chord.EMajor),
                    new LabeledWavAudio.Label(5898, Silence.SILENCE),
            });

    public static LabeledWavAudio e_minor_one_chord = new LabeledWavAudio(com.tabbeo.test.R.raw.e_minor_one_chord,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(4367, Chord.EMinor),
                    new LabeledWavAudio.Label(7030, Silence.SILENCE),
            });

    public static LabeledWavAudio f_mayor_bad_quality = new LabeledWavAudio(com.tabbeo.test.R.raw.f_mayor_bad_quality,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(1267, Chord.FMajor),
                    new LabeledWavAudio.Label(8128, Silence.SILENCE),
            });

    public static LabeledWavAudio f_mayor_poor_quality = new LabeledWavAudio(com.tabbeo.test.R.raw.f_mayor_poor_quality,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(903, Chord.FMajor),
            });

    public static LabeledWavAudio three_chords_fast_pace = new LabeledWavAudio(com.tabbeo.test.R.raw.three_chords_fast_pace,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(2156, Chord.GMajor),
                    new LabeledWavAudio.Label(2671, Chord.FMajor),
                    new LabeledWavAudio.Label(3192, Chord.EMajor),
                    new LabeledWavAudio.Label(3694, Silence.SILENCE),
            });

    public static LabeledWavAudio three_chords_normal_pace = new LabeledWavAudio(com.tabbeo.test.R.raw.three_chords_normal_pace,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(2197, Chord.GMajor),
                    new LabeledWavAudio.Label(3218, Chord.FMajor),
                    new LabeledWavAudio.Label(4337, Chord.EMajor),
                    new LabeledWavAudio.Label(5775, Silence.SILENCE),
            });

    public static LabeledWavAudio three_chords_slow_pace = new LabeledWavAudio(com.tabbeo.test.R.raw.three_chords_slow_pace,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(2570, Chord.GMajor),
                    new LabeledWavAudio.Label(4138, Chord.FMajor),
                    new LabeledWavAudio.Label(6004, Chord.EMajor),
            });

    public static LabeledWavAudio three_chords_very_fast_pace = new LabeledWavAudio(com.tabbeo.test.R.raw.three_chords_very_fast_pace,
            new LabeledWavAudio.Label[]{
                    new LabeledWavAudio.Label(0, Silence.SILENCE),
                    new LabeledWavAudio.Label(2660, Chord.GMajor),
                    new LabeledWavAudio.Label(2878, Chord.FMajor),
                    new LabeledWavAudio.Label(3163, Chord.EMajor),
                    new LabeledWavAudio.Label(3509, Silence.SILENCE),
            });


    public static LabeledWavAudio[] noiseWavs = {background_noise, intense_noise, silence};

    public static LabeledWavAudio[] pitchWavs = {e_bad_quality, e_intermittent, e_noisy_background, e_one_note, e_poor_quality, three_notes_fast_pace,
            three_notes_normal_pace, three_notes_slow_pace, three_notes_very_fast_pace};

    public static LabeledWavAudio[] chordWavs = {a_major_noisy_background, e_major_intermittent, e_major_one_chord, e_minor_one_chord, f_mayor_bad_quality,
            f_mayor_poor_quality, three_chords_fast_pace, three_chords_normal_pace, three_chords_slow_pace, three_chords_very_fast_pace};
}