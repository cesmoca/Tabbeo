package com.tabbeo.unittests.Mocks;

import com.tabbeo.Detector.IDetector;
import com.tabbeo.Music.Playable.Playable;

public class MockDetector implements IDetector {


    @Override
    public Object detect(float[] audioData) {
        return null;
    }

    @Override
    public Playable getPlayableFromDetectionResult(Object o) {
        return null;
    }

    @Override
    public int getBufferSize() {
        return 0;
    }
}
