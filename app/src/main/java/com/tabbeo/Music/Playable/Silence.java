package com.tabbeo.Music.Playable;

import com.tabbeo.R;
import com.tabbeo.TabbeoApp;

public class Silence implements Playable {

    private Silence(){} // Only allowed to be built in the package Playable

    @Override
    public boolean equals(Object o) {
        return o instanceof Silence;
    }

    public final static Silence SILENCE =  new Silence();

    @Override
    public String toString(){ return TabbeoApp.getContext().getString(R.string.silence); }
}
