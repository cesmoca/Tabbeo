package com.tabbeo.Widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.tabbeo.Music.Music.Duration;
import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.Music.Track;
import com.tabbeo.R;

public class ChordStrokesStreamWidget extends ExerciseWidget {
    private Rect _strokesLayoutRect = new Rect();
    protected static Paint _finishLinePaint = new Paint();

    private int _pickWidth;
    private long _msPerBeat = 0;
    private float _beatWidth = 0;
    private float _widthOneMs;

    private Paint _pickStrokePaint = new Paint();
    private Bitmap _pickStrokeDownBitmap;
    private Bitmap _pickStrokeUpBitmap;
    private Rect _initialPositionPickStrokeRect = new Rect();

    // Temp Rect for onDraw
    private Rect _pickStrokeRect = new Rect();

    public ChordStrokesStreamWidget(Context context, AttributeSet attrs) {
        super(context, attrs);

        initDrawingParameters(context);
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        _strokesLayoutRect.set(0, 0, r-l, b-t);
        _pickWidth = _strokesLayoutRect.height();

        _msPerBeat = _metronomeTimeRef.getMsPerBeat();

        Duration shortestNoteIndex = getShortestNote(_exercise.getTrack());

        float nShortestNotesInTick = shortestNoteIndex.divisor/(float) _exercise.getTrack().getBeatsPerMeasure().measure;
        _beatWidth = _pickWidth * nShortestNotesInTick;

        _widthOneMs = _beatWidth / (float) _msPerBeat;

        int initialPosition = (int) (_strokesLayoutRect.centerX() - _pickWidth/2.0f);

        //noinspection SuspiciousNameCombination
        _initialPositionPickStrokeRect.set(0, 0, _pickWidth, _pickWidth); // Squared rect, why not
        _initialPositionPickStrokeRect.offsetTo(initialPosition, _strokesLayoutRect.top);
    }

    @Override
    protected void drawBackground(ICanvas canvas) {}

    @Override
    protected void drawForeground(ICanvas canvas, long virtualTimestamp) {
        Bitmap pickStrokeBitmap;
        _pickStrokeRect.set(_initialPositionPickStrokeRect);

        _pickStrokeRect.offset((int) (-1 * virtualTimestamp * _widthOneMs), 0); // We bring the first pick all the way to the left

        // Here we drawSprite the stuff
        for(int i = 0; i < _exercise.getTrack().getNPlayables(); ++i){
            Chord.ChordStrokePattern strokePattern = (Chord.ChordStrokePattern) _exercise.getTrack().getStrokePattern(i);

            // Set the color and the pick, if it is either UP or DOWN
            switch(strokePattern){
                case PICK_UP_STROKE:
                    pickStrokeBitmap = _pickStrokeUpBitmap;
                    break;
                case PICK_DOWN_STROKE:
                    pickStrokeBitmap = _pickStrokeDownBitmap;
                    break;
                default:
                    throw new RuntimeException("Unknown chord stroke type: "+strokePattern);
            }

            // Set the color
            _pickStrokePaint.setColorFilter(new LightingColorFilter(_context.getResources().getColor(((Chord) _exercise.getTrack().getPlayable(i)).getRoot().getColorId()), 0));

            canvas.drawBitmap(pickStrokeBitmap, null, _pickStrokeRect, _pickStrokePaint); // Pick shape

            _pickStrokeRect.offset((int)(_metronomeTimeRef.getPlayableDurationInMs(i) * _widthOneMs), 0);
        }

        // Draw the playing line
        canvas.drawLine(_strokesLayoutRect.centerX(), _strokesLayoutRect.top, _strokesLayoutRect.centerX(), _strokesLayoutRect.bottom, _finishLinePaint);
    }

    @Override
    public void setAlpha(float alpha) {
        _pickStrokePaint.setAlpha((int) (alpha * 255));
        _finishLinePaint.setAlpha((int) (alpha * 255));
    }

    @Override
    public void loadWidgetExercise() {
        // Nothing to do really, yet. Only if drawForeground proves to be too slow
    }

    private void initDrawingParameters(Context context) {
        // Stroke properties
        _pickStrokePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // Pick Stroke Down
        _pickStrokeDownBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.full_pick_white);

        // Pick Stroke Up
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        _pickStrokeUpBitmap = Bitmap.createBitmap(_pickStrokeDownBitmap, 0, 0, _pickStrokeDownBitmap.getWidth(), _pickStrokeDownBitmap.getHeight(), matrix, false);
        _pickStrokeUpBitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);

        // Finish line paint
        _finishLinePaint.setColor(Color.BLACK);
        _finishLinePaint.setStrokeWidth(3.0f);
    }

    protected static Duration getShortestNote(Track track) {
        // Note's durations are divisors, so the larger, the shorter duration
        Duration shortestNoteDuration = Duration.whole_dotted;

        for(int i = 0; i < track.getNPlayables(); ++i){
            if(track.getDuration(i).divisor > shortestNoteDuration.divisor)
                shortestNoteDuration = track.getDuration(i);
        }

        return shortestNoteDuration;
    }
}
