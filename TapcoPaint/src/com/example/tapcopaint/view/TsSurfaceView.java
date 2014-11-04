package com.example.tapcopaint.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.example.tapcopaint.utils.FilterLog;

public class TsSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "TsSurfaceView";
    FilterLog log = new FilterLog(TAG);
    int id = -1;
    Bitmap bgBitmap;
    Bitmap bitmapPaint;

    private Canvas mCanvas;

    float translateX = 1f;
    float translateY = 1f;

    private Paint historyPaint;

    // private boolean mDrawing = false;

    public void setId(int id) {
        this.id = id;
        // if (id != -1) {
        // bgBitmap = BitmapFactory.decodeResource(getResources(), id);
        // }
        thread = new DrawThread(getHolder());
    }

    CommandManager commandManager;
    private Boolean _run;
    protected DrawThread thread;

    public TsSurfaceView(Context context) {
        super(context);
        initRender(context);
    }

    public TsSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRender(context);
    }

    public TsSurfaceView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        initRender(context);
    }

    private void initRender(Context context) {
        log.d("log>>> " + "initRender");
        getHolder().addCallback(this);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setWillNotDraw(false);

        minScale = 1;
        maxScale = 3;
        superMinScale = SUPER_MIN_MULTIPLIER * minScale;
        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale;
        commandManager = new CommandManager();

        historyPaint = new Paint();
        historyPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
    }

    class DrawThread extends Thread {
        private SurfaceHolder mSurfaceHolder;

        public DrawThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;

        }

        public void setRunning(boolean run) {
            _run = run;
        }

        @Override
        public void run() {
            mCanvas = null;
            while (_run) {
                try {
                    mCanvas = mSurfaceHolder.lockCanvas();
                    fixTrans();

                    mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    mCanvas.translate(translateX, translateY);
                    mCanvas.scale(zoom, zoom);
                    commandManager.executeAll(mCanvas);
                } finally {
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                }
                // }
            }
        }
    }

    Path path;
    Paint paint;

    public void onMyDraw(Path path, Paint paint) {
        this.paint = paint;
        this.path = path;
    }

    public void addDrawingPath(DrawingPath drawingPath, boolean save) {
        commandManager.addCommand(drawingPath, save);
    }

    public void drawCurrent(DrawingPath drawingPath) {
        commandManager.drawCurrent(drawingPath);
    }

    public boolean hasMoreRedo() {
        return commandManager.hasMoreRedo();
    }

    public void redo() {
        commandManager.redo();
        // redrawSurface();
    }

    public void undo() {
        commandManager.undo();
        // redrawSurface();
    }

    public void clear() {
        path = null;
        commandManager.clear();
        // redrawSurface();
    }

    public void earse() {
        commandManager.earse();
    }

    public boolean hasMoreUndo() {
        return commandManager.hasMoreRedo();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        log.v("log>>> surfaceCreated:" + id);
        // if (bgBitmap != null) {
        // int bWidth = bgBitmap.getWidth();
        // int bHeight = bgBitmap.getHeight();
        // int vWidth = getWidth();
        // int vHeight = getHeight();
        // int newWidth = bWidth;
        // int newHeight = bHeight;
        //
        // float scale = Math.min((float) vWidth / bWidth, (float) vHeight / bHeight);
        // newWidth = (int) (bWidth * scale);
        // newHeight = (int) (bHeight * scale);
        //
        // FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(newWidth, newHeight);
        // this.setLayoutParams(params);
        // bgBitmap = Bitmap.createScaledBitmap(bgBitmap, newWidth, newHeight, true);
        // }
        bitmapPaint = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        thread.setRunning(true);
        if (!thread.isInterrupted()) {
            thread = new DrawThread(getHolder());
            thread.start();
            // redrawSurface();
        }
    }

    public void setRunning(boolean isRun) {
        this._run = isRun;
    }

    // public void setDrawing(boolean isDrawing) {
    // mDrawing = isDrawing;
    // }

    public void stopThread() {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                thread.interrupt();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void saveBitmap() {
        Bitmap bm = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas();
        c.setBitmap(bm);
        List<DrawingPath> currentStack = commandManager.getCurrentStack();
        for (DrawingPath dp : currentStack) {
            c.drawPath(dp.path, dp.paint);
        }
        try {
            this.setDrawingCacheEnabled(true);
            FileOutputStream fos = new FileOutputStream(
                    new File(Environment.getExternalStorageDirectory() + "/tmp.png"));
            bm.compress(CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private float zoom = 1f;

    private float minScale;
    private float maxScale;
    private float superMinScale;
    private float superMaxScale;
    private static final float SUPER_MIN_MULTIPLIER = .75f;
    private static final float SUPER_MAX_MULTIPLIER = 8.25f;

    public void scaleCanvas(double deltaScale, float focusX, float focusY, boolean isStretchImageToSuper) {
        // float lowerScale, upperScale;
        // if (isStretchImageToSuper) {
        // lowerScale = superMinScale;
        // upperScale = superMaxScale;
        // } else {
        // lowerScale = minScale;
        // upperScale = maxScale;
        // }
        //
        // float origScale = zoom;
        // zoom *= deltaScale;
        //
        // if (zoom > upperScale) {
        // zoom = upperScale;
        // deltaScale = zoom / origScale;
        // } else if (zoom < lowerScale) {
        // zoom = lowerScale;
        // deltaScale = zoom / origScale;
        // }

        zoom *= deltaScale;
        zoom = Math.max(1, Math.min(zoom, 5));

        translateX = focusX - focusX * zoom;
        translateY = focusY - focusY * zoom;

    }

    public void setTranslate(float mX, float mY) {
        translateX += mX;
        translateY += mY;
    }

    private void fixTrans() {

        int displayWidth = getWidth();
        int displayHeight = getHeight();

        // If translateX times -1 is lesser than zero, let's set it to zero.
        // This takes care of the left bound
        if ((translateX * -1) < 0) {
            translateX = 0;
        }

        // This is where we take care of the right bound. We compare translateX
        // times -1 to (scaleFactor - 1) * displayWidth.
        // If translateX is greater than that value, then we know that we've
        // gone over the bound. So we set the value of
        // translateX to (1 - scaleFactor) times the display width. Notice that
        // the terms are interchanged; it's the same
        // as doing -1 * (scaleFactor - 1) * displayWidth
        else if ((translateX * -1) > (zoom - 1) * displayWidth) {
            translateX = (1 - zoom) * displayWidth;
        }

        if (translateY * -1 < 0) {
            translateY = 0;
        }

        // We do the exact same thing for the bottom bound, except in this case
        // we use the height of the display
        else if ((translateY * -1) > (zoom - 1) * displayHeight) {
            translateY = (1 - zoom) * displayHeight;
        }
    }

    public float getZoom() {
        return zoom;
    }

}
