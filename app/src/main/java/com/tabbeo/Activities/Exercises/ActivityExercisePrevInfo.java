package com.tabbeo.Activities.Exercises;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.instabug.library.compat.InstabugActionBarActivity;
import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.CourseLibrary.ExercisesLibrary;
import com.tabbeo.R;
import com.tabbeo.Storage.PointsCollection;
import com.tabbeo.Storage.StorageService;
import com.tabbeo.Widgets.PicksScore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ActivityExercisePrevInfo extends InstabugActionBarActivity {
    private final static String LOG_TAG = "PreExercise";
    private final int ANIM_DELAY = 250; // ms. A little delay for the animations to let the activity load

    private TableLayout _scoresTable;
    private LayoutInflater _inflater;
    private Exercise _exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_prev_info);

        String exerciseName = (String) getIntent().getExtras().get("ExerciseName");
        _exercise = ExercisesLibrary.getExerciseByName(exerciseName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.exercise_prev_info_toolbar);
        setSupportActionBar(toolbar);

        // Set the text title
        getSupportActionBar().setTitle(_exercise.getName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        _inflater = (LayoutInflater) getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        _scoresTable = (TableLayout) findViewById(R.id.exercise_prev_info_TableBelow);

        Button playButton = (Button) findViewById(R.id.PreExercise_Button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(ActivityExercisePrevInfo.this, getExerciseActivity(_exercise.getType()));
                intent.putExtra("ExerciseName", _exercise.getName());
                intent.putExtra("ExerciseTempo", 50 /*hardcoded tempo. This will be calculated from a different place*/);
                startActivity(intent);
                finish(); // We close the pre exercise. Don't go back to it
            }
        });

        // Set the type and difficulty of exercise_frame
        TextView exerciseTypeAndDifficulty = (TextView) findViewById(R.id.exercise_prev_info_Difficulty_Type_TextView);
        exerciseTypeAndDifficulty.setText(getResources().getString(_exercise.getType().getStringRes())+" - "+getResources().getString(R.string.PreExercise_Difficulty)+" (NONE)");

        // Set the description
        if(_exercise.getDescriptionId() != null) {
            TextView exerciseDescription = (TextView) findViewById(R.id.exercise_prev_info_description);
            exerciseDescription.setText(_exercise.getDescriptionId());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Functionality for the back button
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Set the scores
        _scoresTable.removeAllViews();

        PointsCollection points = StorageService.getExercisePoints(getApplicationContext(), _exercise.getName());

        if (!points.isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

            for (PointsCollection.DailyPointsEntry entry : points) {
                addScore(dateFormat.format(entry.date.getTime()), entry.accumulatedExperiencePoints, entry.maxPicksScore);
            }

            PointsCollection.DailyPointsEntry maxPicksScoreEntry = points.getMaxPointsEntry();
            fillScorePicks(maxPicksScoreEntry.maxPicksScore);
        }
    }

    private void fillScorePicks(final float picksScore) {
        if (picksScore < 0 || picksScore > 5) {
            throw new RuntimeException("Invalid maxPicksScore: " + picksScore);
        }

        final PicksScore picksScoreContainer = (PicksScore) findViewById(R.id.exercise_prev_info_PicksScore_Container);

        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                picksScoreContainer.setPicksScore(picksScore);
            }
        },ANIM_DELAY);

        // Update the summary phrase
        TextView summaryPhrase = (TextView) findViewById(R.id.exercise_prev_info_SummaryText);
        String summaryStr = "";
        if (picksScore < 1) {
            summaryStr = getString(R.string.PreExercise_YouNeedToPlay);
        } else if (picksScore < 2) {
            summaryStr = getString(R.string.PreExercise_Congrats1);
        } else if (picksScore < 3) {
            summaryStr = getString(R.string.PreExercise_Congrats2);
        } else if (picksScore < 4) {
            summaryStr = getString(R.string.PreExercise_Congrats3);
        } else if (picksScore < 5) {
            summaryStr = getString(R.string.PreExercise_Congrats4);
        } else if (picksScore == 5) {
            summaryStr = getString(R.string.PreExercise_Congrats5);
        }
        summaryPhrase.setText(summaryStr);
    }

    private void addScore(String date, int experience, final float picksScore) {
        LinearLayout scoresRow = (LinearLayout) _inflater.inflate(R.layout.score_row, _scoresTable);

        TextView dateTextView = (TextView) scoresRow.findViewById(R.id.score_row_date_textview);
        dateTextView.setText(date);

        TextView pointsTextView = (TextView) scoresRow.findViewById(R.id.score_row_points_textview);
        pointsTextView.setText("" + experience);

        final PicksScore picksScoreContainer = (PicksScore) scoresRow.findViewById(R.id.score_row_picks_score);

        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                picksScoreContainer.setPicksScore(picksScore);
            }
        },ANIM_DELAY);
    }

    java.lang.Class getExerciseActivity(Exercise.Type type) {
        if (type == Exercise.Type.Slideshow)
            return ActivityExerciseSlideshow.class;
        else if (type == Exercise.Type.Melody || type == Exercise.Type.ChordsRhythm || type == Exercise.Type.MelodyDynamic)
            return ActivityExerciseOnOffButton.class;

        throw new RuntimeException("Invalid exercise type");
    }

}
