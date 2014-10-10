package com.example.tapcopaint.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.tapcopaint.R;

public class TsSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    int id = -1;

    public void setId(int id) {
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

                    if (imageBackground != null) {

                        canvas.drawBitmap(imageBackground, 0, 0, new Paint(Paint.DITHER_FLAG));
                    }
                    // canvas.drawColor(0, PorterDuff.Mode.CLEAR);

                    commandManager.executeAll(canvas);
                } finally {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);

                }
            }

        }

    }

    public void addDrawingPath(DrawingPath drawingPath) {
        commandManager.addCommand(drawingPath);
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
        commandManager.clear();
    }

    public boolean hasMoreUndo() {
        return commandManager.hasMoreRedo();
    }

    Bitmap imageBackground;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (id != -1) {

            imageBackground = BitmapFactory.decodeResource(getResources(), id);
            imageBackground = Bitmap.createScaledBitmap(imageBackground, getWidth(), getHeight(), true);
        }
        thread.setRunning(true);
        thread.start();
    }

    public void setRunning(boolean isRun) {
        this._run = isRun;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        // TODO Auto-generated method stub
        // boolean retry = true;
        // thread.setRunning(false);
        // while (retry) {
        // try {
        // thread.join();
        // retry = false;
        // } catch (InterruptedException e) {
        // // we will try it again and again...
        // }
        // }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

}
