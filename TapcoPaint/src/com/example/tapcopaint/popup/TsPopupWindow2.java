package com.example.tapcopaint.popup;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.tapcopaint.R;
import com.example.tapcopaint.adapter.ColorAdapter;
import com.example.tapcopaint.utils.FilterLog;
import com.example.tapcopaint.utils.PaintUtil;

public class TsPopupWindow2 extends PopupWindows implements OnSeekBarChangeListener, OnItemClickListener {
    private static final String TAG = "TsPopupWindow";
    FilterLog log = new FilterLog(TAG);
    View anchor;
    PopupWindow popupWindow;
    // get width, height
    WindowManager windowManager;
    int screenW;
    int screenH;
    View rootView;
    Drawable drawable;
    Context context;
    LayoutInflater inflater;
    ImageView arrowDown;

    //
    private String color;
    private List<String> list = new ArrayList<String>();
    private ColorAdapter adapter;

    private SeekBar seekBarR, seekBarG, seekBarB;
    private TextView txtR, txtG, txtB;
    private ImageView imgPreview;
    IColorPickerListener listener;

    public interface IColorPickerListener {
        public void onIColorPickerDone(String color);
    }

    public void setOnListener(IColorPickerListener listener) {
        this.listener = listener;
    }

    public TsPopupWindow2(View anchor, String color) {
        super(anchor.getContext());
        this.color = color;
        this.anchor = anchor;
        this.context = anchor.getContext();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setRootViewId(R.layout.ts_popup_window);

        //
        // initLayout(rootView);
    }

    public void setRootViewId(int id) {
        mRootView = (ViewGroup) inflater.inflate(id, null);
        arrowDown = (ImageView) mRootView.findViewWithTag("down");

        // This was previously defined on show() method, moved here to prevent force close that occured
        // when tapping fastly on a view to show quickaction dialog.
        // Thanx to zammbi (github.com/zammbi)
        mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        setContentView(mRootView);
    }

    // public TsPopupWindow2(View anchor) {
    // log.d("log>>> " + "TsPopupWindow");
    // this.anchor = anchor;
    // this.context = anchor.getContext();
    // this.popupWindow = new PopupWindow(context);
    // this.popupWindow.setTouchInterceptor(new OnTouchListener() {
    //
    // @Override
    // public boolean onTouch(View v, MotionEvent event) {
    // if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
    // popupWindow.dismiss();
    // return true;
    // }
    // return false;
    // }
    // });
    // windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    // Display display = windowManager.getDefaultDisplay();
    // Point point = new Point();
    // display.getSize(point);
    // screenW = point.x;
    // screenH = point.y;
    // log.d("log>>> " + "screenW:" + screenW + ";screenH:" + screenH);
    // inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    // onCreate();
    // }

    protected void onCreate() {
        log.d("log>>> " + "onCreate");
        rootView = inflater.inflate(R.layout.ts_popup_window, null);
        rootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        log.d("log>>> " + "onCreate rootView W:" + rootView.getMeasuredWidth());
        arrowDown = (ImageView) rootView.findViewWithTag("down");

    }

    private int rootWidth = 0;

    public void show2() {
        preShow();

        int xPos, yPos, arrowPos;

        int[] location = new int[2];

        anchor.getLocationOnScreen(location);

        Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1]
                + anchor.getHeight());

        // mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        mRootView.measure(0,0);
        

        int rootHeight = mRootView.getMeasuredHeight();

        if (rootWidth == 0) {
            rootWidth = mRootView.getMeasuredWidth();
        }
        Log.e("", ">>>rootWidth: " + rootWidth + ";rootHeight:" + rootHeight);
        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

        LinearLayout ll2 = (LinearLayout) mRootView.findViewById(R.id.tracks);
        int[] location2 = new int[2];

        ll2.getLocationOnScreen(location2);

        log.d("log>>> " + "WW:" + ll2.getMeasuredWidth() + ";HH:" + ll2.getMeasuredHeight());
        Rect anchorRect2 = new Rect(location2[0], location2[1], location2[0] + ll2.getWidth(), location2[1]
                + ll2.getHeight());
        ll2.getLocationOnScreen(location2);
        log.d("log>>> " + "ll2 rect left:" + anchorRect2.left + ";right:" + anchorRect2.right + ";top:"
                + anchorRect2.top + ";bottom:" + anchorRect2.bottom);

