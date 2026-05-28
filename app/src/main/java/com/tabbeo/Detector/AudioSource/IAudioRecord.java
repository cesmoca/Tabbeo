package com.tabbeo.Detector.AudioSource;

public interface IAudioRecord {
    int getState();
    void startRecording();
    int read(short[] audioData, int posAudioBuffer, int bufferSize);
    void stop();
    void release();
}
