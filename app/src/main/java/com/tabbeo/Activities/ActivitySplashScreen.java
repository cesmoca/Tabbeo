package com.tabbeo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.instabug.library.compat.InstabugActivity;
import com.tabbeo.Activities.MainContainer.ActivityMainContainer;
import com.tabbeo.Detector.AudioSource.MicAudioSource;
import com.tabbeo.Detector.DetectorManager;
import com.tabbeo.R;

public class ActivitySplashScreen extends InstabugActivity {
    private ImageView _tabbeoLogo;
    private ImageView _circle;
    private Animation _logoScaleAnim;
    private Animation _circleScaleAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logging);

        // Let's start loading the resources from the beginning
        DetectorManager.loadResoucesInBackground();

        // Let's find a valid sample rate
        MicAudioSource.findValidSampleRateInBackground();

        _tabbeoLogo = (ImageView) findViewById(R.id.Logging_Tabbeo_Logo);
        _logoScaleAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_anim);
        _logoScaleAnim.setFillAfter(true);

        _circle = (ImageView) findViewById(R.id.Logging_blue_circle);
        _circleScaleAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.circle_scale_up_anim);
        _circleScaleAnim.setFillAfter(true);
        _circleScaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent().setClass(ActivitySplashScreen.this, ActivityMainContainer.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });


    }

    @Override
    protected void onStart(){
        super.onStart();

        _tabbeoLogo.startAnimation(_logoScaleAnim);
        _circle.startAnimation(_circleScaleAnim);
    }

}
