package com.tabbeo.Trainer;


import com.tabbeo.Music.IMetronomeTimeRef;
import com.tabbeo.Music.Playable.Playable;

public interface ITrainer {

    enum Type { Timed, Interactive }

    enum ContinueDetecting {Yes, No}

    void startTraining();
    void stopTraining();
    Playable getExpectedPlayable();
    ContinueDetecting onAnalysis(long realTimestamp, Playable detectedPlayable);
    IMetronomeTimeRef getMetronomeTimeRef();
    void pauseDetecting();
    void resumeDetecting();
}
