package com.example.tapcopaint.view;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Scroller;

import com.example.tapcopaint.R;
import com.example.tapcopaint.graphic2.BitmapSurfaceRenderer;
import com.example.tapcopaint.graphic2.MyRender;
import com.example.tapcopaint.graphic2.SurfaceRenderer;
import com.example.tapcopaint.utils.FilterLog;
import com.example.tapcopaint.utils.PaintUtil;

public class TsSurfaceRender extends SurfaceView implements SurfaceHolder.Callback, OnGestureListener {

    private static final String TAG = "TsSurfaceView";
    FilterLog log = new FilterLog(TAG);
    int id = -1;
    Bitmap bitmapBackGround;
    Bitmap bitmapPaint;

    private Canvas mCanvas;

    private boolean mDrawing = false;
    private Context context;

    SurfaceRenderer renderer_;

    private TouchHandler touch_;
    private GestureDetector gesture_;
    private ScaleGestureDetector scaleGesture_;
    private long lastScaleTime_ = 0;
    private GameSurfaceViewThread thread_ = null;

    public void setId(int id) {
        this.id = id;
        renderer_ = new MyRender(context, id);
    }

    CommandManager commandManager;
    private Boolean _run;

    public TsSurfaceRender(Context context) {
        super(context);
        getHolder().addCallback(this);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setWillNotDraw(false);
        this.context = context;
        this.touch_ = new TouchHandler(context);
        initRender();

    }

    public TsSurfaceRender(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setWillNotDraw(false);
        this.context = context;
        this.touch_ = new TouchHandler(context);

        initRender();

    }

    private void initRender() {

        // Set SurfaceHolder callback
        getHolder().addCallback(this);
        // Initialize touch handlers
        this.gesture_ = new GestureDetector(context, this);
        this.scaleGesture_ = new ScaleGestureDetector(context, new ScaleListener());

        InputStream iStream = getResources().openRawResource(R.drawable.pic2);
        // try {
        // ((BitmapSurfaceRenderer) renderer_).setBitmap(iStream);
        // } catch (IOException e) {
        // Log.e(TAG, e.getMessage());
        // e.printStackTrace();
        // }
        // try {
        // iStream.close();
        // } catch (IOException e) {
        // Log.e(TAG, e.getMessage());
        // e.printStackTrace();
        // }
        // Allow focus
        setFocusable(true);
        path = new Path();
        mPaint = resetPaint();
        commandManager = new CommandManager();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        log.v("log>>> surfaceCreated:" + id);

        this.thread_ = new GameSurfaceViewThread(holder);
        this.thread_.setName("drawThread");
        this.thread_.setRunning(true);
        this.thread_.start();
        this.renderer_.start();
        this.touch_.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        this.touch_.stop();
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
                            TsSurfaceRender.this.renderer_.draw(canvas);
                            // canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                            // commandManager.executeAll(canvas);
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
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            // TODO Auto-generated method stub\
            log.d("log>>> " + "onScaleBegin");
            return true;
            // return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            // TODO Auto-generated method stub
            log.d("log>>> " + "onScaleEnd");

        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            log.d("log>>> " + "onScale .............");
//            float scaleFactor = detector.getScaleFactor();
//            if (scaleFactor != 0f && scaleFactor != 1.0f) {
//                scaleFactor = 1 / scaleFactor;
//                this.screenFocus.set(detector.getFocusX(), detector.getFocusY());
//                TsSurfaceRender.this.renderer_.zoom(scaleFactor, this.screenFocus);
//                invalidate();
//            }
//            TsSurfaceRender.this.lastScaleTime_ = System.currentTimeMillis();
            
            ((MyRender)renderer_).scaleCanvas(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY(), true);
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
            TsSurfaceRender.this.renderer_.suspend(false);
            // Get position
            synchronized (this) {
                this.state_ = TouchState.IN_TOUCH;
                this.touchDown_.x = (int) event.getX();
                this.touchDown_.y = (int) event.getY();
                Point p = new Point();
                TsSurfaceRender.this.renderer_.getViewPosition(p);
                this.viewCenterAtDown_.set(p.x, p.y);
            }
            return true;
        }

