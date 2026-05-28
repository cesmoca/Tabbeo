/**
 * Copyright (C) 2009 by Aleksey Surkov.
 * *
 * * Permission to use, copy, modify, and distribute this software and its
 * * documentation for any purpose and without fee is hereby granted, provided
 * * that the above copyright notice appear in all copies and that both that
 * * copyright notice and this permission notice appear in supporting
 * * documentation.  This software is provided "as is" without express or
 * * implied warranty.
 */

package com.tabbeo.Widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class PitchPrecision extends View {

    //private DetectionResult _detectionResult;
    private Handler _handler;
    private final static int UI_UPDATE_MS = 50;

    private void initView() {
        // UI update cycle.
        _handler = new Handler();
        Timer _timer = new Timer();
        _timer.schedule(new TimerTask() {
                            public void run() {
                                _handler.post(new Runnable() {
                                    public void run() {
                                        invalidate();
                                    }
                                });
                            }
                        },
                UI_UPDATE_MS,
                UI_UPDATE_MS);
    }

    public PitchPrecision(Context context) {
        super(context);
        initView();
    }

    public PitchPrecision(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

    }

    protected void onDraw(Canvas canvas) {
        //if (_detectionResult != null && _detectionResult.getPlayable() instanceof Note)
        //    drawPitchPrecision(canvas, _detectionResult.getFrequency());
    }

    //public void setDetectionResults(DetectionResult fr) {
        //_detectionResult = fr;
   // }

    private static void PitchMistakeColor(Paint paint, double pitchMistake) {
        final int pitchMistakeColor = (int) (Math.abs(pitchMistake) * 500); // pitchMistake is -0.5 - 0.5, so color is 0 - 250.

        if (pitchMistake > 0) {
            // reddish
            paint.setARGB(180, pitchMistakeColor, 250 - pitchMistakeColor, 30);
        } else {
            // blueish
            paint.setARGB(180, 30, 250 - pitchMistakeColor, pitchMistakeColor);
        }
    }

    private static double PitchMistake(double pitch) {
        throw new RuntimeException("This widget is not supported anymore. We will delete it soon.");
        //final double distanceFromA4 = Note.getAccurateHalfTonesFromA4(pitch);
        //return distanceFromA4 - Math.round(distanceFromA4);
    }

    private static void drawPitchPrecision(Canvas canvas, double pitch) {
        final int tuneNeedleY = 20;
        final int tuneHairRadius = 20;
        Paint notePaint = new Paint();

        final double pitchMistake = PitchMistake(pitch);
        PitchMistakeColor(notePaint, pitchMistake);

        final int width = canvas.getWidth();
        final int centerX = width / 2;

        float posX = (float) (width * (0.5 + pitchMistake));

        // hair
        canvas.drawLine(centerX, tuneNeedleY - tuneHairRadius, centerX, tuneNeedleY + tuneHairRadius, notePaint);

        // horizontal line
        canvas.drawLine(0, tuneNeedleY, width, tuneNeedleY, notePaint);

        // needle
        canvas.drawCircle(posX, tuneNeedleY, 5, notePaint);
    }
}