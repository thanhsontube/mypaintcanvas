package com.example.tapcopaint.fragment;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.util.Log;
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

public class PaintFragment2 extends BaseFragment {

    private static final String TAG = "PaintFragment2";
    private int id;
    private ImageView img;
    private Paint mPaint;
    private TsView rootView;

    private static final Xfermode[] sModes = { new PorterDuffXfermode(PorterDuff.Mode.CLEAR),
            new PorterDuffXfermode(PorterDuff.Mode.SRC), new PorterDuffXfermode(PorterDuff.Mode.DST),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER), new PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_IN), new PorterDuffXfermode(PorterDuff.Mode.DST_IN),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT), new PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP), new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),
            new PorterDuffXfermode(PorterDuff.Mode.XOR), new PorterDuffXfermode(PorterDuff.Mode.DARKEN),
            new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN), new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),
            new PorterDuffXfermode(PorterDuff.Mode.SCREEN) };

    private static final String[] sLabels = { "Clear", "Src", "Dst", "SrcOver", "DstOver", "SrcIn", "DstIn", "SrcOut",
            "DstOut", "SrcATop", "DstATop", "Xor", "Darken", "Lighten", "Multiply", "Screen" };

    FilterLog log = new FilterLog(TAG);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // View rootView = (ViewGroup) inflater.inflate(R.layout.paint_fragment, container, false);
        // img = (ImageView) rootView.findViewWithTag("icon");
        // img.setImageResource(id);
        // View rootView = new DrawView(getActivity());

        rootView = new TsView(getActivity());
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.gesture_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_undo:
            rootView.undo();
            break;
        case R.id.action_redo:
            rootView.redo();
            break;
        case R.id.action_earse:
            rootView.earse();
            break;
        case R.id.action_clear:
            rootView.clear();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class TsView extends View implements OnTouchListener {
        private static final float TOUCH_TOLERANCE = 4;
        private Canvas canvas;
        private List<Path> listPaths = new ArrayList<Path>();
        private List<Path> listPathsRedo = new ArrayList<Path>();
        private Path path;
        private Bitmap imageBackground;
        private int w, h;
        private float mX, mY;

        private Paint paint;
        private boolean isUndo;

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
            canvas = new Canvas(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888));
            imageBackground = Bitmap.createScaledBitmap(imageBackground, getWidth(), getHeight(), true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            log.d("log>>> " + "onDraw");

            // draw a back ground

            canvas.drawBitmap(imageBackground, 0, 0, null);

            // draw all path before
            for (Path p : listPaths) {
                canvas.drawPath(p, paint);
            }
            // draw path
            canvas.drawPath(path, paint);

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
            // path.lineTo(mX, mY);
            listPaths.add(path);
            canvas.drawPath(path, paint);
            path = new Path();
            log.d("log>>> " + "touchUp");
            if (isUndo) {
                listPathsRedo.clear();
                isUndo = false;
            }
        }

        int value;

        public void clear() {
            listPaths.clear();
            listPathsRedo.clear();
            canvas.drawBitmap(imageBackground, 0, 0, null);
            invalidate();
        }

        int i = 0;

        public void earse() {
            Toast.makeText(getActivity(), "MODE:" + sLabels[i], Toast.LENGTH_SHORT).show();
            log.d("log>>> " + "MODE:" + sLabels[i]);
            paint.setXfermode(sModes[i]);
            i++;
            if (i == sModes.length - 1) {
                i = 0;
            }
            invalidate();
        }

        public void undo() {
            if (listPaths.size() == 0) {
                Toast.makeText(getActivity(), "Stack empty", Toast.LENGTH_SHORT).show();
                return;
            }

            isUndo = true;

            listPathsRedo.add(listPaths.get(listPaths.size() - 1));
            listPaths.remove(listPaths.size() - 1);
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
