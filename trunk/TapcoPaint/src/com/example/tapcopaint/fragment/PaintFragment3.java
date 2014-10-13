package com.example.tapcopaint.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tapcopaint.R;
import com.example.tapcopaint.base.BaseFragment;
import com.example.tapcopaint.utils.FilterLog;
import com.example.tapcopaint.view.DrawingPath;
import com.example.tapcopaint.view.TsSurfaceView;

public class PaintFragment3 extends BaseFragment implements OnTouchListener {

    private static final String TAG = "PaintFragment2";
    private int id;
    private Paint mPaint;

    FilterLog log = new FilterLog(TAG);
    public Xfermode MODE_EARSE = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    @Override
    protected String generateTitle() {
        return "Paint";
    }

    public static PaintFragment3 newInstance(int id) {
        PaintFragment3 f = new PaintFragment3();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt("id");
        }

        setHasOptionsMenu(true);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

    }

    public Paint resetpaint() {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        return mPaint;
    }

    ImageView img1, img2;
    TsSurfaceView tsSurfaceView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (ViewGroup) inflater.inflate(R.layout.paint_fragment2, container, false);
        tsSurfaceView = (TsSurfaceView) rootView.findViewById(R.id.tsSurfaceView1);
        tsSurfaceView.setId(id);
        tsSurfaceView.setOnTouchListener(this);
        img1 = (ImageView) rootView.findViewWithTag("icon1");
        img2 = (ImageView) rootView.findViewWithTag("icon2");
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.gesture_main, menu);
    }

    int i = 0;
    boolean isEarse;

    // TODO item menu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_undo:
            tsSurfaceView.undo();
            break;
        case R.id.action_redo:
            tsSurfaceView.redo();
            break;
        case R.id.action_earse:
            isEarse = !isEarse;
            // Paint mPaint = new Paint();
            // mPaint.setAntiAlias(true);
            // mPaint.setDither(true);
            // mPaint.setColor(Color.RED);
            // mPaint.setStyle(Paint.Style.STROKE);
            // mPaint.setStrokeJoin(Paint.Join.ROUND);
            // mPaint.setStrokeCap(Paint.Cap.ROUND);
            // mPaint.setStrokeWidth(12);
            // mPaint.setXfermode(MODE_EARSE);
            //
            // currentDrawingPath.paint = mPaint;
            break;
        case R.id.action_clear:
            tsSurfaceView.clear();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static final float TOUCH_TOLERANCE = 4;

    DrawingPath currentDrawingPath;
    float mX, mY;

    private void touchStart(float x, float y) {
        mX = x;
        mY = y;
        log.d("log>>> " + "touchStart");
        currentDrawingPath = new DrawingPath();
        mPaint = resetpaint();
        if (isEarse) {
            mPaint.setXfermode(MODE_EARSE);
        } else {
            mPaint.setXfermode(null);
        }
        currentDrawingPath.paint = mPaint;
        currentDrawingPath.path = new Path();
        currentDrawingPath.path.moveTo(x, y);
        currentDrawingPath.path.lineTo(x, y);
         tsSurfaceView.addDrawingPath(currentDrawingPath, false);
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            currentDrawingPath.path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
        tsSurfaceView.addDrawingPath(currentDrawingPath, false);
    }

    private void touchUp(float x, float y) {
        currentDrawingPath.path.lineTo(mX, mY);
        tsSurfaceView.addDrawingPath(currentDrawingPath, true);
        tsSurfaceView.clearTmpStack();
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
        return true;

    }

}
