package com.tabbeo.unittests.Mocks;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;

import com.tabbeo.Widgets.ICanvas;

public class MockCanvas extends ICanvas {
    public int timesCalledDrawText;
    public int timesCalledDrawCircle;
    public int timesCalledDrawBitmap;
    public int timesCalledDrawLine;
    public boolean hasCalledScale;

    public MockCanvas(){
        reset();
    }

    public void reset(){
        timesCalledDrawText = 0;
        timesCalledDrawCircle = 0;
        timesCalledDrawBitmap = 0;
        timesCalledDrawLine = 0;
        hasCalledScale = false;
    }

    @Override
    public void drawText(String s, float v, float v1, Paint chordsPaint) {
        timesCalledDrawText++;
    }

    @Override
    public void drawCircle(float v, float v1, float v2, Paint circlePaint) {
        timesCalledDrawCircle++;
    }

    @Override
    public void drawBitmap(Bitmap bitmap, Rect o, Rect rect, Paint notePaint) {
        timesCalledDrawBitmap++;
    }

    @Override
    public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
        timesCalledDrawLine++;
    }

    @Override
    public void drawRect(Rect rect, Paint paint) {

    }

    @Override
    public void scale(float circleScaleFactor, float circleScaleFactor1, float v, float v1) {
        hasCalledScale = true;
    }

    @Override
    public void save() {}

    @Override
    public void restore() {}

}
