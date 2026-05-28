package com.tabbeo.Widgets.Sprites;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;

import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.Utils.TextSizeUtils;
import com.tabbeo.Widgets.ICanvas;

public class ChordNameSprite extends  ISprite{
    private Paint _chordsPaint;
    private int _textBottom;
    private Chord _chord;
    private int _centerX;

    // Temp structures for onLayout
    private Rect _textBounds = new Rect();

    public ChordNameSprite(Context context, Chord chord, long startTime, long endTime) {
        super(startTime, endTime);

        _chord = chord;

        // Create the Paints
        _chordsPaint = new Paint();

        _chordsPaint.setColor(context.getResources().getColor(chord.getRoot().getColorId()));
        _chordsPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void setAlpha(float alpha) {
        _chordsPaint.setAlpha((int) (alpha * 255));
    }

    @Override
    protected void drawSprite(ICanvas canvas, long virtualTimestamp) {
        canvas.drawText(_chord.toString(), _centerX, _textBottom, _chordsPaint);
    }

    public void onLayout(Rect rect){
        _centerX = rect.centerX();

        _chordsPaint.setTextSize(TextSizeUtils.getTextSizeToFitInRect(_chord.toString(), rect).textSize * 0.85f);

        _chordsPaint.getTextBounds(_chord.toString(), 0, _chord.toString().length(), _textBounds);

        _textBottom = (int)(rect.centerY() + _textBounds.height()/2.0f);
    }
}
