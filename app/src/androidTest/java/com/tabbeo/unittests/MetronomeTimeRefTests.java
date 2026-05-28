package com.tabbeo.unittests;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.tabbeo.ExerciseLibraryTest;
import com.tabbeo.Music.IMetronomeTimeRef;
import com.tabbeo.Music.MetronomeTicker;
import com.tabbeo.Music.MetronomeTimeRefInteractive;
import com.tabbeo.Music.MetronomeTimeRefTimed;
import com.tabbeo.Music.Track;

class MetronomeTimeRefTimedTest extends MetronomeTimeRefTimed {

    public MetronomeTimeRefTimedTest(Track track, int tempo) {
        super(track, tempo);
    }

    public long getRealMelodyStartTime() {
        return _realMelodyStartTime;
    }

    public long getEndTimestamp() {
        return _endTimestamp;
    }
}

class MetronomeTimeRefInteractiveTest extends MetronomeTimeRefInteractive {

    public MetronomeTimeRefInteractiveTest(Track track, int tempo) {
        super(track, tempo);
    }

    public long getFromVirtualTimestamp() {
        return _fromVirtualTimestamp;
    }

    public long getFromRealTimestamp() {
        return _fromRealTimestamp;
    }

    public long getAdvanceTimeDuration() {
        return _advanceTimeDuration;
    }
}

class IMetronomeTimeRefTest extends IMetronomeTimeRef{
    public long virtualTimestamp;

    public IMetronomeTimeRefTest(Track track, int tempo) {
        super(track, tempo);
    }

    @Override
    public long getVirtualTimestamp(long realTimestamp) {
        return virtualTimestamp;
    }

    @Override
    public void advanceToNextPlayable(long realTimestamp) {
    }

    public void setEndOfExercise(boolean endOfExercise){
        _endOfExercise = endOfExercise;
    }

    public void setCurrentPlayableIndex(int playableIndex){
        _currentPlayableIndex = playableIndex;
    }

    public long getEndTimestamp(){
        return _endTimestamp;
    }

}

public class MetronomeTimeRefTests extends InstrumentationTestCase {
    int _tempo = 75;
    MetronomeTimeRefTimedTest _metronomeTimed;
    MetronomeTimeRefInteractiveTest _metronomeInteractive;
    IMetronomeTimeRefTest _iMetronome;

    @Override
    public void setUp() {
        _metronomeTimed = new MetronomeTimeRefTimedTest(ExerciseLibraryTest.twoNotesTrack, _tempo);
        _metronomeInteractive = new MetronomeTimeRefInteractiveTest(ExerciseLibraryTest.twoNotesTrack, _tempo);
        _iMetronome = new IMetronomeTimeRefTest(ExerciseLibraryTest.twoNotesTrack, _tempo);

        assertFalse(_metronomeTimed.isPlaying());
        assertFalse(_metronomeInteractive.isPlaying());
        assertFalse(_iMetronome.isPlaying());
    }

    @SmallTest
    public void testIMetronomeTimeRef_TempoAlwaysWithinLimits() {
        try {
            _iMetronome = new IMetronomeTimeRefTest(ExerciseLibraryTest.twoNotesTrack, -1);
            assertTrue(false);
        } catch (RuntimeException ignored) {
        }

        try {
            _iMetronome = new IMetronomeTimeRefTest(ExerciseLibraryTest.twoNotesTrack, 0);
            assertTrue(false);
        } catch (RuntimeException ignored) {
        }

        try {
            _iMetronome = new IMetronomeTimeRefTest(ExerciseLibraryTest.twoNotesTrack, IMetronomeTimeRef.MAX_TEMPO + 1);
            assertTrue(false);
        } catch (RuntimeException ignored) {
        }
    }

    @SmallTest
    public void testMsPerBeat_IsCorrect() {
        long msPerBeat = (long) (60 * 1000 / (double) _tempo);
        assertEquals(msPerBeat, _iMetronome.getMsPerBeat());
    }

    @SmallTest
    public void testCountdownAlwaysPositive() {
        int countdown = _iMetronome.getNBeatsInCountdown();
        assertTrue(countdown >= 0);
    }

    @SmallTest
    public void testIMetronomeTimeRef_IsPlaying() {
        assertFalse(_iMetronome.isPlaying());

        _iMetronome.start(0 /*now*/);
        assertEquals(-1, _iMetronome.getCurrentPlayableIndex());

        assertTrue(_iMetronome.isPlaying());

        _iMetronome.stop();
        assertFalse(_iMetronome.isPlaying());
        assertEquals(-1, _iMetronome.getCurrentPlayableIndex());

        _iMetronome.stop();
        assertFalse(_iMetronome.isPlaying());
        assertEquals(-1, _iMetronome.getCurrentPlayableIndex());
    }

