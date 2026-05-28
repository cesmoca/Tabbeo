package com.tabbeo.Activities.Exercises;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher.ViewFactory;

import com.tabbeo.Activities.TutorialSequencer.ITutorialSequencer;
import com.tabbeo.R;

public class ActivityExerciseSlideshow extends IActivityExerciseFrame {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _inflater.inflate(R.layout.exercise_slideshow, _exerciseMainLayout);

        // Set the text title
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_exercise_tutorial_basic_concepts));

        ImageSwitcher imageSwitcher = (ImageSwitcher) findViewById(R.id.exercise_slideshow_imageswitcher);

        imageSwitcher.setFactory(new ViewFactory() {

            public View makeView() {
                return new ImageView(ActivityExerciseSlideshow.this);
            }
        });

        _exerciseMainLayout.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                _experiencePoints++;

                if(!_tutorialSequencer.injectEvent(getThisActivity(), ITutorialSequencer.Event.Touch)){
                    return; // End of tutorial already
                }
                _picksScoreContainer.setPicksScore(_exercise.experiencePointsToPicks(_experiencePoints));
            }
        });

        restartLevel();
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
    protected void restartLevel() {
        super.restartLevel();
    }
}






