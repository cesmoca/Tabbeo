package com.tabbeo.Activities.Exercises;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instabug.library.compat.InstabugActionBarActivity;
import com.tabbeo.Activities.TutorialSequencer.ITutorialSequencer;
import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.CourseLibrary.ExercisesLibrary;
import com.tabbeo.R;
import com.tabbeo.Storage.StorageService;
import com.tabbeo.StudentProfile;
import com.tabbeo.Widgets.PicksScore;

public abstract class IActivityExerciseFrame extends InstabugActionBarActivity {
    public final static int MAX_SCORE_PICKS = 5;

    protected Toolbar _toolbar;
    protected LayoutInflater _inflater;

    protected Exercise _exercise;
    protected int _experiencePoints;
    protected ITutorialSequencer _tutorialSequencer;

    protected RelativeLayout _exerciseMainLayout;
    protected PicksScore _picksScoreContainer;
    protected RelativeLayout _explanationContainer;

    // Debug info
    protected TextView _debugVirtualTimestampTextView;
    protected TextView _debugExpectedPlayableTextView;
    protected TextView _debugDetectedPlayableTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_frame);

        String exerciseName = (String) getIntent().getExtras().get("ExerciseName");
        _exercise = ExercisesLibrary.getExerciseByName(exerciseName);

        // Initializing the main exercise layout and the post exercise
        _inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        _toolbar = (Toolbar) findViewById(R.id.exercise_frame_toolbar);
        setSupportActionBar(_toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        _exerciseMainLayout = (RelativeLayout) findViewById(R.id.exercise_frame_main_layout);
        _explanationContainer = (RelativeLayout) findViewById(R.id.exercise_frame_bottom_explanation_layout);

        final ImageButton helpButton = (ImageButton) findViewById(R.id.exercise_frame_help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpDialogFragment helpDialog = new HelpDialogFragment();
                helpDialog.show(getSupportFragmentManager(), "helpDialog");            }
        });

        // Let's set the debug info
        LinearLayout debugInfoLayout = (LinearLayout) findViewById(R.id.exercise_frame_debug_info_layout);
        if(StudentProfile.getDebugModeEnabled()){
            debugInfoLayout.setVisibility(View.VISIBLE);
            _debugVirtualTimestampTextView = (TextView) findViewById(R.id.exercise_frame_debug_virtual_timestamp_textview);
            _debugExpectedPlayableTextView = (TextView) findViewById(R.id.exercise_frame_debug_expected_playable_textview);
            _debugDetectedPlayableTextView = (TextView) findViewById(R.id.exercise_frame_debug_detected_playable_textview);
        }else{
            debugInfoLayout.setVisibility(View.GONE);
        }

        _picksScoreContainer = (PicksScore) findViewById(R.id.exercise_frame_ScorePicks_Container);

        _tutorialSequencer = _exercise.getTutorial();
    }

    protected void showExerciseEnding() {
        ExerciseEndingDialogFragment exerciseEndingDialog = new ExerciseEndingDialogFragment();

        Bundle args = new Bundle();
        args.putFloat("PicksScore", _exercise.experiencePointsToPicks(_experiencePoints));
        args.putInt("ExperiencePoints", _experiencePoints);
        args.putInt("ExerciseMaxPoints", _exercise.getMaxPoints());

        exerciseEndingDialog.setArguments(args);
        exerciseEndingDialog.show(getSupportFragmentManager(), "exerciseEndingDialog");
    }

    final protected IActivityExerciseFrame getThisActivity(){ return this; }

    protected void restartLevel() {
        _experiencePoints = 0;

        // Emptying the picks
        _picksScoreContainer.reset();


        if (_tutorialSequencer != null) {
            _tutorialSequencer.start(this);
        }
    }

    public void endOfTrainingShowSummary(){
        StorageService.addPracticePoints(getApplicationContext(), this._exercise.getName(), _experiencePoints, _exercise);
        showExerciseEnding();
    }
}
