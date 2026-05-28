package com.tabbeo.unittests;

import android.os.Looper;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.tabbeo.Activities.Exercises.ActivityExerciseOnOffButton;
import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.Detector.IDetectorManager;
import com.tabbeo.ExerciseLibraryTest;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.Trainer.ITrainer;
import com.tabbeo.Trainer.Trainer;
import com.tabbeo.Widgets.IOnOffButton;
import com.tabbeo.unittests.Mocks.MockDetectorManager;
import com.tabbeo.unittests.Mocks.MockExerciseWidget;
import com.tabbeo.unittests.Mocks.MockOnOffButton;



class ITrainerTest extends Trainer {
    public ITrainerTest(Type type, ActivityExerciseOnOffButton ActivityExerciseOnOffButton, IDetectorManager detector, Exercise exercise, IOnOffButton onOffButton, IMetronomeTimeRefTest mockMetronomeTimeRef) {
        super(type, ActivityExerciseOnOffButton, exercise, onOffButton, 50 /*tempo*/);
        _detector = detector;
        _metronomeTimeRef = mockMetronomeTimeRef;
    }

    public Exercise getExercise() {
        return _exercise;
    }

    public void setExercise(Exercise exercise) {
        _exercise = exercise;
    }

    public boolean getCurrentPlayableAlreadyPlayed() {
        return _playablePlayed;
    }

    public void setCurrentPlayableAlreadyPlayed(boolean currentPlayableAlreadyPlayed) {
        _playablePlayed = currentPlayableAlreadyPlayed;
    }

    public boolean accessor_tooLateToDetect(long expectedPlayableDuration, long deltaTime) {
        return tooLateToPlayRight(expectedPlayableDuration, deltaTime);
    }

    public float getMaxRatioDelay() {
        return MAX_RATIO_DELAY;
    }

    public boolean accessor_isPlayableDetected(long virtualTimestamp, Playable detectedPlayable){
        return isPlayableDetected(virtualTimestamp, detectedPlayable);
    }

    public void setCurrentlyDetectingPlayableIndex(int index){
        _currentlyDetectingPlayableIndex = index;
    }

    public int getCurrentlyDetectingPlayableIndex(){
        return _currentlyDetectingPlayableIndex;
    }
}

public class TrainerTests extends InstrumentationTestCase {
    private ITrainerTest _iTrainer;
    private MockDetectorManager _mockDetector;
    private MockOnOffButton _mockOnOffButton;
    private com.tabbeo.unittests.Mocks.MockActivityExercise _mockActivityExercise;
    private Exercise _exercise = ExerciseLibraryTest.twoNotesExerciseTimed;
    private IMetronomeTimeRefTest _metronomeTimeRefTest;

    @Override
    public void setUp() {
        _mockDetector = new MockDetectorManager();
        _mockOnOffButton = new MockOnOffButton();

        _metronomeTimeRefTest = new IMetronomeTimeRefTest(ExerciseLibraryTest.twoNotesTrack, 50 /*tempo*/);
        _mockActivityExercise = new com.tabbeo.unittests.Mocks.MockActivityExercise();

        _iTrainer = new ITrainerTest(ITrainer.Type.Interactive, _mockActivityExercise, _mockDetector, _exercise, _mockOnOffButton, _metronomeTimeRefTest);
    }

    @SmallTest
    public void testITrainer_PauseDetecting_DetectorRunning_CallsStop() {
        assertFalse(_mockDetector.stopCalled);
        _iTrainer.pauseDetecting();
        assertTrue(_mockDetector.stopCalled);
    }

    @SmallTest
    public void testITrainer_ResumeDetecting_DetectorNotRunning_CallsStop() {
        assertFalse(_mockDetector.startCalled);
        _iTrainer.resumeDetecting();
        assertTrue(_mockDetector.startCalled);
    }

