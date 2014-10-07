package com.example.tapcopaint.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.example.tapcopaint.R;
import com.example.tapcopaint.base.BaseFragment;

public class PaintFragment2 extends BaseFragment {

    private int id;
    private ImageView img;
    private Paint mPaint;

    @Override
    protected String generateTitle() {
        return "Paint";
    }

    public static PaintFragment2 newInstance(int id) {
        PaintFragment2 f = new PaintFragment2();
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
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // View rootView = (ViewGroup) inflater.inflate(R.layout.paint_fragment, container, false);
        // img = (ImageView) rootView.findViewWithTag("icon");
        // img.setImageResource(id);
        View rootView = new DrawView(getActivity());
        return rootView;
    }

    public class DrawView extends View implements OnTouchListener {
        private Canvas canvas;
        private Path mPath;
        // private Paint mPaint;
        public ArrayList<Path> paths = new ArrayList<Path>();
        public ArrayList<Boolean> listBooleans = new ArrayList<Boolean>();
        public ArrayList<Path> undonePaths = new ArrayList<Path>();
        public ArrayList<Boolean> listBooleansUndo = new ArrayList<Boolean>();

        HashMap<Path, Boolean> map = new HashMap<Path, Boolean>();

        public boolean isEarseMode = false;

        private Bitmap mBitmap;

        public DrawView(Context context) {
            super(context);
            setFocusable(true);
            setFocusableInTouchMode(true);
            this.setOnTouchListener(this);
            mPath = new Path();
            canvas = new Canvas();
            mBitmap = BitmapFactory.decodeResource(getResources(), id);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // canvas.drawColor(Color.WHITE);
            // canvas.setBitmap(mBitmap);
            // canvas.drawBitmap(mBitmap, 0, 0, null);
            canvas.drawBitmap(mBitmap, 0, 0, null);

            int i = 0;
            // for (Path p : paths) {
            // Log.v("", ">>>DRAWER:" + paths.size());
            // boolean isEarse = listBooleans.get(i);
            // if (isEarse) {
            // canvas.drawPath(p, paintEarse);
            // } else {
            //
            // canvas.drawPath(p, mPaint);
            // }
            // i++;
            // }
            //
            // if (isEarse) {
            // canvas.drawPath(mPath, paintEarse);
            // } else {
            //
            // canvas.drawPath(mPath, mPaint);
            // }

            for (Path p : paths) {
                Log.v("", ">>>DRAWER:" + paths.size());
                boolean isEarse = listBooleans.get(i);
                if (isEarse) {
                    mPaint.setColor(Color.TRANSPARENT);
                    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                } else {
                    mPaint.setColor(Color.RED);
                    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
                }
                canvas.drawPath(p, mPaint);
                i++;
            }

            // if (isEarse) {
            // mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            // } else {
            // mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
            // }

            canvas.drawPath(mPath, mPaint);

        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        public void setEarse(boolean isEarse) {
            invalidate();
        }

        private void touch_start(float x, float y) {
            undonePaths.clear();
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            if (isEarseMode) {
                mPaint.setColor(Color.TRANSPARENT);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            } else {
                mPaint.setColor(Color.RED);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
            }
            canvas.drawPath(mPath, mPaint);
            paths.add(mPath);
            listBooleans.add(isEarseMode);
            map.put(mPath, isEarseMode);
            mPath = new Path();

        }

        public void onClickUndo() {
            if (paths.size() >= 0) {
                undonePaths.add(paths.remove(paths.size() - 1));
                listBooleansUndo.add(listBooleans.remove(listBooleans.size() - 1));
                invalidate();
            } else {

            }
        }

        public void onClickRedo() {
            if (undonePaths.size() >= 0) {
                paths.add(undonePaths.remove(undonePaths.size() - 1));
                listBooleans.add(listBooleansUndo.remove(listBooleansUndo.size() - 1));
                invalidate();
            } else {

            }
        }

        public boolean isEraseMode() {
            return isEarseMode;
        }

        public void setEarse() {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        }

        @Override
        public boolean onTouch(View arg0, MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isEraseMode()) {
                    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                }
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
            }
            return true;
        }
    }

}
