package com.tabbeo.Detector.AudioSource;

import android.media.AudioRecord;

public class AudioRecordProxy implements IAudioRecord{
    private AudioRecord _recorder;
    public AudioRecordProxy(int voiceRecognition, int sampleRate, int channelMode, int encoding, int audioBufferSize) {
        _recorder = new AudioRecord(voiceRecognition, sampleRate, channelMode, encoding, audioBufferSize);
    }

    @Override
    public int getState() {
        return _recorder.getState();
    }

    @Override
    public void startRecording() {
        _recorder.startRecording();
    }

    @Override
    public int read(short[] audioData, int posAudioBuffer, int bufferSize) {
        return _recorder.read(audioData, posAudioBuffer, bufferSize);
    }

    @Override
    public void stop() {
        _recorder.stop();
    }

    @Override
    public void release() {
        _recorder.release();
    }
}
