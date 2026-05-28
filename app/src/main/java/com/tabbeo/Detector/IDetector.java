package com.tabbeo.Detector;

import com.tabbeo.Music.Playable.Playable;

public interface IDetector<DetectionResult> {
    DetectionResult detect(float[] audioData);
    Playable getPlayableFromDetectionResult(DetectionResult detectionResult);
    int getBufferSize();
}
