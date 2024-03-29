package com.example.tapcopaint.view;

import android.content.Context;
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
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Scroller;

import com.example.tapcopaint.graphic2.MyRender;
import com.example.tapcopaint.paint.TsPaint;
import com.example.tapcopaint.utils.FilterLog;

public class TsSurfaceRender extends SurfaceView implements SurfaceHolder.Callback, OnGestureListener {

    private static final String TAG = "TsSurfaceView";
    public static final Xfermode MODE_EARSE = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    public static final Xfermode MODE_OVER = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
    private static final float TOUCH_TOLERANCE = 4;
    FilterLog log = new FilterLog(TAG);
    private int id = -1;

    private MyRender renderer_;
    // private TouchHandler touch_;
    private GestureDetector gesture_;
    private ScaleGestureDetector scaleGesture_;
    private long lastScaleTime_ = 0;
    private GameSurfaceViewThread thread_ = null;
    private TsState tsState = TsState.TS_NONE;

    private DrawingPath currentDrawingPath;
    private Path path;
    private float mX, mY;
    private boolean isErase = false;
    private boolean isZooming = false;

    public void setImage(Context context, int id) {
        log.d("log>>> " + "setId");
        this.id = id;
        renderer_.setId(id);
    }

    public TsSurfaceRender(Context context) {
        super(context);
        initRender(context);
    }

    public TsSurfaceRender(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRender(context);
    }

    public TsSurfaceRender(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        initRender(context);
    }

    private void initRender(Context context) {
        log.d("log>>> " + "initRender");
        getHolder().addCallback(this);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setWillNotDraw(false);
        renderer_ = new MyRender(context);
        // this.touch_ = new TouchHandler(context);

        this.gesture_ = new GestureDetector(context, this);
        this.scaleGesture_ = new ScaleGestureDetector(context, new ScaleListener());

        path = new Path();
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        log.v("log>>> surfaceCreated:" + id);
        this.thread_ = new GameSurfaceViewThread(holder);
        this.thread_.setName("drawThread");
        this.thread_.setRunning(true);
        this.thread_.start();
        this.renderer_.start();
        // this.touch_.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

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

    // TODO touch event

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gesture_.onTouchEvent(event);
        this.scaleGesture_.onTouchEvent(event);

        int action = MotionEventCompat.getActionMasked(event);
        float x = event.getX();
        float y = event.getY();
        switch (action & MotionEventCompat.ACTION_MASK) {
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

        return true;

    }

    private void touchStart(float x, float y) {
        tsState = TsState.TS_INTOUCH;
        path.reset();
        path.moveTo(x, y);
        mX = x;
        mY = y;

        currentDrawingPath = new DrawingPath();
        currentDrawingPath.paint = TsPaint.getRedPaint();
        if (isErase) {
            currentDrawingPath.paint.setXfermode(MODE_EARSE);
        }
        currentDrawingPath.path = new Path();
        currentDrawingPath.path.moveTo(x, y);
        currentDrawingPath.path.lineTo(x, y);

    }

    private void touchMove(float x, float y) {
        tsState = TsState.TS_MOVE;
        long SCALE_MOVE_GUARD = 500;
        if (this.scaleGesture_.isInProgress() || System.currentTimeMillis() - this.lastScaleTime_ < SCALE_MOVE_GUARD) {
            tsState = TsState.TS_ZOOM;
            return;
        }
        if (renderer_.getZoom() != 1f) {
            if (isZooming) {
                log.d("log>>> " + "DRAG ZOOM");
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                dx = x - mX;
                dy = y - mY;
                mX = x;
                mY = y;
                renderer_.setTranslate(dx, dy);
            } else {
                log.d("log>>> " + "DRAW AFTER ZOOMING");
            }
            return;
        }

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            currentDrawingPath.path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp(float x, float y) {
        if (renderer_.getZoom() == 1) {

            path.lineTo(mX, mY);
            currentDrawingPath.path.lineTo(mX, mY);
            if (tsState != TsState.TS_ZOOM) {
                renderer_.addStorePath(currentDrawingPath);
            }
        }
        tsState = TsState.TS_NONE;
    }

    // TODO onGesturelistener

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
        // return this.touch_.fling(e1, e2, velocityX, velocityY);
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (tsState == TsState.TS_MOVE) {
            // draw a path at here
            // path.rQuadTo(e1.getX(), e1.getY(), e2.getX(), e2.getY());
            // pathDraw.path = path;
            // renderer_.addCurrentpath(pathDraw);
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        log.d("log>>> " + "onSingleTapUp");
        return false;
    }

    public enum TsState {
        TS_NONE, TS_INTOUCH, TS_ZOOM, TS_MOVE;
    }

    // TODO scale

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
            isZooming = true;
            if (scaleFactor == 0f || scaleFactor == 1.0f) {
                isZooming = false;
                // return true;
            }
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();
            this.screenFocus.set(focusX, focusY);
            renderer_.zoomCanvas(scaleFactor, focusX, focusY);
            invalidate();
            TsSurfaceRender.this.lastScaleTime_ = System.currentTimeMillis();
            return true;
        }
    }

    public enum TouchState {

        NO_TOUCH(0), IN_TOUCH(1), ON_FLING(2), IN_FLING(3);

        TouchState(int ni) {
            nativeInt = ni;
        }

        final int nativeInt;

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

        private DrawingPath tsPathStore;
        private DrawingPath tsPathDraw;

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

                renderer_.setPreviousPosition(new Point(touchDown_.x, touchDown_.y));

                Path path = new Path();
                path.moveTo(this.touchDown_.x, this.touchDown_.y);
                path.moveTo(this.touchDown_.x, this.touchDown_.y);
                Paint paint = TsPaint.getRedPaint();

                tsPathStore = new DrawingPath(path, paint);
                tsPathDraw = new DrawingPath(path, paint);
                // renderer_.addCurrentpath(tsPathDraw);

            }
            return true;
        }

        /** Handle a move event_ */
        boolean move(MotionEvent event) {
            if (this.state_ == TouchState.IN_TOUCH) {
                int index = MotionEventCompat.getActionIndex(event);

                int mX = renderer_.getPreviousPosition().x;
                int mY = renderer_.getPreviousPosition().y;
                float x = MotionEventCompat.getX(event, 0);
                float y = MotionEventCompat.getY(event, 0);

                float dx = Math.abs(event.getX() - renderer_.getPreviousPosition().x);
                float dy = Math.abs(event.getY() - renderer_.getPreviousPosition().y);
                tsPathDraw.path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);

                tsPathStore.path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                // renderer_.addCurrentpath(tsPathDraw);
                renderer_.setPreviousPosition(new Point(mX, mY));

                float zoom = TsSurfaceRender.this.renderer_.getZoom();
                if (zoom != 1.0f) {
                    log.d("log>>> " + "move IN_TOUCH");
                }
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

            renderer_.addStorePath(tsPathStore);
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
            log.d("log>>> " + "fling");
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

    // controller
    /**
     * undo
     */
    public void undo() {
        renderer_.getManager().undo();
    }

    public void redo() {
        renderer_.getManager().redo();
    }

    public void clear() {
        renderer_.getManager().clear();
    }

    int i = 1;

    public void configPaint() {
        if (i == 1) {
            renderer_.zoomCanvas(2.0f, 100, 500);
            i++;

        } else {
            renderer_.zoomCanvas(0.5f, 100, 500);
            i--;
        }

    }

    public void erasing() {
        isErase = !isErase;
    }

    public void zoom() {
        renderer_.zoomCanvas(2.0f, 100, 100);
        isZooming = !isZooming;
    }

}
