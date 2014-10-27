package com.example.tapcopaint.fragment;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.tapcopaint.R;
import com.example.tapcopaint.base.BaseFragment;
import com.example.tapcopaint.paint.TsPaint;
import com.example.tapcopaint.popup.TsPopupWindow;
import com.example.tapcopaint.popup.TsPopupWindow.IColorPickerListener;
import com.example.tapcopaint.utils.FilterLog;
import com.example.tapcopaint.utils.PaintUtil;
import com.example.tapcopaint.view.TsSurfaceRender;

public class TsRenderFragment extends BaseFragment implements OnClickListener {

    private TsSurfaceRender tsSurfaceRender;
    private int id;

    // private TsImageView img;
    private ImageView imgErase, imgZoom;
    private static final String TAG = "PaintFragment";
    private Paint mPaint;

    FilterLog log = new FilterLog(TAG);
    private boolean isErase;
    private boolean isZoom;
    View colorView;

    private SeekBar opacitySeekbar;
    private TextView opacityTxt;

    private SeekBar weightSeekbar;
    private View weightPointView;

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
        View rootView = (ViewGroup) inflater.inflate(R.layout.surface_render, container, false);
        tsSurfaceRender = (TsSurfaceRender) rootView.findViewById(R.id.paint_tssurfaceRender);
        tsSurfaceRender.setImage(getActivity(), id);
        mPaint = TsPaint.getRedPaint();
        initBtn(rootView);
        initColorView(rootView);
        return rootView;
    }

    private void initBtn(View rootView) {

        // gestureDetector = new GestureDetector(getActivity(), this);

        imgErase = (ImageView) rootView.findViewById(R.id.paint_img_earse);
        imgZoom = (ImageView) rootView.findViewById(R.id.paint_img_zoom);

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

        // colorview
        colorView = rootView.findViewById(R.id.color_review);
        colorView.setOnClickListener(this);
    }

    private void initColorView(View rootView) {
        opacityTxt = (TextView) rootView.findViewById(R.id.opacity_value);
        opacitySeekbar = (SeekBar) rootView.findViewById(R.id.opacity_sb);
        opacitySeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                opacityTxt.setText(progress + "%");
                mPaint = PaintUtil.setAlpha(mPaint, progress);
            }
        });

        weightSeekbar = (SeekBar) rootView.findViewById(R.id.weight_sb);
        weightSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPaint = PaintUtil.setStrokeWidth(mPaint, progress);
                weightPointView.setLayoutParams(new RelativeLayout.LayoutParams(PaintUtil.getStrokeWidth(progress),
                        PaintUtil.getStrokeWidth(progress)));
            }
        });
        weightPointView = (View) rootView.findViewById(R.id.weight_review_point);
        weightPointView.setLayoutParams(new RelativeLayout.LayoutParams(PaintUtil.getStrokeWidth(weightSeekbar
                .getProgress()), PaintUtil.getStrokeWidth(weightSeekbar.getProgress())));
        setWeightColorView(getColorView());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.paint_btn_cancel:
            break;
        case R.id.paint_btn_done:
            break;
        case R.id.paint_back:
            break;
        case R.id.paint_forward:
            break;
        case R.id.paint_delete:
            isZoom = false;
            isErase = false;
            if (isErase) {
                imgErase.setImageResource(R.drawable.ic_erase_red);
            } else {
                imgErase.setImageResource(R.drawable.ic_erase_blue);
            }
            break;
        case R.id.paint_edit:
            isZoom = false;
            break;
        case R.id.paint_erase:
            isZoom = false;
            isErase = !isErase;
            break;
        case R.id.paint_move:
            isZoom = !isZoom;
            break;
        case R.id.color_review:
            int i = 0;
            Drawable background = colorView.getBackground();
            if (background instanceof ColorDrawable) {

                i = ((ColorDrawable) background).getColor();
            }
            String color = null;
            if (i != 0) {
                color = PaintUtil.getColor(i);
                log.d("log>>> " + "color:" + color);
            }

            TsPopupWindow tsPopupWindow2 = new TsPopupWindow(v, color);
            tsPopupWindow2.setOnListener(colorPickerListener);
            tsPopupWindow2.show();

            break;

        default:
            break;
        }

        if (isErase) {
            imgErase.setImageResource(R.drawable.ic_erase_red);
        } else {
            imgErase.setImageResource(R.drawable.ic_erase_blue);
        }

        if (isZoom) {
            imgZoom.setImageResource(R.drawable.ic_zoom_red);
        } else {
            imgZoom.setImageResource(R.drawable.ic_zoom_blue);
        }

    }

    private int getColorView() {
        Drawable background = colorView.getBackground();
        if (background instanceof ColorDrawable) {
            return ((ColorDrawable) background).getColor();
        }
        return 0;
    }

    private void setWeightColorView(int intColor) {
        LayerDrawable bgDrawable = (LayerDrawable) weightPointView.getBackground();
        final GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.shape_circle);
        shape.setColor(intColor);
    }

    IColorPickerListener colorPickerListener = new IColorPickerListener() {

        @Override
        public void onIColorPickerDone(String color) {
            colorView.setBackgroundColor(Color.parseColor(color));
            setWeightColorView(getColorView());
            mPaint = TsPaint.getRedPaint();
        }
    };

}
