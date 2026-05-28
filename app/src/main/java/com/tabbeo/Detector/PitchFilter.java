package com.tabbeo.Detector;

import android.support.annotation.NonNull;

import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.Music.Playable.Silence;

public class PitchFilter {
    protected final int N_ITERATIONS_TO_ADAPT_NEW_NOTE = 4; // How many consecutive times we should see a note to consider it detected
    //protected final int N_NOISE_ITERATIONS_BEFORE_GIVING_UP = 2; // We drop fast. How many consecutive times we are seeing "noise" (different notes than the detected) before consider it not detected

    protected int _newPlayableIterations;
    //protected int _iterationsNoise;
    protected boolean _isChangingPlayable;
    protected Playable _newPlayableCandidate;
    protected Playable _currentPlayable;

    public PitchFilter(){
        reset();
    }

    @NonNull
    public Playable filter(final @NonNull Playable newPlayable) {
        // We change to a new note if it is detected for longer than TIME_TO_ADAPT_NEW_NOTE
        // otherwise we assume we are detecting the current note
        // However, if we detect "noise" for more than MAX_NOISE_TIME_BEFORE_GIVING_UP_NOTE, we assume
        // we are not detecting anything
        if (!newPlayable.equals(_currentPlayable)) {
            if (_isChangingPlayable) {
                _newPlayableIterations ++;
                //_iterationsNoise ++;

                if (newPlayable.equals(_newPlayableCandidate)) {

                    if (_newPlayableIterations >= N_ITERATIONS_TO_ADAPT_NEW_NOTE) {
                        _currentPlayable = _newPlayableCandidate;
                        _isChangingPlayable = false;
                        //_iterationsNoise = 0;
                        _newPlayableIterations = 0;
                    }
                } else {
                    _newPlayableIterations = 0;
                    _newPlayableCandidate = newPlayable;
                }
            } else {
                _isChangingPlayable = true;
                _newPlayableIterations = 0;
                _newPlayableCandidate = newPlayable;
            }

            // Too much time changing notes, we are not detecting anything
            // If we are in silence, we do not take into account the noise
            //if (_iterationsNoise >= N_NOISE_ITERATIONS_BEFORE_GIVING_UP) {
            //    _currentPlayable = Silence.SILENCE;
            //    _isChangingPlayable = false;
            //    _iterationsNoise = 0;
            //}
        } else {
            _isChangingPlayable = false;
        }

        return _currentPlayable;
    }

    protected void reset() {
        _newPlayableIterations = 0;
        _isChangingPlayable = false;
        //_iterationsNoise = 0;
        _newPlayableCandidate = Silence.SILENCE;
        _currentPlayable = Silence.SILENCE;
    }
}