package com.tabbeo.Music;

import android.support.annotation.CallSuper;

public abstract class IMetronomeTimeRef {

    public enum Type {TIMED, INTERACTIVE}

    public static final int MAX_TEMPO = 250;
    private static final int DEFAULT_COUNTDOWN = 3;

    // Timed & common members
    final private int _nBeatsInCountdown = DEFAULT_COUNTDOWN;
    final private long _msPerBeat;
    final protected long _countdownDuration;

    private boolean _playing;

    // We are more than a metronome, we also now the start time for each playable and their durations
    final private long[] _playablesStartTimes;
    final private long[] _playablesDurations;
    protected final long _endTimestamp;

    protected int _currentPlayableIndex;
    protected boolean _endOfExercise;

    // It is important to note that we "tweak" the tempo, so that
    // msPerBeat is an exact integer number. Better for the engine to work this way
    public IMetronomeTimeRef(Track track, int tempo) {
        if (tempo <= 0 || tempo > MAX_TEMPO)
            throw new RuntimeException("Invalid tempo specified: " + tempo);

        _msPerBeat = (long) (60 * 1000 / (double) tempo);
        _countdownDuration = _nBeatsInCountdown * _msPerBeat;
        _playing = false;

        // Let's set the playables' durations in ms
        _playablesDurations = new long[track.getNPlayables()];
        for (int i = 0; i < track.getNPlayables(); ++i) {
            _playablesDurations[i] = getDurationInMs(track, i);
        }

        // Let's set the playables' start times
        _playablesStartTimes = new long[track.getNPlayables()];
        _playablesStartTimes[0] = 0; // First playable's start time is always 0
        for (int i = 1; i < track.getNPlayables(); ++i) {
            _playablesStartTimes[i] = _playablesStartTimes[i - 1] + _playablesDurations[i - 1];
        }

        int lastPlayableIndex = _playablesDurations.length - 1;
        _endTimestamp = _playablesStartTimes[lastPlayableIndex] + _playablesDurations[lastPlayableIndex];
    }

    @CallSuper
    public void start(long realTimestamp) {
        if (_playing)
            throw new RuntimeException("We are starting the metronome time ref, but it is already started");

        _playing = true;
        _currentPlayableIndex = -1;
        _endOfExercise = false;
    }

    @CallSuper
    public void stop() {
        _currentPlayableIndex = -1;
        _playing = false;
    }

    protected abstract long getVirtualTimestamp(long realTimestamp);

    final public long getTimestamp(long realTimestamp) {
        long virtualTimestamp = getVirtualTimestamp(realTimestamp);

        if (_currentPlayableIndex == -1) { // Still in countdown
            if (virtualTimestamp >= 0) _currentPlayableIndex = 0;
        } else {
            if (virtualTimestamp >= (getPlayableStartTime(_currentPlayableIndex) + getPlayableDurationInMs(_currentPlayableIndex))) {
                if (_currentPlayableIndex < _playablesDurations.length - 1) {
                    _currentPlayableIndex++;
                }

                if (virtualTimestamp >= _endTimestamp) {
                    _endOfExercise = true;
                }
            }
        }
        return virtualTimestamp;
    }

    public long getMsPerBeat() {
        return _msPerBeat;
    }

    public int getNBeatsInCountdown() {
        return _nBeatsInCountdown;
    }

    public boolean isPlaying() {
        return _playing;
    }

    public int getCurrentPlayableIndex() {
        return _currentPlayableIndex;
    }

    public long getCurrentPlayableDuration() {
        return _playablesDurations[_currentPlayableIndex];
    }

    public long getPlayableStartTime(int playableIndex) {
        return _playablesStartTimes[playableIndex];
    }

    public long getCountdownDuration(){ return _countdownDuration; }

    public long getPlayableDurationInMs(int playableIndex) {
        return _playablesDurations[playableIndex];
    }

    public boolean isEndOfExercise() {
        return _endOfExercise;
    }

    private long getDurationInMs(Track track, int playableIndex) {
        double beatsPerNote = 1.0d / track.getDuration(playableIndex).divisor * track.getBeatsPerMeasure().measure;
        return (long) (_msPerBeat * beatsPerNote);
    }

    public abstract void advanceToNextPlayable(long realTimestamp);

    public long getTrackDurationInMs(){
        return _endTimestamp;
    }
}