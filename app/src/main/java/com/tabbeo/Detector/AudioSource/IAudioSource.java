package com.tabbeo.Detector.AudioSource;

public interface IAudioSource {
    int[] _sampleRates = new int[] { 44100, 22050, 11025, 8000 };

    float[] getLatest();
    void start(int bufferSize) throws InterruptedException;
    void stop();
}
