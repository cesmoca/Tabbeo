package com.tabbeo.Activities.MainContainer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.CourseLibrary.ExercisesLibrary;
import com.tabbeo.R;
import com.tabbeo.Storage.PointsCollection;
import com.tabbeo.Storage.StorageService;


public class FragmentProfile extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        // Current Level
        TextView levelTextView = (TextView) getView().findViewById(R.id.fragment_profile_level_textview);
        levelTextView.setText(getView().getResources().getString(R.string.Profile_Level)+" X");

        // Completion Progress
        ProgressBar completionProgressBar = (ProgressBar) getView().findViewById(R.id.fragment_profile_completion_progressbar);

        // Let's calculate the progress - Very basic and simple
        int totalExercises = ExercisesLibrary.courseExercises.length;
        int completedExercises = 0;

        for(Exercise exercise : ExercisesLibrary.courseExercises){
            PointsCollection pc = StorageService.getExercisePoints(getView().getContext(), exercise.getName());
            if(pc.size() != 0) completedExercises++;
        }

        int progress = completedExercises*100/totalExercises; // 0-100
        completionProgressBar.setProgress(progress);

        // Total accumulatedExperiencePoints
        TextView totalExperienceTextView = (TextView) getView().findViewById(R.id.fragment_profile_total_experience_textview);
        int totalExperience = StorageService.getTotalExperiencePoints(getView().getContext());
        totalExperienceTextView.setText(getView().getResources().getString(R.string.Profile_TotalExperience)+" "+totalExperience);
    }
}
