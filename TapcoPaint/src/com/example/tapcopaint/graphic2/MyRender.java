package com.example.tapcopaint.graphic2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.FloatMath;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.example.tapcopaint.R;
import com.example.tapcopaint.paint.TsPaint;
import com.example.tapcopaint.utils.FilterLog;
import com.example.tapcopaint.view.CommandManager;
import com.example.tapcopaint.view.TsSurfaceRender;
import com.example.tapcopaint.zoom.ZoomVariables;

public class MyRender extends SurfaceRenderer {
    private static final String TAG = "MyRender";

    FilterLog log = new FilterLog(TAG);

    int id = -1;

    public static enum RenderMode {
        NONE, DRAW, ZOOM, FLING
    };

    private RenderMode mMode;

    public MyRender(Context c) {
        super(c);
    }

    public MyRender(Context c, int id) {
        this(c);
        this.id = id;
        commandManager = new CommandManager();
    }

    public void setId(int id) {
        log.d("log>>> " + "setId");
        this.id = id;
    }

    @Override
    public void start() {
        log.d("log>>> " + "start id:" + id);
        mMode = RenderMode.NONE;
        minScale = 1;
        maxScale = 3;
        superMinScale = SUPER_MIN_MULTIPLIER * minScale;
        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale;
        matrix = new Matrix();
        m = new float[9];

    }

    @Override
    public void stop() {

    }

    @Override
    public void suspend(boolean b) {

    }

    @Override
    protected void drawBase() {
        if (id == -1) {
            log.d("log>>> " + "id = -1");
            return;
        }
        // drawOriginalImage(context_, id, viewPort_);

    }

    @Override
    protected void drawLayer() {
        drawPath();
    }

    @Override
    protected void drawFinal() {
        // drawOriginalImage(context_, id, viewPort_);
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
    private float normalizedScale = 1f;

    private float minScale;
    private float maxScale;
    private float superMinScale;
    private float superMaxScale;
    private static final float SUPER_MIN_MULTIPLIER = .75f;
    private static final float SUPER_MAX_MULTIPLIER = 1.25f;

    private float fScale = 0.5f;

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

        // center the image
        float paddingX = canvasW - (scaleX * backgroundW);
        float paddingY = canvasH - (scaleY * backgroundH);
        if (mMode != RenderMode.ZOOM) {

            matrix.setScale(scaleX, scaleY);
            matrix.postTranslate(paddingX / 2, paddingY / 2);
        }

        // canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        paint.setXfermode(TsSurfaceRender.MODE_OVER);

        canvas.drawBitmap(bitmap, matrix, paint);

        onDrawReady = true;

    }

    public void setZoom(float scale, float focusX, float focusY, ScaleType scaleType) {
        if (!onDrawReady) {
            delayedZoomVariables = new ZoomVariables(scale, focusX, focusY, scaleType);
            return;
        }

        if (scaleType != mScaleType) {
            this.mScaleType = scaleType;
        }

    }

    public void scaleCanvas(double deltaScale, float focusX, float focusY, boolean isStretchImageToSuper) {

        mMode = RenderMode.ZOOM;

        // reset zoom

        float lowerScale, upperScale;
        if (isStretchImageToSuper) {
            lowerScale = superMinScale;
            upperScale = superMaxScale;
        } else {
            lowerScale = minScale;
            upperScale = maxScale;
        }

        float origScale = normalizedScale;
        normalizedScale *= deltaScale;

        if (normalizedScale > upperScale) {
            normalizedScale = upperScale;
            deltaScale = normalizedScale / origScale;
        } else if (normalizedScale < lowerScale) {
            normalizedScale = lowerScale;
            deltaScale = normalizedScale / origScale;
        }

        matrix.postScale((float) deltaScale, (float) deltaScale, focusX, focusY);

        Canvas canvas = new Canvas(viewPort_.bitmap_);
        RectF f = new RectF();
        myPath.computeBounds(f, true);

        float a, b;
        a = (float) deltaScale;
        b = (float) deltaScale;
        log.d("log>>> " + "scale a:" + a + ";b:" + b + ";x:" + focusX + ";y:" + focusY);

        canvas.scale((float) deltaScale, (float) deltaScale, focusX, focusY);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(bitmap, matrix, paint);
        canvas.drawPath(myPath, resetPaint());

        // myPath.transform(matrix);

    }

