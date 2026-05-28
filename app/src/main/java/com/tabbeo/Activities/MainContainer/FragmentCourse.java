package com.tabbeo.Activities.MainContainer;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tabbeo.Activities.Exercises.ActivityExercisePrevInfo;
import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.CourseLibrary.ExercisePlanner;
import com.tabbeo.CourseLibrary.ExercisesLibrary;
import com.tabbeo.R;
import com.tabbeo.StudentProfile;
import com.tabbeo.Widgets.TabbeoButton;

public class FragmentCourse extends Fragment {
    private static final String LOG_TAG = "FragmentWorldMap";
    private final static int _widthButtonInDp = 80; /*dp*/
    private final static int _marginInDp = 15; /*dp*/

    private int _widthButtonInPx;
    private int _marginInPx;
    private LinearLayout _courseExercisesContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        _courseExercisesContainer = (LinearLayout) this.getView().findViewById(R.id.fragment_course_exercises_container);

        // Changing density pixels (dp / dip) into pixels (px)
        _widthButtonInPx  = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _widthButtonInDp , getResources().getDisplayMetrics());
        _marginInPx  = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _marginInDp , getResources().getDisplayMetrics());
    }

    @Override
    public void onStart(){
        super.onStart();

        // Removing all exercises first
        _courseExercisesContainer.removeAllViews();

        // Adding the course exercises
        {
            ExercisesLibrary.loadExercises(ExercisesLibrary.courseExercises);
            int nExercisesAvailable = ExercisePlanner.howManyCourseExecisesAvailable(getView().getContext());
            for (Exercise exercise : ExercisesLibrary.courseExercises) {
                addExerciseToCourseLayout(exercise, nExercisesAvailable > 0 /*enabled*/);

                if (nExercisesAvailable > 0) nExercisesAvailable--;
            }
        }

        // Adding the test exercises
        if(StudentProfile.getDebugModeEnabled()){
            ExercisesLibrary.loadExercises(ExercisesLibrary.testExercises);

            // Showing a label
            TextView textExercisesLabel = new TextView(getView().getContext());
            textExercisesLabel.setText(getView().getResources().getString(R.string.Course_Test_Exercises));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            layoutParams.setMargins(0, _marginInPx, 0, _marginInPx);
            textExercisesLabel.setLayoutParams(layoutParams);
            textExercisesLabel.setGravity(Gravity.CENTER);
            textExercisesLabel.setAllCaps(true);
            textExercisesLabel.setTextColor(getView().getResources().getColor(R.color.tabbeo_light_blue));

            _courseExercisesContainer.addView(textExercisesLabel);

            for (Exercise exercise : ExercisesLibrary.testExercises) {
                addExerciseToCourseLayout(exercise, true /*enabled*/);
            }
        }

    }

    private void addExerciseToCourseLayout(final Exercise exercise, boolean enabled){
        TabbeoButton exerciseButton = new TabbeoButton(getView().getContext());
        exerciseButton.setImageResource(GetExerciseImageId(exercise.getType()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(_widthButtonInPx, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        layoutParams.setMargins(0, _marginInPx, 0, _marginInPx);
        exerciseButton.setLayoutParams(layoutParams);

        exerciseButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(getActivity(), ActivityExercisePrevInfo.class);
                intent.putExtra("ExerciseName", exercise.getName());
                startActivity(intent);
            }
        });

        exerciseButton.setEnabled(enabled);

        _courseExercisesContainer.addView(exerciseButton);
    }

    int GetExerciseImageId(Exercise.Type type) {
        if (type == Exercise.Type.Slideshow)
            return R.drawable.exercise_icon_basic_concepts;
        else if (type == Exercise.Type.Melody)
            return R.drawable.exercise_icon_melody;
        else if (type == Exercise.Type.MelodyDynamic)
            return R.drawable.exercise_icon_melody;
        else if (type == Exercise.Type.ChordsRhythm)
            return R.drawable.exercise_icon_chords;

        throw new RuntimeException("Icon not found for exercise type: "+type);
    }
}
