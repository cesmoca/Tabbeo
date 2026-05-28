package com.tabbeo.Widgets;

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ToggleButton;

import com.tabbeo.Music.IMetronomeTimeRef;
import com.tabbeo.R;

public class OnOffButton extends ToggleButton implements IOnOffButton{
    private Animation _loadingAnim;
    protected IMetronomeTimeRef _metronomeTimeRef;

    private TimeAnimator _playingTimeAnimator;
    private PlayingAnimationTimeListener _playingAnimationTimeListener;

    public OnOffButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            _loadingAnim = AnimationUtils.loadAnimation(context, R.anim.onoff_button_loading_anim);
            _playingTimeAnimator = new TimeAnimator();
        }

        setTextOff("");
        setTextOn("");
        setText("");
        setTypeface(null, Typeface.BOLD);
        setTextColor(Color.WHITE);
        setBackgroundResource(R.drawable.onoff_button_selector);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int widthButtonInPx = r-l;
        setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)(widthButtonInPx*0.5));
    }

    @Override
    public void startPlayingAnim(final IMetronomeTimeRef metronome) {
        if(!metronome.isPlaying()) throw new RuntimeException("The metronome should have started before staring the onOffButton anim");

        _metronomeTimeRef = metronome;

        setChecked(true);
        setText("" + _metronomeTimeRef.getNBeatsInCountdown());

        _playingAnimationTimeListener = new PlayingAnimationTimeListener(this, _metronomeTimeRef);
        _playingTimeAnimator.setTimeListener(_playingAnimationTimeListener);

        _playingTimeAnimator.start();
    }

    @Override
    public void stopPlayingAnim() {
        _playingTimeAnimator.setTimeListener(null);
        _playingTimeAnimator.cancel();

        setBackgroundResource(R.drawable.onoff_button_selector);
        setChecked(false);
        setScaleX(1.0f);
        setScaleY(1.0f);
    }

    // Called externally
    public void startLoadingAnim() {
        setEnabled(false);
        startAnimation(_loadingAnim);
    }

    // Called externally
    public void stopLoadingAnim() {
        setEnabled(true);
        clearAnimation();
    }

    private static class PlayingAnimationTimeListener implements TimeAnimator.TimeListener {
        private final static float SCALE_FACTOR_FROM = 1.0f;
        private final static float SCALE_FACTOR_TO = 0.75f;

        private ToggleButton _button;
        private boolean _shrinking = true;
        private float _halfBeatInMs;
        private float _lastRatio = 0;
        private int _countdown;

        IMetronomeTimeRef _metronomeTimeRef;

        public PlayingAnimationTimeListener(ToggleButton button, IMetronomeTimeRef metronomeTimeRef) {
            _button = button;
            _halfBeatInMs = metronomeTimeRef.getMsPerBeat() / 2.0f;
            _metronomeTimeRef = metronomeTimeRef;
            _countdown = _metronomeTimeRef.getNBeatsInCountdown();
        }

        @Override
        public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
            updateAnimation(_metronomeTimeRef.getTimestamp(SystemClock.elapsedRealtime()));
        }

        protected void updateAnimation(long elapsedTimeSinceMetronomeStart) {
            float ratio = mod(elapsedTimeSinceMetronomeStart, _halfBeatInMs) / _halfBeatInMs;

            if (ratio < _lastRatio) {
                _shrinking = !_shrinking;

                // Countdown logic
                if (_countdown > 0 && _shrinking) {
                    _countdown--;
                    if (_countdown > 0) {
                        _button.setText("" + _countdown);
                    } else {
                        _button.setText("");
                        _button.setBackgroundResource(R.drawable.onoff_button_pause);
                    }
                }
            }

            _lastRatio = ratio;

            float currentScale;
            if (_shrinking) {
                currentScale = SCALE_FACTOR_FROM - (SCALE_FACTOR_FROM - SCALE_FACTOR_TO) * ratio;
            } else {
                currentScale = SCALE_FACTOR_TO + (SCALE_FACTOR_FROM - SCALE_FACTOR_TO) * ratio;
            }

            _button.setScaleX(currentScale);
            _button.setScaleY(currentScale);
        }

        // Modulus without negative results
        protected static float mod(float x, float y) {
            float result = x % y;
            if (result < 0)
                result += y;
            return result;
        }
    }
}

