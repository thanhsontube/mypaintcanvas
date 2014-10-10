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
    boolean isClear;

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
            isClear = !isClear;
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

    public class TsView extends View implements OnTouchListener {
        private static final float TOUCH_TOLERANCE = 4;
        private Canvas canvas;
        private List<Path> listPaths = new ArrayList<Path>();
        private List<Path> listPathsRedo = new ArrayList<Path>();

        private List<Bitmap> listBitmap = new ArrayList<Bitmap>();
        private List<Bitmap> listBitmapRedo = new ArrayList<Bitmap>();
        private Path path;
        private Bitmap imageBackground;

        private Bitmap mBitmap;
        private int w, h;
        private float mX, mY;

        private Paint paint;
        private boolean isUndo;

        private boolean isEarse;

        public TsView(Context context) {
            super(context);
            log.d("log>>> " + "TsView contructor");
            setFocusable(true);
            setFocusableInTouchMode(true);
            setOnTouchListener(this);
            imageBackground = BitmapFactory.decodeResource(getResources(), id);

            path = new Path();
            paint = mPaint;

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            log.d("log>>> " + "onSizeChanged w:" + w + ";h:" + h);
            super.onSizeChanged(w, h, oldw, oldh);
            this.w = w;
            this.h = h;
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            canvas = new Canvas();
            imageBackground = Bitmap.createScaledBitmap(imageBackground, getWidth(), getHeight(), true);

            // Bitmap bitmap = Bitmap.createBitmap(mBitmap);
            // listBitmap.add(bitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
        }

        private void touchStart(float x, float y) {
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
            path.lineTo(mX, mY);
            canvas.drawPath(path, paint);

            if (isEarse) {

            } else {

            }
            listPaths.add(path);
            Bitmap bitmap = Bitmap.createBitmap(mBitmap);
            // listBitmap.add(bitmap);
            path = new Path();
            log.d("log>>> " + "touchUp");
            if (isUndo) {
                listPathsRedo.clear();
                isUndo = false;
            }
        }

        int value;

        public void clear() {
            mPaint.setXfermode(null);
            isEarse = false;
            listPaths.clear();
            listPathsRedo.clear();
            canvas.drawBitmap(imageBackground, 0, 0, null);
            invalidate();
        }

        public void earse() {
            isEarse = true;
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

            // invalidate();
        }

        public void undo() {
            if (listPaths.size() == 0) {
                Toast.makeText(getActivity(), "Stack empty", Toast.LENGTH_SHORT).show();
                return;
            }

            isUndo = true;

            listPathsRedo.add(listPaths.get(listPaths.size() - 1));
            listPaths.remove(listPaths.size() - 1);

            // listBitmap.remove(listBitmap.size() - 1);
            // mBitmap = Bitmap.createBitmap(listBitmap.get(listBitmap.size() - 2));
            invalidate();
        }

        public void redo() {

            if (listPathsRedo.size() == 0) {
                Toast.makeText(getActivity(), "At Top, can not redo", Toast.LENGTH_SHORT).show();
                return;
            }

            listPaths.add(listPathsRedo.get(listPathsRedo.size() - 1));
            listPathsRedo.remove(listPathsRedo.size() - 1);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    DrawingPath currentDrawingPath;
    float mX, mY;

    private void touchStart(float x, float y) {
        mX = x;
        mY = y;
        log.d("log>>> " + "touchStart");
        currentDrawingPath = new DrawingPath();
        currentDrawingPath.paint = mPaint;
        currentDrawingPath.path = new Path();
        currentDrawingPath.path.moveTo(x, y);
        currentDrawingPath.path.lineTo(x, y);
        // tsSurfaceView.addDrawingPath(currentDrawingPath);
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            currentDrawingPath.path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp(float x, float y) {
        currentDrawingPath.path.lineTo(mX, mY);
        tsSurfaceView.addDrawingPath(currentDrawingPath);
        tsSurfaceView.setRunning(true);
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
