package com.tabbeo.Widgets.Sprites;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;

import com.tabbeo.Music.Music;
import com.tabbeo.R;
import com.tabbeo.Widgets.ICanvas;

public class RotatingBall extends IFretboardElementSprite {
    private CircleParams _circleParams = new CircleParams();
    private Paint _rotatingBallPaint = new Paint();
    private Music.GuitarString _string;
    private float _rotatingBallRadius;
    private int _noteFret;
    private int _minFret;
    private long _anticipationDuration;

    public RotatingBall(Context context, Music.GuitarString string, int noteFret, int minFret, long playableStartTime, long anticipationDuration) {
        super(playableStartTime, anticipationDuration, 0);

        _string = string;
        _noteFret = noteFret;
        _minFret = minFret;
        _anticipationDuration = anticipationDuration;

        _rotatingBallPaint.setColor(context.getResources().getColor(R.color.tabbeo_light_green));
        _rotatingBallPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onLayout(Rect defaultPlayableRect, Rect cellRect) {
        getCircleParamsForPlayablePosition(_circleParams, _string, _noteFret, _minFret, defaultPlayableRect, cellRect);
        _rotatingBallRadius = _circleParams.radius / 3;
    }

    @Override
    public void setAlpha(float alpha) {
        _rotatingBallPaint.setAlpha((int) (alpha * 255));
    }

    @Override
    protected void drawSprite(ICanvas canvas, long virtualTimestamp) {
        if(virtualTimestamp > _playableStartTime) return;

        double rotatingBallAngle = (virtualTimestamp - _startTime) * Math.PI / _anticipationDuration;

        // Let's calculate the position
        int rotatingBallX = (int) (_circleParams.center.x - _circleParams.radius*Math.sin(rotatingBallAngle));
        int rotatingBallY = (int) (_circleParams.center.y + _circleParams.radius*Math.cos(rotatingBallAngle));

        canvas.drawCircle(rotatingBallX, rotatingBallY, _rotatingBallRadius, _rotatingBallPaint);
    }
}
