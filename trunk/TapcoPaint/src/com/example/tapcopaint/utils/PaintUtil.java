package com.example.tapcopaint.utils;

import android.graphics.Color;
import android.graphics.Paint;

public class PaintUtil {

    private static int WIDTH_LV_1 = 1;
    private static int WIDTH_LV_2 = 3;
    private static int WIDTH_LV_3 = 5;
    private static int WIDTH_LV_4 = 10;
    private static int WIDTH_LV_5 = 20;

    public static Paint setAlpha(Paint p, int a) {
        p.setAlpha((int) a * 255 / 100);
        return p;
    }

    public static Paint setStrokeWidth(Paint p, int level) {
        switch (level) {
        case 0:
            p.setAntiAlias(false);
            p.setStrokeWidth(WIDTH_LV_1);
            break;
        case 1:
            p.setAntiAlias(true);
            p.setStrokeWidth(WIDTH_LV_2);
            break;
        case 2:
            p.setAntiAlias(true);
            p.setStrokeWidth(WIDTH_LV_3);
            break;
        case 3:
            p.setAntiAlias(true);
            p.setStrokeWidth(WIDTH_LV_4);
            break;
        case 4:
            p.setAntiAlias(true);
            p.setStrokeWidth(WIDTH_LV_5);
            break;
        default:
            break;
        }
        return p;
    }

    public static int getStrokeWidth(int level) {
        switch (level) {
        case 0:
            return WIDTH_LV_1;
        case 1:
            return WIDTH_LV_2;
        case 2:
            return WIDTH_LV_3;
        case 3:
            return WIDTH_LV_4;
        case 4:
            return WIDTH_LV_5;
        default:
            return WIDTH_LV_1;
        }
    }

    public static Paint setColor(Paint p, String color) {
        p.setColor(Color.parseColor(color));
        return p;
    }

    public static Paint setColor(Paint p, int r, int g, int b) {
        p.setColor(Color.rgb(r, g, b));
        return p;
    }

    public static int[] getRGB(String colorStr) {

        // int intColor = (int) Long.parseLong(color, 16);
        int r = Integer.valueOf(colorStr.substring(1, 3), 16);
        int g = Integer.valueOf(colorStr.substring(3, 5), 16);
        int b = Integer.valueOf(colorStr.substring(5, 7), 16);
        int[] c = new int[] { r, g, b };
        return c;
    }

	public static String getColor(int intColor) {
		String hexColor = Integer.toHexString(intColor).substring(2);
		return hexColor;
	}

}
