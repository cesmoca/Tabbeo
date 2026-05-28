package com.tabbeo.Detector.AudioSource;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.tabbeo.R;

public class MicAudioSource implements Runnable, IAudioSource {
    private final static String LOG_TAG = "MicAudioSource";

    private final static int CHANNEL_MODE = AudioFormat.CHANNEL_IN_MONO;
    private final static int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private final static double AUDIO_CARD_BUFFER_SECS = 3.0; /* Audio buffer for the card in seconds */

    private final static int FACTOR_BUFFER_BIGGER = 10; // How many times our buffer is bigger than the fftChunkSize, to avoid overwrites

    private Activity _activityExercise;

    private short[] _audioData; // Managed as a circular buffer
    private int _audioDataSize = 0;
    private int _audioBufferSize = 0;
    private int _posAudioBuffer = 0; // Head of the circular buffer
    private int _bufferSize = -1;
    private float[] _outputAudioBuffer = null;

    // Sample rate
    private static int _sampleRate = 44100; // Default
    private static boolean _foundValidSampleRate = false;

    // Locks
    private Thread _recorderThread;
    private final Object _audioReadLock = new Object();

    public MicAudioSource(Activity activityExercise) {
        super();

        _activityExercise = activityExercise;


        _audioBufferSize = (int) Math.round(_sampleRate * AUDIO_CARD_BUFFER_SECS);
        int minBufferSize = AudioRecord.getMinBufferSize(_sampleRate, CHANNEL_MODE, ENCODING);
        if (minBufferSize > _audioBufferSize)
            _audioBufferSize = minBufferSize;

        Log.d(LOG_TAG, "[MicAudioSource] Recording buffer size: " + _audioBufferSize);

        // Multiple of READ_SIZE_BYTES, and bigger than fftChunkSize, by several multiples (TODO how to guarantee that the recorder does not overwrite us?)
        // READ_BYTES_SIZE was how much we read from the stream at a time. We did something smaller than the samples buffer size. Maybe that does not make sense anymore
        //_audioDataSize = (int) Math.ceil(_bufferSize / _readBufferSize) * _readBufferSize * FACTOR_BUFFER_BIGGER; // Avoid the recorder to overwrite what we use
    }

    @Override
    public void start(int bufferSize) throws InterruptedException{
        if (Thread.currentThread().equals(_recorderThread))
            throw new RuntimeException("We are trying to start ourselves. Do not call this API from the same thread");

        if (_recorderThread != null) {
                _recorderThread.interrupt();
                _recorderThread.join();
        }

        _bufferSize = bufferSize;
        _audioDataSize = _bufferSize * FACTOR_BUFFER_BIGGER; // Avoid the recorder to overwrite what we use
        _outputAudioBuffer = new float[_bufferSize];

        Log.d(LOG_TAG, "Recording audio data size: " + _audioDataSize);
        Log.d(LOG_TAG, "Recording Buffer_Size: " + _bufferSize);

        // Cleaning the data buffer
        _audioData = new short[_audioDataSize];

        _recorderThread = new Thread(this);
        _recorderThread.start();
    }

    @Override
    public void stop() {
        if (Thread.currentThread().equals(_recorderThread))
            throw new RuntimeException("We are trying to start ourselves. Do not call this API from the same thread");

        if(_recorderThread != null){
            _recorderThread.interrupt();
        }

        _audioData = null;
    }

    @Override
    public float[] getLatest() {

        int newestChunkSize;
        int newestChunkOffset;
        int oldestChunkSize = 0;
        int posAudioBuffer;

        synchronized (_audioReadLock) {
            posAudioBuffer = _posAudioBuffer;
            newestChunkSize = _posAudioBuffer;
            if (newestChunkSize < _bufferSize) {
                oldestChunkSize = _bufferSize - _posAudioBuffer;
                newestChunkOffset = oldestChunkSize;
            } else {
                newestChunkSize = _bufferSize;
                newestChunkOffset = _posAudioBuffer - _bufferSize;
            }
        }

        //System.arraycopy(_audioData, posAudioBuffer, audioBuffer, 0, oldestChunkSize); // First chunk (oldest)
        if (_audioData != null) {
            for (int i = 0; i < oldestChunkSize; ++i) {
                _outputAudioBuffer[i] = _audioData[posAudioBuffer + i];
            }

            //System.arraycopy(_audioData, 0, audioBuffer, newestChunkOffset, newestChunkSize); // Second chunk (newest)
            for (int i = 0; i < newestChunkSize; ++i) {
                _outputAudioBuffer[i + newestChunkOffset] = _audioData[i];
            }
        } else {
            return null;
        }

        return _outputAudioBuffer;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

        IAudioRecord recorder = createAudioRecord();

        if (!_foundValidSampleRate) {
            showErrorAndFinish("We could not find a supported sample rate", true /*shouldCrash*/);
        }

        if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
            showErrorAndFinish("AudioRecord state is UNINITIALIZED.", true /*shouldCrash*/);
            return;
        }

        try {
            recorder.startRecording();
        } catch (IllegalStateException ignore) {
            showErrorAndFinish("AudioRecord.startRecording throw an IllegalStateException", true /*shouldCrash*/);
        }

        int ret;
        while (!Thread.interrupted()) {

            if ((ret = recorder.read(_audioData, _posAudioBuffer, _bufferSize)) < 0) {
                if (ret == AudioRecord.ERROR_INVALID_OPERATION) {
                    showErrorAndFinish(_activityExercise.getString(R.string.micaudiosource_cant_access_mic), false /*shouldCrash*/);
                } else if (ret == AudioRecord.ERROR_BAD_VALUE) {
                    showErrorAndFinish("ERROR_BAD_VALUE in AudioRecord.read", true /*shouldCrash*/);
                } else {
                    throw new RuntimeException("Unknown error thrown by AudioRecord.read: " + ret);
                }
                return;
            }

            synchronized (_audioReadLock) {
                _posAudioBuffer += _bufferSize;
                if (_posAudioBuffer == _bufferSize)
                    _posAudioBuffer = 0;
            }
        }

        recorder.stop();
        recorder.release();
    }

    public static void findValidSample() {
        for (int sampleRate : _sampleRates) {
            if (AudioRecord.getMinBufferSize(sampleRate, CHANNEL_MODE, ENCODING) > 0) {
                // Valid sample rate
                _sampleRate = sampleRate;
                _foundValidSampleRate = true;
                break;
            }
        }
    }

    public static void findValidSampleRateInBackground() {
        AsyncTask<Void, Void, Void> loadingTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                findValidSample();
                return null;
            }
        };

        loadingTask.execute();
    }

    private void showErrorAndFinish(final String errorMsg, boolean shouldCrash) {

        _activityExercise.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(_activityExercise.getApplication().getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
        _activityExercise.finish();

        if (shouldCrash)
            throw new RuntimeException(errorMsg);
    }

    protected IAudioRecord createAudioRecord() {
        // VOICE_RECOGNITION applies automatically some noise-reduction filters
        return new AudioRecordProxy(AudioSource.VOICE_RECOGNITION, _sampleRate, CHANNEL_MODE, ENCODING, _audioBufferSize);
    }

    public static int getSampleRate() {
        return _sampleRate;
    }
}