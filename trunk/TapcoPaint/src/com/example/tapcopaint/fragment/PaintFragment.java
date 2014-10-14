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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.tapcopaint.R;
import com.example.tapcopaint.base.BaseFragment;
import com.example.tapcopaint.base.BaseFragmentActivity.OnBackPressListener;
import com.example.tapcopaint.popup.ActionItem;
import com.example.tapcopaint.popup.QuickAction;
import com.example.tapcopaint.utils.FilterLog;
import com.example.tapcopaint.utils.PaintUtil;
import com.example.tapcopaint.view.DrawingPath;
import com.example.tapcopaint.view.TsSurfaceView;

public class PaintFragment extends BaseFragment implements OnClickListener,
		OnBackPressListener, OnTouchListener {

	private int id;
	private ImageView img, imgErase;
	private static final String TAG = "PaintFragment";
	private Paint mPaint;

	FilterLog log = new FilterLog(TAG);
	public Xfermode MODE_EARSE = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
	private TsSurfaceView tsSurfaceView;
	private boolean isErase;
	private View rootView;

	private static final float TOUCH_TOLERANCE = 4;

	private DrawingPath currentDrawingPath;
	private float mX, mY;
	private Path path;

	private SeekBar opacitySeekbar;
	private TextView opacityTxt;

	private SeekBar weightSeekbar;
	private View weightPointView;

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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = (ViewGroup) inflater.inflate(R.layout.paint_fragment,
				container, false);
		initBtn(rootView);
		initColorView(rootView);
		path = new Path();
		mPaint = resetPaint();
		return rootView;
	}

	private void initBtn(View rootView) {

		imgErase = (ImageView) rootView.findViewWithTag("erase");

		tsSurfaceView = (TsSurfaceView) rootView
				.findViewById(R.id.paint_tssurface);
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

	private void initColorView(View rootView) {
		opacityTxt = (TextView) rootView.findViewById(R.id.opacity_value);
		opacitySeekbar = (SeekBar) rootView.findViewById(R.id.opacity_sb);
		opacitySeekbar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar arg0) {

					}

					@Override
					public void onStartTrackingTouch(SeekBar arg0) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
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
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress < 1) {
					progress = 1;
					seekBar.setProgress(progress);
				}
				seekBar.setProgress(progress);
				mPaint = PaintUtil.setStrokeWidth(mPaint, progress);
				weightPointView
						.setLayoutParams(new RelativeLayout.LayoutParams(
								PaintUtil.getStrokeWidth(progress), PaintUtil
										.getStrokeWidth(progress)));
			}
		});
		weightPointView = (View) rootView
				.findViewById(R.id.weight_review_point);
	}

	public Paint resetPaint() {
		Paint mPaint = new Paint();
		// default
		mPaint.setDither(true);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		// option
		mPaint = PaintUtil.setStrokeWidth(mPaint, weightSeekbar.getProgress());
		mPaint = PaintUtil.setAlpha(mPaint, opacitySeekbar.getProgress());
		return mPaint;
	}

	@Override
	public void onClick(View v) {
		QuickAction quickAction = new QuickAction(v);
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
			ActionItem item4 = new ActionItem(getResources().getDrawable(
					R.drawable.ic_navigation_back));
			ActionItem item5 = new ActionItem(getResources().getDrawable(
					R.drawable.ic_navigation_forward));
			ActionItem item6 = new ActionItem(getResources().getDrawable(
					R.drawable.ic_edit));
			item4.setTitle("A");
			item5.setTitle("B");
			item6.setTitle("C");

			quickAction.addActionItem(item4);
			quickAction.addActionItem(item5);
			quickAction.addActionItem(item6);
			quickAction.show();
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
			ActionItem item1 = new ActionItem(getResources().getDrawable(
					R.drawable.ic_navigation_back));
			ActionItem item2 = new ActionItem(getResources().getDrawable(
					R.drawable.ic_navigation_forward));
			ActionItem item3 = new ActionItem(getResources().getDrawable(
					R.drawable.ic_edit));
			item1.setTitle("A");
			item2.setTitle("B");
			item3.setTitle("C");

			quickAction.addActionItem(item1);
			quickAction.addActionItem(item2);
			quickAction.addActionItem(item3);
			quickAction.show();
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onBackPress() {
		return false;
	}

	private void touchStart(float x, float y) {
		path.reset();
		path.moveTo(x, y);
		mX = x;
		mY = y;
		currentDrawingPath = new DrawingPath();
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
			path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			currentDrawingPath.path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
		tsSurfaceView.addDrawingPath(currentDrawingPath, false);
	}

	private void touchUp(float x, float y) {
		path.lineTo(mX, mY);
		tsSurfaceView.onMyDraw(path, mPaint);
		currentDrawingPath.path.lineTo(mX, mY);
		tsSurfaceView.addDrawingPath(currentDrawingPath, true);
		tsSurfaceView.clearTmpStack();

		mPaint = resetPaint();
		if (isErase) {
			mPaint.setXfermode(MODE_EARSE);
		} else {
			mPaint.setXfermode(null);
		}
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

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		log.d("log>>> " + "onHiddenChanged");
		if (isHidden()) {
			if (tsSurfaceView != null) {
				tsSurfaceView.stopThread();
			}
		}
	}

	/**
	 * fix bug home button
	 */
	@Override
	public void onPause() {
		super.onPause();
		log.d("log>>> " + "onPause");
		if (tsSurfaceView != null) {
			tsSurfaceView.stopThread();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		log.d("log>>> " + "onResume");
		if (tsSurfaceView == null) {
			tsSurfaceView = (TsSurfaceView) rootView
					.findViewById(R.id.paint_tssurface);
		}
	}

}
