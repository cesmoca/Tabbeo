package com.tabbeo.Widgets.Sprites;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.CallSuper;

import com.tabbeo.CourseLibrary.IInversion;
import com.tabbeo.Music.GuitarNote;
import com.tabbeo.Music.Music;
import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.R;
import com.tabbeo.Widgets.ICanvas;

public class ChordInversionSprite extends IFretboardElementSprite {
    // When we animate the playing of the strings in a chord
    // they are lit one at a time. This is the max difference time between each
    private static final int TIME_DIFF_BETWEEN_STRINGS_ANIM_MS = 25;

    private static Bitmap _deafStringBitmap;
    private static Paint _fingerCapoPaint;

    private IFretboardElementSprite[] _spritesInStrings = new IFretboardElementSprite[6];;
    private StringSprite[] _stringSprites;
    private Rect _fingerCapoRect = new Rect();
    private Chord.ChordStrokePattern _strokePattern;
    private boolean _hasCapoBeenFound = false;
    private int _minFret;
    private Music.GuitarString _capoString;
    private GuitarNote _capoGuitarNote;

    private static class DeafStringSprite extends IFretboardElementSprite {
        private Music.GuitarString _string;
        private Rect _rect = new Rect();
        private int _minFret;
        private Paint _paint = new Paint();

        public DeafStringSprite(Music.GuitarString string, int minFret, long playableStartTime, long anticipationDuration) {
            super(playableStartTime, anticipationDuration, 0 /*playableDuration*/);
            _string = string;
            _minFret = minFret;
        }

        @Override
        public void setAlpha(float alpha) {
            _paint.setAlpha((int) (alpha * 255));
        }

        @Override
        public void drawSprite(ICanvas canvas, long virtualTimestamp) {
            canvas.drawBitmap(_deafStringBitmap, null, _rect, _paint);
        }

        @CallSuper
        public void onLayout(Rect defaultPlayableRect, Rect cellRect) {
            getRectForPlayablePosition(_rect, _string, _minFret, _minFret, defaultPlayableRect, cellRect);

        }
    }

    public ChordInversionSprite(Context context, IInversion inversion, Chord.ChordStrokePattern strokePattern, int minFret, int maxFret, long playableStartTime, long anticipationDuration, long playableDuration) {
        super(playableStartTime, anticipationDuration, playableDuration);

        _minFret = minFret;

        initStaticResources(context);

        _strokePattern = strokePattern;

        for (int i = Music.GuitarString.Sixth.getStringNumber(); i >= Music.GuitarString.First.getStringNumber(); --i) {
            Music.GuitarString string = Music.GuitarString.values()[i];
            GuitarNote guitarNote = inversion.getGuitarNotes()[string.getStringNumber()];

            if (guitarNote == null) { // Deaf string
                _spritesInStrings[string.getStringNumber()] = new DeafStringSprite(string, minFret, playableStartTime, anticipationDuration);
            } else //noinspection StatementWithEmptyBody
                if (guitarNote.getFret() == 0) { // Open string
            } else { // Finger-pushed string
                if (inversion.hasFingerCapo() && guitarNote.getFinger() == Music.Finger.Finger1) {
                    if (_hasCapoBeenFound) continue; // We only add one capo, with one circle
                    _capoString = string;
                    _capoGuitarNote = guitarNote;
                    _hasCapoBeenFound = true;
                }

                _spritesInStrings[string.getStringNumber()] = new FretboardCircleSprite(context, string, guitarNote.getFinger().toString(), guitarNote.getFinger().getColorId(), guitarNote.getFret(), minFret, playableStartTime, anticipationDuration);
            }
        }

        // Let's add the stringSprites with an animation
        Music.GuitarString[] usedStrings = new Music.GuitarString[inversion.getNPlayingStrings()];
        int usedStringIndex = 0;
        for (GuitarNote guitarNote : inversion.getGuitarNotes()) {
            if (guitarNote == null) continue; // Avoid deaf strings
            usedStrings[usedStringIndex] = guitarNote.getString();
            usedStringIndex++;
        }

        long diffOffsetInMs = TIME_DIFF_BETWEEN_STRINGS_ANIM_MS; // We calculate an offset start time for the strings, simulating the pick movement up or down

        // What if the duration of the playable is so short, that this delay is longer than the actual duration?
        if (playableDuration / usedStrings.length < diffOffsetInMs) {
            diffOffsetInMs = playableDuration / usedStrings.length;
        }

        long currentOffset = 0;
        switch (_strokePattern) {
            case PICK_UP_STROKE:
                // Nothing, it behaves as an up stroke pattern as it is
                break;
            case PICK_DOWN_STROKE:
                currentOffset = diffOffsetInMs * (usedStrings.length - 1);
                diffOffsetInMs *= -1;
                break;
            default:
                throw new RuntimeException("Unknown chord stroke pattern");
        }

        _stringSprites = new StringSprite[usedStrings.length];
        for (int i = 0; i < _stringSprites.length; ++i) {
            _stringSprites[i] = new StringSprite(context, usedStrings[i], playableStartTime + currentOffset, playableDuration, minFret, maxFret);
            currentOffset += diffOffsetInMs;
        }

    }

