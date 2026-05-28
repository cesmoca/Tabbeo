package com.tabbeo.unittests.Mocks;

import com.tabbeo.Detector.AudioSource.IAudioSource;

public class MockAudioSource implements IAudioSource {
    public boolean startCalled;
    public boolean stopCalled;

    public MockAudioSource() {
        reset();
    }

    public void reset() {
        startCalled = false;
        stopCalled = false;
    }

    @Override
    public float[] getLatest() {
        return new float[0];
    }

    @Override
    public void start(int bufferSize) {
        startCalled = true;
    }

    @Override
    public void stop() {
        stopCalled = true;
    }
}
