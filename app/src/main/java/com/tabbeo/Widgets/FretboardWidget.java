package com.tabbeo.Widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.tabbeo.CourseLibrary.IInversion;
import com.tabbeo.Music.ChordsLibrary;
import com.tabbeo.Music.GuitarNote;
import com.tabbeo.Music.Music;
import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.R;
import com.tabbeo.Widgets.Sprites.AnticipationCircleSprite;
import com.tabbeo.Widgets.Sprites.ChordInversionSprite;
import com.tabbeo.Widgets.Sprites.FretboardCircleSprite;
import com.tabbeo.Widgets.Sprites.IFretboardElementSprite;
import com.tabbeo.Widgets.Sprites.GuitarNoteSprite;
import com.tabbeo.Widgets.Sprites.ISprite;
import com.tabbeo.Widgets.Sprites.RotatingBall;

import java.util.ArrayList;
import java.util.List;

/**
 * Layout design
 * |   0   |   1   |   2   |   3   |   4   |
 * |  str1 |cellRec|       |       |       |
 * |  str2 |       |       |  | \  |       |
 * |  str3 |       |       |  | |  |       |
 * |  str4 |       |       |  | /  |       |
 * |  str5 |       |       |       |       |
 * |  str6 |       |       |       |       |
 */
public class FretboardWidget extends ExerciseWidget {
    private final static float N_ROWS = 8.0f; // 6 string + 1 fret numbers + bottom margin row

    // This guy is just for testing purposes. It should GO OUT
    public enum NOTE_REPRESENTATION{
        MELODY,
        SCALE_CHANGE_LIGHTING,
        SCALE_ANTICIPATION_CIRCLE,
        SCALE_ROTATING_BALL
    }

    private Paint _fretNumbersPaint = new Paint();
    private Paint _alphaPaint = new Paint();

    private Rect _cellRect = new Rect(); // Represents a cell (fret) in the upper most left corner
    private Rect _defaultPlayableRect = new Rect(); // Rect for the circle
    private Rect _fretMarkRect = new Rect(); // Mark in frets 3, 5, 7, 9...

    private static Bitmap[][] _fretBitmaps = null;
    private static Bitmap[] _stringBitmaps;
    private static Bitmap[] _headSideBitmaps;
    private static Bitmap _fretMarkBitmap;

    private IFretboardElementSprite[] _playableSprites;
    private List<FretboardCircleSprite> _scaleNotesSprites;

    // Temp structures for onDraw
    private Rect _textBounds = new Rect();
    private Rect _fretNumberRect = new Rect();
    private Rect _openStringRect = new Rect();
    private Rect _fretRect = new Rect();
    private Rect _headSideRect = new Rect();
    private Rect _fretMarkRectTemp = new Rect();
    private Rect _stringRect = new Rect();

    public FretboardWidget(Context context, AttributeSet attrs) {
        super(context, attrs);

        initBitmaps();

        _fretNumbersPaint.setColor(_context.getResources().getColor(R.color.tabbeo_light_blue));
        _fretNumbersPaint.setTextAlign(Paint.Align.CENTER);
        _fretNumbersPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        onLayoutBackground(l, t, r, b);

        if(!_isExerciseLoaded) return;

        onLayoutForeground(l, t, r, b);
    }

