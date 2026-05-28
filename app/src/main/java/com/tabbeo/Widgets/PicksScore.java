package com.tabbeo.Widgets;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.tabbeo.Activities.Exercises.ActivityExerciseOnOffButton;
import com.tabbeo.R;

import java.util.ArrayList;
import java.util.List;

public class PicksScore extends View {
    private final static String LOG_TAG = "PicksScore";

    private enum PicksColor {Blue, White}

    private final int TIME_FILL_PICK = 750; /*ms*/
    private Handler _handler;

    private float _picksScore;

    private Bitmap _emptyPickBitmap;
    private Bitmap _fullPickBitmap;
    private int _pickCellWidth; // We divide the space for each pick. this is the width
    private Rect _defaultPickRect = new Rect(); // Where the first pick should be drawn
    private float[] _pickFillings;// How much is each pick filled. We keep track individually, to animate them cool
    private Paint _pickPaint = new Paint();
    private AnimatorSet _animatorSet = new AnimatorSet();

    // Temp Rects for onDraw
    private Rect _tempOnDrawRect = new Rect();
    private Rect _srcBitmapFillingRect = new Rect();
    private Rect _dstFillingRect = new Rect();

    private static Bitmap _blueEmptyPickBitmap;
    private static Bitmap _blueFullPickBitmap;
    private static Bitmap _whiteEmptyPickBitmap;
    private static Bitmap _whiteFullPickBitmap;

    public PicksScore(Context context, AttributeSet attrs) {
        super(context, attrs);

        _handler = new Handler(context.getMainLooper());
        _pickPaint.setFilterBitmap(true);

        // Getting the picks color from the custom attribute
        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.PicksScore);
        String strPicksColor = styledAttrs.getString(R.styleable.PicksScore_picks_color);
        styledAttrs.recycle();

        initBitmaps(context);

        PicksColor picksColor = PicksColor.Blue; // Default option. We are not going to force to put the attribute every time
        if (strPicksColor != null) {
            if (strPicksColor.equalsIgnoreCase("blue")) picksColor = PicksColor.Blue;
            else if (strPicksColor.equalsIgnoreCase("white")) picksColor = PicksColor.White;
            else throw new RuntimeException("Unknown picks color");
        }

        switch (picksColor) {
            case Blue:
                _emptyPickBitmap = _blueEmptyPickBitmap;
                _fullPickBitmap = _blueFullPickBitmap;
                break;
            case White:
                _emptyPickBitmap = _whiteEmptyPickBitmap;
                _fullPickBitmap = _whiteFullPickBitmap;
                break;
        }

