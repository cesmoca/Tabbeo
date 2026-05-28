package com.tabbeo.Storage;


import android.content.Context;
import android.widget.Toast;

import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.CourseLibrary.ExercisesLibrary;

public class StorageService {
    private final static String LOG_TAG = "Storage";
    protected static IStorageImpl _storageImpl = new StorageImplSharedPreferences();

    protected StorageService(IStorageImpl storageImpl) {
        _storageImpl = storageImpl;
    }

    public static PointsCollection getExercisePoints(Context context, String exerciseId) {
        return _storageImpl.getExercisePoints(context, exerciseId);
    }

    public static void addPracticePoints(Context context, String exerciseId, int experiencePoints, Exercise exercise) {
        PointsCollection pointsCollection = getExercisePoints(context, exerciseId);

        PointsCollection.DailyPointsEntry dailyPointsEntry = pointsCollection.getOrCreateTodayPointsEntry();

        float picksScore = exercise.experiencePointsToPicks(experiencePoints);

        dailyPointsEntry.accumulatedExperiencePoints += experiencePoints;
        dailyPointsEntry.maxPicksScore = Math.max(dailyPointsEntry.maxPicksScore, picksScore);

        _storageImpl.storeCollection(context, exerciseId, pointsCollection);
    }

    public static int getTotalExperiencePoints(Context context){
        int totalExperience = 0;

        for(Exercise exercise : ExercisesLibrary.courseExercises){
            PointsCollection pc = getExercisePoints(context, exercise.getName());
            for(PointsCollection.DailyPointsEntry pe : pc){
                totalExperience += pe.accumulatedExperiencePoints;
            }

        }

        return totalExperience;
    }

    public static void ClearScores(Context context) {
        _storageImpl.ClearScores(context);
        if (context != null)
            Toast.makeText(context, "Scores cleared", Toast.LENGTH_SHORT).show();
    }

    public interface IStorageImpl {
        PointsCollection getExercisePoints(Context context, String exerciseId);

        void storeCollection(Context context, String exerciseId, PointsCollection points);

        void ClearScores(Context context);
    }
}