    public void onLayoutBackground(int l, int t, int r, int b) {
        int viewWidth = r - l;
        int viewHeight = b - t;

        // We assume all fret images are the same size
        Drawable fretDrawable = _context.getResources().getDrawable(R.drawable.fret_0_top);

        float viewAspectRatio = viewWidth / (float) viewHeight;
        assert fretDrawable != null;
        float fretboardAspectRatio = (fretDrawable.getIntrinsicWidth() * (_exercise.getMaxFret() - _exercise.getMinFret() + 1)) / (fretDrawable.getIntrinsicHeight() * N_ROWS);

        float _fretScale;
        if (viewAspectRatio < fretboardAspectRatio) {
            // Fit width
            int finalFretWidth = (int) (viewWidth / (float) (_exercise.getMaxFret() - _exercise.getMinFret() + 1));
            _fretScale = finalFretWidth / (float) fretDrawable.getIntrinsicWidth();
        } else {
            // Fit height
            int finalFretHeight = (int) (viewHeight / N_ROWS);
            _fretScale = finalFretHeight / (float) fretDrawable.getIntrinsicHeight();
        }

        int finalFretWidth = (int) (_fretScale * fretDrawable.getIntrinsicWidth());
        int finalFretHeight = (int) (_fretScale * fretDrawable.getIntrinsicHeight());
        _cellRect.set(0, 0, finalFretWidth, finalFretHeight);
        //noinspection SuspiciousNameCombination
        _defaultPlayableRect.set(0, 0, finalFretHeight, finalFretHeight);
        _defaultPlayableRect.offset((int) (_cellRect.width() / 2.0f - _defaultPlayableRect.width() / 2.0f), 0);
        _defaultPlayableRect.offset(0, _cellRect.height()); // Skip the fret numbering row

        _fretNumbersPaint.setTextSize(_cellRect.height() / 2.0f);

        _fretMarkRect.set(0, 0, (int) (_fretScale * _fretMarkBitmap.getWidth()), (int) (_fretScale * _fretMarkBitmap.getHeight()));
        _fretMarkRect.offset(0, _cellRect.height()); // Skip the numbers

        // Let's center it vertically
        int fretboardTopOffset = (int) ((viewHeight - N_ROWS * finalFretHeight) / 2.0f);
        if (fretboardTopOffset > 0) {
            _cellRect.offset(0, fretboardTopOffset);
            _defaultPlayableRect.offset(0, fretboardTopOffset);
            _fretMarkRect.offset(0, fretboardTopOffset);
        }

        // Let's center it horizontally
        int fretboardLeftOffset = (int) ((viewWidth - (_exercise.getMaxFret() - _exercise.getMinFret() + 1) * _cellRect.width()) / 2.0f);
        if (fretboardLeftOffset > 0) {
            _cellRect.offset(fretboardLeftOffset, 0);
            _defaultPlayableRect.offset(fretboardLeftOffset, 0);
            _fretMarkRect.offset(fretboardLeftOffset, 0);
        }
    }

    public void onLayoutForeground(int l, int t, int r, int b) {
        // Let's notify the sprites that the onLayout situation has changed
        for(IFretboardElementSprite s : _playableSprites){
            s.onLayout(_defaultPlayableRect, _cellRect);
        }

        // The scale pattern is considered background
        if(_exercise.getTrack().getScalePattern() != null) {
            for (IFretboardElementSprite s : _scaleNotesSprites) {
                s.onLayout(_defaultPlayableRect, _cellRect);
            }
        }

    }

