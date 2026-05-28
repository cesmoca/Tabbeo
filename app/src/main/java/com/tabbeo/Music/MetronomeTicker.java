package com.tabbeo.Music;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class MetronomeTicker implements IMetronomeTicker, Runnable {
    private AudioTrack _audioTrack;
    private byte[] _sample;
    private Thread _thread;

    public MetronomeTicker() {
        _audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SampleGenerator.SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, SampleGenerator.SAMPLE_RATE, AudioTrack.MODE_STREAM);
    }

    @Override
    public void start(long msPerBeat) {
        if (_thread != null) {
            try { // Wait for the thread to finish
                _thread.interrupt();
                _thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException("We ourselves got interrupted while joining. ");
            }
        }

        _sample = SampleGenerator.generateSample(msPerBeat);

        _thread = new Thread(this);
        _thread.start();
    }

    @Override
    public void stop() {
        if (Thread.currentThread().equals(_thread))
            throw new RuntimeException("We are trying to start ourselves. Do not call this API from the same thread");

        _audioTrack.pause();
        _audioTrack.flush();

        _thread.interrupt();
    }

    @Override
    public void run() {
        _audioTrack.play();

        while (!Thread.interrupted()) {
            _audioTrack.write(_sample, 0, _sample.length);
        }
    }
}

class SampleGenerator {
    public static final int SAMPLE_RATE = 44100;
    public static final int TICK_MS = 100; /* Minimum time for the tick sound */
    public static final int FREQ_TONE = 300; // A very low freq sound that the pitch detector does not detect

    public static byte[] generateSample(long msPerBeat) {
        //int nSamples = (int) (60 / (float) tempo * SAMPLE_RATE);
        int nSamples = (int) (msPerBeat * SAMPLE_RATE / 1000);
        double[] sample = new double[nSamples];

        writeTickSound(sample);

        return get16BitPcm(sample);
    }

    public static void writeTickSound(double[] sample) {

        double samplesPerPeriod = SAMPLE_RATE / FREQ_TONE;
        double msWavePeriod = 1000 / FREQ_TONE;
        int wavePeriodsPerTick = (int) Math.ceil(TICK_MS / msWavePeriod);

        int nSamplesTick = (int) samplesPerPeriod * wavePeriodsPerTick;
        double alpha = 2 * Math.PI / (samplesPerPeriod);

        for (int i = 0; i < nSamplesTick; ++i) {
            sample[i] = Math.sin(i * alpha);
        }
    }

    private static byte[] get16BitPcm(double[] samples) {
        byte[] generatedSound = new byte[2 * samples.length];
        int index = 0;
        for (double sample : samples) {
            // scale to maximum amplitude
            short maxSample = (short) (sample * Short.MAX_VALUE);
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSound[index++] = (byte) (maxSample & 0x00ff);
            generatedSound[index++] = (byte) ((maxSample & 0xff00) >>> 8);
        }
        return generatedSound;
    }
}