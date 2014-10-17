package com.example.tapcopaint.fragment;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.text.method.MovementMethod;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.tapcopaint.R;
import com.example.tapcopaint.base.BaseFragment;
import com.example.tapcopaint.base.BaseFragmentActivity.OnBackPressListener;
import com.example.tapcopaint.popup.TsPopupWindow;
import com.example.tapcopaint.popup.TsPopupWindow.IColorPickerListener;
import com.example.tapcopaint.trash.ActionItem;
import com.example.tapcopaint.trash.QuickAction2;
import com.example.tapcopaint.utils.FilterLog;
import com.example.tapcopaint.utils.PaintUtil;
import com.example.tapcopaint.view.DrawingPath;
import com.example.tapcopaint.view.TsSurfaceRender;
import com.example.tapcopaint.view.TsSurfaceView;
import com.example.tapcopaint.view.TsSurfaceViewRender;

public class TsRenderFragment extends BaseFragment {

    public static int KK = 0;

    private int id;
    private ImageView img, imgErase;
    private static final String TAG = "PaintFragment";
    private Paint mPaint;

    FilterLog log = new FilterLog(TAG);
    public Xfermode MODE_EARSE = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private TsSurfaceView tsSurfaceView;
    private TsSurfaceViewRender tsSurfaceViewRender;
    private boolean isErase;
    private View rootView;
    View colorView;

    private static final float TOUCH_TOLERANCE = 4;

    private DrawingPath currentDrawingPath;
    private float mX, mY;
    private Path path;

    private SeekBar opacitySeekbar;
    private TextView opacityTxt;

    private SeekBar weightSeekbar;
    private View weightPointView;

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private TsSurfaceRender tsSurfaceRender;

    @Override
    protected String generateTitle() {
        return "Paint";
    }

    public static TsRenderFragment newInstance(int id) {
        TsRenderFragment f = new TsRenderFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.surface_render, container, false);
        tsSurfaceRender = (TsSurfaceRender) rootView.findViewById(R.id.paint_tssurfaceRender);
        tsSurfaceRender.setId(id);
        return rootView;
    }

}
