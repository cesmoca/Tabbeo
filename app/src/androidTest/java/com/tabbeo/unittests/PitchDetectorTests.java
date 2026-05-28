package com.tabbeo.unittests;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.tabbeo.Detector.DetectorManager;
import com.tabbeo.Detector.PitchDetector;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.Music.Playable.Silence;
import com.tabbeo.TabbeoAppTest;

public class PitchDetectorTests extends InstrumentationTestCase {
    private PitchDetector _pitchDetector;
    private Note _a4 = Note.A4;

    @Override
    public void setUp() {
        DetectorManager.loadResources();

        _pitchDetector = new PitchDetector();

        TabbeoAppTest.setContext(getInstrumentation().getContext());
    }

    @MediumTest
    public void testGetPlayableFromDetectionResult_DetectPitchA4() throws InterruptedException {
        float detectionResult = 440.0f;

        Playable detectedPlayable = null;

        for(int i = 0; i<50; ++i) {
            // This result is filtered, so it takes a while to come.
            detectedPlayable = _pitchDetector.getPlayableFromDetectionResult(detectionResult);
            if (detectedPlayable != Silence.SILENCE) break;
        }

        assertEquals(_a4, detectedPlayable);
    }

    @MediumTest
    public void testGetPlayableFromDetectionResult_DetectPitch_DoesNotDetectFrequency() throws InterruptedException {
        float detectionResult = 0.0f;

        Playable detectedPlayable = _pitchDetector.getPlayableFromDetectionResult(detectionResult);

        assertEquals(Silence.SILENCE, detectedPlayable);
    }

}
