package com.tabbeo.unittests;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.tabbeo.Activities.Exercises.ActivityExerciseOnOffButton;
import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.CourseLibrary.ExercisesLibrary;
import com.tabbeo.Detector.AudioSource.MicAudioSource;
import com.tabbeo.ExerciseLibraryTest;
import com.tabbeo.unittests.Mocks.MockStorageImpl;
import com.tabbeo.unittests.Mocks.MockTrainer;
import com.tabbeo.R;
import com.tabbeo.Storage.StorageService;
import com.tabbeo.Widgets.OnOffButton;
import com.tabbeo.Widgets.PicksScore;

class StorageServiceTest extends StorageService{

    private StorageServiceTest(IStorageImpl storageImpl) {
        super(storageImpl);
    }

    public static void setStorageImplementation(IStorageImpl storageImpl) {
        _storageImpl = storageImpl;
    }
}

public class ActivityExerciseOnOffButtonTests extends ActivityInstrumentationTestCase2<ActivityExerciseOnOffButton> {
    private ActivityExerciseOnOffButton _activity;
    private OnOffButton _onOffButton;
    private PicksScore _picksScoreContainer;

    public ActivityExerciseOnOffButtonTests() {
        super(ActivityExerciseOnOffButton.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MicAudioSource.findValidSample();

        // Avoid to write to the actual storage
        StorageServiceTest.setStorageImplementation(new MockStorageImpl());
    }

    // We introduce this function so that we can have some control over how to initialize
    // the OnOffButton activity. Do not use UiThreadTest or it will not work!!
    private void initActivity(Exercise exercise, boolean useMockTrainer) {
        ExercisesLibrary.loadExercise(exercise);

        Intent intent = new Intent().setClass(getInstrumentation().getTargetContext(), ActivityExerciseOnOffButton.class);
        intent.putExtra("ExerciseName", exercise.getName());
        intent.putExtra("ExerciseTempo", 50);

        setActivityIntent(intent);
        _activity = getActivity();

        if(useMockTrainer){
            MockTrainer _mockTrainer = new MockTrainer();
            _activity.test_setTrainer(_mockTrainer);
        }

        _onOffButton = (OnOffButton) _activity.findViewById(R.id.exercise_onoffbutton_OnOffButton);
        _picksScoreContainer = (PicksScore) _activity.findViewById(R.id.exercise_frame_ScorePicks_Container);
    }


    @MediumTest
    //@UiThreadTest WE CAN'T USE THIS OR THE INITACTIVITY METHOD WILL NOT WORK PROPERLY
    public void testMockTrainer_IsChecked() throws InterruptedException {
        initActivity(ExerciseLibraryTest.twoChordsExerciseTimed, true /*usMockTrainer*/);

        assertTrue(!_onOffButton.isChecked());

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _onOffButton.performClick();

                assertTrue(_onOffButton.isChecked());

                _onOffButton.performClick();

                assertTrue(!_onOffButton.isChecked());
            }
        });

        getInstrumentation().waitForIdleSync();
    }

    @SmallTest
    public void testMockTrainer_UpdateProgress_Played_AddsPoints() {
        initActivity(ExerciseLibraryTest.twoChordsExerciseTimed, true /*usMockTrainer*/);

        assertEquals(0, _activity.test_getTotalPoints());
        _activity.updateProgress(true, 5);
        assertEquals(5, _activity.test_getTotalPoints());
        _activity.updateProgress(true, 5);
        assertEquals(10, _activity.test_getTotalPoints());
    }

    @SmallTest
    public void testMockTrainer_EndOfTraining_CalledFromNotUiThread_IsOk() throws InterruptedException {
        initActivity(ExerciseLibraryTest.twoChordsExerciseTimed, true /*usMockTrainer*/);

        _activity.endOfTrainingShowSummary();
    }

    @SmallTest
    public void testMockTrainer_SetPicksScore_EverythingOk() {
        initActivity(ExerciseLibraryTest.twoChordsExerciseTimed, true /*usMockTrainer*/);

        try {
            _picksScoreContainer.setPicksScore(-5); // Not ok
            assertTrue(false);
        } catch (RuntimeException ignore) {}

        _picksScoreContainer.setPicksScore(0); // Ok. If the student gets everything wrong

        _picksScoreContainer.setPicksScore(2); // Ok
        _picksScoreContainer.setPicksScore(ActivityExerciseOnOffButton.MAX_SCORE_PICKS); // OK

        try {
            _picksScoreContainer.setPicksScore(ActivityExerciseOnOffButton.MAX_SCORE_PICKS + 1); // Not ok
            assertTrue(false);
        } catch (RuntimeException ignore) {}
    }

    @SmallTest
    public void testMockTrainer_SetPicksScore_CantGoDownwards() {
        initActivity(ExerciseLibraryTest.twoChordsExerciseTimed, true /*usMockTrainer*/);

        _picksScoreContainer.setPicksScore(2); // Ok

        try {
            _picksScoreContainer.setPicksScore(1); // Not Ok, don't go downwards
            assertTrue(false);
        } catch (RuntimeException ignore) {}
    }

    @SmallTest
    //@UiThreadTest WE CAN'T USE THIS OR THE INITACTIVITY METHOD WILL NOT WORK PROPERLY
    public void testMockTrainer_StartExercise_StopExercise_GoBack_DoesNotCrash() {
        initActivity(ExerciseLibraryTest.twoChordsExerciseTimed, true /*usMockTrainer*/);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _onOffButton.performClick(); // On
                _onOffButton.performClick(); // Off
                _activity.onBackPressed();
            }
        });

        getInstrumentation().waitForIdleSync();
    }

    @SmallTest
    //@UiThreadTest WE CAN'T USE THIS OR THE INITACTIVITY METHOD WILL NOT WORK PROPERLY
    public void testMockTrainer_OnResume_CallsSetCheckedFalseInOnOffButton() {
        /*
        So listen. Right now we want that in an orientation change, everything restarts.
        It is easy, even nice. Doing otherwise will we hard. We like it this way
        If Android handles the orientation and recreates the activity, it has a hard
        time trying to set the onOffButton to false. It seems that the way to get it
        is calling setChecked(false) in the OnResume! So please do not delete this
        test if you are not changing the behaviour
         */
        initActivity(ExerciseLibraryTest.twoChordsExerciseTimed, true /*usMockTrainer*/);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _onOffButton.setChecked(true);
                assertTrue(_onOffButton.isChecked());
                getInstrumentation().callActivityOnResume(_activity);
                assertFalse(_onOffButton.isChecked());
            }
        });


        getInstrumentation().waitForIdleSync();
    }

    @MediumTest
    //@UiThreadTest WE CAN'T USE THIS OR THE INITACTIVITY METHOD WILL NOT WORK PROPERLY
    public void testTimedTrainer_Melody_StartAndStop() {
        initActivity(ExerciseLibraryTest.twoNotesExerciseTimed, false /*useMockTrainer*/);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _onOffButton.performClick(); // On
                _onOffButton.performClick(); // Off
            }
        });

        getInstrumentation().waitForIdleSync();
    }

    @MediumTest
    //@UiThreadTest WE CAN'T USE THIS OR THE INITACTIVITY METHOD WILL NOT WORK PROPERLY
    public void testTimedTrainer_Chords_StartAndStop() {
        initActivity(ExerciseLibraryTest.twoChordsExerciseTimed, false /*useMockTrainer*/);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _onOffButton.performClick(); // On
                _onOffButton.performClick(); // Off
            }
        });

        getInstrumentation().waitForIdleSync();
    }

    @MediumTest
    //@UiThreadTest WE CAN'T USE THIS OR THE INITACTIVITY METHOD WILL NOT WORK PROPERLY
    public void testInteractiveTrainer_Melody_StartAndStop() {
        initActivity(ExerciseLibraryTest.twoNotesExerciseInteractive, false /*useMockTrainer*/);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _onOffButton.performClick(); // On
                _onOffButton.performClick(); // Off
            }
        });

        getInstrumentation().waitForIdleSync();
    }

    @MediumTest
    //@UiThreadTest WE CAN'T USE THIS OR THE INITACTIVITY METHOD WILL NOT WORK PROPERLY
    public void testInteractiveTrainer_Chords_StartAndStop() {
        initActivity(ExerciseLibraryTest.twoChordsExerciseInteractive, false /*useMockTrainer*/);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _onOffButton.performClick(); // On
                _onOffButton.performClick(); // Off
            }
        });

        getInstrumentation().waitForIdleSync();
    }
}