    public void scaleCanvas2(double deltaScale, float focusX, float focusY, boolean isStretchImageToSuper) {

        mMode = RenderMode.ZOOM;

        // reset zoom

        float lowerScale, upperScale;
        if (isStretchImageToSuper) {
            lowerScale = superMinScale;
            upperScale = superMaxScale;
        } else {
            lowerScale = minScale;
            upperScale = maxScale;
        }

        float origScale = normalizedScale;
        normalizedScale *= deltaScale;

        if (normalizedScale > upperScale) {
            normalizedScale = upperScale;
            deltaScale = normalizedScale / origScale;
        } else if (normalizedScale < lowerScale) {
            normalizedScale = lowerScale;
            deltaScale = normalizedScale / origScale;
        }

        // matrix.postScale((float) deltaScale, (float) deltaScale, focusX, focusY);

        log.d("log>>> " + "focusX:" + focusX + ";focusY:" + focusY + ";deltaScale:" + deltaScale);
        //
        scaleX *= (float) deltaScale;
        scaleY *= (float) deltaScale;
        pointMidFinger.x = focusX;
        pointMidFinger.y = focusY;

        // move center

        newW = canvasW * scaleX;
        newH = canvasH * scaleY;

        float tx = newW / 2;
        float ty = newH / 2;

        translateX = tx - pointMid.x;
        translateY = ty - pointMid.y;

        // newW = canvasW * scaleX;
        // newH = canvasH * scaleY;
        //
        // float tx = focusX * scaleX;
        // float ty = focusY * scaleY;
        //
        // translateX = tx - focusX;
        // translateY = ty - focusY;

        // matrix.getValues(m);
        // float transX = m[Matrix.MTRANS_X];
        // float transY = m[Matrix.MTRANS_Y];
        //
        // float fixTransX = getFixTrans(transX, canvasW, backgroundW);
        // float fixTransY = getFixTrans(transY, canvasH, backgroundH);
        //
        // if (fixTransX != 0 || fixTransY != 0) {
        // translateX = fixTransX;
        // translateY = fixTransY;
        // }

        float[] fa = getAbsolutePosition(pointMid.x, pointMid.y, focusX, focusY);

        screenSize = new PointF(viewPort_.bitmap_.getWidth(), this.viewPort_.bitmap_.getHeight());
        sceneSize = new PointF(getBackgroundSize());
        // sceneSize = new PointF(fa[0], fa[1]);

        sceneSize.x = getAbsolutePosition(pointMid.x, fa[0], scaleX);
        sceneSize.y = getAbsolutePosition(pointMid.y, fa[1], scaleY);

        // translateX = screenSize.x / 2;
        // translateY = screenSize.y / 2;

        float screenWidthToHeight = screenSize.x / screenSize.y;
        float screenHeightToWidth = screenSize.y / screenSize.x;

        RectF w1 = new RectF(this.viewPort_.window);
        RectF w2 = new RectF();
        PointF sceneFocus = new PointF(w1.left + (focusX / screenSize.x) * w1.width(), w1.top + (focusY / screenSize.y)
                * w1.height());

        float w2Width = viewPort_.getPhysicalWidth() * scaleX;
        float w2Height = w2Width * screenHeightToWidth;

        w2.left = sceneFocus.x - ((focusX / screenSize.x) * w2Width);
        w2.top = sceneFocus.y - ((focusY / screenSize.y) * w2Height);
        if (w2.left < 0)
            w2.left = 0;
        if (w2.top < 0)
            w2.top = 0;
        w2.right = w2.left + w2Width;
        w2.bottom = w2.top + w2Height;
        if (w2.right > sceneSize.x) {
            w2.right = sceneSize.x;
            w2.left = w2.right - w2Width;
        }
        if (w2.bottom > sceneSize.y) {
            w2.bottom = sceneSize.y;
            w2.top = w2.bottom - w2Height;
        }

        // pointMidFinger.x = w2.left;
        // pointMidFinger.y = w2.top;

        // Point p1 = new Point();
        // viewPort_.getOrigin(p1);
        // translateX = p1.x;
        // translateY = p1.y;

        // midPoint(pointMidFinger, focusX, focusY);

        // final float x = (float) ((viewPort_.getPhysicalWidth() - deltaScale)/deltaScale);
        // final float y = (float) ((viewPort_.getPhysicalHeight() - deltaScale)/deltaScale);
        //
        // translateX = x - focusX + focusX; // canvas X
        // translateY = y - focusY + focusY; // canvas Y

        // translateX = focusX;
        // translateY = focusY;

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
        drawLayerFisrt(context, viewPort);
    }

    // draw path
    public CommandManager commandManager;
    Path myPath;

    public void drawLayerFisrt(Context context, ViewPort viewPort) {
        Canvas mCanvas = new Canvas(viewPort_.bitmap_);
        // mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        // commandManager.executeAll(mCanvas);

        Paint p = resetPaint();

        // mCanvas.drawLine(0, 0, 300, 300, p);

        myPath = new Path();
        myPath.moveTo(0, 0);
        myPath.lineTo(400, 500);
        mCanvas.drawPath(myPath, p);

    }

    public Paint resetPaint() {
        Paint mPaint = new Paint();
        // default
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        mPaint.setColor(Color.RED);
        // option

        return mPaint;
    }

    float translateX = 0f;
    float translateY = 0f;

    float scaleX = 1;
    float scaleY = 1;

    float mScaleFactor = 1;

    PointF pointMidFinger = new PointF();
    PointF pointMid = new PointF();

    PointF screenSize = new PointF();
    PointF sceneSize = new PointF();

    Point pA1 = new Point();

    float newW, newH;

