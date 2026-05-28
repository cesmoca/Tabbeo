package com.tabbeo.Music;

public class MetronomeTimeRefInteractive extends IMetronomeTimeRef {
    protected long _fromVirtualTimestamp;
    protected long _fromRealTimestamp;
    protected long _advanceTimeDuration;

    public MetronomeTimeRefInteractive(Track track, int tempo) {
        super(track, tempo);
    }

    @Override
    public void start(long realTimestamp) {
        super.start(realTimestamp);
        _fromVirtualTimestamp = -_countdownDuration;
        _fromRealTimestamp = realTimestamp;
        _advanceTimeDuration = _countdownDuration; // Since virtual timestamp is 0, _melodyStartTime is equal to the duration of the countdown
    }

    @Override
    public void stop() {
        super.stop();
        _fromVirtualTimestamp = 0;
        _fromRealTimestamp = 0;
        _advanceTimeDuration = 0;
    }

    @Override
    protected long getVirtualTimestamp(long realTimestamp) {
        long elapsedTime = realTimestamp - _fromRealTimestamp;
        if (elapsedTime > _advanceTimeDuration) elapsedTime = _advanceTimeDuration;

        return _fromVirtualTimestamp + elapsedTime;
    }

    @Override
    public void advanceToNextPlayable(long realTimestamp) {
        if(_fromVirtualTimestamp != getPlayableStartTime(_currentPlayableIndex) - _advanceTimeDuration) {
            throw new RuntimeException("We are calling advanceNextPlayable in the wrong moment. We should be stuck at the very beginning of a playable. FromVirtualTimestamp: "
                    + _fromVirtualTimestamp + ", CurrentPlayableStartTime: " + getPlayableStartTime(_currentPlayableIndex) + ", AdvanceTimeDuration: " + _advanceTimeDuration);
        }

        _fromVirtualTimestamp += _advanceTimeDuration;
        _advanceTimeDuration = getPlayableDurationInMs(_currentPlayableIndex);
        _fromRealTimestamp = realTimestamp;
    }
}
