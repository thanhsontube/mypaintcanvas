package com.example.tapcopaint.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.tapcopaint.utils.FilterLog;

public class TsSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "TsSurfaceView";
    FilterLog log = new FilterLog(TAG);
    int id = -1;
    Bitmap bitmapBackGround;
    Bitmap bitmapPaint;

    public void setId(int id) {
        log.v(">>> setid:" + id);
        this.id = id;
    }

    CommandManager commandManager;
    private Boolean _run;
    protected DrawThread thread;

    public TsSurfaceView(Context context) {
        super(context);
    }

    public TsSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setWillNotDraw(false);

        commandManager = new CommandManager();
        thread = new DrawThread(getHolder());
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
            Canvas canvas = null;
            while (_run) {
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);

                    if (bitmapBackGround != null) {
                        // canvas.drawBitmap(bitmapBackGround, 0, 0, new Paint(Paint.DITHER_FLAG));
                        //
                        // canvas.drawColor(Color.GREEN);
//                        canvas.drawBitmap(bitmapPaint, 0, 0, new Paint(Paint.DITHER_FLAG));
                    }
                    canvas.drawColor(0, PorterDuff.Mode.CLEAR);
//                    if (path != null && paint != null) {
//
//                        canvas.drawPath(path, paint);
//                    }
                    commandManager.executeAll(canvas);
                } finally {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);

                }
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
        log.d("log>>> " + "addDrawingPath currentStack size:" + commandManager.currentStackLength());
        commandManager.addCommand(drawingPath, save);
    }

    public void clearTmpStack() {
        commandManager.clearTempStack();
    }

    public boolean hasMoreRedo() {
        return commandManager.hasMoreRedo();
    }

    public void redo() {
        commandManager.redo();
    }

    public void undo() {
        commandManager.undo();
    }

    public void clear() {
        path = null;
        commandManager.clear();
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
        if (id != -1) {

            bitmapBackGround = BitmapFactory.decodeResource(getResources(), id);
            bitmapBackGround = Bitmap.createScaledBitmap(bitmapBackGround, getWidth(), getHeight(), true);
        }

        bitmapPaint = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        thread.setRunning(true);
        if (!thread.isInterrupted()) {
            thread.start();

        }
    }

    public void setRunning(boolean isRun) {
        this._run = isRun;
    }

    public void stopThread() {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                thread.interrupt();
                retry = false;
            } catch (InterruptedException e) {
                // we will try it again and again...
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        // TODO Auto-generated method stub
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // we will try it again and again...
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

}