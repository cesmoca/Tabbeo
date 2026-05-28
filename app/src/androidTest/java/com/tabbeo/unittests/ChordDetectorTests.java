package com.tabbeo.unittests;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.tabbeo.Detector.ChordDetector;
import com.tabbeo.Detector.DetectorManager;
import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.Music.Music.Root;
import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.Music.Playable.Silence;
import com.tabbeo.TabbeoAppTest;
import com.tabbeo.unittests.Mocks.MockTrainer;

public class ChordDetectorTests extends InstrumentationTestCase {
    private MockTrainer _mockTrainer;
    private ChordDetector _chordDetector;
    private Chord _aMajor = Chord.AMajor;

    @Override
    public void setUp() {
        DetectorManager.loadResources();

        _mockTrainer = new MockTrainer();
        _chordDetector = new ChordDetector(_mockTrainer);

        TabbeoAppTest.setContext(getInstrumentation().getContext());
    }

    @Override
    public void tearDown() {
        _mockTrainer = null;
        _chordDetector = null;
    }

    @SmallTest
    public void testDecodeChordCode() {
        try {
            new Chord(-1);
            assertTrue(false);
        }catch(Exception ignore){}

        int AMajorCode = (Root.A.getIndex() << 4) | (Chord.Mode.Major.ordinal());
        assertEquals(_aMajor, new Chord(AMajorCode));
    }


    @MediumTest
    public void testGetPlayableFromDetectionResult_DetectChordAMajor() throws InterruptedException {
        Chord aM = _aMajor;
        _mockTrainer.expectedPlayable = aM;

        int[] detectionResult = new int[] {(aM.getRoot().getIndex() << 4) | (Chord.Mode.Major.ordinal())};

        Playable detectedPlayable = _chordDetector.getPlayableFromDetectionResult(detectionResult);

        assertEquals(aM, detectedPlayable);
    }

    @MediumTest
    public void testGetPlayableFromDetectionResult_DetectChord_NoChordDetected() throws InterruptedException {
        int[] detectionResult = new int[]{};

        Playable detectedPlayable = _chordDetector.getPlayableFromDetectionResult(detectionResult);

        assertEquals(Silence.SILENCE, detectedPlayable);
    }
}
