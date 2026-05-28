package com.tabbeo.Widgets.Sprites;

import android.graphics.Rect;

import com.tabbeo.Music.Music;

public abstract class IFretboardElementSprite extends ISprite {
    protected long _playableStartTime;

    public IFretboardElementSprite(long playableStartTime, long anticipationDuration, long playableDuration) {
        super(playableStartTime - anticipationDuration, playableStartTime + playableDuration);
        _playableStartTime = playableStartTime;
    }

    public abstract void onLayout(Rect defaultPlayableRect, Rect cellRect);

    protected static void getCircleParamsForPlayablePosition(CircleParams dst, Music.GuitarString string, int fret, int minFret, Rect defaultPlayableRect, Rect cellRect){
        int relativeFret = fret - minFret;

        dst.set(defaultPlayableRect.centerX(), defaultPlayableRect.centerY(), defaultPlayableRect.width()/2.0f);

        dst.center.x += relativeFret * cellRect.width();
        dst.center.y += string.getStringNumber() * cellRect.height();
    }
}
