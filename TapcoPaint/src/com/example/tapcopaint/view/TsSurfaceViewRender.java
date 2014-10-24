package com.example.tapcopaint.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Scroller;

import com.example.tapcopaint.R;
import com.example.tapcopaint.graphic2.BitmapSurfaceRenderer;
import com.example.tapcopaint.graphic2.SurfaceRenderer;
import com.example.tapcopaint.utils.FilterLog;

public class TsSurfaceViewRender extends SurfaceView implements SurfaceHolder.Callback, OnGestureListener {

    private static final String TAG = "TsSurfaceView";
    FilterLog log = new FilterLog(TAG);
    int id = -1;
    Bitmap bitmapBackGround;
    Bitmap bitmapPaint;

    private Canvas mCanvas;

    private boolean mDrawing = false;
    private Context context;

    public void setId(int id) {
        this.id = id;
    }

    CommandManager commandManager;
    private Boolean _run;
    protected DrawThread thread;

    public TsSurfaceViewRender(Context context) {
        super(context);
        this.context = context;
    }

    public TsSurfaceViewRender(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setWillNotDraw(false);

        commandManager = new CommandManager();
        thread = new DrawThread(getHolder());
        initRender();
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
                if (mDrawing) {
                    try {
                        mCanvas = mSurfaceHolder.lockCanvas(null);
                        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                        commandManager.executeAll(mCanvas);
                    } finally {
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    }
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
        redrawSurface();
    }

    public void undo() {
        commandManager.undo();
        redrawSurface();
    }

    public void clear() {
        path = null;
        commandManager.clear();
        redrawSurface();
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
            thread = new DrawThread(getHolder());
            thread.start();
            redrawSurface();
        }

        this.thread_ = new GameSurfaceViewThread(holder);
        this.thread_.setName("drawThread");
        this.thread_.setRunning(true);
        this.thread_.start();
        this.renderer_.start();
        // this.touch_.start();
    }

    public void setRunning(boolean isRun) {
        this._run = isRun;
    }

    public void setDrawing(boolean isDrawing) {
        mDrawing = isDrawing;
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

        // this.touch_.stop();
        this.renderer_.stop();
        this.thread_.setRunning(false);
        // this.thread_.surfaceDestroyed();
        boolean retry2 = true;
        while (retry2) {
            try {
                this.thread_.join();
                retry2 = false;
            } catch (InterruptedException e) {
                // Repeat until success
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        this.renderer_.setViewSize(w, h);
    }

    private void redrawSurface() {
        mDrawing = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawing = false;
            }
        }, 300);
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

    public void scale(float x, float y) {
        commandManager.scale(x, y);
    }

    SurfaceRenderer renderer_;
    private TouchHandler touch_;
    private GestureDetector gesture_;
    private ScaleGestureDetector scaleGesture_;
    private long lastScaleTime_ = 0;
    private GameSurfaceViewThread thread_ = null;

    private void initRender() {
        this.touch_ = new TouchHandler(context);
        // Set SurfaceHolder callback
        getHolder().addCallback(this);
        // Initialize touch handlers
        this.gesture_ = new GestureDetector(context, this);
        this.scaleGesture_ = new ScaleGestureDetector(context, new ScaleListener());
        renderer_ = new BitmapSurfaceRenderer(context);
        InputStream iStream = getResources().openRawResource(R.drawable.pic2);
        try {
            ((BitmapSurfaceRenderer) renderer_).setBitmap(iStream);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        try {
            iStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        // Allow focus
        setFocusable(true);
    }

    /** The Rendering thread for the MicaSurfaceView */
    class GameSurfaceViewThread extends Thread {
        private final SurfaceHolder surfaceHolder_;
        private boolean running_ = false;

        public GameSurfaceViewThread(SurfaceHolder surfaceHolder) {
            setName("GameSurfaceViewThread");
            this.surfaceHolder_ = surfaceHolder;
        }

        public void setRunning(boolean b) {
            this.running_ = b;
        }

        @Override
        public void run() {
            Canvas canvas;
            // Handle issue 58385 in Android 4.3
            int delayMillis = 5;
            if (Build.VERSION.SDK_INT == 18)
                delayMillis = 475;
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                // NOOP
            }
            // This is the rendering loop; it goes until asked to quit.
            while (this.running_) {
                // CPU timeout - help keep things cool
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    // NOOP
                }
                // Render Graphics
                canvas = null;
                try {
                    canvas = this.surfaceHolder_.lockCanvas();
                    if (canvas != null) {
                        synchronized (this.surfaceHolder_) {
                            TsSurfaceViewRender.this.renderer_.draw(canvas);
                        }
                    }
                } finally {
                    if (canvas != null) {
                        this.surfaceHolder_.unlockCanvasAndPost(canvas);
                    }
                }
            }

        }

    }

