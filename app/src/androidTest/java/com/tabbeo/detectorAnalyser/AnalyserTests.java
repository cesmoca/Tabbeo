package com.tabbeo.detectorAnalyser;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.tabbeo.Detector.PitchDetector;
import com.tabbeo.TabbeoAppTest;
import com.tabbeo.detectorAnalyser.Analyser.ChordAnalyser;
import com.tabbeo.detectorAnalyser.Analyser.IAnalyser;
import com.tabbeo.detectorAnalyser.Analyser.LabeledWavAudio;
import com.tabbeo.detectorAnalyser.Analyser.PitchAnalyser;
import com.tabbeo.detectorAnalyser.Analyser.TestAudioLibrary;

import java.io.IOException;

/*
IMPORTANT: The report files are stores in /data/data/com.tabbeo/files
You need a rooted phone, and a rooted ES Explorer to access it
 */
public class AnalyserTests extends InstrumentationTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        System.loadLibrary("Detector");
        TabbeoAppTest.setContext(getInstrumentation().getContext());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /*
    Conclusions for e_intermittent:
    - FComb 2.21%. Very poor results. Discarded
    - MComb 24.63%, Poor results. Discarded.
    - Schmitt 50.73%, and didn't get any different note, only null when supposed to be E4. Will study further. It was FAST.
    - YIN: 5.9%, almost two minutes to do the same job. You are kidding me, rite? DISCARDED
    - YINFF: 80.04% in 2 seconds. Found many different notes. The king so far

    Conclusions for three_notes_noormal_pace:
    - FComb 5.25%, one second... Still discarded
    - MComb: 13.15% two  seconds... discarded
    - Schmitt 55.26% freaking fast, no strange notes. Just null when it shouldn't. Maybe it is not very sensitive?
    - YIN: 11.18% in 42 seconds. OMG!
    - YINFFT: 89.47% in 1 second with some strange but short notes.

    Samples Buffer Size for e_intermittent (Rough approx):
    - It does not have to be power of two. Analysed from 1000-10000 in steps of 250
    - It seems that the values are around:
      - For Schmitt --- Best(53.15%): 3250, 6500,8000, 9750. ---Worse(47.7%): 9250, 9000... Not that much change in correctness is what I mean
      - For YINFFT  --- Best(80.78%): 1250, 1500, 1750---Worse (57.77%): 9250, 8750, 8250, 7500...

    // At this point we discard Smitt. It is a good guy, just to conservative. I prefer YINFFT with a good thoughtful filter

    >Samples Buffer Size for e_intermittent, three_notes_normal_pace and three_notes_very_fast (broader):
    - Analysing YINFFT with smaller buffer sizes 50-3000 in steps of 250 and overlap divisor from 2-8 to get a bigger picture. ~9 wait
    - Conclusions:
      - e_intermittent: buffer size 1050 - 1500 with high overlap divisors 5,6,7,8. BEST: 1300/8 81.09%
      - three_notes_normal_pace: buffer size 800 - 1500 with high overlap divisors 5,6,7,8. BEST 1300/8 95.16%
      - three_notes_very_fast_pace: 550 - 2550 with very high overlap divisors 5,6,7,8 BEST 550/8 81.27%

   > Nice analysis. We narrow the search to Buffer Size 800-1500 in steps of 100. Samples divisor 8-10. Tossing in YINFFT threshold 0.5 - 0.8 in steps of 0.5. ~23' wait
    - Conclusions
      - e_intermittent: buffer size 1300-1500 with high overlap divisors 9,10 YINFFT 0.6-0.7 BEST 1400/10 YINFFT 0.65 83.83%
      -three_notes_normal_pace: buffer size 900-1400 with high overlap divisors 9,10 YINFFT 0.55-0.65 BEST 1400/10 YINFFT 0.55 95.06%
      -three_notes_very_fast_pace: buffer size 900-1400 with high overlap divisors YINFFT 0.5-0.55 BEST 900/10 0.5 84.83%

    Ok, it seems that the overlap divisors does not know limits. The higher, the better. Let's keep pushing
    Also, it seems that the faster the notes, the lower the buffer size and yinfft threshold.

    >Finer analysis. Buffer size 900-1400 steps of 50. Overlap divisors 11,12. Yinfft 0.5-0.65 in steps of 0.25. >20' of wait. There we go.
    - e_intermittent: buffer size 1300-1400, high overlap divisors, YINFFT 0.6-0.65 BEST 1400/12 YINFFT 0.65 83.83%
    - three_notes_normal_pace: buffer size 900-1400 high overlap divisors YINFFT 0.5-0.6 BEST 1350/12 YINFFT 0.75 95.25%
    - three_notes_very_fast_pace: buffer size 900-1400 high overlap size YINFFT 0.5-0.55 BEST 900/12 YINFFT 0.5 84.83%

    >>>>  Winning config: buffer size: 1200, overlap divisor 10, YINFFT 0.6 <<<<
        e_intermittent 80.66/83.83: three_notes_normal_pace:93.07/95.06 three_notes_very_fast_pace:81.73/84.83

    Now that we have enough data, let's pick one and see how the results compare to the previous ones. If they are close enough, we have a winner :)
    Let's figure out the post-filter analysis.

     It seems that most blinks are not there for more than two samples (54ms), so we set a limit of 55

     For the Max_noise_before_giving_up... I don't even understand the reason why we want to keep a note being detected if it is not
     (it was set to 500ms wtf). In this case, I prefer to make it a little harder to adapt, and easier to drop. We are going to keep it low,
     twice the amount  needed to adapt a new note. It seems to work fine. :)
     */


    @LargeTest
    public void testPitchDetector() throws IOException, InterruptedException {

        //LabeledWavAudio[] testWavs = {TestAudioLibrary.e_intermittent, TestAudioLibrary.three_notes_normal_pace, TestAudioLibrary.three_notes_very_fast_pace};
        LabeledWavAudio[] testWavs = {TestAudioLibrary.e_intermittent};

        PitchDetector.DetectionType[] worthDetectionTypes = {PitchDetector.DetectionType.aubio_pitch_yinfft};

        for(PitchDetector.DetectionType detectionType : worthDetectionTypes) {
            PitchAnalyser pitchAnalyser = new PitchAnalyser(getInstrumentation(), IAnalyser.Mode.PRE_FILTER, detectionType);

            pitchAnalyser.analyzeWavs(testWavs);
            //pitchAnalyser.analyzeWavs(TestAudioLibrary.noiseWavs);
            //pitchAnalyser.analyzeWavs(TestAudioLibrary.pitchWavs);
        }
    }

    /*

        [EDIT: There was a paradigmatic change. Now we measure the score by checking how much we get right when notes are playing, we ignore silences.
        Silences are "easy" to cut out with a filter, but we want to make sure the actual chord has been gotten right.
        We pretty much are starting the analysis from scratch with this in mind :D The pitch detectors works wonderfully, so that is not needed.]

        [EDIT2: We are approachiing all of this wrong. The score that we use to sort the configurations is completely misleading. We are repeating
        the analysis with a different score. The Chords detector does not work as well as the pitch detector, so we have to completely
        adjust it to what we want. We are rewriting the goal]

        [EDIT3: We rewrote the ChordCorrelator completely, to be more accurate and benefit chords played clearly. We are going to have to repeat
        all the analysis. We have three new parameters: CORRELATION THRESHOLD (new behaviour, [100, -100], FOURTH COMPONENT THRESHOLD [100, -100]
        and INTENSITY THRESHOLD [0, inf].

        GOAL: Tabbeo wants to detect at CERTAIN POINTS that a chord has been played in an ACCURATE and FAST way.
            - We do not care what we detect in "silences", when we are not looking for a chord to be detected
            - Once the begnining of the chord has been played we DO  NOT CARE how long it is being played later

        When a chord needs to be played, we want it to be ACCURATE (give me one or two candidates at the most), not all of them.
        Measure how fast it is detecting it since the theoretical stuff. Once it is detected FAST and ACCURATE, we stop caring until the next chord.

        Welcome back. We are going to start with the analysis of the ChordDetector. Many parameters, interesting stuff. There we go.

        First off, let's give the statistics of the former configuration. It is hard to tell since so much has changed, but approx:
        e_major_intermittent: 0% 0 delay (whut? It seems that 96 as minimun frequency didn't work very well)
        three_chords_normal_pace: 33.33% 134.44 delay
        three_chords_fast_pace: 33.33% 24.33 delay

        Let's start investigating BINS. From 1 to 15.

        >>> 12 seems like the fastest and most accurate... We kind of knew that already <<<

        Ok, MIN FREQUENCY, 86-102 in steps of 1. ~11'. Then redoing with higher values 102-108 to see how it goes
        e_major_intermittent: 100%/118 99,100,101 - 100%/133 98,102
        three_chords_normal_pace: 100%/340 99,100 - 100%/402 97,98,101
        three_chords_fast_pace: 66%/198 99,98,

        >>> Best and fastest divisor seems to be 99, folowed by 98 <<<

        Ok, now INSTANT TUNNING INTERTIA, 0-3 in 0.5

        >>> It seems that a higher divisor made it faster, choosing 2! <<<

        Let's play with the booleans TUNNING and PEAK WINDOWING, just to see what would happen

        >>> Both enabled seemed to work slightly faster <<<

        Now SPARSE QKERNEL CONSANT. 0-0.1 in steps of 0.01.

        >>> There is not much difference here (slightly slower for high values ~0.9). Defaulting to 0.0054 since it gives the same good results <<<

        By this time, the times we work with are 100%/104, 100%/340 and 666.6%/198.
        Ok, now the three big. FILTER INERTIA, CORRELATION THRESHOLD, AND SECOND CANDIDATE THRESHOLD

        1/ We are going to first try a combination of FILTER INTERTIA and CORRELATION THRESHOLD while SECOND CANDIDATE is 1, which means no second candidate at all.

        Running 0-1 for both in steps of 0.2. ~13'. Ok...
        Running again [0.55-0.85] in steps of 0.025.

        >>> 0.6 sounds like a good compromise. A higher divisor is a liiil faster for e_intermittent,
        but detects less for three_chords_fast_pace. Also 0.6 is faster for three_chords_normal_pace.
        We are missing some notes detected if we use less than 0.7 for e_intermittent, but for 0.7
        we miss for three_chords_fast_pace. We choose 0.6 for fast_pace, and it is faster and quieter <<<

        Let's see what are normal noise levels for INTENSITY THRESHOLD. We are going to use the "noise" wavs,
        and also do some live tests.

        >> I would say 80-100 is a good threshold. With everything "quiet" it does not go over 10,
        and when I play the guitar it could well be in the 200-600 range <<<

        Now let's play with CORRELATION THRESHOLD. We are going to turn off fourth components all together, so
        that we only detect chords made of 2 and 3 notes.

     */
    @LargeTest
    public void testChordDetector() throws IOException, InterruptedException {
        ChordAnalyser chordAnalyser = new ChordAnalyser(getInstrumentation(), IAnalyser.Mode.PRE_FILTER);

       // LabeledWavAudio[] testWavs = {TestAudioLibrary.e_major_intermittent, TestAudioLibrary.three_notes_normal_pace, TestAudioLibrary.three_chords_fast_pace};
        LabeledWavAudio[] testWavs = {TestAudioLibrary.e_major_intermittent};

        chordAnalyser.analyzeWavs(testWavs);
        //chordAnalyser.analyzeWavs(TestAudioLibrary.noiseWavs);
        //chordAnalyser.analyzeWavs(TestAudioLibrary.chordWavs);
    }
}