//        ViewTreeObserver viewTreeObserver = mRootView.getViewTreeObserver();
//        if (viewTreeObserver.isAlive()) {
//            viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                  
//                    int a = mRootView.getWidth();
//                    int b = mRootView.getHeight();
//                    log.d("log>>> " + "a:" + a + ";b:" + b);
//                }
//            });
//        }

        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0);

    }

    /**
     * show TsPopupwindow to screen
     */

    public void show() {
        log.d("log>>> " + "show");
        preShow();
        int xPos, yPos;
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);

        log.d("log>>> " + "anchor x:" + location[0] + ";y:" + location[1]);

        Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1]
                + anchor.getHeight());

        log.d("log>>> " + "rect left:" + anchorRect.left + ";right:" + anchorRect.right + ";top:" + anchorRect.top
                + ";bottom:" + anchorRect.bottom);

        rootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        int rootWidth = rootView.getMeasuredWidth();
        int rootHeight = rootView.getMeasuredHeight();

        log.d("log>>> " + "rootWidth:" + rootWidth + ";rootHeight:" + rootHeight);

        // xPos = anchorRect.left + anchorRect.width() / 2;
        xPos = anchorRect.centerX();
        yPos = anchorRect.top - rootHeight - 12;

        if (rootWidth > screenW) {
            log.e("log>>> " + "error rootWidth:" + rootWidth);
            xPos = 16;

        } else {
            xPos = (screenW - rootWidth) / 2;
        }
        log.d("log>>> " + "final xPos:" + xPos + ";yPos:" + yPos);
        showArrow(anchorRect.centerX() - xPos);
        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }

    public void showArrow(int requestedX) {
        final int arrowWidth = arrowDown.getMeasuredWidth();
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) arrowDown.getLayoutParams();
        marginLayoutParams.leftMargin = requestedX - (arrowWidth / 2);
    }

    private void initLayout(View dialog) {
        imgPreview = (ImageView) dialog.findViewById(R.id.color_pick_img_preview);

        seekBarR = (SeekBar) dialog.findViewById(R.id.color_pick_seekbar_r);
        seekBarG = (SeekBar) dialog.findViewById(R.id.color_pick_seekbar_g);
        seekBarB = (SeekBar) dialog.findViewById(R.id.color_pick_seekbar_b);

        seekBarR.setOnSeekBarChangeListener(this);
        seekBarG.setOnSeekBarChangeListener(this);
        seekBarB.setOnSeekBarChangeListener(this);

        txtR = (TextView) dialog.findViewById(R.id.color_pick_txt_value_r);
        txtG = (TextView) dialog.findViewById(R.id.color_pick_txt_value_g);
        txtB = (TextView) dialog.findViewById(R.id.color_pick_txt_value_b);

        GridView lv = (GridView) dialog.findViewById(R.id.color_pick_grid);
        lv.setOnItemClickListener(this);
        adapter = new ColorAdapter(context, list);
        lv.setAdapter(adapter);
        updateView(color);
        initGrid();
    }

    private void updateView(String color) {
        imgPreview.setBackgroundColor(Color.parseColor(color));
        int[] intColor = PaintUtil.getRGB(color);
        if (intColor == null || intColor.length == 0) {
            return;
        }
        txtR.setText("R:" + intColor[0]);
        txtG.setText("G:" + intColor[1]);
        txtB.setText("B:" + intColor[2]);

        seekBarR.setProgress(intColor[0]);
        seekBarG.setProgress(intColor[1]);
        seekBarB.setProgress(intColor[2]);
    }

    void initGrid() {
        list.clear();
        String[] arrayColor = context.getResources().getStringArray(R.array.array_color_picker);
        for (String string : arrayColor) {
            list.add(string);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == seekBarR) {
            txtR.setText("R:" + progress);
        }
        if (seekBar == seekBarG) {
            txtG.setText("G:" + progress);
        }
        if (seekBar == seekBarB) {
            txtB.setText("B:" + progress);
        }

        String color = PaintUtil.getColor(seekBarR.getProgress(), seekBarG.getProgress(), seekBarB.getProgress());
        log.d("log>>> " + "color:" + color);
        imgPreview.setBackgroundColor(Color.parseColor(color));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        String color = list.get(arg2);
        updateView(color);

    }
}
