package com.example.tapcopaint.view;

import java.util.ArrayList;
import java.util.List;

import com.example.tapcopaint.utils.FilterLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class TsCustomView extends View implements OnTouchListener {
    private static final String TAG = "TsCustomView";
    FilterLog log = new FilterLog(TAG);
    private static final float TOUCH_TOLERANCE = 4;
    private Canvas canvas;

    private List<TsPath> listTsPaths = new ArrayList<TsPath>();

    private List<Path> listPaths = new ArrayList<Path>();
    private List<Path> listPathsRedo = new ArrayList<Path>();

    private List<Bitmap> listBitmap = new ArrayList<Bitmap>();
    private List<Bitmap> listBitmapRedo = new ArrayList<Bitmap>();
    private Path path;
    private Bitmap imageBackground;

    private Bitmap bitmapPaint;
    private int w, h;
    private float mX, mY;

    private Paint paint;
    private int id;
    private boolean isUndo;

    private boolean isEarse;

    private TsPath tsPath;

    public TsCustomView(Context context) {
        super(context);
        log.d("log>>> " + "TsView contructor");
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
        // imageBackground = BitmapFactory.decodeResource(context.getResources(), id);
        path = new Path();
        tsPath = new TsPath();
    }

    public TsCustomView(Context context, Paint paint, int id) {
        // super(context);
        this(context);
        this.paint = paint;
        this.id = id;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        log.d("log>>> " + "onSizeChanged w:" + w + ";h:" + h);
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        bitmapPaint = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        imageBackground = BitmapFactory.decodeResource(getResources(), id);
        imageBackground = Bitmap.createScaledBitmap(imageBackground, getWidth(), getHeight(), true);

        listTsPaths.add(new TsPath(tsPath));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // bitmapPaint = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        // draw a background
        canvas.drawBitmap(imageBackground, 0, 0, null);
        // draw a bitmap paint with Paint.DITHER_FLAG
        canvas.drawBitmap(bitmapPaint, 0, 0, new Paint(Paint.DITHER_FLAG));

        paint.setXfermode(null);
        canvas.drawPath(tsPath, paint);

        if (isEarse) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }

        // if (isUndo) {
        // canvas.drawPath(listTsPaths.get(listTsPaths.size() - 1), paint);
        // } else {
        // canvas.drawPath(path, paint);
        // }

        // draw path with main paint
    }

    private void touchStart(float x, float y) {
        isUndo = false;
        log.d("log>>> " + "touchStart");
        path.reset();
        path.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp(float x, float y) {
        // // path.lineTo(mX, mY);
        // canvas.drawPath(path, paint);
        //
        // if (isEarse) {
        //
        // } else {
        //
        // }
        // listPaths.add(path);
        //
        // path.reset();
        // // path = new Path();
        // log.d("log>>> " + "touchUp");
        // if (isUndo) {
        // listPathsRedo.clear();
        // isUndo = false;
        // }

        path.lineTo(mX, mY);
        listPaths.add(path);
        // commit the path to our offscreen
        canvas.drawPath(path, paint);
        tsPath.addPath(path);
        // Path p = new Path(tsPath);
        listTsPaths.add(new TsPath(tsPath));
        // kill this so we don't double draw
        path.reset();
    }

    public void undo() {
        if (listPaths.size() == 0) {
            Toast.makeText(getContext(), "Stack empty", Toast.LENGTH_SHORT).show();
            return;
        }

        isUndo = true;

        listTsPaths.remove(listTsPaths.size() - 1);
        tsPath = listTsPaths.get(listTsPaths.size() - 1);

        listPathsRedo.add(listPaths.get(listPaths.size() - 1));
        listPaths.remove(listPaths.size() - 1);
        invalidate();
    }

    public void earse() {
        isEarse = true;
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        // invalidate();
    }

    public void clear() {
        paint.setXfermode(null);
        isEarse = false;
        listPaths.clear();
        listPathsRedo.clear();
        listTsPaths.clear();
        canvas.drawBitmap(imageBackground, 0, 0, null);
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            touchStart(x, y);
            break;

        case MotionEvent.ACTION_MOVE:
            touchMove(x, y);
            break;
        case MotionEvent.ACTION_UP:
            touchUp(x, y);
            break;

        default:
            break;
        }
        invalidate();
        return true;

    }

}
