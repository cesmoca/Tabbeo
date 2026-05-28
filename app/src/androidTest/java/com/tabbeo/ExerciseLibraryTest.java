package com.tabbeo;

import com.tabbeo.Activities.Exercises.IActivityExerciseFrame;
import com.tabbeo.Activities.TutorialSequencer.ITutorialSequencer;
import com.tabbeo.CourseLibrary.Exercise;
import com.tabbeo.Music.Music;
import com.tabbeo.Music.Playable.Chord;
import com.tabbeo.Music.Playable.Note;
import com.tabbeo.Music.Track;
import com.tabbeo.Trainer.Trainer;
import com.tabbeo.Widgets.FretboardWidget;

public class ExerciseLibraryTest {
    private ExerciseLibraryTest(){} // This class is not instantiable

    public static Track oneNoteTrack, twoNotesTrack;
    public static Track twoChordsTrack;

    public static Exercise slideshowExercise;
    public static Exercise twoNotesExerciseTimed, twoNotesExerciseInteractive;
    public static  Exercise twoChordsExerciseTimed, twoChordsExerciseInteractive;

    static{

        slideshowExercise = new Exercise("NoTrackEx", Exercise.Type.Slideshow, null, null /*track*/, 0, 0, null /*descriptionId*/, FretboardWidget.NOTE_REPRESENTATION.SCALE_ANTICIPATION_CIRCLE) {
            @Override
            public ITutorialSequencer createTutorial() {
                final ITutorialSequencer.TutorialSequencerExerciseFrame ts = new ITutorialSequencer.TutorialSequencerExerciseFrame();
                ts.addStep(null, ITutorialSequencer.Event.Touch, null)
                  .addStep(null, null, new ITutorialSequencer.TutorialStepListener() {
                      @Override
                      public void onStart(IActivityExerciseFrame activity) {
                          activity.endOfTrainingShowSummary();
                      }
                  });
                return ts;
            }
        };

        oneNoteTrack = new Track(new Music.BeatsPerMeasure(1, 2));
        oneNoteTrack.addMeasure(new Track.TrackPlayable(Note.E4, Music.Duration.half));


        twoNotesTrack = new Track(new Music.BeatsPerMeasure(2, 2))  ;
        twoNotesTrack.addMeasure(new Track.TrackPlayable(Note.E4, Music.Duration.half),
                new Track.TrackPlayable(Note.A4, Music.Duration.half));

        twoNotesExerciseTimed = new Exercise("twoNotesTimed", Exercise.Type.Melody, Trainer.Type.Timed, twoNotesTrack, 0, 5, null /*descriptionId*/, FretboardWidget.NOTE_REPRESENTATION.SCALE_ANTICIPATION_CIRCLE);
        twoNotesExerciseInteractive = new Exercise("twoNotsInteractive", Exercise.Type.Melody, Trainer.Type.Interactive, twoNotesTrack, 0, 5, null /*descriptionId*/, FretboardWidget.NOTE_REPRESENTATION.SCALE_ANTICIPATION_CIRCLE);

        twoChordsTrack = new Track(new Music.BeatsPerMeasure(2, 2))  ;
        twoChordsTrack.addMeasure(new Track.TrackPlayable(Chord.EMajor, Music.Duration.half, Chord.ChordStrokePattern.PICK_DOWN_STROKE),
                new Track.TrackPlayable(Chord.AMajor, Music.Duration.half, Chord.ChordStrokePattern.PICK_UP_STROKE));

        twoChordsExerciseTimed = new Exercise("twoChordsTimed", Exercise.Type.Melody, Trainer.Type.Timed, twoChordsTrack, 0, 5, null /*descriptionId*/, FretboardWidget.NOTE_REPRESENTATION.SCALE_ANTICIPATION_CIRCLE);
        twoChordsExerciseInteractive = new Exercise("twoChordsInteractive", Exercise.Type.Melody, Trainer.Type.Interactive, twoChordsTrack, 0, 5, null /*descriptionId*/, FretboardWidget.NOTE_REPRESENTATION.SCALE_ANTICIPATION_CIRCLE);
    }

}