    @SmallTest
    public void testITrainer_StartTraining_CallsOnOffButton_StartPlayAnimation() {
        assertFalse(_metronomeTimeRefTest.isPlaying());
        assertFalse(_mockOnOffButton.callStartPlayingAnim);

        _iTrainer.startTraining();

        assertTrue(_metronomeTimeRefTest.isPlaying());
        assertTrue(_mockOnOffButton.callStartPlayingAnim);
    }

    /*
  StopTraining touches the OnOffButton, which needs to be executed on an UIThread
  However, at the end of the training StopTraining is called by the detector thread,
  not ui, so we need to make sure it is posted in a uithread
  If everything is ok, this will not crash
   */
    @SmallTest
    public void testOnAnalysis_StopTraining_CalledFromUIThread() throws InterruptedException {
        final Boolean callStopPlayingAnim[] = {false};

        MockOnOffButton onOffButton = new MockOnOffButton() {
            @Override
            public void stopPlayingAnim() {
                assertEquals(Thread.currentThread(), Looper.getMainLooper().getThread());
                callStopPlayingAnim[0] = true;
            }
        };

        ITrainerTest iTrainer = new ITrainerTest(ITrainer.Type.Interactive, _mockActivityExercise, _mockDetector, _exercise, onOffButton, _metronomeTimeRefTest);
        _metronomeTimeRefTest.setEndOfExercise(true);
        iTrainer.onAnalysis(0, null);

        // Let's give some time to the AsyncTask to do its thing
        for (int i = 0; i < 100; i++) {
            if (callStopPlayingAnim[0]) break;
            Thread.sleep(10);
        }

        assertTrue(callStopPlayingAnim[0]);
    }

    @SmallTest
    public void testGetExpectedPlayable() {
        _iTrainer.startTraining();

        _iTrainer.setCurrentlyDetectingPlayableIndex(-1); // We are in countdown
        Playable p = _iTrainer.getExpectedPlayable();
        assertTrue(_exercise.getTrack().getPlayable(0) == p); // Returns the first playable

        _iTrainer.setCurrentlyDetectingPlayableIndex(0); // We are in a normal, not last playable, and it has not been already played
        _iTrainer.setCurrentPlayableAlreadyPlayed(false);
        p = _iTrainer.getExpectedPlayable();
        assertTrue(_exercise.getTrack().getPlayable(0) == p); // Returns the first playable

        _iTrainer.setCurrentlyDetectingPlayableIndex(0); // We are in a normal, not last playable, and it has been already played
        _iTrainer.setCurrentPlayableAlreadyPlayed(true);
        p = _iTrainer.getExpectedPlayable();
        assertTrue(_exercise.getTrack().getPlayable(1) == p); // Returns the next one

        int nPlayables = _exercise.getTrack().getNPlayables();
        Playable lastPlayable = _exercise.getTrack().getPlayable(nPlayables - 1);

        _iTrainer.setCurrentlyDetectingPlayableIndex(nPlayables - 1); // We are in the last playable, and it has not been already played
        _iTrainer.setCurrentPlayableAlreadyPlayed(false);
        p = _iTrainer.getExpectedPlayable();
        assertTrue(lastPlayable == p); // Returns the last playable

        _iTrainer.setCurrentlyDetectingPlayableIndex(nPlayables - 1); // We are in the last playable, and it has been already played
        _iTrainer.setCurrentPlayableAlreadyPlayed(true);
        p = _iTrainer.getExpectedPlayable();
        assertTrue(lastPlayable == p); // Returns the last playable
    }

    @SmallTest
    public void testStartTraining_StartsMetronome() {
        assertFalse(_iTrainer.getMetronomeTimeRef().isPlaying());
        _iTrainer.startTraining();
        _iTrainer.setCurrentlyDetectingPlayableIndex(0);

        assertEquals(false, _iTrainer.getCurrentPlayableAlreadyPlayed());
        assertTrue(_iTrainer.getMetronomeTimeRef().isPlaying());

        _iTrainer.stopTraining();
        assertFalse(_iTrainer.getMetronomeTimeRef().isPlaying());
    }

