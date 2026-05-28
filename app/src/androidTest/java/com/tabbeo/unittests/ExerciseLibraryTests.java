package com.tabbeo.unittests;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.CourseLibrary.ExercisesLibrary;

public class ExerciseLibraryTests  extends InstrumentationTestCase {

    @Override
    protected void setUp() throws Exception { }

    @Override
    protected void tearDown() throws Exception {
    }

    @SmallTest
    public void testThereIsNoCollisionInExerciseNames() {
        // Let's create one array with all of the exercises, for easyness
        Exercise[] allExercises = new Exercise[ExercisesLibrary.courseExercises.length + ExercisesLibrary.testExercises.length];
        {
            int i = 0;
            for (Exercise e : ExercisesLibrary.courseExercises) {
                allExercises[i] = e;
                i++;
            }

            for (Exercise e : ExercisesLibrary.testExercises) {
                allExercises[i] = e;
                i++;
            }
        }

        // Now, let's check there is no collision
        for(int i=0; i< allExercises.length; ++i){
            Exercise e1 = allExercises[i];
            for(int j=i+1; j<allExercises.length; ++j){
                Exercise e2 = allExercises[j];
                assertNotSame(e1.getName(), e2.getName());
            }
        }
    }
}
