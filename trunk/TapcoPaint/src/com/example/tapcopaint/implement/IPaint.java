package com.example.tapcopaint.implement;

import android.graphics.Paint;

public interface IPaint {
    Paint setAlpha(Paint p, int a);

    // level = px

    // 1=1px (không anti-aliasing)
    // 2=3px (có anti-aliasing)
    // 3=5px (có anti-aliasing)
    // 4=10px (có anti-aliasing)
    // 5=20px (có anti-aliasing)

    Paint setStrokeWidth(Paint p, int level);

    // String format : #ff00ff

    String setColor(int r, int g, int b);

    // color #ff00ff
    int[] getRGB(String color);

    // color #ff00ff
    Paint setColor(Paint p, String color);

    Paint setColor(Paint p, int r, int g, int b);

}