        /** Handle a move event_ */
        boolean move(MotionEvent event) {
            if (this.state_ == TouchState.IN_TOUCH) {
                float zoom = TsSurfaceRender.this.renderer_.getZoom();
                float deltaX = (event.getX() - this.touchDown_.x) * zoom;
                float deltaY = (event.getY() - this.touchDown_.y) * zoom;
                float newX = this.viewCenterAtDown_.x - deltaX;
                float newY = this.viewCenterAtDown_.y - deltaY;
                TsSurfaceRender.this.renderer_.setViewPosition((int) newX, (int) newY);
                TsSurfaceRender.this.invalidate();
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
            TsSurfaceRender.this.renderer_.getViewPosition(this.viewCenterAtFling_);
            TsSurfaceRender.this.renderer_.getViewSize(this.viewSizeAtFling_);
            this.backgroundSizeAtFling_ = TsSurfaceRender.this.renderer_.getBackgroundSize();
            synchronized (this) {
                this.state_ = TouchState.ON_FLING;
                TsSurfaceRender.this.renderer_.suspend(true);
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
                        log.d("log>>> " + "TouchState.IN_FLING");
                        TouchHandler.this.scroller_.computeScrollOffset();
                        TsSurfaceRender.this.renderer_.setViewPosition(TouchHandler.this.scroller_.getCurrX(),
                                TouchHandler.this.scroller_.getCurrY());
                        if (TouchHandler.this.scroller_.isFinished()) {
                            TsSurfaceRender.this.renderer_.suspend(false);
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
        return this.touch_.fling(e1, e2, velocityX, velocityY);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean consumed = this.gesture_.onTouchEvent(event);
        if (consumed) {

            return true;
        }
        this.scaleGesture_.onTouchEvent(event);
        // Calculate actual event_ position in background view
        Point c = new Point();
        this.renderer_.getViewPosition(c);
        float s = this.renderer_.getZoom();
        int x = (int) (c.x + (event.getX() * s));
        int y = (int) (c.y + (event.getY() * s));
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            touchStart(x, y);
            return this.touch_.down(event);
        case MotionEvent.ACTION_MOVE:
            // log.d("log>>> " + "ACTION_MOVE");

            long SCALE_MOVE_GUARD = 500;
            if (this.scaleGesture_.isInProgress()
                    || System.currentTimeMillis() - this.lastScaleTime_ < SCALE_MOVE_GUARD) {

                break;
            }
            return this.touch_.move(event);
        case MotionEvent.ACTION_UP:
            touchUp(x, y);
            return this.touch_.up(event);
        case MotionEvent.ACTION_POINTER_UP:
            final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
            final int pointerId = event.getPointerId(pointerIndex);
            log.d("log>>> " + "ACTION_POINTER_UP:" + pointerIndex);
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            log.d("log>>> " + "ACTION_POINTER_DOWN:");
            break;
        case MotionEvent.ACTION_CANCEL:
            return this.touch_.cancel(event);
        default:
            break;
        }
        return super.onTouchEvent(event);
    }

    private ImageView img, imgErase;
    private Paint mPaint;

    public Xfermode MODE_EARSE = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private boolean isErase;
    private View rootView;
    View colorView;

    private static final float TOUCH_TOLERANCE = 4;

    private DrawingPath currentDrawingPath;
    private float mX, mY;
    private Path path;

    private void touchStart(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        mX = x;
        mY = y;

        if (isErase) {
            mPaint.setXfermode(MODE_EARSE);
        } else {
            mPaint.setXfermode(null);
        }

        currentDrawingPath = new DrawingPath();
        currentDrawingPath.paint = mPaint;
        currentDrawingPath.path = new Path();
        currentDrawingPath.path.moveTo(x, y);
        currentDrawingPath.path.lineTo(x, y);
        drawCurrent(currentDrawingPath);
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            currentDrawingPath.path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
        drawCurrent(currentDrawingPath);
    }

    private void touchUp(float x, float y) {
        path.lineTo(mX, mY);
        onMyDraw(path, mPaint);
        currentDrawingPath.path.lineTo(mX, mY);
        addDrawingPath(currentDrawingPath, true);
        drawCurrent(null);
        mPaint = resetPaint();
    }

    public Paint resetPaint() {
        Paint mPaint = new Paint();
        // default
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        return mPaint;
    }

    public void onMyDraw(Path path, Paint paint) {
        this.mPaint = paint;
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

}
