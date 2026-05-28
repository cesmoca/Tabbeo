package com.tabbeo.Widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.AttributeSet;
import android.view.View;

import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.Music.IMetronomeTimeRef;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ExerciseWidget extends View implements IExerciseWidget{
    private final static int UI_UPDATE_MS = 25; /*ms; 40 fps; = 1000ms / 40fps = 25 ms */

    protected Context _context;
    private Timer _drawingTimer;
    private Handler _handler;
    protected Exercise _exercise;
    protected IMetronomeTimeRef _metronomeTimeRef;
    private CanvasProxy canvasProxy = new CanvasProxy();
    protected boolean _isExerciseLoaded = false;

    public ExerciseWidget(Context context, AttributeSet attrs) {
        super(context, attrs);

        _context = context;
        _handler = new Handler(context.getMainLooper());

        setBackgroundColor(Color.TRANSPARENT);

        // UI update cycle.
        _drawingTimer = new Timer();
        _drawingTimer.schedule(new TimerTask() {
            public void run() {
                _handler.post(new Runnable() {
                    public void run() {
                        invalidate();
                    }
                });
            }
        }, UI_UPDATE_MS, UI_UPDATE_MS);
    }

    @Override
    final protected void onDraw(Canvas canvas){
        canvasProxy.canvas = canvas;
        draw(canvasProxy, SystemClock.elapsedRealtime());
    }

    final protected void draw(ICanvas canvas, long realTimestamp){
        drawBackground(canvas);

        if(!_metronomeTimeRef.isPlaying()) return;

        long virtualTimestamp = _metronomeTimeRef.getTimestamp(realTimestamp);
        drawForeground(canvas, virtualTimestamp);
    }

    @Override
    @CallSuper
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        if(_exercise == null) throw new RuntimeException("You need to call init right after you inflate the widget. Otherwise onLayout will be called and bad things will happen");
    }

    @Override
    final public void loadExercise(){
        if(_isExerciseLoaded) throw new RuntimeException("The exercise has been loaded already");
        loadWidgetExercise();
        _isExerciseLoaded = true;
    }

    protected abstract void loadWidgetExercise();

    @Override
    final public void init(Exercise exercise, IMetronomeTimeRef metronomeTimeRef){
        _exercise = exercise;
        _metronomeTimeRef = metronomeTimeRef;
    }

    protected abstract void drawBackground(ICanvas canvas);
    protected abstract void drawForeground(ICanvas canvas, long virtualTimestamp);

    public abstract void setAlpha(float alpha);

    protected long getAnticipationDuration(int playableIndex){
        // The time that we show that sprite is the duration
        // of the last playable (the one that is playing right now)
        if(playableIndex == 0) return _metronomeTimeRef.getCountdownDuration();
        else return _metronomeTimeRef.getPlayableDurationInMs(playableIndex - 1) - 1; // -1 To make sure there is no overlaps
    }

    public static class CanvasProxy extends ICanvas{
        public Canvas canvas;

        @Override
        public void drawText(String text, float x, float y, Paint paint) {
            canvas.drawText(text, x, y, paint);
        }

        @Override
        public void drawRect(Rect rect, Paint paint){
            canvas.drawRect(rect, paint);
        }

        @Override
        public void save() {
            canvas.save();
        }

        @Override
        public void scale(float sx, float sy, float px, float py) {
            canvas.scale(sx, sy, px, py);
        }

        @Override
        public void drawCircle(float cx, float cy, float radius, Paint paint) {
            canvas.drawCircle(cx, cy, radius, paint);
        }

        @Override
        public void restore() {
            canvas.restore();
        }

        @Override
        public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
            canvas.drawBitmap(bitmap, src, dst, paint);
        }

        @Override
        public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }
}
