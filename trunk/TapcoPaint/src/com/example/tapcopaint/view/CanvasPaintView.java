package com.example.tapcopaint.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class CanvasPaintView extends View implements OnTouchListener {

	private static final float TOUCH_TOLERANCE = 4;
	private List<Canvas> listTsPaths = new ArrayList<Canvas>();
	private List<Canvas> listTsPathsRedo = new ArrayList<Canvas>();

	private Bitmap mImageBackground;
	private Bitmap mBitmapPaint;

	private float mX, mY;
	private Paint mPaint;
	private int id;
	private Path mPath;
//	private TsPath mTsPath;
	private Canvas mCanvas;

	private boolean isUndo;

	public CanvasPaintView(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setOnTouchListener(this);
		mPath = new Path();
//		mTsPath = new TsPath();
		mCanvas = new Canvas();
	}

	public CanvasPaintView(Context context, Paint paint, int id) {
		this(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setOnTouchListener(this);
		this.mPaint = paint;
		this.id = id;
		mPath = new Path();
//		mTsPath = new TsPath();
		mCanvas = new Canvas();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mImageBackground = BitmapFactory.decodeResource(getResources(), id);
		mImageBackground = Bitmap.createScaledBitmap(mImageBackground,
				getWidth(), getHeight(), true);
		mBitmapPaint = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(mImageBackground, 0, 0, null);
		canvas.drawBitmap(mBitmapPaint, 0, 0, null);
//		canvas.drawPath(mTsPath, mPaint);
		canvas.drawPath(mPath, mPaint);
	}

	private void touchStart(float x, float y) {
		isUndo = false;
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touchMove(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touchUp(float x, float y) {
		mPath.lineTo(mX, mY);
//		mTsPath.addPath(mPath);
		mCanvas.drawPath(mPath, mPaint);
//		listTsPaths.add(new TsPath(mTsPath));
		listTsPaths.add(mCanvas);
		mPath.reset();
		invalidate();
	}

	public void undo() {
		if (listTsPaths.size() == 0) {
			Toast.makeText(getContext(), "Stack empty", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		isUndo = true;
//		listTsPathsRedo.add(listTsPaths.get(listTsPaths.size() - 1));
//		listTsPaths.remove(listTsPaths.size() - 1);
//		if (listTsPaths.size() > 0) {
//			mTsPath.reset();
//			mTsPath.set(listTsPaths.get(listTsPaths.size() - 1));
//		} else {
//			mTsPath.reset();
//		}
		
		if (listTsPaths.size() > 0) {
			mCanvas = listTsPaths.get(listTsPaths.size() -1);
			draw(mCanvas);
		}
		
//		invalidate();
	}

	public void redo() {

	}

	public void erase() {

	}

	public void clear() {

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
		invalidate();
		return true;
	}

}