    protected void initBitmaps() {
        if (_fretBitmaps != null)
            return;

        // Fret bitmaps
        _fretBitmaps = new Bitmap[3][];
        for (int i = 0; i < 3; ++i) {
            _fretBitmaps[i] = new Bitmap[2];
        }

        _fretBitmaps[0][0] = BitmapFactory.decodeResource(_context.getResources(), R.drawable.fret_0_top);
        _fretBitmaps[1][0] = BitmapFactory.decodeResource(_context.getResources(), R.drawable.fret_0_middle);
        _fretBitmaps[2][0] = BitmapFactory.decodeResource(_context.getResources(), R.drawable.fret_0_bottom);
        _fretBitmaps[0][1] = BitmapFactory.decodeResource(_context.getResources(), R.drawable.fret_1_top);
        _fretBitmaps[1][1] = BitmapFactory.decodeResource(_context.getResources(), R.drawable.fret_1_middle);
        _fretBitmaps[2][1] = BitmapFactory.decodeResource(_context.getResources(), R.drawable.fret_1_bottom);

        _stringBitmaps = new Bitmap[6];
        _stringBitmaps[0] = BitmapFactory.decodeResource(_context.getResources(), R.drawable.string_1);
        _stringBitmaps[1] = BitmapFactory.decodeResource(_context.getResources(), R.drawable.string_2);
        _stringBitmaps[2] = BitmapFactory.decodeResource(_context.getResources(), R.drawable.string_3);
        _stringBitmaps[3] = BitmapFactory.decodeResource(_context.getResources(), R.drawable.string_4);
        _stringBitmaps[4] = BitmapFactory.decodeResource(_context.getResources(), R.drawable.string_5);
        _stringBitmaps[5] = BitmapFactory.decodeResource(_context.getResources(), R.drawable.string_6);

        _headSideBitmaps = new Bitmap[2];
        _headSideBitmaps[0] = BitmapFactory.decodeResource(_context.getResources(), R.drawable.head_side_1);
        _headSideBitmaps[1] = BitmapFactory.decodeResource(_context.getResources(), R.drawable.head_side_2);

        _fretMarkBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.fretboard_mark);
    }

    @Override
    protected void drawForeground(ICanvas canvas, long virtualTimestamp) {
        for(ISprite s : _playableSprites){
            s.draw(canvas, virtualTimestamp);
        }
    }

    @Override
    protected void drawBackground(ICanvas canvas) {
        // Drawing the fretboard
        _fretNumbersPaint.getTextBounds("0", 0, 1, _textBounds);

        _fretNumberRect.set(_cellRect);
        _fretNumberRect.offset(0, (_cellRect.height() - _textBounds.height()) / 2);

        // Drawing the number of the frets (we skip the 0)
        for (int i = _exercise.getMinFret(); i <= _exercise.getMaxFret(); ++i) {
            if (i != 0)
                canvas.drawText("" + i, _fretNumberRect.exactCenterX(), _fretNumberRect.exactCenterY(), _fretNumbersPaint);
            _fretNumberRect.offset(_cellRect.width(), 0);
        }

        // Drawing the frets
        _openStringRect.set(_cellRect);
        _openStringRect.offset(0, _cellRect.height());

        for (int i = 0; i < 6; ++i) {

            // Some super-ugly logic to decide what bitmaps for the frets to choose
            int indexFretBitmaps1;
            if (i == 0) indexFretBitmaps1 = 0;
            else if (i == 5) indexFretBitmaps1 = 2;
            else indexFretBitmaps1 = 1;

            _fretRect.set(_openStringRect);
            for (int j = 0; j < (_exercise.getMaxFret() - _exercise.getMinFret() + 1); ++j) {
                int nFret = j + _exercise.getMinFret();

                int indexFretBitmaps2 = (nFret == 0) ? 0 : 1;

                canvas.drawBitmap(_fretBitmaps[indexFretBitmaps1][indexFretBitmaps2], null, _fretRect, _alphaPaint);
                _fretRect.offset(_cellRect.width(), 0);
            }
            _openStringRect.offset(0, _cellRect.height());
        }

        // Drawing the sides of the head of the guitar, only if we are drawing the head of the guitar
        if(_exercise.getMinFret() == 0) {
            _headSideRect.set(_cellRect);
            canvas.drawBitmap(_headSideBitmaps[0], null, _headSideRect, _alphaPaint);
            _headSideRect.offset(0, (int) (N_ROWS - 1) * _headSideRect.height());
            canvas.drawBitmap(_headSideBitmaps[1], null, _headSideRect, _alphaPaint);
        }

        // Drawing the marks in the frets
        for (int j = 0; j < (_exercise.getMaxFret() - _exercise.getMinFret() + 1); ++j) {
            int nFret = j + _exercise.getMinFret();
            switch (nFret) {
                case 3:
                case 5:
                case 7:
                case 9:
                case 12:
                    _fretMarkRectTemp.set(_fretMarkRectTemp);
                    _fretMarkRectTemp.offset(j * _fretMarkRectTemp.width(), 0);
                    canvas.drawBitmap(_fretMarkBitmap, null, _fretMarkRectTemp, _alphaPaint);
                    break;
            }
        }

        // Drawing the strings
        _stringRect.set(_cellRect);
        _stringRect.right = _stringRect.left + _cellRect.width() * (_exercise.getMaxFret() - _exercise.getMinFret() + 1);
        _stringRect.offset(0, _cellRect.height());
        for (int i = 0; i < 6; ++i) {
            canvas.drawBitmap(_stringBitmaps[i], null, _stringRect, _alphaPaint);
            _stringRect.offset(0, _cellRect.height());
        }

        // If we have a scale, let's draw it
        if(_scaleNotesSprites != null){
            for(ISprite s : _scaleNotesSprites){
                s.draw(canvas, 1);
            }
        }
    }

    protected static GuitarNote findGuitarNote(Note note, int minFret, int maxFret) {
        if (minFret > maxFret)
            throw new RuntimeException("Frets do not make sense. MinFret: " + minFret + ", MaxFret: " + maxFret);

        if (minFret < 0 || maxFret < 0)
            throw new RuntimeException("Frets do not make sense. MinFret: " + minFret + ", MaxFret: " + maxFret);

        if (minFret > Music.MAX_FRETS_GUITAR || maxFret > Music.MAX_FRETS_GUITAR)
            throw new RuntimeException("Frets do not make sense. MinFret: " + minFret + ", MaxFret: " + maxFret);

        Music.GuitarString guitarString = null;

        // Lets look for the string
        for (Music.GuitarString s : Music.GuitarString.values()) {

            Note noteMinFret = s.getNote(minFret);

            if (note.compareTo(noteMinFret) >= 0) {
                guitarString = s;
                break;
            }
        }

        if (guitarString == null) {
            throw new RuntimeException("The note is too low for our fretboard: " + note);
        }

        //Let's look for the fret
        int fret = note.compareTo(guitarString.getOpenNote());

        if (fret > maxFret) {
            throw new RuntimeException("The note is too high for our string: " + guitarString + ", note: " + note);
        }

        return new GuitarNote(guitarString, fret);
    }

    @Override
    public void setAlpha(float alpha) {
        _fretNumbersPaint.setAlpha((int) (alpha * 255));
        _alphaPaint.setAlpha((int) (alpha * 255));

        if(_playableSprites != null) {
            for (ISprite s : _playableSprites) {
                if(s != null) s.setAlpha(alpha);
            }
        }
    }

    @Override
    protected void loadWidgetExercise() {
        _playableSprites = new IFretboardElementSprite[_exercise.getTrack().getNPlayables()];

        for(int i = 0; i < _exercise.getTrack().getNPlayables(); ++i){
            Playable playable = _exercise.getTrack().getPlayable(i);

            if (playable instanceof Note) {
                Note note = (Note) playable;
                GuitarNote guitarNote = findGuitarNote(note, _exercise.getMinFret(), _exercise.getMaxFret());

                // Here, we change it according to the representation, for the scales
                switch (_exercise.getNoteRepresentation()) {
                    case SCALE_CHANGE_LIGHTING: // We show it gray, and colorful when it plays
                        _playableSprites[i] = new FretboardCircleSprite(_context, guitarNote.getString(), guitarNote.getNote().getRoot().toString(), guitarNote.getNote().getRoot().getColorId(), guitarNote.getFret(), _exercise.getMinFret(), _metronomeTimeRef.getPlayableStartTime(i), getAnticipationDuration(i));
                        break;
                    case SCALE_ANTICIPATION_CIRCLE:
                        _playableSprites[i] = new AnticipationCircleSprite(_context, guitarNote.getString(), guitarNote.getFret(), _exercise.getMinFret(), _metronomeTimeRef.getPlayableStartTime(i), getAnticipationDuration(i));
                        break;
                    case SCALE_ROTATING_BALL:
                        _playableSprites[i] = new RotatingBall(_context, guitarNote.getString(), guitarNote.getFret(), _exercise.getMinFret(), _metronomeTimeRef.getPlayableStartTime(i), getAnticipationDuration(i));
                        break;
                    default: // We use the default GuitarNoteSprite, with the anticipation circle and the inner circle getting bigger
                        _playableSprites[i] = new GuitarNoteSprite(_context, guitarNote, _exercise.getMinFret(), _exercise.getMaxFret(), _metronomeTimeRef.getPlayableStartTime(i), getAnticipationDuration(i), _metronomeTimeRef.getPlayableDurationInMs(i));
                }



            } else if (playable instanceof Chord) {
                Chord chord = (Chord) playable;
                Chord.ChordStrokePattern strokePattern = (Chord.ChordStrokePattern) _exercise.getTrack().getStrokePattern(i);

                List<IInversion> inversions = ChordsLibrary.getInversions(chord);
                if (inversions == null || inversions.size() == 0) {
                    throw new RuntimeException("We do not have an inversion for chord " + chord);
                }

                IInversion inversion = inversions.get(0);

                _playableSprites[i] = new ChordInversionSprite(_context, inversion, strokePattern, _exercise.getMinFret(), _exercise.getMaxFret(),
                        _metronomeTimeRef.getPlayableStartTime(i), getAnticipationDuration(i), _metronomeTimeRef.getPlayableDurationInMs(i));

            } else {
                throw new RuntimeException("Unknown type of playable: " + playable);
            }
        }

        // If we have a scale, let's load these special sprites
        // Now, if we have a scale, we draw it permanently
        if(_exercise.getTrack().getScalePattern() != null){
            _scaleNotesSprites = new ArrayList<>();
            for(GuitarNote scaleNote : _exercise.getTrack().getScalePattern()) {
                switch (_exercise.getNoteRepresentation()) {
                    case SCALE_CHANGE_LIGHTING: // We show it gray, and colorful when it plays
                        _scaleNotesSprites.add(new FretboardCircleSprite(_context, scaleNote.getString(), scaleNote.getNote().getRoot().toString(), R.color.note_gray, scaleNote.getFret(), _exercise.getMinFret(), _metronomeTimeRef.getTrackDurationInMs(), _metronomeTimeRef.getTrackDurationInMs())); // Trick to make it show all the time
                        break;
                    case SCALE_ANTICIPATION_CIRCLE: // For these two, we show the scale with all its color
                    case SCALE_ROTATING_BALL:
                        _scaleNotesSprites.add(new FretboardCircleSprite(_context, scaleNote.getString(), scaleNote.getNote().getRoot().toString(), scaleNote.getNote().getRoot().getColorId(), scaleNote.getFret(), _exercise.getMinFret(), _metronomeTimeRef.getTrackDurationInMs(), _metronomeTimeRef.getTrackDurationInMs())); // Trick to make it show all the time
                        break;
                    default:
                        // We assume there is no scale here, and it is just a melody exercise, so we do not add anything
                }
            }
        }
    }
}
