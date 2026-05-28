package com.tabbeo.detectorAnalyser.Analyser;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.util.Log;

import com.tabbeo.Detector.AudioSource.IAudioSource;
import com.tabbeo.Detector.AudioSource.MicAudioSource;
import com.tabbeo.Music.Playable.Playable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LabeledWavAudio {
    private static final String LOG_TAG = "LabeledWavAudio";

    public static class Label {
        public long startTimestamp;
        public Playable playable;

        public Label(long startTimestamp, Playable playable) {
            this.startTimestamp = startTimestamp;
            this.playable = playable;
        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof Label)) return false;
            Label l = (Label) o;
            return startTimestamp == l.startTimestamp;
        }

        @Override
        public String toString(){
            return "["+startTimestamp+"] "+playable;
        }
    }

    private float[] _data;

    private int _resId;
    private String _name;
    private Label[] _labels;
    private double _msPerBuffer;

    private double _elapsedTimeInMs;
    private int _currentDataPos;
    private int _currentLabelPos;

    public LabeledWavAudio(int resId, Label[] labels) {
        _resId = resId;
        _labels = labels;
    }

    public void load(Resources resources, boolean verbose) throws IOException {
        if(_data != null) return; // This guy has been loaded already ;)

        _name = resources.getResourceName(_resId);

        Log.d(LOG_TAG, "Loading wav file: "+_name);
        _data = WavLoader.loadWav(_resId, resources, verbose);
    }

    public boolean getLatest(float[] dst){
        for(int i=0; i<dst.length; ++i){
            dst[i] = _data[_currentDataPos];

            _currentDataPos++;
            if(_currentDataPos == _data.length) return false;
        }
        _elapsedTimeInMs += _msPerBuffer;

        return true;
    }

    public Label[] getLabels() {
        return _labels;
    }

    final public Playable getLabeledPlayable(long timeStamp) {
        return getLabel(timeStamp).playable;
    }

    final public Label getLabel(long timeStamp) {
        if(_currentLabelPos < (_labels.length-1) && _labels[_currentLabelPos+1].startTimestamp <= timeStamp){
            _currentLabelPos++;
        }

        return _labels[_currentLabelPos];
    }

    final public int getNPlayables(){
        int n = 0;

        for(Label label : _labels){
            if(label.playable != null) n++;
        }
        return n;
    }

    final public double getMsPerBuffer(){ return _msPerBuffer; }

    public void rewind(int sampleBufferSize) throws IOException {
        _currentDataPos = 0;
        _elapsedTimeInMs = 0;
        _currentLabelPos = 0;
        _msPerBuffer = (sampleBufferSize / ((double) MicAudioSource.getSampleRate()/1000));

    }

    public long getElapsedTime(){ return (long) _elapsedTimeInMs; }

    @Override
    public String toString() { return _name; }
}

@SuppressWarnings("ResultOfMethodCallIgnored")
class WavLoader{
        private static final String LOG_TAG="WavLoader";
        private WavLoader(){} // Just a static helper class to load wav files to memory

        public static float[] loadWav(int resId, Resources resources, boolean verbose) throws IOException {

            AssetFileDescriptor fd = resources.openRawResourceFd(resId);

            FileInputStream fileInputStream = fd.createInputStream();

            int headerSize = checkAndSkipHeader(fileInputStream, MicAudioSource.getSampleRate(), verbose);

            byte[] rawData = new byte[(int)(fd.getLength() - headerSize)];
            float[] data = new float[(int)((fd.getLength() - headerSize)/2)];

            if (fileInputStream.read(rawData) < 0)
                throw new AssertionError("Something went wrong loading the wav into memory");

            // Loading the data into memory
            for(int i = 0; i< data.length; ++i) {
                data[i] = (rawData[i*2+1] << 8) | rawData[i*2]; // Converting from little to big endian
            }

            return data;
        }

