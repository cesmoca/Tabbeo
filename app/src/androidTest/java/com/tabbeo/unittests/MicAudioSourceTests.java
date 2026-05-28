package com.tabbeo.unittests;

import android.app.Activity;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.tabbeo.Detector.AudioSource.IAudioRecord;
import com.tabbeo.Detector.AudioSource.MicAudioSource;
import com.tabbeo.unittests.Mocks.MockAudioRecord;

class MicAudioSourceTest extends MicAudioSource{
    public MicAudioSourceTest(Activity activityExercise) {
        super(activityExercise);
    }

    @Override
    protected IAudioRecord createAudioRecord(){
        return new MockAudioRecord();
    }
}
public class MicAudioSourceTests extends InstrumentationTestCase {

    @SmallTest
    public void testPitchCorrectBufferSize() throws InterruptedException {
        int bufferSize = 2048;
        MicAudioSourceTest micAudioSource = new MicAudioSourceTest(null /*activityExercise*/);
        micAudioSource.start(bufferSize);
        float[] audioBuffer = micAudioSource.getLatest();
        assertEquals(bufferSize, audioBuffer.length);
        micAudioSource.stop();
    }

    @SmallTest
    public void testChordCorrectBufferSize() throws InterruptedException {
        int chordBufferSize = 8000;
        MicAudioSourceTest micAudioSource = new MicAudioSourceTest(null /*activityExercise*/);
        micAudioSource.start(chordBufferSize);
        float[] audioBuffer = micAudioSource.getLatest();
        assertEquals(chordBufferSize, audioBuffer.length);
        micAudioSource.stop();
    }

    @SmallTest
    public void testNotStartedReturnsNullBuffer() throws InterruptedException {
        MicAudioSourceTest micAudioSource = new MicAudioSourceTest(null /*activityExercise*/);
        float[] audioBuffer = micAudioSource.getLatest();
        assertNull(audioBuffer);
    }

    @SmallTest
    public void testFastStartAndStop() throws InterruptedException {
        int bufferSize = 1000;
        MicAudioSourceTest micAudioSource = new MicAudioSourceTest(null /*activityExercise*/);
        micAudioSource.start(bufferSize);
        micAudioSource.stop();
        micAudioSource.start(bufferSize);
        micAudioSource.stop();
        micAudioSource.start(bufferSize);
        micAudioSource.stop();
        micAudioSource.start(bufferSize);
        micAudioSource.stop();
        micAudioSource.start(bufferSize);

        float[] audioBuffer = micAudioSource.getLatest();
        assertNotNull(audioBuffer);

        micAudioSource.stop();

        audioBuffer = micAudioSource.getLatest();
        assertNull(audioBuffer);

    }
}

