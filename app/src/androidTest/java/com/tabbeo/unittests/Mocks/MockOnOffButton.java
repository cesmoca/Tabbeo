package com.tabbeo.unittests.Mocks;

import com.tabbeo.Music.IMetronomeTimeRef;
import com.tabbeo.Widgets.IOnOffButton;

public class MockOnOffButton implements IOnOffButton {
    public boolean callStartPlayingAnim;

    public MockOnOffButton(){
        reset();
    }

    @Override
    public void startPlayingAnim(IMetronomeTimeRef metronome) {
        callStartPlayingAnim = true;
    }

    @Override
    public void stopPlayingAnim() {
    }

    @Override
    public void setChecked(boolean checked) {
    }

    public void reset(){
        callStartPlayingAnim = false;
    }
}
