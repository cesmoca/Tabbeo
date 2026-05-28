package com.tabbeo.Utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.tabbeo.Widgets.Sprites.CircleParams;

import java.util.HashMap;
import java.util.Map;

public class TextSizeUtils {
    // We cache the text size for each combination of number of letters x size of the rect.
    // We asume that there is not that many combinations of number of letters and sizes of rects
    // We calculate them once they are asked
    private static Map<TextSizeConf, TextSizeInRect> _textSizes = new HashMap<>();
    private static Paint _paint = new Paint();
    private static Rect _textBounds = new Rect();

    static class TextSizeConf{
        int textLength;
        Rect rect;

        public TextSizeConf(int textLength, Rect rect){
            this.textLength = textLength;
            this.rect = rect;
        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof TextSizeConf)) return false;
            TextSizeConf t = (TextSizeConf) o ;
            return textLength == t.textLength && rect.equals(t.rect);
        }

        @Override
        public int hashCode(){
            return textLength;
        }

        @Override
        public String toString(){
            return "Length: "+textLength+" Width: "+rect.width()+" Height: "+rect.height();
        }
    }

    public static class TextSizeInRect{
        public float textSize = 0;
        public float textVerticalOffset = 0;
    }

    public static TextSizeInRect getTextSizeToFitInRect(String text, Rect rect) {
        TextSizeConf expectedConf = new TextSizeConf(text.length(), rect);
        TextSizeInRect textSizeInRect = _textSizes.get(expectedConf);

        if(textSizeInRect == null)
            textSizeInRect = createTextSizeToFitInRect(expectedConf);

        return textSizeInRect;
    }

    protected static TextSizeInRect createTextSizeToFitInRect(TextSizeConf conf){
        TextSizeInRect textSizeInRect = new TextSizeInRect();
        final String str = "AAAAAAAAAAAAAAAAAAAA";
        final float sizeStep = 8f;

        if(conf.textLength > 20) throw new RuntimeException("This function only works with texts up to 20 chars. See the hardcoded string on top");

        do {
            textSizeInRect.textSize += sizeStep;
            _paint.setTextSize(textSizeInRect.textSize);
            _paint.getTextBounds(str, 0, conf.textLength, _textBounds); // It only counts the first textLenght characters
        }
        while (conf.rect.width() > _textBounds.width() && conf.rect.height() > _textBounds.height());

        textSizeInRect.textSize -= sizeStep;

        // Now let's calculate the vertical offset
        _paint.setTextSize(textSizeInRect.textSize);
        _paint.getTextBounds(str, 0, conf.textLength, _textBounds);
        textSizeInRect.textVerticalOffset = _textBounds.height() / 2.0f;

        _textSizes.put(conf, textSizeInRect); // Let's cache it

        return textSizeInRect;
    }
}
