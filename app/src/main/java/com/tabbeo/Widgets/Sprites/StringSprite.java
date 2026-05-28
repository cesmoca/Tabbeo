package com.tabbeo.Widgets.Sprites;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;

import com.tabbeo.Music.Music;
import com.tabbeo.R;
import com.tabbeo.Widgets.ICanvas;

public class StringSprite extends ISprite{
    private Music.GuitarString _string;
    private Rect _rect = new Rect();
    private Paint _stringPaint = new Paint();
    private int _minFret, _maxFret;

    private static Bitmap _stringLightBitmap;

    public StringSprite(Context context, Music.GuitarString string, long startTime, long duration, int minFret, int maxFret) {
        super(startTime + 1, startTime+duration);
        initBitmaps(context);

        _string = string;

        _minFret = minFret;
        _maxFret = maxFret;
    }

    private void initBitmaps(Context context) {
        if(_stringLightBitmap != null) return;
        _stringLightBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.string_light);
    }

    @Override
    public void setAlpha(float alpha) {
        _stringPaint.setAlpha((int) (alpha * 255));
    }

    @Override
    protected void drawSprite(ICanvas canvas, long virtualTimestamp) {
        int alpha = (int) interpolate(255, 0, _startTime, _endTime, virtualTimestamp);
        _stringPaint.setAlpha(alpha);
        canvas.drawBitmap(_stringLightBitmap, null, _rect, _stringPaint);
    }

    public void onLayout(Rect cellRect) {
        _rect.set(cellRect.left, cellRect.top, cellRect.left + cellRect.width() * (_maxFret - _minFret + 1), cellRect.bottom);

        for (int i = 0; i <= _string.getStringNumber(); i++) {
            _rect.offset(0, cellRect.height()); // Bring it to the string
        }
    }
}