package com.example.tapcopaint.implement;

import android.graphics.Paint;

public interface IPaint {
    Paint setAlpha(Paint p, int a);

    // level = px

    // 1=1px (kh�ng anti-aliasing)
    // 2=3px (c� anti-aliasing)
    // 3=5px (c� anti-aliasing)
    // 4=10px (c� anti-aliasing)
    // 5=20px (c� anti-aliasing)

    Paint setStrokeWidth(Paint p, int level);

    // String format : #ff00ff

    String setColor(int r, int g, int b);

    // color #ff00ff
    int[] getRGB(String color);

    // color #ff00ff
    Paint setColor(Paint p, String color);

    Paint setColor(Paint p, int r, int g, int b);

}
