package com.tabbeo.Widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.tabbeo.R;

public class TabbeoButton extends ImageButton {
    public static final String LOG_TAG = "TabbeoImageButton";

    public TabbeoButton(Context context) {
        super(context);
        init();
    }

    public TabbeoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setAdjustViewBounds(true);
        setPadding(5, 5, 5, 5);
        setBackgroundColor(Color.TRANSPARENT);

    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if(!isEnabled()){
            // We are using a multiplier. The light blue color we use is RGB 0,159,227
            // And we want to achieve a light gray color RGB 175,175, 175
            // The multiplier are two colors in which blue * mul + add = gray
            // This mul so far is 0
            // The add so far is RGB 175,175, 175
            // At some point we need to automate this, do the calculations automatically
            int add = 0xFFAFAFAF;
            setColorFilter(new LightingColorFilter(0, add));
            return;

        }

        if (isPressed()) {
            setColorFilter(R.color.worldmap_exercisebutton_pressed_tint);
        } else {
            setColorFilter(null);
        }
        invalidate();
    }

}
