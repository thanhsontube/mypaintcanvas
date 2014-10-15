package com.example.tapcopaint.popup;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.example.tapcopaint.R;
import com.example.tapcopaint.utils.FilterLog;

public class TsPopupWindow {
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

    public TsPopupWindow(View anchor) {
        log.d("log>>> " + "TsPopupWindow");
        this.anchor = anchor;
        this.context = anchor.getContext();
        this.popupWindow = new PopupWindow(context);
        this.popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        screenW = point.x;
        screenH = point.y;
        log.d("log>>> " + "screenW:" + screenW + ";screenH:" + screenH);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        onCreate();
    }

    protected void onCreate() {
        log.d("log>>> " + "onCreate");
        rootView = inflater.inflate(R.layout.ts_popup_window, null);
        rootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        log.d("log>>> " + "onCreate rootView W:" + rootView.getMeasuredWidth());
        arrowDown = (ImageView) rootView.findViewWithTag("down");

    }

    int i = 0;

    private void preShow() {

        if (rootView == null) {
            throw new IllegalStateException("setContentView was not called with a view to display.");

        }
        if (drawable == null) {
            popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        } else {
            popupWindow.setBackgroundDrawable(drawable);
        }

        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Center);
        popupWindow.setContentView(rootView);
        log.d("log>>> " + "preShow rootView W:" + rootView.getMeasuredWidth());
        switch (i) {
        case 0:
            popupWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Left);

            break;
        case 1:
            popupWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Left);
            break;
        case 2:
            popupWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Right);
            break;
        case 3:
            popupWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Right);
            break;
        case 4:
            popupWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Center);
            break;
        case 5:
            popupWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Center);
            break;

        default:
            break;
        }
        i++;
        if (i == 6) {
            i = 0;
        }
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
}
