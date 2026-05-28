package com.tabbeo.Widgets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.Widgets.Sprites.ChordNameSprite;
import com.tabbeo.Widgets.Sprites.ISprite;

import java.util.ArrayList;
import java.util.List;

public class ChordNamesWidget extends ExerciseWidget {
    private final static float CURRENT_CHORD_WIDTH_RATIO = 0.7f;

    private List<ChordNameSprite> _incomingChordNamesSprites = new ArrayList<>();
    private List<ChordNameSprite> _anticipatingChordNamesSprites = new ArrayList<>();

    private Rect _incomingChordRect = new Rect();
    private Rect _anticipatingChordRect = new Rect();

    public ChordNamesWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        if(!_isExerciseLoaded) return;

        super.onLayout(changed, l, t, r, b);
        int viewWidth = r - l;
        int viewHeight = b-t;
        int currentChordWidth = (int)(viewWidth * CURRENT_CHORD_WIDTH_RATIO);

        _incomingChordRect.set(0, 0, currentChordWidth, viewHeight);
        _anticipatingChordRect.set(currentChordWidth, 0, viewWidth, viewHeight);

        for(ChordNameSprite s : _incomingChordNamesSprites){
            s.onLayout(_incomingChordRect);
        }

        for(ChordNameSprite s : _anticipatingChordNamesSprites){
            s.onLayout(_anticipatingChordRect);
        }
    }

    @Override
    protected void drawBackground(ICanvas canvas) { }

    @Override
    protected void drawForeground(ICanvas canvas, long virtualTimestamp) {
        for(ISprite s : _incomingChordNamesSprites){
            s.draw(canvas, virtualTimestamp);
        }

        for(ISprite s : _anticipatingChordNamesSprites){
            s.draw(canvas, virtualTimestamp);
        }
    }

    @Override
    public void setAlpha(float alpha) {
        for(ISprite s : _incomingChordNamesSprites){
            s.setAlpha(alpha);
        }

        for(ISprite s : _anticipatingChordNamesSprites){
            s.setAlpha(alpha);
        }
    }

    @Override
    protected void loadWidgetExercise() {
        for(int i = 0; i < _exercise.getTrack().getNPlayables(); ++i){
            if(!(_exercise.getTrack().getPlayable(i) instanceof Chord))
                throw new RuntimeException("We found something that is not a chord: "+_exercise.getTrack().getPlayable(i));

            Chord incomingChord = (Chord) _exercise.getTrack().getPlayable(i);

            Chord anticipatingChord = null;
            for(int j = i+1; j < _exercise.getTrack().getNPlayables(); ++j){
                Chord c = (Chord) _exercise.getTrack().getPlayable(j);
                if(!c.equals(incomingChord)){
                    anticipatingChord =  c;
                    break;
                }
            }

            long incomingChordStartTime = _metronomeTimeRef.getPlayableStartTime(i);
            long incomingChordAnticipationDuration = getAnticipationDuration(i);

            _incomingChordNamesSprites.add(new ChordNameSprite(_context, incomingChord, incomingChordStartTime - incomingChordAnticipationDuration, incomingChordStartTime));
            if(anticipatingChord != null) _anticipatingChordNamesSprites.add(new ChordNameSprite(_context, anticipatingChord, incomingChordStartTime - incomingChordAnticipationDuration, incomingChordStartTime));
        }
    }
}