    @Override
    public void setAlpha(float alpha) {
        _fingerCapoPaint.setAlpha((int) (alpha * 255));

        for (ISprite sprite : _spritesInStrings) {
            if (sprite != null) { // Avoid open strings (not sprite needed to be drawn)
                sprite.setAlpha(alpha);
            }
        }
    }

    @Override
    protected void drawSprite(ICanvas canvas, long virtualTimestamp) {
        // Drawing the anticipation chord
        if (virtualTimestamp <= _playableStartTime) {
            if (_fingerCapoRect != null) {
                canvas.drawRect(_fingerCapoRect, _fingerCapoPaint);
            }

            for (ISprite sprite : _spritesInStrings) {
                if (sprite != null) { // Avoid open strings (not sprite needed to be drawn)
                    sprite.drawSprite(canvas, virtualTimestamp);
                }
            }

        } else { // Drawing the strings
            for (StringSprite s : _stringSprites) {
                s.draw(canvas, virtualTimestamp);
            }

        }
    }

    private static void initStaticResources(Context context) {
        if (_deafStringBitmap != null) return;

        _deafStringBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.deaf_string);

        _fingerCapoPaint = new Paint();
        _fingerCapoPaint.setColor(context.getResources().getColor(R.color.finger1));
    }

    protected static void getRectForPlayablePosition(Rect dst, Music.GuitarString string, int fret, int minFret, Rect defaultPlayableRect, Rect cellRect) {
        int relativeFret = fret - minFret;

        dst.set(defaultPlayableRect);
        dst.offset(relativeFret * cellRect.width(), string.getStringNumber() * cellRect.height());
    }

    public void onLayout(Rect defaultPlayableRect, Rect cellRect) {
        if (_hasCapoBeenFound) {
            getRectForPlayablePosition(_fingerCapoRect, _capoString, _capoGuitarNote.getFret(), _minFret, defaultPlayableRect, cellRect);
            int halfWidth = _fingerCapoRect.width() / 2;
            _fingerCapoRect.top = defaultPlayableRect.top; // We extend it all the way up to the beginning of the fret
            //noinspection SuspiciousNameCombination
            _fingerCapoRect.bottom -= halfWidth; // So that it starts from the center of the circle, not from the very bottom
        }
        // Let's notify the sprites that the onLayout situation has changed
        for (IFretboardElementSprite s : _spritesInStrings) {
            if (s != null) { // Avoid open strings (not sprite needed to be drawn)
                s.onLayout(defaultPlayableRect, cellRect);
            }
        }

        // Let's notify the strings
        for (StringSprite s : _stringSprites) {
            s.onLayout(cellRect);
        }
    }
}
