package com.example.tapcopaint.popup;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.example.tapcopaint.R;

public class PopupWindowForQuickAction {
	protected final View anchor;
	protected final PopupWindow window;
	private View rootView;
	private Drawable background = null;
	protected final WindowManager windowManager;

	public PopupWindowForQuickAction(View anchor) {
		this.anchor = anchor;
		this.window = new PopupWindow(anchor.getContext());

		window.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					PopupWindowForQuickAction.this.window.dismiss();
					
					return true;
				}
				
				return false;
			}
		});

		windowManager = (WindowManager) anchor.getContext().getSystemService(Context.WINDOW_SERVICE);
		onCreate();
	}

	protected void onCreate() {}

	protected void onShow() {}

	protected void preShow() {
		if (rootView == null) {
			throw new IllegalStateException("setContentView was not called with a view to display.");
		}
		
		onShow();

		if (background == null) {
			window.setBackgroundDrawable(new BitmapDrawable());
		} else {
			window.setBackgroundDrawable(background);
		}

		window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		window.setTouchable(true);
		window.setFocusable(true);
		window.setOutsideTouchable(true);

		window.setContentView(rootView);
	}

	public void setBackgroundDrawable(Drawable background) {
		this.background = background;
	}

	public void setContentView(View root) {
		this.rootView = root;
		
		window.setContentView(root);
	}

	public void setContentView(int layoutResID) {
		LayoutInflater inflator =
				(LayoutInflater) anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		setContentView(inflator.inflate(layoutResID, null));
	}

	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		window.setOnDismissListener(listener);
	}

	public void showDropDown() {
		showDropDown(0, 0);
	}

	public void showDropDown(int xOffset, int yOffset) {
		preShow();

		window.setAnimationStyle(R.style.Animations_PopDownMenu);
		//window.setAnimationStyle(R.style.Animations_PopDownMenu_Left);

		window.showAsDropDown(anchor, xOffset, yOffset);
	}

	public void showLikeQuickAction() {
		showLikeQuickAction(0, 0);
	}

	public void showLikeQuickAction(int xOffset, int yOffset) {
		preShow();

		window.setAnimationStyle(R.style.Animations_PopUpMenu_Center);

		int[] location = new int[2];
		anchor.getLocationOnScreen(location);

		Rect anchorRect =
				new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1]
					+ anchor.getHeight());

		rootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		rootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		int rootWidth 		= rootView.getMeasuredWidth();
		int rootHeight 		= rootView.getMeasuredHeight();

		int screenWidth 	= windowManager.getDefaultDisplay().getWidth();
		//int screenHeight 	= windowManager.getDefaultDisplay().getHeight();

		int xPos 			= ((screenWidth - rootWidth) / 2) + xOffset;
		int yPos	 		= anchorRect.top - rootHeight + yOffset;

		if (rootHeight > anchorRect.top) {
			yPos = anchorRect.bottom + yOffset;
			
			window.setAnimationStyle(R.style.Animations_PopDownMenu_Center);
		}

		window.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}
	
	public void dismiss() {
		window.dismiss();
	}
}
