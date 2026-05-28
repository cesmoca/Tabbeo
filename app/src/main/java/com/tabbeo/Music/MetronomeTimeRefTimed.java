package com.tabbeo.Music;

import android.support.annotation.CallSuper;

public class MetronomeTimeRefTimed extends IMetronomeTimeRef {
    protected long _realMelodyStartTime;

    public MetronomeTimeRefTimed(Track track, int tempo) {
        super(track, tempo);
    }

    @Override
    public void start(long realTimestamp) {
        super.start(realTimestamp);
        _realMelodyStartTime = realTimestamp + _countdownDuration; // The melody start time is timestamp 0. In countdown we will have negative countdowns
    }

    @CallSuper
    public void stop() {
        super.stop();
        _realMelodyStartTime = 0;
    }

    @Override
    protected long getVirtualTimestamp(long realTimestamp) {
        return realTimestamp - _realMelodyStartTime;
    }

    @Override
    public void advanceToNextPlayable(long realTimestamp) {
        // Empty on purpose. We do not need to do anything for the timed, but we use IMetronomeTimeRef in
        // the Trainer, so we need to have the same interface than for the interactive.. I know, ugly ugly
    }
}
