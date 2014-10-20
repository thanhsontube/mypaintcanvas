package com.example.tapcopaint.graphic2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.example.tapcopaint.utils.FilterLog;
import com.example.tapcopaint.zoom.ZoomVariables;

public class MyRender extends SurfaceRenderer {
    private static final String TAG = "MyRender";

    FilterLog log = new FilterLog(TAG);

    int id;

    protected MyRender(Context c) {
        super(c);
    }

    public MyRender(Context c, int id) {
        this(c);
        this.id = id;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    @Override
    public void suspend(boolean b) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void drawBase() {
        // TODO Auto-generated method stub
        drawOriginalImage(context_, id, viewPort_);

    }

    @Override
    protected void drawLayer() {
        // TODO Auto-generated method stub
        drawLayer(context_, viewPort_);

    }

    @Override
    protected void drawFinal() {
        // TODO Auto-generated method stub

    }

    private final Rect canvasRect_ = new Rect(0, 0, 0, 0);
    private final Rect imageRect_ = new Rect(0, 0, 0, 0);
    private final Point dstSize_ = new Point();
    private int canvasW, canvasH;
    private int backgroundW, backgroundH;
    BitmapFactory.Options options;

    /**
     * draw imagebackground
     */
    int i = 1;
    public static final int VALUE_LOG = 5;
    Bitmap bitmap = null;
    Matrix matrix;
    private Matrix prevMatrix;
    Paint paint;
    private float[] m;
    private ScaleType mScaleType;
    private AQuery aQuery;
    private boolean onDrawReady;
    private ZoomVariables delayedZoomVariables;

    // bitmap = Bitmap.createBitmap(viewPort.getPhysicalWidth(), viewPort.getPhysicalHeight(),
    // Bitmap.Config.ARGB_8888);

    public void drawOriginalImage(Context context, int id, ViewPort viewPort) {
        aQuery = new AQuery(context);
        i++;
        canvasW = viewPort.getPhysicalWidth();
        canvasH = viewPort.getPhysicalHeight();

        if (i == VALUE_LOG) {
            log.d("log>>> " + "canvasW :" + canvasW + ";canvasH:" + canvasH);
        }
        canvasRect_.set(0, 0, canvasW, canvasH);
        paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        options = new BitmapFactory.Options();
        m = new float[9];

        // mScaleType = ScaleType.CENTER_INSIDE;
        mScaleType = ScaleType.FIT_CENTER;
        // mScaleType = ScaleType.MATRIX;

        bitmap = aQuery.getCachedImage(id);
        if (bitmap == null) {
            if (i == VALUE_LOG) {
                log.e("log>>> " + "drawOriginalImage Error: decode bitmap error id:" + id);
            }
            return;
        }

        backgroundW = bitmap.getWidth();
        backgroundH = bitmap.getHeight();
        if (i == VALUE_LOG) {
            log.d("log>>> " + "backgroundW :" + backgroundW + ";backgroundH:" + backgroundH);
        }

        matrix = new Matrix();
        prevMatrix = new Matrix();

        drawMatrix(context, id, viewPort);
    }

    public void drawMatrix(Context context, int id, ViewPort viewPort) {
        Canvas canvas = new Canvas(viewPort_.bitmap_);

        matrix.getValues(m);
        prevMatrix.setValues(m);

        // scale image for view

        float scaleX = (float) canvasW / backgroundW;
        float scaleY = (float) canvasH / backgroundH;
        switch (mScaleType) {
        case CENTER:
            scaleX = scaleY = 1;
            break;

        case CENTER_CROP:
            scaleX = scaleY = Math.max(scaleX, scaleY);
            break;

        case CENTER_INSIDE:
            scaleX = scaleY = Math.min(1, Math.min(scaleX, scaleY));

        case FIT_CENTER:
            scaleX = scaleY = Math.min(scaleX, scaleY);
            break;

        case FIT_XY:
            break;
        default:
            break;
        }

        // matrix.setTranslate(-10, -40);
        matrix.setScale(scaleX, scaleY);

        // center the image
        float paddingX = canvasW - (scaleX * backgroundW);
        float paddingY = canvasH - (scaleY * backgroundH);

        matrix.postTranslate(paddingX / 2, paddingY / 2);
        canvas.drawBitmap(bitmap, matrix, paint);
        onDrawReady = true;

    }
    
    public void setZoom (float scale, float focusX, float focusY, ScaleType scaleType) {
        if (!onDrawReady) {
            delayedZoomVariables = new ZoomVariables(scale, focusX, focusY, scaleType);
            return;
        }
        
        if (scaleType != mScaleType) {
            this.mScaleType = scaleType;
        }
        
    }

    public void drawCurrent(Context context, int id, ViewPort viewPort) {

        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);

        i++;
        if (i == viewPort.getPhysicalWidth() - 1) {
            i = 0;
        }

        if (bitmap != null) {
            Canvas canvas = new Canvas(viewPort_.bitmap_);

            // get w,h of bitmap

            if (i == 3) {

                log.d("log>>> " + "!= null bitmap w:" + bitmap.getWidth() + ";h:" + bitmap.getHeight());
            }
            canvas.drawBitmap(bitmap, canvasRect_, imageRect_, paint);
            return;

        }

        // bitmap = aQuery.getCachedImage(id);
        // options.inJustDecodeBounds = true;
        // BitmapFactory.decodeResource(context_.getResources(), id, options);
        //
        // int w = options.outWidth;
        // int h = options.outHeight;
        // log.d("log>>> " + "opt w:" + w);
        options.inJustDecodeBounds = false;
        Bitmap bitmapt = BitmapFactory.decodeResource(context_.getResources(), id, options);

        int w = bitmapt.getWidth();
        int h = bitmapt.getHeight();
        log.d("log>>> " + "opt w:" + w);

        // int inSampleSize = 1;
        if (w > canvasW) {
            options.inJustDecodeBounds = false;

            int newW = canvasW;
            int newH = newW * h / w;
            log.d("log>>> " + "newW:" + newW + ";newH:" + newH);
            options.inJustDecodeBounds = false;
            // Bitmap t = BitmapFactory.decodeResource(context_.getResources(), id, options);
            bitmap = Bitmap.createScaledBitmap(bitmapt, newW, newH, false);
            // return;

        } else {
            options.inJustDecodeBounds = false;
            bitmap = Bitmap.createBitmap(bitmapt);
            log.d("log>>> " + "W1:" + bitmap.getWidth() + ";H2:" + bitmap.getHeight());
            // return;
        }
        bitmapt.recycle();
        imageRect_.set(0, 0, bitmap.getWidth(), bitmap.getHeight());

        // if (w > canvasW || h > canvasH) {
        // final int halfHeight = h / 2;
        // final int halfWidth = w / 2;
        //
        // // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // // height and width larger than the requested height and width.
        // while ((halfHeight / inSampleSize) > canvasH && (halfWidth / inSampleSize) > canvasW) {
        // inSampleSize *= 2;
        // }
        // }

        // if (i == 3) {

        // log.d("log>>> " + "befor bitmap info W:" + options.outWidth + ";H:" + options.outHeight);
        // log.d("log>>> " + "befor Viewport info W:" + viewPort.getPhysicalWidth() + ";H:" +
        // viewPort.getPhysicalHeight());
        // }
        // options.inSampleSize = inSampleSize;
        // BitmapFactory.decodeResource(context_.getResources(), id, options);
        // // if (i == 3) {
        //
        // log.d("log>>> " + "after bitmap info W:" + options.outWidth + ";H:" + options.outHeight);
        // log.d("log>>> " + "after Viewport info W:" + viewPort.getPhysicalWidth() + ";H:" +
        // viewPort.getPhysicalHeight());
        // }
        // options.inJustDecodeBounds = false;
        // bitmap = BitmapFactory.decodeResource(context_.getResources(), id, options);
        // Canvas canvas = new Canvas(viewPort_.bitmap_);

        // get w,h of bitmap
        // imageRect_.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        // log.d("log>>> " + "!= null bitmap w:" + bitmap.getWidth());
        // canvas.drawBitmap(bitmap, canvasRect_, imageRect_, paint);

        // if (i < 4) {
        //
        // log.d("log>>> " + "after bitmap info W:" + options.outWidth + ";H:" + options.outHeight);
        // log.d("log>>> " + "after Viewport info W:" + viewPort.getPhysicalWidth() + ";H:"
        // + viewPort.getPhysicalHeight());
        // }
        // decode again bitmap

        // imageRect_.set(0, 0, options.outWidth, options.outHeight);
        // canvasRect_.set(0, 0, viewPort.getPhysicalWidth(), viewPort.getPhysicalHeight());
        // Canvas canvas = new Canvas(viewPort.bitmap_);
        // Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
        // canvas.drawBitmap(bitmap, canvasRect_, imageRect_, null);

    }

    /**
     * draw layer
     * 
     */
    public void drawLayer(Context context, ViewPort viewPort) {
    }

}