    @SmallTest
    public void testTooLateToDetect() {
        float maxRatioDelay = _iTrainer.getMaxRatioDelay();
        long expectedPlayableDuration = 50;

        long elapsedTime = (long) (maxRatioDelay*expectedPlayableDuration - 10); // Within time

        boolean tooLate = _iTrainer.accessor_tooLateToDetect(expectedPlayableDuration, elapsedTime);
        assertFalse(tooLate);

        elapsedTime = (long) (maxRatioDelay*expectedPlayableDuration + 10); // Out of time

        tooLate = _iTrainer.accessor_tooLateToDetect(expectedPlayableDuration, elapsedTime);
        assertTrue(tooLate);
    }

    @SmallTest
    public void testOnAnalysis_EndOfTraining_Called() throws InterruptedException {
        assertFalse(_mockActivityExercise.endOfTrainingCalled);

        _iTrainer.startTraining();
        _iTrainer.setCurrentlyDetectingPlayableIndex(0);
        assertEquals(false, _iTrainer.getCurrentPlayableAlreadyPlayed());

        // First, let's play the first note
        ITrainer.ContinueDetecting continueDetecting = _iTrainer.onAnalysis(0, null);

        assertEquals(ITrainer.ContinueDetecting.Yes, continueDetecting);
        assertFalse(_mockActivityExercise.endOfTrainingCalled);
        assertFalse(_mockActivityExercise.updateProgressCalled);
        assertFalse(_mockActivityExercise.playedWell);
        assertEquals(0, _mockActivityExercise.points);


        // Now, let's go all the way until the end
        _metronomeTimeRefTest.setEndOfExercise(true);
        continueDetecting = _iTrainer.onAnalysis(_metronomeTimeRefTest.getEndTimestamp(), null);

        assertEquals(ITrainer.ContinueDetecting.No, continueDetecting); // To stopPlayingAnim the init
        assertTrue(_mockActivityExercise.endOfTrainingCalled);
        assertTrue(_mockActivityExercise.updateProgressCalled);
        assertFalse(_mockActivityExercise.playedWell);
        assertEquals(0, _mockActivityExercise.points);
    }

    @SmallTest
    public void testOnAnalysis_WrongNote() {
        Playable detectedPlayable = Note.F2;

        _iTrainer.startTraining();
        _iTrainer.setCurrentlyDetectingPlayableIndex(0);

        assertEquals(false, _iTrainer.getCurrentPlayableAlreadyPlayed());

        ITrainer.ContinueDetecting continueDetecting = _iTrainer.onAnalysis(0, detectedPlayable);

        assertEquals(ITrainer.ContinueDetecting.Yes, continueDetecting);
        assertFalse(_mockActivityExercise.updateProgressCalled);
        assertTrue(_mockActivityExercise.updateProgressTestCalled);

        _iTrainer.stopTraining();
    }

    @SmallTest
    public void testOnAnalysis_RigthNote_OnTime() {
        Playable detectedPlayable = _exercise.getTrack().getPlayable(0);
        _iTrainer.startTraining();
        _iTrainer.setCurrentlyDetectingPlayableIndex(0);

        assertEquals(false, _iTrainer.getCurrentPlayableAlreadyPlayed());
        assertEquals(0, _iTrainer.getCurrentlyDetectingPlayableIndex());

        // Doing right on time
        _metronomeTimeRefTest.virtualTimestamp = 0;
        ITrainer.ContinueDetecting continueDetecting = _iTrainer.onAnalysis(0, detectedPlayable);
        assertEquals(0, _iTrainer.getCurrentlyDetectingPlayableIndex());

        assertEquals(ITrainer.ContinueDetecting.Yes, continueDetecting);
        assertTrue(_mockActivityExercise.updateProgressCalled);
        assertTrue(_mockActivityExercise.updateProgressTestCalled);
        assertTrue(_mockActivityExercise.playedWell);
        assertEquals(Trainer.POINTS_HIT, _mockActivityExercise.points);
        _iTrainer.stopTraining();
    }

