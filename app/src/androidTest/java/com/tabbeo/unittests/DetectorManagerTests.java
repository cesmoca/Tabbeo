package com.tabbeo.unittests;

import android.app.Activity;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.tabbeo.Detector.AudioSource.IAudioSource;
import com.tabbeo.Detector.DetectorManager;
import com.tabbeo.Detector.IDetector;
import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.TabbeoAppTest;
import com.tabbeo.Trainer.ITrainer;
import com.tabbeo.unittests.Mocks.MockAudioSource;
import com.tabbeo.unittests.Mocks.MockDetector;
import com.tabbeo.unittests.Mocks.MockExercise;
import com.tabbeo.unittests.Mocks.MockTrainer;

class DetectorManagerTest extends DetectorManager {
    public MockAudioSource mockAudioSource;

    public DetectorManagerTest(ITrainer trainer, IDetector pitchDetector, IDetector chordDetector) {
        super(trainer, null);
        _chordDetector = chordDetector;
        _pitchDetector = pitchDetector;
    }

    @Override
    protected IAudioSource createAudioSource(Activity activityExercise) {
        mockAudioSource =  new MockAudioSource();
        return mockAudioSource;
    }

    public boolean isRunning() {
        return _detectorThread != null && _detectorThread.isAlive();
    }

    public void accessor_setRightTypeOfDetector() {
        setRightTypeOfDetector();
    }

    public void accessor_startDetector(IDetector detector) {
        startDetector(detector);
    }

    public IDetector getCurrentDetector() {
        return _currentDetector;
    }

    public Thread getDetectorThread() {
        return _detectorThread;
    }
}

public class DetectorManagerTests extends InstrumentationTestCase {
    private MockTrainer _mockTrainer;
    private DetectorManagerTest _detector;
    private Note _a4 = Note.A4;
    private Chord _aMajor = Chord.AMajor;
    private MockDetector _mockPitchDetector;
    private MockDetector _mockChordDetector;

    @Override
    public void setUp() {
        DetectorManager.loadResources();

        _mockTrainer = new MockTrainer();
        _mockPitchDetector = new MockDetector();
        _mockChordDetector = new MockDetector();
        _detector = new DetectorManagerTest(_mockTrainer, _mockPitchDetector, _mockChordDetector);

        TabbeoAppTest.setContext(getInstrumentation().getContext());
    }

    @Override
    public void tearDown() {
        if(_detector.isRunning()) _detector.stop();
    }

    @SmallTest
    public void testStartShutdown() throws InterruptedException {
        _detector.start();
        _detector.stop();

        assertTrue(_detector.getDetectorThread().isInterrupted() || !_detector.isRunning()); // We finished or are finishing
        assertNull(_detector.getCurrentDetector());
    }

    @SmallTest
    public void testCallStopBeforeStart_NothingHappens() {
        // We have decided that if you call stop in a non-started detector, it does nothing.
        // We wanted to make it crash, but there are cases in which stop means just make sure it is
        // stopped, even though it might be already
        _detector.stop();
    }
        @SmallTest
    public void testFastStartShutdown() {
        _detector.start();
        _detector.stop();
        _detector.start();
        _detector.stop();

        assertTrue(_detector.getDetectorThread().isInterrupted() || !_detector.isRunning()); // We finished or are finishing

        _detector.start();
        _detector.stop();
        _detector.start();
        _detector.stop();

        assertTrue(_detector.getDetectorThread().isInterrupted() || !_detector.isRunning()); // We finished or are finishing
    }

    @MediumTest
    public void testOnAnalysysReturnsTrue_DetectorContinues() throws InterruptedException {
        _mockTrainer.continueDetecting = ITrainer.ContinueDetecting.Yes;
        _detector.start();
        assertTrue(_detector.isRunning());

        _detector.stop();

        assertTrue(_detector.getDetectorThread().isInterrupted() || !_detector.isRunning()); // We finished or are finishing
    }

