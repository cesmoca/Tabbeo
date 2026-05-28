package com.tabbeo.Widgets.Sprites;

import com.tabbeo.Widgets.ICanvas;

public abstract class ISprite {
    protected long _startTime, _endTime;

    public ISprite(long startTime, long endTime){
        _startTime = startTime;
        _endTime = endTime;
    }

    public abstract void setAlpha(float alpha);

    public void draw(ICanvas canvas, long virtualTimestamp){
        if(virtualTimestamp < _startTime || virtualTimestamp > _endTime)
            return;
        drawSprite(canvas, virtualTimestamp);
    }

    protected abstract void drawSprite(ICanvas canvas, long virtualTimestamp);

    public static float interpolate(float fromValue, float toValue, long startTime, long endTime, long now){
        float factor;

        if(now < startTime) factor = 0.f;
        else if(now > endTime) factor = 1.f;
        else factor = (now - startTime)/(float)(endTime - startTime);

        return fromValue + (toValue - fromValue) * factor;
    }
}