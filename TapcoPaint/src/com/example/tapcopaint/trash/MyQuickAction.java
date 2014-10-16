package com.example.tapcopaint.trash;

import java.util.ArrayList;
import java.util.List;

import com.example.tapcopaint.R;
import com.example.tapcopaint.adapter.ColorAdapter;
import com.example.tapcopaint.dialog.ColorPickerDialog.IColorPickerListener;
import com.example.tapcopaint.utils.FilterLog;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MyQuickAction extends PopupWindowForQuickAction {

    public static final int ANIM_GROW_FROM_LEFT = 1;
    public static final int ANIM_GROW_FROM_RIGHT = 2;
    public static final int ANIM_GROW_FROM_CENTER = 3;
    public static final int ANIM_REFLECT = 4;
    public static final int ANIM_AUTO = 5;

    public static final int STYLE_BUTTON = 1;
    public static final int STYLE_LIST = 2;

    private View rootView;
    private ImageView mArrowUp;
    private ImageView mArrowDown;
    private Animation mTrackAnim;
    private LayoutInflater inflater;
    private Context context;

    private ViewGroup mTrack;
    // private ScrollView scroller;

    private int itemLayoutId;
    private int animStyle;

    private static final String TAG = "MyQuickAction";
    private String color;
    private List<String> list = new ArrayList<String>();
    private ColorAdapter adapter;

    private SeekBar seekBarR, seekBarG, seekBarB;
    private TextView txtR, txtG, txtB;
    private ImageView imgPreview;
    FilterLog log = new FilterLog(TAG);
    IColorPickerListener listener;

    public interface IColorPickerListener {
        public void onIColorPickerDone(String color);
    }

    public void setOnListener(IColorPickerListener listener) {
        this.listener = listener;
    }

    public MyQuickAction(View anchor) {
        super(anchor);
        context = anchor.getContext();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (itemLayoutId == -1) {
            itemLayoutId = R.layout.common_action_item_btn;
        }

        this.itemLayoutId = itemLayoutId;

        setLayoutId(R.layout.common_popup, 0);

        animStyle = ANIM_AUTO;

        setAnimTrack(R.anim.rail, new Interpolator() {
            public float getInterpolation(float t) {
                final float inner = (t * 1.55f) - 1.1f;

                return 1.2f - inner * inner;
            }
        });
    }

    public MyQuickAction(View anchor, String color) {
        this(anchor);
        this.color = color;
    }

    public void setLayoutId(int layoutId, int layoutStyle) {
        rootView = (ViewGroup) inflater.inflate(layoutId, null);
//        View view = rootView.findViewById(R.id.view_color_pick);

        mArrowDown = (ImageView) rootView.findViewById(R.id.arrow_down);
        mArrowDown.setAlpha(100);
        mArrowUp = (ImageView) rootView.findViewById(R.id.arrow_up);
        mArrowUp.setAlpha(100);

        setContentView(rootView);

        // mTrack = (ViewGroup) rootView.findViewById(R.id.tracks);

        // scroller = null;
    }

    public void setAnimTrack(int animId, Interpolator interpolator) {
        mTrackAnim = AnimationUtils.loadAnimation(anchor.getContext(), animId);
        if (interpolator != null)
            mTrackAnim.setInterpolator(interpolator);
    }

    public void show() {
        showListStyle();
    }

    private void showListStyle() {
        preShow();

        int xPos, yPos;

        int[] location = new int[2];

        anchor.getLocationOnScreen(location);

        Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1]
                + anchor.getHeight());

        // createActionList();

        rootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        rootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        int rootHeight = rootView.getMeasuredHeight();
        int rootWidth = rootView.getMeasuredWidth();

        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();

        // automatically get X coord of popup (top left)
        if ((anchorRect.left + rootWidth) > screenWidth) {
            xPos = anchorRect.left - (rootWidth - anchor.getWidth());
        } else {
            if (anchor.getWidth() > rootWidth) {
                xPos = anchorRect.centerX() - (rootWidth / 2);
            } else {
                xPos = anchorRect.left;
            }
        }

        int dyTop = anchorRect.top;
        int dyBottom = screenHeight - anchorRect.bottom;

        boolean isOnTop = (dyTop > dyBottom) ? true : false;

        if (isOnTop) {
            if (rootHeight > dyTop) {
                yPos = 15;
                // LayoutParams l = scroller.getLayoutParams();
                // l.height = dyTop - anchor.getHeight();
            } else {
                yPos = anchorRect.top - rootHeight;
            }
        } else {
            yPos = anchorRect.bottom;

            if (rootHeight > dyBottom) {
                // LayoutParams l = scroller.getLayoutParams();
                // l.height = dyBottom;
            }
        }

        showArrow(((isOnTop) ? R.id.arrow_down : R.id.arrow_up), anchorRect.centerX() - xPos);

        setAnimationStyle(screenWidth, anchorRect.centerX(), isOnTop);

        window.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }

    private void showArrow(int whichArrow, int requestedX) {
        final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp : mArrowDown;
        final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown : mArrowUp;

        final int arrowWidth = mArrowUp.getMeasuredWidth();

        showArrow.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow.getLayoutParams();

        param.leftMargin = requestedX - arrowWidth / 2;

        hideArrow.setVisibility(View.INVISIBLE);
    }

    private void setAnimationStyle(int screenWidth, int requestedX, boolean isOnTop) {
        int arrowPos = requestedX - mArrowUp.getMeasuredWidth() / 2;

        switch (animStyle) {
        case ANIM_GROW_FROM_LEFT:
            window.setAnimationStyle((isOnTop) ? R.style.Animations_PopUpMenu_Left
                    : R.style.Animations_PopDownMenu_Left);
            break;

        case ANIM_GROW_FROM_RIGHT:
            window.setAnimationStyle((isOnTop) ? R.style.Animations_PopUpMenu_Right
                    : R.style.Animations_PopDownMenu_Right);
            break;

        case ANIM_GROW_FROM_CENTER:
            window.setAnimationStyle((isOnTop) ? R.style.Animations_PopUpMenu_Center
                    : R.style.Animations_PopDownMenu_Center);
            break;

        case ANIM_AUTO:
            if (arrowPos <= screenWidth / 4) {
                window.setAnimationStyle((isOnTop) ? R.style.Animations_PopUpMenu_Left
                        : R.style.Animations_PopDownMenu_Left);
            } else if (arrowPos > screenWidth / 4 && arrowPos < 3 * (screenWidth / 4)) {
                window.setAnimationStyle((isOnTop) ? R.style.Animations_PopUpMenu_Center
                        : R.style.Animations_PopDownMenu_Center);
            } else {
                window.setAnimationStyle((isOnTop) ? R.style.Animations_PopDownMenu_Right
                        : R.style.Animations_PopDownMenu_Right);
            }

            break;
        }
    }

}