    @SmallTest
    public void testOnAnalysis_RigthNote_WithingDelayTime() {
        Playable detectedPlayable = _exercise.getTrack().getPlayable(0);

        _iTrainer.startTraining();
        _iTrainer.setCurrentlyDetectingPlayableIndex(0);

        assertEquals(false, _iTrainer.getCurrentPlayableAlreadyPlayed());

        _metronomeTimeRefTest.virtualTimestamp = 5;
        ITrainer.ContinueDetecting continueDetecting = _iTrainer.onAnalysis(0, detectedPlayable);

        assertEquals(ITrainer.ContinueDetecting.Yes, continueDetecting);
        assertTrue(_mockActivityExercise.updateProgressCalled);
        assertTrue(_mockActivityExercise.updateProgressTestCalled);
        assertTrue(_mockActivityExercise.playedWell);
        assertEquals(Trainer.POINTS_HIT, _mockActivityExercise.points);

        _iTrainer.stopTraining();
    }

    @SmallTest
    public void testOnAnalysis_RigthNote_OutOfTime() {
        Playable detectedPlayable = _exercise.getTrack().getPlayable(0);
        _iTrainer.startTraining();

        _iTrainer.setCurrentlyDetectingPlayableIndex(0);


        assertFalse(_iTrainer.getCurrentPlayableAlreadyPlayed());
        assertFalse(_mockActivityExercise.playedWell);

        // Let's play the note, but out of time
        _metronomeTimeRefTest.setCurrentPlayableIndex(1);
        ITrainer.ContinueDetecting continueDetecting = _iTrainer.onAnalysis(0, detectedPlayable);

        assertEquals(ITrainer.ContinueDetecting.Yes, continueDetecting);
        assertTrue(_mockActivityExercise.updateProgressCalled);
        assertTrue(_mockActivityExercise.updateProgressTestCalled);
        assertFalse(_mockActivityExercise.playedWell);
        assertFalse(_mockActivityExercise.endOfTrainingCalled);
        assertEquals(1, _iTrainer.getCurrentlyDetectingPlayableIndex()); // It should have moved on to

        // We go all the way ot the end
        _mockActivityExercise.reset();
        _metronomeTimeRefTest.setEndOfExercise(true);
        continueDetecting = _iTrainer.onAnalysis(0, detectedPlayable);

        assertEquals(ITrainer.ContinueDetecting.No, continueDetecting);
        assertTrue(_mockActivityExercise.updateProgressCalled);
        assertTrue(_mockActivityExercise.updateProgressTestCalled);
        assertFalse(_mockActivityExercise.playedWell);
        assertTrue(_mockActivityExercise.endOfTrainingCalled);

        _iTrainer.stopTraining();
    }

