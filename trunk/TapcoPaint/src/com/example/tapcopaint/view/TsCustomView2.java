package com.example.tapcopaint.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.tapcopaint.utils.FilterLog;
import com.example.tapcopaint.utils.ImageCache;

public class TsCustomView2 extends View implements OnTouchListener {
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

    private AQuery aQuery;

    public TsCustomView2(Context context) {
        super(context);
        log.d("log>>> " + "TsView contructor");
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
        // imageBackground = BitmapFactory.decodeResource(context.getResources(), id);
        path = new Path();
        tsPath = new TsPath();
        aQuery = new AQuery(context);
    }

    public TsCustomView2(Context context, Paint paint, int id) {
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
        canvas = new Canvas(bitmapPaint);
        imageBackground = BitmapFactory.decodeResource(getResources(), id);
        imageBackground = Bitmap.createScaledBitmap(imageBackground, getWidth(), getHeight(), true);

        listTsPaths.add(new TsPath(tsPath));
        setmImageCache(new ImageCache(getContext()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw a background
        // canvas.drawBitmap(imageBackground, 0, 0, null);
        // draw a bitmap paint with Paint.DITHER_FLAG

        if (listPaths.size() > 0 ) {
            if(isUndo) {
                
                bitmapPaint = getmImageCache().get(String.valueOf(listPaths.size() - 1));
                int i = 0;
                for (Path p : listPaths) {
                    Bitmap bitmap = getmImageCache().get(String.valueOf(i));
                    i++;
                    log.d("log>>> " + "bitmapPaintLOG:" + bitmap);
                }
            }
        } else {
            if(isUndo) {
                
                bitmapPaint = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            }
        }
        
        if(listPaths.size() > 0  && !isUndo) {
            bitmapPaint = getmImageCache().get(String.valueOf(listPaths.size() - 1));
        }

        log.d("log>>> " + "bitmapPaint:" + bitmapPaint);
        if (bitmapPaint != null) {

            canvas.drawBitmap(bitmapPaint, 0, 0, new Paint(Paint.DITHER_FLAG));
        } else {
            log.d("log>>> " + "bitmapPaint is NULL");
        }

        canvas.drawPath(path, paint);

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
        path.lineTo(mX, mY);
        listPaths.add(path);
        new LazyImageSetTask(bitmapPaint, listPaths.size() - 1).execute();
        // commit the path to our offscreen
        canvas.drawPath(path, paint);
        path.reset();


    }

    public void undo() {

        log.d("log>>> " + "undo: listTsPaths:" + listTsPaths.size());
        if (listPaths.size() == 0) {
            Toast.makeText(getContext(), "Stack empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = getmImageCache().get(String.valueOf(listPaths.size() - 1));
        bitmap.recycle();

        isUndo = true;
        listPathsRedo.add(listPaths.get(listPaths.size() - 1));
        listPaths.remove(listPaths.size() - 1);
        invalidate();
    }

    public void earse() {
        log.d("log>>> " + "earse");
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
        tsPath.reset();
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

    public ImageCache getmImageCache() {
        return mImageCache;
    }

    public void setmImageCache(ImageCache mImageCache) {
        this.mImageCache = mImageCache;
    }

    private ImageCache mImageCache;

    class LazyImageSetTask extends AsyncTask<Void, Void, Bitmap> {
        final Bitmap src;
        final int position;

        public LazyImageSetTask(Bitmap src, int pos) {
            this.src = Bitmap.createBitmap(src);
            this.position = pos;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            log.d("log>>> " + "doInBackground position:" + position + ";src:" + src);

            synchronized (getmImageCache()) {
                getmImageCache().put(String.valueOf(position), src);
            }
            // src.recycle();
            return src;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

        }
    }

}
