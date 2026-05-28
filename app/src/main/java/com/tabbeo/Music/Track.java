package com.tabbeo.Music;

import com.tabbeo.Music.Playable.Playable;
import com.tabbeo.Music.Music.Duration;

import java.util.ArrayList;
import java.util.Collections;

public class Track {
    private ArrayList<TrackPlayable> _trackPlayables = new ArrayList<>();
    private Music.BeatsPerMeasure _beatsPerMeasure;
    private GuitarNote[] _scalePattern;

    public static class TrackPlayable {
        public Playable playable;
        public Duration duration;
        public Playable.StrokePattern strokePattern;

        public TrackPlayable(Playable playable, Duration duration){
            this(playable, duration, null);
        }

        public TrackPlayable(Playable playable, Duration duration, Playable.StrokePattern strokePattern){
            this.playable = playable;
            this.duration = duration;
            this.strokePattern = strokePattern;
        }

        @Override
        public boolean equals(Object o){
            throw new RuntimeException("Do not use this equal. Maybe you are trying to use the Playable equals");
        }
    }

    public Track(Music.BeatsPerMeasure beatsPerMeasure, GuitarNote[] scalePattern){
        _beatsPerMeasure = beatsPerMeasure;
        _scalePattern = scalePattern;
    }

    public Track(Music.BeatsPerMeasure beatsPerMeasure) {
        this(beatsPerMeasure, null /*scalePattern*/);
    }

    public void addMeasure(TrackPlayable... trackPlayables) {
        // Here we check that the payables set are actually good
        float totalBeats = 0;
        for (TrackPlayable trackPlayable : trackPlayables) {
            totalBeats += _beatsPerMeasure.measure / trackPlayable.duration.divisor;
        }

        if(totalBeats != _beatsPerMeasure.beats) {
            throw new RuntimeException("[addMeasure] Invalid measure");
        }

        Collections.addAll(_trackPlayables, trackPlayables);
    }

    final public Music.BeatsPerMeasure getBeatsPerMeasure() {
        return _beatsPerMeasure;
    }

    final public int getNPlayables() {
        return _trackPlayables.size();
    }

    final public GuitarNote[] getScalePattern(){
        return _scalePattern;
    }

    final public Playable getPlayable(int playableIndex) {
        return _trackPlayables.get(playableIndex).playable;
    }

    final public Duration getDuration(int playableIndex) {
        return _trackPlayables.get(playableIndex).duration;
    }

    final public Playable.StrokePattern getStrokePattern(int playableIndex){
        return _trackPlayables.get(playableIndex).strokePattern;
    }
}
