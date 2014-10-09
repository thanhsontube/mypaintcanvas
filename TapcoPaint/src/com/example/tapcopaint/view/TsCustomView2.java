package com.example.tapcopaint.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.tapcopaint.R;
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
    private Bitmap bgBitmapPaint;
    private int w, h;
    private float mX, mY;

    private Paint paint;
    private int id;
    private boolean isUndo;

    private boolean isEarse;

    private TsPath tsPath;

    private AQuery aQuery;

    private Stack<String> stackBitmaps = new Stack<String>();

    public TsCustomView2(Context context) {
        super(context);
        log.d("log>>> " + "TsView contructor");
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
        // imageBackground =
        // BitmapFactory.decodeResource(context.getResources(), id);
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
        // log.d("log>>> " + "onSizeChanged w:" + w + ";h:" + h);
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        bitmapPaint = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        imageBackground = BitmapFactory.decodeResource(getResources(), id);
        imageBackground = Bitmap.createScaledBitmap(imageBackground, getWidth(), getHeight(), true);

        canvas = new Canvas(imageBackground);
        listTsPaths.add(new TsPath(tsPath));
        mImageCache = new ImageCache(getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // canvas = new Canvas(imageBackground);
        canvas.drawBitmap(imageBackground, 0, 0, new Paint(Paint.DITHER_FLAG));
        log.d("log>>> " + bitmapPaint);
        canvas.drawPath(path, paint);

    }

    private void touchStart(float x, float y) {
        isUndo = false;
        // log.d("log>>> " + "touchStart");
        // log.d("log>>> " + "bitmapPaint:" + bitmapPaint);
        // path.reset();
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
        log.d("log>>> " + "touchUp");
        // log.d("log>>> " + "bitmapPaint:" + bitmapPaint);
        path.lineTo(mX, mY);
        listPaths.add(path);
        canvas.drawPath(path, paint);
        path.reset();

        if (listener != null) {
            listener.setOnUpdate(imageBackground);
        }

        new LazyImageSetTask2(imageBackground).execute();
        // commit the path to our offscreen

    }

    public void undo() {

        // log.d("log>>> " + "undo: listTsPaths:" + listTsPaths.size());
        // if (listPaths.size() == 0) {
        // Toast.makeText(getContext(), "Stack empty",
        // Toast.LENGTH_SHORT).show();
        // return;
        // }

        // canvas.drawBitmap(bgBitmapPaint, 0, 0, new Paint(Paint.DITHER_FLAG));

        if (stackBitmaps.size() == 0) {
            Toast.makeText(getContext(), "Stack empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = getmImageCache().get(stackBitmaps.peek());
        log.d("log>>> " + " ");
        log.d("log>>> " + "undo remove key:" + bitmap.toString());
        stackBitmaps.pop();
        log.d("log>>> " + "undo end remove key:" + bitmap.toString());
        log.d("log>>> " + " ");
        bitmap.recycle();

        isUndo = true;
        // listPathsRedo.add(listPaths.get(listPaths.size() - 1));
        // listPaths.remove(listPaths.size() - 1);

        if (stackBitmaps.size() > 0) {
            log.d("log>>> " + "restore bitmap " + stackBitmaps.peek());
            if (isUndo) {
                imageBackground = mImageCache.get(stackBitmaps.peek());

                if (listener != null) {
                    listener.setOnUpdate(imageBackground);
                }
            }

        } else {
            log.d("log>>> " + "no bitmap in stackBitmap");
        }

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

    class LazyImageSetTask2 extends AsyncTask<Void, Void, Bitmap> {
        Bitmap bitmap;

        public LazyImageSetTask2(Bitmap src) {

            this.bitmap = Bitmap.createBitmap(src);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            stackBitmaps.add(bitmap.toString());
            synchronized (mImageCache) {
                mImageCache.put(bitmap.toString(), bitmap);
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            log.d("log>>> " + "--- onPostExecute current list ---");
            int i = 0;
            for (String key : stackBitmaps) {
                log.d("log>>> " + i++ + "-" + key + ";lbitmap:" + mImageCache.get(key));
            }

            // log.d("log>>> " + " ");
            // log.d("log>>> " + "--- onPostExecute draw again ---");
            // log.d("log>>> " + "get " + stackBitmaps.peek());
            // bitmapPaint = mImageCache.get(stackBitmaps.peek());

            // if (listener != null) {
            // listener.setOnUpdate(bitmapPaint);
            // }
            // invalidate();
            // log.d("log>>> " + "--- onPostExecute end draw again ---");
            // log.d("log>>> " + " ");

        }
    }

    public ITsCustomListener listener;

    public interface ITsCustomListener {
        public void setOnUpdate(Bitmap bitmap);
    }

    public void setOnTsListener(ITsCustomListener listener) {
        this.listener = listener;
    }

}