    @SmallTest
    public void testMelodyTime_IsCorrect() {
        int countdown = _iMetronome.getNBeatsInCountdown();
        long msPerBeat = _iMetronome.getMsPerBeat();
        _iMetronome.start(0/*now*/);
        assertEquals(-1, _iMetronome.getCurrentPlayableIndex());
        long melodyStartTime = _iMetronome.getCountdownDuration();

        assertEquals(countdown * msPerBeat, melodyStartTime);
    }

    @SmallTest
    public void testGetMsPerBeat_ItShouldBeAnExactNumber() {
        // We have decided that, given any tempo, we are going to "tweak it" a little bit so that
        // MsPerBeat is an exact integer number (the engine will thank this).
        _iMetronome = new IMetronomeTimeRefTest(ExerciseLibraryTest.twoNotesTrack, 50);
        assertEquals(0, 0L);

        _iMetronome = new IMetronomeTimeRefTest(ExerciseLibraryTest.twoNotesTrack, 150);
        assertEquals(0, 0L);

        _iMetronome = new IMetronomeTimeRefTest(ExerciseLibraryTest.twoNotesTrack, 22);
        assertEquals(0, 0L);

        _iMetronome = new IMetronomeTimeRefTest(ExerciseLibraryTest.twoNotesTrack, 31);
        assertEquals(0, 0L);
    }

    @SmallTest
    public void testIMetronomeTimeRef_StartTimesAndDurationsAreCorrect(){
        _iMetronome = new IMetronomeTimeRefTest(ExerciseLibraryTest.twoNotesTrack, 50);
        assertEquals(0, _iMetronome.getPlayableStartTime(0));
        assertEquals(0, _iMetronome.getCurrentPlayableIndex());
        assertEquals(_iMetronome.getPlayableDurationInMs(1), _iMetronome.getPlayableStartTime(1));

        _iMetronome = new IMetronomeTimeRefTest(ExerciseLibraryTest.twoNotesTrack, 100);
        assertEquals(0, _iMetronome.getPlayableStartTime(0));
        assertEquals(0, _iMetronome.getCurrentPlayableIndex());
        assertEquals(_iMetronome.getPlayableDurationInMs(1), _iMetronome.getPlayableStartTime(1));

        _iMetronome = new IMetronomeTimeRefTest(ExerciseLibraryTest.oneNoteTrack, 50);
        assertEquals(0, _iMetronome.getPlayableStartTime(0));
        assertEquals(0, _iMetronome.getCurrentPlayableIndex());
    }

    @SmallTest
    public void testMetronomeTimeRefTimed_CountdownIsNegativeTimestamp() {
        long now = 100;

        _metronomeTimed.start(now);
        assertEquals(-1, _metronomeTimed.getCurrentPlayableIndex());

        long countdownDuration = _metronomeTimed.getCountdownDuration();

        assertFalse(_metronomeTimed.isEndOfExercise());

        assertEquals(-countdownDuration, _metronomeTimed.getTimestamp(now));
        assertEquals(-countdownDuration + 1, _metronomeTimed.getTimestamp(now + 1));
        assertEquals(-countdownDuration + 10, _metronomeTimed.getTimestamp(now + 10));

        assertEquals(-1, _metronomeTimed.getCurrentPlayableIndex());

        assertEquals(0,_metronomeTimed.getTimestamp(now + _metronomeTimed.getCountdownDuration()));
        assertEquals(1,_metronomeTimed.getTimestamp(now + _metronomeTimed.getCountdownDuration()) + 1);
        assertEquals(10,_metronomeTimed.getTimestamp(now + _metronomeTimed.getCountdownDuration()) + 10);

        assertEquals(0, _metronomeTimed.getCurrentPlayableIndex());

        assertEquals(800,_metronomeTimed.getTimestamp(now + _metronomeTimed.getCountdownDuration() + _metronomeTimed.getPlayableDurationInMs(0)));
        assertEquals(1, _metronomeTimed.getCurrentPlayableIndex());

        assertFalse(_metronomeTimed.isEndOfExercise());

        // We go all the way to the end
        assertEquals(_metronomeTimed.getEndTimestamp(),_metronomeTimed.getTimestamp(now + _metronomeTimed.getCountdownDuration() + _metronomeTimed.getPlayableDurationInMs(0) + _metronomeTimed.getPlayableDurationInMs(0)));

        assertTrue(_metronomeTimed.isEndOfExercise());

    }