    /**
     * Scale Listener Used to change the scale factor on the GameSurfaceRenderer
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private final PointF screenFocus = new PointF();

        public ScaleListener() {
            super();
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            if (scaleFactor != 0f && scaleFactor != 1.0f) {
                scaleFactor = 1 / scaleFactor;
                this.screenFocus.set(detector.getFocusX(), detector.getFocusY());
                TsSurfaceViewRender.this.renderer_.zoom(scaleFactor, this.screenFocus);
                invalidate();
            }
            TsSurfaceViewRender.this.lastScaleTime_ = System.currentTimeMillis();
            return true;
        }
    }

    enum TouchState {
        NO_TOUCH, IN_TOUCH, ON_FLING, IN_FLING
    }

    class TouchHandler {
        // Current Touch State
        TouchState state_ = TouchState.NO_TOUCH;
        // Point initially touched
        private final Point touchDown_ = new Point(0, 0);
        // View Center onTouchDown
        private final Point viewCenterAtDown_ = new Point(0, 0);
        // View Center onFling
        private final Point viewCenterAtFling_ = new Point();
        // View Center onFling
        private final Point viewSizeAtFling_ = new Point();
        // View Center onFling
        private Point backgroundSizeAtFling_ = new Point();
        // Scroller
        final Scroller scroller_;
        // Thread for handling
        TouchHandlerThread touchThread_;

        TouchHandler(Context context) {
            this.scroller_ = new Scroller(context);
        }

        void start() {
            this.touchThread_ = new TouchHandlerThread(this);
            this.touchThread_.setName("touchThread");
            this.touchThread_.start();
        }

        void stop() {
            this.touchThread_.isRunning_ = false;
            this.touchThread_.interrupt();
            boolean retry = true;
            while (retry) {
                try {
                    this.touchThread_.join();
                    retry = false;
                } catch (InterruptedException e) {
                    // Wait until done
                }
            }
            this.touchThread_ = null;
        }

        /** Handle a down event_ */
        boolean down(MotionEvent event) {
            // Cancel rendering suspension
            TsSurfaceViewRender.this.renderer_.suspend(false);
            // Get position
            synchronized (this) {
                this.state_ = TouchState.IN_TOUCH;
                this.touchDown_.x = (int) event.getX();
                this.touchDown_.y = (int) event.getY();
                Point p = new Point();
                TsSurfaceViewRender.this.renderer_.getViewPosition(p);
                this.viewCenterAtDown_.set(p.x, p.y);
                
            }
            return true;
        }

        /** Handle a move event_ */
        boolean move(MotionEvent event) {
            if (this.state_ == TouchState.IN_TOUCH) {
                float zoom = TsSurfaceViewRender.this.renderer_.getZoom();
                float deltaX = (event.getX() - this.touchDown_.x) * zoom;
                float deltaY = (event.getY() - this.touchDown_.y) * zoom;
                float newX = this.viewCenterAtDown_.x - deltaX;
                float newY = this.viewCenterAtDown_.y - deltaY;
                TsSurfaceViewRender.this.renderer_.setViewPosition((int) newX, (int) newY);
                TsSurfaceViewRender.this.invalidate();
                return true;
            }
            return false;
        }

        /** Handle an up event_ */
        boolean up(MotionEvent event) {
            if (this.state_ == TouchState.IN_TOUCH) {
                this.state_ = TouchState.NO_TOUCH;
            }
            return true;
        }

        /** Handle a cancel event_ */
        boolean cancel(MotionEvent event) {
            if (this.state_ == TouchState.IN_TOUCH) {
                this.state_ = TouchState.NO_TOUCH;
            }
            return true;
        }

        boolean fling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            TsSurfaceViewRender.this.renderer_.getViewPosition(this.viewCenterAtFling_);
            TsSurfaceViewRender.this.renderer_.getViewSize(this.viewSizeAtFling_);
            this.backgroundSizeAtFling_ = TsSurfaceViewRender.this.renderer_.getBackgroundSize();
            synchronized (this) {
                this.state_ = TouchState.ON_FLING;
                TsSurfaceViewRender.this.renderer_.suspend(true);
                this.scroller_.fling(this.viewCenterAtFling_.x, this.viewCenterAtFling_.y, (int) -velocityX,
                        (int) -velocityY, 0, this.backgroundSizeAtFling_.x - this.viewSizeAtFling_.x, 0,
                        this.backgroundSizeAtFling_.y - this.viewSizeAtFling_.y);
                this.touchThread_.interrupt();
            }
            return true;
        }

        /**
         * Touch Handler Thread
         */
        class TouchHandlerThread extends Thread {
            private final TouchHandler touchHandler_;
            boolean isRunning_ = false;

            TouchHandlerThread(TouchHandler touch) {
                this.touchHandler_ = touch;
                setName("touchThread");
            }

            @Override
            public void run() {
                this.isRunning_ = true;
                while (this.isRunning_) {
                    while ((this.touchHandler_.state_ != TouchState.ON_FLING)
                            && (this.touchHandler_.state_ != TouchState.IN_FLING)) {
                        try {
                            Thread.sleep(Integer.MAX_VALUE);
                        } catch (InterruptedException e) {
                            // NOOP
                        }
                        if (!this.isRunning_)
                            return;
                    }
                    synchronized (this.touchHandler_) {
                        if (this.touchHandler_.state_ == TouchState.ON_FLING) {
                            this.touchHandler_.state_ = TouchState.IN_FLING;
                        }
                    }
                    if (this.touchHandler_.state_ == TouchState.IN_FLING) {
                        TouchHandler.this.scroller_.computeScrollOffset();
                        TsSurfaceViewRender.this.renderer_.setViewPosition(TouchHandler.this.scroller_.getCurrX(),
                                TouchHandler.this.scroller_.getCurrY());
                        if (TouchHandler.this.scroller_.isFinished()) {
                            TsSurfaceViewRender.this.renderer_.suspend(false);
                            synchronized (this.touchHandler_) {
                                this.touchHandler_.state_ = TouchState.NO_TOUCH;
                                try {
                                    Thread.sleep(5);
                                } catch (InterruptedException e) {
                                    // NOOP
                                }
                            }
                        }
                    }
                }
            }

            public void setRunning(boolean b) {
                this.isRunning_ = b;
            }

        }

    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

}
