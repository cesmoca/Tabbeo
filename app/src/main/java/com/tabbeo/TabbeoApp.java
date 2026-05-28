package com.tabbeo;

import android.app.Application;
import android.content.Context;

import com.instabug.library.IBGInvocationEvent;
import com.instabug.library.Instabug;

public class TabbeoApp extends Application {
    protected static Context _context;

    // For the runtime permissions starting API 23 Marshmallow
    public static final int TABBEO_PERMISSION_RECORD_AUDIO = 0;

    @Override
    public void onCreate(){
        super.onCreate();

        // Let's set the static context for the app
        _context = getApplicationContext();

        // Initializing Instabug :)
        new Instabug.Builder(this, "9a2f6d7c662ee24bacd0a31b61e6caf5")
                .setInvocationEvent(IBGInvocationEvent.IBGInvocationEventShake)
                .build();
        Instabug.setPrimaryColor(getResources().getColor(R.color.tabbeo_light_blue));

        // Let's not use Instabug in debug builds
        if( BuildConfig.DEBUG) Instabug.disable();

    }

    public static Context getContext(){ return _context; }
}