    public void drawRect(ViewPort viewPort) {
        Canvas canvas = new Canvas(viewPort_.bitmap_);

        backgroundW = viewPort_.bitmap_.getWidth();
        backgroundH = viewPort_.bitmap_.getHeight();

        canvasW = viewPort_.getPhysicalWidth();
        canvasH = viewPort_.getPhysicalHeight();
        matrix.getValues(m);
        pointMid = new PointF(viewPort_.getPhysicalWidth() / 2, viewPort_.getPhysicalHeight() / 2);

        Point p = new Point();
        viewPort.getSize(p);

        // log.d("log>>> " + "Viewport X:" + p.x + ";y:" + p.y);

        RectF rectF = new RectF(100, 100, 300, 500);

        // canvas.drawColor(Color.WHITE);
        //
        // canvas.scale(scaleX, scaleY);
        //
        // canvas.translate(-translateX / scaleX, -translateY / scaleY);

        Paint pCircle = resetPaint();
        pCircle.setColor(Color.GREEN);

        float r = 100;

        // mid of rectangle

        canvas.drawPoint(200, 300, resetPaint());

        // mid of circle

        canvas.drawPoint(550, 300, resetPaint());

        canvas.drawCircle(canvasW / 2, canvasH / 2, r, pCircle);

        canvas.drawRect(rectF, resetPaint());

        // draw line
        Path p1 = new Path();
        p1.moveTo(200, 300);
        p1.lineTo(550, 300);
        Paint pPath = resetPaint();
        pPath.setStrokeWidth(5);
        pPath.setColor(Color.BLUE);

        canvas.drawPath(p1, pPath);

        // draw mid point
        canvas.drawCircle(pointMidFinger.x, pointMidFinger.y, 25, TsPaint.getBlackPaint());

        // screen size

        canvas.drawCircle(screenSize.x, screenSize.y, 25, TsPaint.getRedPaint());

        // scenesize
        canvas.drawCircle(sceneSize.x, sceneSize.y, 25, TsPaint.getGreenPaint());

        canvas.drawCircle(pointMid.x, pointMid.y, 25, TsPaint.getBluePaint());

        // a1
        pA1.x = 100;
        pA1.y = 100;
        canvas.drawCircle(pA1.x, pA1.y, 25, TsPaint.getRedPaint());

        // text
        float spaceX = (pointMid.x - pA1.x) / scaleX;
        float spaceY = (pointMid.y - pA1.y) / scaleY;

        float edgesX = screenSize.x - pointMid.x;

        float canvasX = viewPort_.getPhysicalWidth() - spaceX - edgesX;
        String text = "space x:" + spaceX + ";canvasX :" + canvasX;

        canvas.drawText(text, 0, 50, TsPaint.getRedPaint(1));

    }

    public float[] getAbsolutePosition(float Ax, float Ay, float mCenterScaleX, float mCenterScaleY) {

        Point p = new Point();
        viewPort_.getSize(p);

        float fromAxToBxInCanvasSpace = (mCenterScaleX - Ax) / mScaleFactor;
        float fromBxToCanvasEdge = p.x - pointMid.x;
        float x = p.x - fromAxToBxInCanvasSpace - fromBxToCanvasEdge;

        float fromAyToByInCanvasSpace = (mCenterScaleY - Ay) / mScaleFactor;
        float fromByToCanvasEdge = p.y - pointMid.y;
        float y = p.y - fromAyToByInCanvasSpace - fromByToCanvasEdge;

        return new float[] { x, y };
    }

    /** Determine the space between the first two fingers */
    public float spacing(float x, float y) {
        return FloatMath.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    public void midPoint(PointF point, float x, float y) {
        point.set(x / 2, y / 2);
    }

    public float getAbsolutePosition(float oldCenter, float newCenter, float mScaleFactor) {
        if (newCenter > oldCenter) {
            return oldCenter + ((newCenter - oldCenter) / mScaleFactor);
        } else {
            return oldCenter - ((oldCenter - newCenter) / mScaleFactor);
        }
    }

    private void fixTrans() {
        matrix.getValues(m);
        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];

        float fixTransX = getFixTrans(transX, canvasW, backgroundW);
        float fixTransY = getFixTrans(transY, canvasH, backgroundH);

        if (fixTransX != 0 || fixTransY != 0) {
            matrix.postTranslate(fixTransX, fixTransY);
        }
    }

    private float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans, maxTrans;

        if (contentSize <= viewSize) {
            minTrans = 0;
            maxTrans = viewSize - contentSize;

        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0;
        }

        if (trans < minTrans)
            return -trans + minTrans;
        if (trans > maxTrans)
            return -trans + maxTrans;
        return 0;
    }

    public void onMove(float x, float y) {
        synchronized (this) {

            translateX = -x / scaleX;
            translateY = -y / scaleY;
        }
    }

    public void drawPath() {
        Canvas canvas = new Canvas(viewPort_.bitmap_);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        manager.restoreAll(canvas);

        Bitmap bitmap = BitmapFactory.decodeResource(context_.getResources(), R.drawable.pic1);
        Paint paint = new Paint();
        paint.setXfermode(TsSurfaceRender.MODE_OVER);
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

}