    @SmallTest
    public void testMetronomeTimeRefTimed_TimePasses() throws InterruptedException {
        assertEquals(0, _metronomeTimed.getRealMelodyStartTime());
        assertFalse(_metronomeTimed.isPlaying());

        long now = 100;

        _metronomeTimed.start(now);
        assertEquals(-1, _metronomeTimed.getCurrentPlayableIndex());

        long countdownDuration = _metronomeTimed.getCountdownDuration();

        assertEquals(now + countdownDuration, _metronomeTimed.getRealMelodyStartTime());
        assertTrue(_metronomeTimed.isPlaying());

        long oldTimestamp = -countdownDuration;

        for (int i = 1; i < 10; ++i) {
            Thread.sleep(10);
            long newTimestamp = _metronomeTimed.getTimestamp(now + i);

            assertTrue(oldTimestamp < newTimestamp);
            assertEquals(-countdownDuration + i, newTimestamp);
            oldTimestamp = newTimestamp;
        }

        _metronomeTimed.stop();
        assertEquals(0, _metronomeTimed.getRealMelodyStartTime());
        assertFalse(_metronomeTimed.isPlaying());
        assertEquals(-1, _metronomeTimed.getCurrentPlayableIndex());

    }

    @SmallTest
    public void testMetronomeTimeRefInteractive_StartStop() {
        long now = 100;

        assertFalse(_metronomeInteractive.isPlaying());

        assertEquals(0, _metronomeInteractive.getFromVirtualTimestamp());
        assertEquals(0, _metronomeInteractive.getFromRealTimestamp());
        assertEquals(0, _metronomeInteractive.getAdvanceTimeDuration());

        // Starting the interactive metronome
        _metronomeInteractive.start(now);
        assertEquals(-1, _metronomeInteractive.getCurrentPlayableIndex());

        long countdownDuration = _metronomeInteractive.getCountdownDuration();

        assertTrue(_metronomeInteractive.isPlaying());

        assertEquals(-countdownDuration, _metronomeInteractive.getFromVirtualTimestamp());
        assertEquals(now, _metronomeInteractive.getFromRealTimestamp());
        assertEquals(_metronomeInteractive.getCountdownDuration(), _metronomeInteractive.getAdvanceTimeDuration());

        // Stopping the interactive metronome
        _metronomeInteractive.stop();
        assertEquals(-1, _metronomeInteractive.getCurrentPlayableIndex());

        assertFalse(_metronomeInteractive.isPlaying());

        assertEquals(0, _metronomeInteractive.getFromVirtualTimestamp());
        assertEquals(0, _metronomeInteractive.getFromRealTimestamp());
        assertEquals(0, _metronomeInteractive.getAdvanceTimeDuration());
    }

    @SmallTest
    public void testMetronomeTimeRefInteractive_CountdownIsNegativeTimestamp() {
        long now = 100;

        _metronomeInteractive.start(now);
        assertEquals(-1, _metronomeInteractive.getCurrentPlayableIndex());

        assertFalse(_metronomeInteractive.isEndOfExercise());

        long countdownDuration = _metronomeInteractive.getCountdownDuration();

        assertEquals(-countdownDuration, _metronomeInteractive.getTimestamp(now));
        assertEquals(-countdownDuration + 1, _metronomeInteractive.getTimestamp(now + 1));
        assertEquals(-countdownDuration + 10, _metronomeInteractive.getTimestamp(now + 10));
        assertEquals(0,_metronomeInteractive.getTimestamp(now + _metronomeInteractive.getCountdownDuration()));

        assertEquals(0, _metronomeInteractive.getCurrentPlayableIndex());

        // From here, it should get stuck at the very beginning of 0, because it is interactive
        assertEquals(0,_metronomeInteractive.getTimestamp(now + _metronomeInteractive.getCountdownDuration() + 1));
        assertEquals(0,_metronomeInteractive.getTimestamp(now + _metronomeInteractive.getCountdownDuration() + 10));
        assertEquals(0,_metronomeInteractive.getTimestamp(now + _metronomeInteractive.getCountdownDuration() + 100));

        assertEquals(0, _metronomeInteractive.getCurrentPlayableIndex());
        assertFalse(_metronomeInteractive.isEndOfExercise());

        // Then we advance to the next note
        now = 1000;
        long nextNoteDuration = _metronomeInteractive.getPlayableDurationInMs(0);
        _metronomeInteractive.advanceToNextPlayable(now);

        assertEquals(0,_metronomeInteractive.getTimestamp(now));
        assertEquals(10,_metronomeInteractive.getTimestamp(now +  10));

        // And we get stuck here, at the begining of playable 1. We check we change to playable 1

        assertEquals(nextNoteDuration,_metronomeInteractive.getTimestamp(now + nextNoteDuration));

        assertEquals(1, _metronomeInteractive.getCurrentPlayableIndex());
        assertFalse(_metronomeInteractive.isEndOfExercise());

        assertEquals(nextNoteDuration,_metronomeInteractive.getTimestamp(now + nextNoteDuration+1));
        assertEquals(nextNoteDuration,_metronomeInteractive.getTimestamp(now + nextNoteDuration+10));
        assertEquals(nextNoteDuration,_metronomeInteractive.getTimestamp(now + nextNoteDuration+100));

        // Ok, we advance all the way until the end
        now = 9999;
        _metronomeInteractive.advanceToNextPlayable(now);
        long virtualTimestamp = nextNoteDuration;
        nextNoteDuration = _metronomeInteractive.getPlayableDurationInMs(0);

        assertEquals(virtualTimestamp,_metronomeInteractive.getTimestamp(now));
        assertFalse(_metronomeInteractive.isEndOfExercise());

        assertEquals(virtualTimestamp + 1,_metronomeInteractive.getTimestamp(now + 1));
        assertFalse(_metronomeInteractive.isEndOfExercise());

        assertEquals(virtualTimestamp + 10,_metronomeInteractive.getTimestamp(now + 10));
        assertFalse(_metronomeInteractive.isEndOfExercise());

        // This should be the very end of the last note of the twoNotesTrack, so it should be the end of the exercise
        assertEquals(virtualTimestamp + nextNoteDuration,_metronomeInteractive.getTimestamp(now + nextNoteDuration));
        assertTrue(_metronomeInteractive.isEndOfExercise());
        _metronomeInteractive.stop();

        assertEquals(-1, _metronomeInteractive.getCurrentPlayableIndex());

    }

