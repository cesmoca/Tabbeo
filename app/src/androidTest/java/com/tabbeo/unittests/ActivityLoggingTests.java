package com.tabbeo.unittests;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.tabbeo.Activities.ActivitySplashScreen;
import com.tabbeo.Activities.Exercises.ActivityExerciseOnOffButton;
import com.tabbeo.StudentProfile;

public class ActivityLoggingTests extends ActivityInstrumentationTestCase2<ActivitySplashScreen> {
    private ActivitySplashScreen _activity;

    public ActivityLoggingTests() {
        super(ActivitySplashScreen.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        Instrumentation instrumentation = getInstrumentation();

        Intent intent = new Intent().setClass(instrumentation.getTargetContext(), ActivityExerciseOnOffButton.class);

        setActivityIntent(intent);
        _activity = getActivity();
    }

    @Override
    public void tearDown() {
        _activity.finish();
    }

    @SmallTest
    public void testShouldSetContextInStudentProfile(){
        StudentProfile.getDebugModeEnabled();
    }
}