    // Returns the header calculateNElements, too
    private static int checkAndSkipHeader(InputStream inputStream, int requiredSampleRate, boolean verbose) throws IOException {
        int bytesSkipped = 0;
        byte[] bytes = new byte[4];

        // read first 4 bytes should be RIFF descriptor
        if (inputStream.read(bytes) < 0) {
            throw new RuntimeException("Error calculating header calculateNElements");
        }

        String descriptorName = getDescriptorName(bytes);
        if(!descriptorName.equals("RIFF")){
            throw new AssertionError("It seems that the file is not wav. The first descriptor is not RIFF");
        }

        bytesSkipped += bytes.length;

        // first subchunk will always be at byte 12 there is no other dependable constant
        inputStream.skip(8);
        bytesSkipped += 8;

        while (true) {
            // read each chunk descriptor
            if (inputStream.read(bytes) < 0) {
                break;
            }
            bytesSkipped += bytes.length;

            descriptorName = getDescriptorName(bytes);

            // read chunk length
            if (inputStream.read(bytes) < 0) {
                break;
            }

            // skip the length of this chunk next bytes should be another descriptor or EOF (ittle endian)
            int chunkLength =
                    (bytes[0] & 0xFF)
                            | (bytes[1] & 0xFF) << 8
                            | (bytes[2] & 0xFF) << 16
                            | (bytes[3] & 0xFF) << 24;

            bytesSkipped += bytes.length;

            if(verbose) Log.d(LOG_TAG, "["+descriptorName+"] "+chunkLength+" bytes");

            switch (descriptorName) {
                case "data":
                    if (verbose)
                        Log.d(LOG_TAG, "Header calculateNElements for this file is: " + bytesSkipped);

                    long nSamplesTotal = chunkLength / 2;
                    long durationMs = (long) (nSamplesTotal / ((float) requiredSampleRate / 1000));

                    if (verbose)
                        Log.d(LOG_TAG, "The duration of the audio file is " + durationMs + " ms");

                    return bytesSkipped;

                case "fmt ":
                    byte[] chunkData = new byte[chunkLength];
                    inputStream.read(chunkData);
                    bytesSkipped += chunkLength;

                    // Audio format (2)
                    int audioFormat =
                            (chunkData[0] & 0xFF) | (chunkData[1] & 0xFF) << 8;
                    if (verbose) Log.d(LOG_TAG, "Audio format: " + audioFormat);

                    if (audioFormat != 1) {
                        throw new AssertionError("The encoding of the wav file should be PCM");
                    }

                    // Num channels (2)
                    int numChannels =
                            (chunkData[2] & 0xFF) | (chunkData[3] & 0xFF) << 8;
                    if (verbose) Log.d(LOG_TAG, "Num channels: " + numChannels);


                    if (numChannels != 1) {
                        throw new AssertionError("The number of channels of the wav file should be 1");
                    }

                    // Sample rate  (4)
                    int sampleRate =
                            (chunkData[4] & 0xFF) | (chunkData[5] & 0xFF) << 8 | (chunkData[6] & 0xFF) << 16 | (chunkData[7] & 0xFF) << 24;
                    if (verbose) Log.d(LOG_TAG, "Sample rate: " + sampleRate);

                    if (sampleRate != requiredSampleRate) {
                        throw new AssertionError("The sample rate of the wav file should be 44100 Hz");
                    }

                    // Byte rate (4)
                    int byteRate =
                            (chunkData[8] & 0xFF) | (chunkData[9] & 0xFF) << 8 | (chunkData[10] & 0xFF) << 16 | (chunkData[11] & 0xFF) << 24;
                    if (verbose) Log.d(LOG_TAG, "Byte rate: " + byteRate);

                    // Block align (2)
                    int blockAlign =
                            (chunkData[12] & 0xFF) | (chunkData[13] & 0xFF) << 8;
                    if (verbose) Log.d(LOG_TAG, "Block align: " + blockAlign);

                    // Bits per sample (2)
                    int bitsPerSample =
                            (chunkData[14] & 0xFF) | (chunkData[15] & 0xFF) << 8;
                    if (verbose) Log.d(LOG_TAG, "BitsPerSample: " + bitsPerSample);

                    if (bitsPerSample != 16) {
                        throw new AssertionError("The bits per sample for the wav file should be 16-bit");
                    }

                    break;
                default:
                    inputStream.skip(chunkLength);
                    bytesSkipped += chunkLength;
                    break;
            }
        }

        throw new RuntimeException("Could not find the 'data' descriptor, so we do not know what the header calculateNElements is");
    }

    private static String getDescriptorName(byte[] bytes) throws IOException {
        return new String(bytes, "US-ASCII");
    }
}
