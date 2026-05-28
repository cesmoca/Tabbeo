package com.tabbeo.unittests.Mocks;

import com.tabbeo.Detector.IDetectorManager;

public class MockDetectorManager implements IDetectorManager {
    public boolean startCalled = false;
    public boolean stopCalled = false;

    @Override
    public void start() {
        startCalled = true;
    }

    @Override
    public void stop() { stopCalled = true; }

}
