package com.tabbeo.CourseLibrary;

import android.support.annotation.NonNull;

import com.tabbeo.Activities.Exercises.IActivityExerciseFrame;
import com.tabbeo.Activities.TutorialSequencer.ITutorialSequencer;
import com.tabbeo.Music.Music;
import com.tabbeo.Music.Track;
import com.tabbeo.R;
import com.tabbeo.Trainer.ITrainer;
import com.tabbeo.Trainer.Trainer;
import com.tabbeo.Widgets.FretboardWidget;

public class Exercise {
    public enum Type {
        Slideshow(R.string.ExerciseType_Slideshow),
        Melody(R.string.ExerciseType_Melody),
        MelodyDynamic(R.string.ExerciseType_MelodyDynamic),
        ChordsRhythm(R.string.ExerciseType_Chords);

        private final int _stringRes;

        Type(int stringRes) {
            _stringRes = stringRes;
        }

        public int getStringRes() {
            return _stringRes;
        }
    }

    private String _name;
    private Track _track;
    private Exercise.Type _type;
    private Trainer.Type _trainerType;
    private int _minFret;
    private int _maxFret;
    private Integer _descriptionId;
    private ITutorialSequencer _tutorialSequencer = null;

    // This guy should GO OUT . TODO
    FretboardWidget.NOTE_REPRESENTATION _noteRepresentation;

    public Exercise(String name, Type type, Trainer.Type trainerType, Track track, int minFret, int maxFret, Integer descriptionId, @NonNull FretboardWidget.NOTE_REPRESENTATION noteRepresentation) {
        _name = name;
        _type = type;
        _trainerType = trainerType;
        _track = track;
        _minFret = minFret;
        _maxFret = maxFret;
        _descriptionId = descriptionId;

        _noteRepresentation = noteRepresentation;

        _tutorialSequencer = createTutorial();

        if (track != null) {
            if (!(minFret >= 0 && minFret <= Music.MAX_FRETS_GUITAR))
                throw new RuntimeException("The minFret for the exercise " + name + " is out of bounds");
            if (!(maxFret >= 0 && maxFret <= Music.MAX_FRETS_GUITAR))
                throw new RuntimeException("The maxFret for the exercise " + name + " is out of bounds");
            if(minFret > maxFret)
                throw new RuntimeException("The minFret: "+minFret+" is bigger than the maxFret: "+maxFret);
        }
    }

    final public Exercise.Type getType(){ return _type; }

    final public String getName(){ return _name; }

    final public Track getTrack(){ return _track; }

    final public Trainer.Type getTrainerType(){ return _trainerType; }

    final public int getMaxFret(){ return _maxFret; }

    final public int getMinFret(){ return _minFret; }

    final public Integer getDescriptionId(){ return _descriptionId; }

    final public ITutorialSequencer getTutorial(){
        return _tutorialSequencer;
    }

    final public FretboardWidget.NOTE_REPRESENTATION getNoteRepresentation(){
        return _noteRepresentation;
    }

    protected ITutorialSequencer createTutorial(){ return null; }

    public int getMaxPoints() {
        if(_track != null) {
            return _track.getNPlayables() * Trainer.POINTS_HIT;
        }else{
            ITutorialSequencer ts = getTutorial();
            if(ts != null) {
                return ts.getNumberExplanations() - 1;
            }else{
                throw new RuntimeException("We do not know how to calculate the max accumulatedExperiencePoints for this exercise");
            }
        }
    }

    @Override
    public String toString(){ return _name; }

    public float experiencePointsToPicks(int experiencePoints) {
        if(experiencePoints > getMaxPoints()){
            throw new RuntimeException("Earned accumulatedExperiencePoints: "+experiencePoints+" are bigger than max accumulatedExperiencePoints: "+getMaxPoints()+" for exercise: "+toString());
        }

        return (experiencePoints / (float) getMaxPoints()) * IActivityExerciseFrame.MAX_SCORE_PICKS;
    }
}