    @MediumTest
    public void testOnAnalysysReturnsFalse_DetectorStops() throws InterruptedException {
        _mockTrainer.continueDetecting = ITrainer.ContinueDetecting.No;
        _detector.start();

        for (int i = 0; _detector.isRunning() && i < 50; ++i) {
            Thread.sleep(50);
        }

        assertFalse(_detector.isRunning());
    }

    @MediumTest
    public void testRealDetector_SwitchDetectors() throws InterruptedException {
        /*
            We start and switch detectors with the real detector just to check
            general RuntimeExceptions. We had an issue that the detector
            calls stop on itself, which gives an interrupted exception
        */
        final MockAudioSource mockAudioSource = new MockAudioSource();
        DetectorManager detector = new DetectorManager(_mockTrainer, null){
            @Override
            protected IAudioSource createAudioSource(Activity activityExercise) {
                return mockAudioSource;
            }
        };

        detector.start();
        Thread.sleep(100); // We change the expected playable after a while
        _mockTrainer.expectedPlayable = _aMajor;
        Thread.sleep(100);
        detector.stop();
    }

    @SmallTest
    public void testSetDetector(){
        assertNull(_detector.getCurrentDetector());
        assertFalse(_detector.mockAudioSource.stopCalled);
        assertFalse(_detector.mockAudioSource.startCalled);

        // We want to start the pitch detector
        _detector.accessor_startDetector(_mockPitchDetector);

        // We always call stop, just in case
        assertTrue(_detector.mockAudioSource.stopCalled);
        assertTrue(_detector.mockAudioSource.startCalled); // But we start it
        assertEquals(_mockPitchDetector, _detector.getCurrentDetector()); // Now the current is pitch detector

        _detector.mockAudioSource.reset();

        // We keep asking for the pitch detector
        _detector.accessor_startDetector(_mockPitchDetector);

        assertTrue(_detector.mockAudioSource.stopCalled); // Now it is called
        assertTrue(_detector.mockAudioSource.startCalled); // But we start it
        assertEquals(_mockPitchDetector, _detector.getCurrentDetector()); // Now the current is pitch detector

        _detector.mockAudioSource.reset();

        // We change to the chord detector
        _detector.accessor_startDetector(_mockChordDetector);
        assertTrue(_detector.mockAudioSource.stopCalled); // Now it is called again
        assertTrue(_detector.mockAudioSource.startCalled); // But we start it
        assertEquals(_mockChordDetector, _detector.getCurrentDetector()); // Now the current is pitch detector
    }


    @SmallTest
    public void testSetRightTypeOfDetector() throws InterruptedException {
        try {
            // The expected playable can't be null, so we should throw
            assertNull(_detector.getCurrentDetector());

            _mockTrainer.expectedPlayable = null;
            _detector.accessor_setRightTypeOfDetector();
            assertTrue(false);
        }catch(RuntimeException ignore){}

        _detector.mockAudioSource.reset();

        assertFalse(_detector.mockAudioSource.startCalled);
        assertNull(_detector.getCurrentDetector());

        // First time expecting something, we start the pitch detector
        _mockTrainer.expectedPlayable = _a4;
        _detector.accessor_setRightTypeOfDetector();

        assertTrue(_detector.mockAudioSource.startCalled);
        assertEquals(_mockPitchDetector, _detector.getCurrentDetector());

        _detector.mockAudioSource.reset();

        // Second time expecting a note, it is already running, does not start anything new
        _detector.accessor_setRightTypeOfDetector();

        assertFalse(_detector.mockAudioSource.startCalled);
        assertEquals(_mockPitchDetector, _detector.getCurrentDetector());

        _detector.mockAudioSource.reset();

        // Now we expect a chord
        _mockTrainer.expectedPlayable = _aMajor;
        _detector.accessor_setRightTypeOfDetector();

        assertTrue(_detector.mockAudioSource.startCalled);
        assertEquals(_mockChordDetector, _detector.getCurrentDetector());

        _detector.mockAudioSource.reset();

        // Second time expecting a chord, it is already running, does not start anything new
        _detector.accessor_setRightTypeOfDetector();

        assertFalse(_detector.mockAudioSource.startCalled);
        assertEquals(_mockChordDetector, _detector.getCurrentDetector());
    }
}