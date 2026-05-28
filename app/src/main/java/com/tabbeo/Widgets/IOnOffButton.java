package com.tabbeo.Widgets;

import com.tabbeo.Music.IMetronomeTimeRef;

public interface IOnOffButton {
    void startPlayingAnim(final IMetronomeTimeRef metronome);
    void stopPlayingAnim();
    void setChecked(boolean checked);
}
