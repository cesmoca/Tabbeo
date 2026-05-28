package com.tabbeo.CourseLibrary;

import android.content.Context;

import com.tabbeo.Activities.Exercises.IActivityExerciseFrame;
import com.tabbeo.Storage.PointsCollection;
import com.tabbeo.Storage.StorageService;
import com.tabbeo.StudentProfile;

public class ExercisePlanner {

    public static int howManyCourseExecisesAvailable(Context context){
        // In debug mode, all of the exercises are available
        if(StudentProfile.getDebugModeEnabled()){
            return ExercisesLibrary.courseExercises.length;
        }

        int nExercisesAvailable = 1; // The first one at least
        for(int i=0; i< ExercisesLibrary.courseExercises.length; ++i) {
            Exercise exercise = ExercisesLibrary.courseExercises[i];

            PointsCollection pointsCollection = StorageService.getExercisePoints(context, exercise.getName());
            PointsCollection.DailyPointsEntry maxDailyPointsEntry = pointsCollection.getMaxPointsEntry();

            // Sanity check
            if(maxDailyPointsEntry != null && maxDailyPointsEntry.maxPicksScore > IActivityExerciseFrame.MAX_SCORE_PICKS){
                throw new RuntimeException("There is something wrong with the scores we are storing. MaxPicksScore for this exercise is "+maxDailyPointsEntry.maxPicksScore+ "which is bigger than the maximun allowed: "+IActivityExerciseFrame.MAX_SCORE_PICKS);
            }

            if(maxDailyPointsEntry != null && maxDailyPointsEntry.maxPicksScore >= IActivityExerciseFrame.MAX_SCORE_PICKS * 0.5f ){
                nExercisesAvailable++;
            }else{
                break;
            }
        }

        // All of them are available
        if(nExercisesAvailable > ExercisesLibrary.courseExercises.length){
            nExercisesAvailable = ExercisesLibrary.courseExercises.length;
        }

        return nExercisesAvailable;
    }
}
