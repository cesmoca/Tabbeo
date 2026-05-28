package com.tabbeo.Widgets.Sprites;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;

import com.tabbeo.Music.GuitarNote;
import com.tabbeo.R;
import com.tabbeo.Widgets.ICanvas;

public class GuitarNoteSprite extends IFretboardElementSprite {
    private final static float NOTE_SCALE_FACTOR_FROM = 0.5f;
    private final static float NOTE_SCALE_FACTOR_TO = 1.0f;
    
    private AnticipationCircleSprite _anticipationCircleSprite;
    private FretboardCircleSprite _noteCircle;
    private StringSprite _stringSprite;

    public GuitarNoteSprite(Context context, GuitarNote guitarNote, int minFret, int maxFret, long playableStartTime, long anticipationDuration, long playableDuration){
        super(playableStartTime, anticipationDuration, playableDuration);

        _noteCircle = new FretboardCircleSprite(context, guitarNote.getString(), guitarNote.getNote().getRoot().toString(), guitarNote.getNote().getRoot().getColorId(), guitarNote.getFret(), minFret, playableStartTime, anticipationDuration);
        _stringSprite = new StringSprite(context, guitarNote.getString(), playableStartTime, playableDuration, minFret, maxFret);
        _anticipationCircleSprite = new AnticipationCircleSprite(context, guitarNote.getString(), guitarNote.getFret(), minFret, playableStartTime, anticipationDuration);
    }

    @Override
    protected void drawSprite(ICanvas canvas, long virtualTimestamp) {
        _anticipationCircleSprite.draw(canvas, virtualTimestamp);

        //  ----Drawing the note circle
        //int noteAlpha = (int) interpolate(0, 255, _startTime, _endTime, virtualTimestamp);
        float noteScaleFactor = interpolate(NOTE_SCALE_FACTOR_FROM, NOTE_SCALE_FACTOR_TO, _startTime, _playableStartTime, virtualTimestamp);

        canvas.save();
        canvas.scale(noteScaleFactor, noteScaleFactor, _noteCircle.getCircleParams().center.x, _noteCircle.getCircleParams().center.y);
        _noteCircle.draw(canvas, virtualTimestamp);
        canvas.restore();

        // -- Drawing the string
        _stringSprite.draw(canvas, virtualTimestamp);
    }

    @Override
    public void onLayout(Rect defaultPlayableRect, Rect cellRect){
        _noteCircle.onLayout(defaultPlayableRect, cellRect);
        _stringSprite.onLayout(cellRect);
        _anticipationCircleSprite.onLayout(defaultPlayableRect, cellRect);
    }

    @Override
    public void setAlpha(float alpha) {
        _noteCircle.setAlpha(alpha);
        _stringSprite.setAlpha(alpha);
        _anticipationCircleSprite.setAlpha(alpha);
    }
}
