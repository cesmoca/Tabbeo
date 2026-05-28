package com.tabbeo.unittests.Mocks;

import android.media.AudioRecord;

import com.tabbeo.Detector.AudioSource.IAudioRecord;

public class MockAudioRecord implements IAudioRecord {
    @Override
    public int getState() {
        return AudioRecord.STATE_INITIALIZED;
    }

    @Override
    public void startRecording() {

    }

    @Override
    public int read(short[] audioData, int posAudioBuffer, int bufferSize) {
        return 0;
    }

    @Override
    public void stop() {

    }

    @Override
    public void release() {

    }
}
