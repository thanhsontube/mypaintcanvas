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
		case 1:
			p.setAntiAlias(false);
			p.setStrokeWidth(WIDTH_LV_1);
			break;
		case 2:
			p.setAntiAlias(true);
			p.setStrokeWidth(WIDTH_LV_2);
			break;
		case 3:
			p.setAntiAlias(true);
			p.setStrokeWidth(WIDTH_LV_3);
			break;
		case 4:
			p.setAntiAlias(true);
			p.setStrokeWidth(WIDTH_LV_4);
			break;
		case 5:
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
		case 1:
			return WIDTH_LV_1;
		case 2:
			return WIDTH_LV_2;
		case 3:
			return WIDTH_LV_3;
		case 4:
			return WIDTH_LV_4;
		case 5:
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

	public static int[] getRGB(String color) {
		int intColor = (int) Long.parseLong(color, 16);
		int r = (intColor >> 16) & 0xFF;
		int g = (intColor >> 8) & 0xFF;
		int b = (intColor >> 0) & 0xFF;
		int[] c = { r, g, b };
		return c;
	}

}
