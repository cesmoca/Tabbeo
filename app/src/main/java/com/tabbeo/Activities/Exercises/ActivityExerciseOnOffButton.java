package com.tabbeo.Activities.Exercises;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.instabug.library.IBGInvocationMode;
import com.instabug.library.Instabug;
import com.tabbeo.Activities.TutorialSequencer.ITutorialSequencer;
import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.Detector.DetectorManager;
import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.TabbeoApp;
import com.tabbeo.Trainer.ITrainer;
import com.tabbeo.Trainer.Trainer;
import com.tabbeo.R;
import com.tabbeo.Widgets.ExerciseWidget;
import com.tabbeo.Widgets.IExerciseWidget;
import com.tabbeo.Widgets.OnOffButton;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class ActivityExerciseOnOffButton extends IActivityExerciseFrame {
    private final static String LOG_TAG = "Exercise";

    protected FrameLayout _exerciseWidgetsLayout;
    protected ITrainer _trainer;
    protected ImageView _chordy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _inflater.inflate(R.layout.exercise_onoffbutton, _exerciseMainLayout);

        // Set the text title
        getSupportActionBar().setTitle(_exercise.getName());

        _chordy = (ImageView) findViewById(R.id.exercise_onoffbutton_RightSide_AnimatedChordy);
        _exerciseWidgetsLayout = (FrameLayout) findViewById(R.id.exercise_onoffbutton_exercise_widgets_container);

        _explanationContainer.setVisibility(View.GONE); // We do not show the explanation here, yet

        final OnOffButton onOffButton = (OnOffButton) findViewById(R.id.exercise_onoffbutton_OnOffButton);
        onOffButton.startLoadingAnim(); // Let's start the animation as soon as possible :)

        int exerciseTempo = (int) getIntent().getExtras().get("ExerciseTempo");

        _trainer = new Trainer(_exercise.getTrainerType(), this, _exercise, onOffButton, exerciseTempo);

        // Let's set the type of exercise
        if (_exercise.getType() == Exercise.Type.Melody) {
            _inflater.inflate(R.layout.exercise_melody, _exerciseWidgetsLayout);
        } else if (_exercise.getType() == Exercise.Type.ChordsRhythm) {
            _inflater.inflate(R.layout.exercise_chords_rhythm, _exerciseWidgetsLayout);
        } else {
            throw new RuntimeException("Unknown type of exercise: "+_exercise.getType());
        }

        // We make sure they are init'd before starting the loading process
        final IExerciseWidget[] exerciseWidgets = inflateExerciseWidgets();
        for (IExerciseWidget widget : exerciseWidgets) {
            widget.init(_exercise, _trainer.getMetronomeTimeRef());
        }

        AsyncTask<Void, Void, Void> loadingTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DetectorManager.loadResources();

                // We prepare the widgets on parallel, and not add them until they are ready
                // This way, we make sure no onLayout is called on them until they are fully loaded.
                // At the end, we call invalidate so that onLayout is called again on them
                for (IExerciseWidget widget : exerciseWidgets) {
                    widget.loadExercise();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void param) {
                // Here we finally add the to the activity and invalidate them so that they receive
                // a proper onLayout call
                _exerciseWidgetsLayout.invalidate();
                onOffButton.stopLoadingAnim();
            }
        };
        loadingTask.execute();

        onOffButton.setOnClickListener(new OnOffButton.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onOffButton.isChecked()) {
                    _trainer.startTraining();
                } else {
                    _trainer.stopTraining();
                    restartLevel();
                }
                if (_tutorialSequencer != null)
                    _tutorialSequencer.injectEvent(getThisActivity(), ITutorialSequencer.Event.OnOffButton);
            }
        });

        if (_tutorialSequencer != null) {
            _exerciseMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!_tutorialSequencer.injectEvent(getThisActivity(), ITutorialSequencer.Event.Touch))
                        return;
                }
            });
        }

        // Starting in API 23 (Marshmallow), we need to ask for permissions on runtime
        // We need the RECORD_AUDIO permission, so let's ask for it here
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                AlertDialog.Builder whyWeNeedMicPermissionDialog = new AlertDialog.Builder(ActivityExerciseOnOffButton.this, R.style.TabbeoDialogTheme);
                whyWeNeedMicPermissionDialog.setTitle(R.string.why_we_need_record_audio_title)
                        .setMessage(R.string.why_we_need_record_audio_explanation)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(ActivityExerciseOnOffButton.this, new String[]{Manifest.permission.RECORD_AUDIO}, TabbeoApp.TABBEO_PERMISSION_RECORD_AUDIO);
                            }
                        }).create().show();
            } else { // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                        TabbeoApp.TABBEO_PERMISSION_RECORD_AUDIO);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Functionality for the back button
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        OnOffButton onOffButton = (OnOffButton) findViewById(R.id.exercise_onoffbutton_OnOffButton);
        onOffButton.setChecked(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        restartLevel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        _trainer.stopTraining();
    }

    public void updateProgressTest(final long virtualTimestamp, final Playable detectedPlayable) {
        if (_debugVirtualTimestampTextView == null) return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _debugVirtualTimestampTextView.setText(String.format(Locale.US, "#%d''%02d", TimeUnit.MILLISECONDS.toSeconds(virtualTimestamp), (virtualTimestamp % 1000) / 10));
                _debugExpectedPlayableTextView.setText("Expected: " + _trainer.getExpectedPlayable());
                _debugDetectedPlayableTextView.setText("Detected: " + detectedPlayable);
            }
        });
    }

    public void updateProgress(final boolean playedWell, final int points) {
        _experiencePoints += points;
        final float picksScore = _exercise.experiencePointsToPicks(_experiencePoints);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update the score
                _picksScoreContainer.setPicksScore(picksScore);

                // Update the chordy
                if (playedWell) {
                    _chordy.setImageResource(R.drawable.chordy_nice);
                } else {
                    _chordy.setImageResource(R.drawable.chordy_bad);
                }

                if (_tutorialSequencer != null && playedWell) {
                    _tutorialSequencer.injectEvent(getThisActivity(), ITutorialSequencer.Event.PlayablePlayedCorrectly);
                }
            }
        });
    }

    @Override
    protected void restartLevel() {
        super.restartLevel();

        // Reset the chordy
        _chordy.setImageResource(R.drawable.chordy_normal);
    }

    protected IExerciseWidget[] inflateExerciseWidgets() {
        IExerciseWidget[] exerciseWidgets;
        if (_exercise.getType() == Exercise.Type.Melody) {
            exerciseWidgets = new ExerciseWidget[]{(ExerciseWidget) findViewById(R.id.exercise_melody_fretboard)};
        }else if (_exercise.getType() == Exercise.Type.MelodyDynamic) {
            exerciseWidgets = new ExerciseWidget[]{(ExerciseWidget) findViewById(R.id.exercise_melody_dynamic_fretboard)};
        } else if (_exercise.getType() == Exercise.Type.ChordsRhythm) {
            exerciseWidgets = new ExerciseWidget[]{(ExerciseWidget) findViewById(R.id.exercise_chords_rhythm_fretboard),
                    (ExerciseWidget) findViewById(R.id.exercise_chords_rhythm_chord_names),
                    (ExerciseWidget) findViewById(R.id.exercise_chords_rhythm_chord_strokes_stream)};
        } else {
            throw new RuntimeException("Unsupported type of exercise: " + _exercise.getType());
        }

        return exerciseWidgets;
    }

    // Starting API 23 Marshmallow, the permissions are requested on runtime. Here we handle
    // the answer of the users to that request
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case TabbeoApp.TABBEO_PERMISSION_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The result has been granted. We do not do anything, just continue :)
                } else {
                    // Permission denied :( We show a toast and close the activity
                    Toast.makeText(getApplicationContext(), R.string.why_we_need_access_to_mic, Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }

    // Interface for tutorials. They need to access the activity components ;)
    public ITrainer getTrainer() {
        return _trainer;
    }

    // ---------------------------------------
    // Test Methods - Do not use in real code
    // ---------------------------------------
    public int test_getTotalPoints() {
        Log.e(LOG_TAG, "[test_getTotalPoints] This API is not intended for production use");
        return _experiencePoints;
    }

    public void test_setTrainer(ITrainer t) {
        Log.e(LOG_TAG, "[test_setTrainer] This API is not intended for production use");
        _trainer = t;
    }

    public void test_setExercise(Exercise e) {
        Log.e(LOG_TAG, "[test_setExercise] This API is not intended for production use");
        _exercise = e;
    }
    //----------------------------------------
    //----------------------------------------
}