    @SmallTest
    public void testMetronomeTimeRefTimed_GetTimestamp_WayOverTheEnd_ReturnsLastIndex(){
        long now = 1234;
        _metronomeTimed.start(now);

        _metronomeTimed.getTimestamp(now);
        assertEquals(-1, _metronomeTimed.getCurrentPlayableIndex());
        _metronomeTimed.getTimestamp(now + _metronomeTimed.getCountdownDuration());
        assertEquals(0, _metronomeTimed.getCurrentPlayableIndex());
        _metronomeTimed.getTimestamp(now + _metronomeTimed.getCountdownDuration() + _metronomeTimed.getPlayableDurationInMs(0));
        assertEquals(1, _metronomeTimed.getCurrentPlayableIndex());
        _metronomeTimed.getTimestamp(now + _metronomeTimed.getCountdownDuration() + _metronomeTimed.getPlayableDurationInMs(0) + _metronomeTimed.getPlayableDurationInMs(1) - 1);
        assertEquals(1, _metronomeTimed.getCurrentPlayableIndex());
        _metronomeTimed.getTimestamp(now + _metronomeTimed.getCountdownDuration() + _metronomeTimed.getPlayableDurationInMs(0) + _metronomeTimed.getPlayableDurationInMs(1));
        assertEquals(1, _metronomeTimed.getCurrentPlayableIndex());
        _metronomeTimed.getTimestamp(now + _metronomeTimed.getCountdownDuration() + _metronomeTimed.getPlayableDurationInMs(0) + _metronomeTimed.getPlayableDurationInMs(1) + 1);
        assertEquals(1, _metronomeTimed.getCurrentPlayableIndex());
        _metronomeTimed.getTimestamp(now + _metronomeTimed.getCountdownDuration() + _metronomeTimed.getPlayableDurationInMs(0) + _metronomeTimed.getPlayableDurationInMs(1) + 10);
        assertEquals(1, _metronomeTimed.getCurrentPlayableIndex());
        _metronomeTimed.getTimestamp(now + _metronomeTimed.getCountdownDuration() + _metronomeTimed.getPlayableDurationInMs(0) + _metronomeTimed.getPlayableDurationInMs(1) + 100);
        assertEquals(1, _metronomeTimed.getCurrentPlayableIndex());
    }

    ////////////////////////////////////////////////////////////////////
    // MetronomeTicker tests
    ////////////////////////////////////////////////////////////////////
    @SmallTest
    public void testMetronomeTicker_FastToggle() {
        long msPerBeat = 250;
        MetronomeTicker ticker = new MetronomeTicker();
        ticker.start(msPerBeat); // On
        ticker.stop(); // Off
        ticker.start(msPerBeat); // On
        ticker.stop(); // Off
        ticker.start(msPerBeat); // On
        ticker.stop(); // Off
        ticker.start(msPerBeat); // On
    }
}
