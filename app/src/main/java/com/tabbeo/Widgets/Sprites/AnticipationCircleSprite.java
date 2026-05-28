package com.tabbeo.Widgets.Sprites;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;

import com.tabbeo.Music.Music;
import com.tabbeo.R;
import com.tabbeo.Widgets.ICanvas;

public class AnticipationCircleSprite extends IFretboardElementSprite{
    private final static float ANTICIPATION_CIRCLE_SCALE_FACTOR_FROM = 1.5f;
    private final static float ANTICIPATION_CIRCLE_SCALE_FACTOR_TO = 1.0f;

    private Paint _anticipationCirclePaint = new Paint();
    private CircleParams _circleParams = new CircleParams();
    private Music.GuitarString _string;
    int _noteFret;
    int _minFret;

    public AnticipationCircleSprite(Context context, Music.GuitarString string, int noteFret, int minFret, long playableStartTime, long anticipationDuration) {
        super(playableStartTime, anticipationDuration, 0);

        _string = string;
        _noteFret = noteFret;
        _minFret = minFret;

        initBitmaps(context);
    }

    private void initBitmaps(Context context) {
        _anticipationCirclePaint = new Paint();
        _anticipationCirclePaint.setColor(context.getResources().getColor(R.color.tabbeo_dark_blue));
        _anticipationCirclePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void drawSprite(ICanvas canvas, long virtualTimestamp) {
        float anticipationCircleScaleFactor = interpolate(ANTICIPATION_CIRCLE_SCALE_FACTOR_FROM, ANTICIPATION_CIRCLE_SCALE_FACTOR_TO, _startTime, _playableStartTime, virtualTimestamp);
        canvas.save();
        canvas.scale(anticipationCircleScaleFactor, anticipationCircleScaleFactor, _circleParams.center.x, _circleParams.center.y);
        canvas.drawCircle(_circleParams.center.x, _circleParams.center.y, _circleParams.radius, _anticipationCirclePaint);
        canvas.restore();
    }

    @Override
    public void setAlpha(float alpha) {
        _anticipationCirclePaint.setAlpha((int) (alpha * 255));
    }

    @Override
    public void onLayout(Rect defaultPlayableRect, Rect cellRect) {
        getCircleParamsForPlayablePosition(_circleParams, _string, _noteFret, _minFret, defaultPlayableRect, cellRect);

        _anticipationCirclePaint.setStrokeWidth(_circleParams.radius * 0.1f);
    }
}