        reset();
    }

    private static void initBitmaps(Context context) {
        if (_whiteEmptyPickBitmap != null) return;

        _blueEmptyPickBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_pick_blue);
        _blueFullPickBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.full_pick_blue);
        _whiteEmptyPickBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_pick_white);
        _whiteFullPickBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.full_pick_white);
    }

    public void setPicksScore(float newPicksScore) {
        // Since there could be overlapping animations, every time setPicksScore we cancel the previous animation
        // And set the new one wherever the last one left it. However, the member _picksScore will have the theoretical
        // that it should have after the animation finished. This way, we properly update _pickScore, but use
        // _pickFillings to see where the animation was left.
        if(newPicksScore < 0 || newPicksScore < _picksScore || newPicksScore > ActivityExerciseOnOffButton.MAX_SCORE_PICKS)
            throw new RuntimeException("Invalid new score. New maxPicksScore: "+newPicksScore+" current maxPicksScore: "+_picksScore);

        if(_picksScore == ActivityExerciseOnOffButton.MAX_SCORE_PICKS) return; // Maybe we are calling it again. That's ok
        if(newPicksScore == 0 || _picksScore == newPicksScore) return; // The poor student did not get anything right (if he got zero):(

        float fillingLeft = newPicksScore;

        List<Animator> animators = new ArrayList<>();
        for(int i = 0; fillingLeft > 0; ++i){
            final int pickIndex = i;

            fillingLeft -= _pickFillings[pickIndex]; // This pick already had something filled
            if(_pickFillings[pickIndex] == 1.0f) continue; // It has been already filled

            float fromValue = _pickFillings[pickIndex];
            float toValue = _pickFillings[pickIndex] + fillingLeft;

            if(toValue > 1.0f){
                toValue = 1.0f;
            }

            fillingLeft -= toValue - fromValue;

            final int animDuration = (int) (TIME_FILL_PICK * (toValue-fromValue));

            ValueAnimator va = new ValueAnimator();
            va.setFloatValues(fromValue, toValue);
            va.setDuration(animDuration);

            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    _pickFillings[pickIndex] = (float) animation.getAnimatedValue();

                    _handler.post(new Runnable() {
                        public void run() {
                            invalidate();
                        }
                    });
                }


            });

            animators.add(va);
        }

        _animatorSet.cancel();
        _animatorSet = new AnimatorSet();
        _animatorSet.playTogether(animators);
        _animatorSet.start();

        _picksScore = newPicksScore;
    }

    public float getPicksScore() {
        return _picksScore;
    }

    public void reset() {
        _picksScore = 0;
        _pickFillings = new float[ActivityExerciseOnOffButton.MAX_SCORE_PICKS];
        _animatorSet.cancel();
        _animatorSet = new AnimatorSet();
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        _pickCellWidth = (r - l) / ActivityExerciseOnOffButton.MAX_SCORE_PICKS;
        int pickCellHeight = b - t;

        float pickCellAspectRatio = _pickCellWidth / (float) pickCellHeight;
        float pickAspectRatio = _emptyPickBitmap.getWidth() / (float) _emptyPickBitmap.getHeight();

        if (pickCellAspectRatio > pickAspectRatio) {
            int pickRectWidth = (int) (pickCellHeight * pickAspectRatio);
            _defaultPickRect.set(0, 0, pickRectWidth, pickCellHeight);
        } else {
            int pickRectWidth = (int) (0.90f * _pickCellWidth);
            int pickRectHeight = (int) (pickRectWidth / pickAspectRatio);
            _defaultPickRect.set(0, 0, pickRectWidth, pickRectHeight);
        }

        // Center horizontally, do not let so much space in between
        _pickCellWidth = (int) (_defaultPickRect.width() / 0.90f);
        int leftMargin = ((r - l) - ActivityExerciseOnOffButton.MAX_SCORE_PICKS * _pickCellWidth) / 2;

        _defaultPickRect.offset(leftMargin, 0);

        // Offset to put the pict rect in the middle of the pick cell rect
        _defaultPickRect.offset((_pickCellWidth - _defaultPickRect.width()) / 2, (pickCellHeight - _defaultPickRect.height()) / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        _tempOnDrawRect.set(_defaultPickRect);
        for(int i = 0; i<ActivityExerciseOnOffButton.MAX_SCORE_PICKS; ++i){
            // Let's paint the countour
            canvas.drawBitmap(_emptyPickBitmap, null, _tempOnDrawRect, _pickPaint);

            // Paint the inside filling
            _srcBitmapFillingRect.set(0, (int) ((1 - _pickFillings[i]) * _fullPickBitmap.getHeight()), _fullPickBitmap.getWidth(), _fullPickBitmap.getHeight());
            _dstFillingRect.set(_tempOnDrawRect.left, _tempOnDrawRect.top + (int) ((1 - _pickFillings[i]) * _tempOnDrawRect.height()), _tempOnDrawRect.right, _tempOnDrawRect.bottom);
            canvas.drawBitmap(_fullPickBitmap, _srcBitmapFillingRect, _dstFillingRect, _pickPaint);

            // Move on to the next pick ;)
            _tempOnDrawRect.offset(_pickCellWidth, 0);
        }
    }
}
