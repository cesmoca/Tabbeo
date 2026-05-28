package com.tabbeo.Widgets.Sprites;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.tabbeo.Music.Music;
import com.tabbeo.Utils.TextSizeUtils;
import com.tabbeo.Widgets.ICanvas;

public class FretboardCircleSprite extends IFretboardElementSprite{
    private int _minFret;
    private int _noteFret;
    private Music.GuitarString _string;
    private CircleParams _circleParams = new CircleParams();
    private Rect _labelRect = new Rect();
    private String _circleText;
    private Paint _textPaint = new Paint();
    private Paint _circlePaint = new Paint();
    private TextSizeUtils.TextSizeInRect _textSizeInRect = new TextSizeUtils.TextSizeInRect();

    public FretboardCircleSprite(Context context, Music.GuitarString string, String label, int colorId, int noteFret, int minFret, long playableStartTime, long anticipationDuration) {
        super(playableStartTime, anticipationDuration, 0 /*playable duration*/);

        _string = string;

        _noteFret = noteFret;
        _minFret = minFret;

        _circleText = label;

        _circlePaint.setColor(context.getResources().getColor(colorId));
        _circlePaint.setStyle(Paint.Style.FILL);

        _textPaint.setColor(Color.WHITE);
        _textPaint.setShadowLayer(20 /*radius*/, 0 /*x offset*/, 0 /*y offset*/, Color.BLACK);
        _textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void setAlpha(float alpha) {
        _textPaint.setAlpha((int) (alpha * 255));
        _circlePaint.setAlpha((int) (alpha * 255));
    }

    @Override
    public void drawSprite(ICanvas canvas, long virtualTimestamp) {
        canvas.drawCircle(_circleParams.center.x, _circleParams.center.y, _circleParams.radius, _circlePaint);
        canvas.drawText(_circleText, _circleParams.center.x, _circleParams.center.y + _textSizeInRect.textVerticalOffset, _textPaint);
    }

    @Override
    public void onLayout(Rect defaultPlayableRect, Rect cellRect) {
        getCircleParamsForPlayablePosition(_circleParams, _string, _noteFret, _minFret, defaultPlayableRect, cellRect);

        _labelRect.set(0, 0, (int) (_circleParams.radius * 2 * 0.8) , (int) (_circleParams.radius * 2 * 0.8)); // We reduce it a little

        _textSizeInRect = TextSizeUtils.getTextSizeToFitInRect(_circleText, _labelRect);
        _textPaint.setTextSize(_textSizeInRect.textSize);
    }

    public CircleParams getCircleParams(){
        return _circleParams;
    }
}
