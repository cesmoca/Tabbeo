package com.tabbeo.Widgets.Sprites;

import android.graphics.Point;

public class CircleParams {
    public Point center;
    public float radius;

    public CircleParams(){
        this.center = new Point();
        radius = 0;
    }

    public CircleParams(Point center, float radius){
        this.center = center;
        this.radius = radius;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof CircleParams)) return false;
        CircleParams c = (CircleParams) o;
        return center.equals(c.center) && radius == c.radius;
    }

    public void set(Point center, float radius){
        this.center = center;
        this.radius = radius;
    }

    public void set(int cx, int cy, float radius){
        this.center.x = cx;
        this.center.y = cy;
        this.radius = radius;
    }
}