    @SmallTest
    public void testOnAnalysis_TwoNotesTrack_Simulation() {
        _iTrainer.startTraining();
        _iTrainer.setCurrentlyDetectingPlayableIndex(0);

        assertEquals(false, _iTrainer.getCurrentPlayableAlreadyPlayed());

        // First note
        Playable detectedPlayable = _exercise.getTrack().getPlayable(0);
        _metronomeTimeRefTest.virtualTimestamp = _metronomeTimeRefTest.getPlayableStartTime(0);
        ITrainer.ContinueDetecting continueDetecting = _iTrainer.onAnalysis(0, detectedPlayable);

        assertEquals(ITrainer.ContinueDetecting.Yes, continueDetecting);
        assertTrue(_mockActivityExercise.updateProgressCalled);
        assertTrue(_mockActivityExercise.updateProgressTestCalled);
        assertTrue(_mockActivityExercise.playedWell);
        assertEquals(Trainer.POINTS_HIT, _mockActivityExercise.points);

        _mockActivityExercise.reset();

        // Second note
        detectedPlayable = _exercise.getTrack().getPlayable(1);
        _metronomeTimeRefTest.virtualTimestamp = _metronomeTimeRefTest.getPlayableStartTime(1);
        continueDetecting = _iTrainer.onAnalysis(0, detectedPlayable);

        assertEquals(1, _iTrainer.getCurrentlyDetectingPlayableIndex());
        assertEquals(ITrainer.ContinueDetecting.Yes, continueDetecting);
        assertTrue(_mockActivityExercise.updateProgressTestCalled);
        assertTrue(_mockActivityExercise.playedWell);
        assertEquals(Trainer.POINTS_HIT, _mockActivityExercise.points);
        assertTrue(_mockActivityExercise.updateProgressCalled);

        // Force it to go out of time
        detectedPlayable = null;

        // So late, it is end of the melody
        _metronomeTimeRefTest.virtualTimestamp = 9999999;
        continueDetecting = _iTrainer.onAnalysis(0, detectedPlayable);

        assertEquals(ITrainer.ContinueDetecting.No, continueDetecting); // So late, it is end of training

        _iTrainer.stopTraining();
    }

    @SmallTest
    public void testIsPlayableDetected() {
        _iTrainer.setCurrentlyDetectingPlayableIndex(0);
        Playable detectedPlayable = _exercise.getTrack().getPlayable(0);

        long currentPlayableStartTime = _metronomeTimeRefTest.getPlayableStartTime(0);
        long currentPlayableDuration = _metronomeTimeRefTest.getCurrentPlayableDuration();

        // Right playable on time
        boolean playedWell = _iTrainer.accessor_isPlayableDetected(currentPlayableStartTime, detectedPlayable);
        assertTrue(playedWell);

        playedWell = _iTrainer.accessor_isPlayableDetected(currentPlayableStartTime + (int)(currentPlayableDuration * _iTrainer.getMaxRatioDelay()) - 1, detectedPlayable);
        assertTrue(playedWell);

        // Right playable outta time
        playedWell = _iTrainer.accessor_isPlayableDetected(currentPlayableStartTime + currentPlayableDuration + 1, detectedPlayable);
        assertFalse(playedWell);

        // Wrong playable on time
        playedWell = _iTrainer.accessor_isPlayableDetected(currentPlayableStartTime, Note.A2);
        assertFalse(playedWell);

        // Wrong playable outta time
        playedWell = _iTrainer.accessor_isPlayableDetected(currentPlayableStartTime + currentPlayableDuration + 1, Note.A2);
        assertFalse(playedWell);

        // ----------------------------------------
        // Now let's try with the second playable
        detectedPlayable = _exercise.getTrack().getPlayable(1);

        _iTrainer.setCurrentlyDetectingPlayableIndex(1);
        currentPlayableStartTime = _metronomeTimeRefTest.getPlayableStartTime(1);
        currentPlayableDuration = _metronomeTimeRefTest.getPlayableDurationInMs(1);

        // Right playable on time
        playedWell = _iTrainer.accessor_isPlayableDetected(currentPlayableStartTime, detectedPlayable);
        assertTrue(playedWell);

        playedWell = _iTrainer.accessor_isPlayableDetected(currentPlayableStartTime + (int)(currentPlayableDuration * _iTrainer.getMaxRatioDelay()) - 1, detectedPlayable);
        assertTrue(playedWell);

        // Right playable outta time
        playedWell = _iTrainer.accessor_isPlayableDetected(currentPlayableStartTime + currentPlayableDuration + 1, detectedPlayable);
        assertFalse(playedWell);

        // Wrong playable on time
        playedWell = _iTrainer.accessor_isPlayableDetected(currentPlayableStartTime, Note.A2);
        assertFalse(playedWell);

        // Wrong playable outta time
        playedWell = _iTrainer.accessor_isPlayableDetected(currentPlayableStartTime + currentPlayableDuration + 1, Note.A2);
        assertFalse(playedWell);
    }



}