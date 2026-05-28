package com.tabbeo.Widgets;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;

public abstract class ICanvas{
    public abstract void save();
    public abstract void scale(float circleScaleFactor, float circleScaleFactor1, float v, float v1);
    public abstract void drawCircle(float cx, float cy, float radius, Paint circlePaint);
    public abstract void restore();
    public abstract void drawBitmap(Bitmap bitmap, Rect o, Rect rect, Paint notePaint);
    public abstract void drawLine(float startX, float startY, float stopX, float stopY, Paint paint);
    public abstract void drawRect(Rect rect, Paint paint);
    public abstract void drawText(String s, float v, float v1, Paint chordsPaint);
}
