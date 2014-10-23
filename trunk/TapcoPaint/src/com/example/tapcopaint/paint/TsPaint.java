package com.example.tapcopaint.paint;

import android.graphics.Color;
import android.graphics.Paint;

public class TsPaint {
    public static Paint getRedPaint() {
        Paint mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        mPaint.setColor(Color.RED);
        return mPaint;
    }

    public static Paint getRedPaint(float stroke) {
        Paint mPaint = getRedPaint();
        mPaint.setStrokeWidth(stroke);
        return mPaint;
    }

    public static Paint getBluePaint() {
        Paint mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        mPaint.setColor(Color.BLUE);
        return mPaint;
    }

    public static Paint getYellowPaint() {
        Paint mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        mPaint.setColor(Color.YELLOW);
        return mPaint;
    }

    public static Paint getGreenPaint() {
        Paint mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        mPaint.setColor(Color.GREEN);
        return mPaint;
    }

    public static Paint getBlackPaint() {
        Paint mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        mPaint.setColor(Color.BLACK);
        return mPaint;
    }

}
