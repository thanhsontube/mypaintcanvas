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
import com.example.tapcopaint.view.TsCustomView;
import com.example.tapcopaint.view.TsCustomView2;

public class PaintFragment2 extends BaseFragment {

    private static final String TAG = "PaintFragment2";
    private int id;
    private ImageView img;
    private Paint mPaint;
    private TsView rootView;

    private MyView myView;

    private TsCustomView tsCustomView;
    private TsCustomView2 tsCustomView2;

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

        // rootView = new TsView(getActivity());
        // return rootView;

        // myView = new MyView(getActivity());
        // return myView;

//        tsCustomView = new TsCustomView(getActivity(), mPaint, id);
//        return tsCustomView;
        
        tsCustomView2 = new TsCustomView2(getActivity(), mPaint, id);
        return tsCustomView2;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.gesture_main, menu);
    }

    int i = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_undo:
            // rootView.undo();
            tsCustomView2.undo();
            break;
        case R.id.action_redo:
            rootView.redo();
            break;
        case R.id.action_earse:
//            rootView.earse();
            tsCustomView2.earse();
            // myView.earse();
            // mPaint.setXfermode(sModes[i]);
            // i++;
            // if (i == sModes.length - 1) {
            // i = 0;
            // }
            break;
        case R.id.action_clear:
            // mPaint.setXfermode(null);
//            rootView.clear();
            tsCustomView.clear();
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
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            log.d("log>>> " + "onDraw");

            // draw a background
            canvas.drawBitmap(imageBackground, 0, 0, null);

            // draw all path before

            paint.setXfermode(null);
            // log.d("log>>> " + "listBitmap:" + listBitmap.size());
            for (Path p : listPaths) {
                canvas.drawPath(p, paint);
            }

            canvas.drawColor(Color.TRANSPARENT);
            canvas.drawBitmap(mBitmap, 0, 0, null);

            // canvas.drawBitmap(listBitmap.get(listBitmap.size() - 1), 0, 0, null);
            // mBitmap = Bitmap.createBitmap(listBitmap.get(listBitmap.size() - 1));

            // draw path
            // if (isEarse) {
            // mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            // }

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

    public class MyView extends View {

        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;

        private Bitmap mBitmap;
        private Bitmap imageBackground;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;

        private Path storepath;

        public MyView(Context c) {
            super(c);

            storepath = new Path();

            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            mBitmap = BitmapFactory.decodeResource(getResources(), id);
            imageBackground = BitmapFactory.decodeResource(getResources(), id);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            // mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

            // mCanvas = new Canvas(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888));
            imageBackground = Bitmap.createScaledBitmap(imageBackground, getWidth(), getHeight(), true);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // canvas.drawColor(0xFFAAAAAA);
            canvas.drawBitmap(imageBackground, 0, 0, null);
            canvas.drawColor(Color.TRANSPARENT);

            canvas.drawBitmap(mBitmap, 0, 0, null);

            // mPaint.setXfermode(null);
            // if (storepath != null) {
            // canvas.drawPath(storepath, mPaint);
            // }

            // if (isEarse) {
            // mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            // }

            canvas.drawPath(mPath, mPaint);
        }

        boolean isEarse;

        public void earse() {
            isEarse = true;
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;

            storepath.moveTo(x, y);
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                storepath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            storepath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
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
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if(tsCustomView2 != null) {
            tsCustomView2.getmImageCache().clear();
        }
        super.onDestroy();
        
    }

}
