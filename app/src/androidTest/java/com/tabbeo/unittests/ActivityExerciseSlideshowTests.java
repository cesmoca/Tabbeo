package com.tabbeo.unittests;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.RelativeLayout;

import com.tabbeo.Activities.Exercises.ActivityExerciseSlideshow;
import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.CourseLibrary.ExercisesLibrary;
import com.tabbeo.ExerciseLibraryTest;
import com.tabbeo.unittests.Mocks.MockStorageImpl;
import com.tabbeo.R;

public class ActivityExerciseSlideshowTests extends ActivityInstrumentationTestCase2<ActivityExerciseSlideshow> {
    private ActivityExerciseSlideshow _activity;
    private Instrumentation _instrumentation;
    private RelativeLayout _exerciseMainLayout;
    private RelativeLayout _explanationContainer;

    public ActivityExerciseSlideshowTests() {
        super(ActivityExerciseSlideshow.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Avoid to write to the actual storage
        StorageServiceTest.setStorageImplementation(new MockStorageImpl());

        _instrumentation = getInstrumentation();

        Exercise exercise = ExerciseLibraryTest.slideshowExercise;
        ExercisesLibrary.loadExercise(exercise);

        Intent intent = new Intent().setClass(_instrumentation.getTargetContext(), ActivityExerciseSlideshow.class);
        intent.putExtra("ExerciseName", exercise.getName());

        setActivityIntent(intent);
        _activity = getActivity();

        _exerciseMainLayout = (RelativeLayout) _activity.findViewById(R.id.exercise_frame_main_layout);
        _explanationContainer = (RelativeLayout) _activity.findViewById(R.id.exercise_frame_bottom_explanation_layout);
    }

    @SmallTest
    @UiThreadTest
    public void testDoTutorialEnd2End() throws InterruptedException {
        assertEquals(View.VISIBLE, _explanationContainer.getVisibility());

        while(_explanationContainer.getVisibility() == View.VISIBLE) {
            _exerciseMainLayout.performClick();
        }

        // Performing a few extra lost clicks
        _exerciseMainLayout.performClick();
        _exerciseMainLayout.performClick();
        _exerciseMainLayout.performClick();

        assertNotSame(View.VISIBLE, _explanationContainer.getVisibility());
    }

}
