package com.example.tapcopaint.fragment;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tapcopaint.R;
import com.example.tapcopaint.base.BaseFragment;
import com.example.tapcopaint.base.BaseFragmentActivity.OnBackPressListener;
import com.example.tapcopaint.utils.FilterLog;
import com.example.tapcopaint.view.DrawingPath;
import com.example.tapcopaint.view.TsSurfaceView;

public class PaintFragment extends BaseFragment implements OnClickListener, OnBackPressListener, OnTouchListener {

    private int id;
    private ImageView img, imgErase;
    private static final String TAG = "PaintFragment";
    private Paint mPaint;

    FilterLog log = new FilterLog(TAG);
    public Xfermode MODE_EARSE = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private TsSurfaceView tsSurfaceView;
    private boolean isErase;

    @Override
    protected String generateTitle() {
        return "Paint";
    }

    public static PaintFragment newInstance(int id) {
        PaintFragment f = new PaintFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt("id");
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (ViewGroup) inflater.inflate(R.layout.paint_fragment, container, false);
        initBtn(rootView);

        return rootView;
    }

    private void initBtn(View rootView) {

        imgErase = (ImageView) rootView.findViewWithTag("erase");

        tsSurfaceView = (TsSurfaceView) rootView.findViewById(R.id.paint_tssurface);
        tsSurfaceView.setOnTouchListener(this);

        img = (ImageView) rootView.findViewWithTag("image");
        img.setImageResource(id);

        View viewCancel = rootView.findViewById(R.id.paint_btn_cancel);
        viewCancel.setOnClickListener(this);

        View viewDone = rootView.findViewById(R.id.paint_btn_done);
        viewDone.setOnClickListener(this);

        View viewBack = rootView.findViewById(R.id.paint_back);
        viewBack.setOnClickListener(this);

        View viewForward = rootView.findViewById(R.id.paint_forward);
        viewForward.setOnClickListener(this);

        View viewDelete = rootView.findViewById(R.id.paint_delete);
        viewDelete.setOnClickListener(this);

        View viewEdit = rootView.findViewById(R.id.paint_edit);
        viewEdit.setOnClickListener(this);

        View viewErase = rootView.findViewById(R.id.paint_erase);
        viewErase.setOnClickListener(this);

        View viewMove = rootView.findViewById(R.id.paint_move);
        viewMove.setOnClickListener(this);
    }

    public Paint resetpaint() {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        return mPaint;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.paint_btn_cancel:
            onBackPress();
            break;
        case R.id.paint_btn_done:
            break;

        case R.id.paint_back:
            tsSurfaceView.undo();
            break;
        case R.id.paint_forward:
            tsSurfaceView.redo();
            break;
        case R.id.paint_delete:
            tsSurfaceView.clear();
            isErase = false;
            if (isErase) {
                imgErase.setImageResource(R.drawable.ic_erase_red);
            } else {
                imgErase.setImageResource(R.drawable.ic_erase_blue);
            }
            break;
        case R.id.paint_edit:
            break;
        case R.id.paint_erase:
            isErase = !isErase;
            if (isErase) {
                imgErase.setImageResource(R.drawable.ic_erase_red);
            } else {
                imgErase.setImageResource(R.drawable.ic_erase_blue);
            }
            break;
        case R.id.paint_move:
            break;
        default:
            break;
        }

    }

    @Override
    public boolean onBackPress() {
        return false;
    }

    private static final float TOUCH_TOLERANCE = 4;

    DrawingPath currentDrawingPath;
    float mX, mY;

    private void touchStart(float x, float y) {
        mX = x;
        mY = y;
        log.d("log>>> " + "touchStart");
        currentDrawingPath = new DrawingPath();
        mPaint = resetpaint();
        if (isErase) {
            mPaint.setXfermode(MODE_EARSE);
        } else {
            mPaint.setXfermode(null);
        }
        currentDrawingPath.paint = mPaint;
        currentDrawingPath.path = new Path();
        currentDrawingPath.path.moveTo(x, y);
        currentDrawingPath.path.lineTo(x, y);
        tsSurfaceView.addDrawingPath(currentDrawingPath, false);
    }

    @Override
    public void onDestroy() {
        if (tsSurfaceView != null) {
            tsSurfaceView.stopThread();
            tsSurfaceView.destroyDrawingCache();

        }
        super.onDestroy();
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            currentDrawingPath.path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
        tsSurfaceView.addDrawingPath(currentDrawingPath, false);
    }

    private void touchUp(float x, float y) {
        currentDrawingPath.path.lineTo(mX, mY);
        tsSurfaceView.addDrawingPath(currentDrawingPath, true);
        tsSurfaceView.clearTmpStack();
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
        return true;

    }

}
