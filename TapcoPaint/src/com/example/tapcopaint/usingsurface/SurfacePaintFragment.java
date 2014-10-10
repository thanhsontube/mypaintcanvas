package com.example.tapcopaint.usingsurface;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.tapcopaint.R;
import com.example.tapcopaint.base.BaseFragment;
import com.example.tapcopaint.utils.FilterLog;

@SuppressLint("ClickableViewAccessibility")
public class SurfacePaintFragment extends BaseFragment implements
		OnClickListener, OnTouchListener {

	private static final String TAG = "SurfacePaintFragment";
	private int id;
	private DrawingSurface drawingSurface;
	private DrawingPath currentDrawingPath;
	private Paint currentPaint;
	private ImageView imageViewBackground;

	FilterLog log = new FilterLog(TAG);

	private Button colorRedBtn;
	private Button colorGreenBtn;
	private Button colorBlueBtn;
	private Button undoBtn;
	private Button redoBtn;
	private Button eraseBtn;
	private Button clearBtn;

	@Override
	protected String generateTitle() {
		return "Paint Surface";
	}

	public static SurfacePaintFragment newInstance(int id) {
		SurfacePaintFragment f = new SurfacePaintFragment();
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
		View rootView = (ViewGroup) inflater.inflate(
				R.layout.paint_surface_fragment, container, false);

		imageViewBackground = (ImageView) rootView
				.findViewById(R.id.backgroundImage);
		drawingSurface = (DrawingSurface) rootView
				.findViewById(R.id.drawingSurface);
		colorRedBtn = (Button) rootView.findViewById(R.id.colorRedBtn);
		colorGreenBtn = (Button) rootView.findViewById(R.id.colorGreenBtn);
		colorBlueBtn = (Button) rootView.findViewById(R.id.colorBlueBtn);
		undoBtn = (Button) rootView.findViewById(R.id.undoBtn);
		redoBtn = (Button) rootView.findViewById(R.id.redoBtn);
		eraseBtn = (Button) rootView.findViewById(R.id.eraseBtn);
		clearBtn = (Button) rootView.findViewById(R.id.clearBtn);

		imageViewBackground.setImageResource(id);
		
//		drawingSurface.setBackgroundResource(id);
		drawingSurface.setOnTouchListener(this);
//		drawingSurface.setZOrderOnTop(true);
//		SurfaceHolder sfhTrackHolder = drawingSurface.getHolder();
//		sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);

		colorRedBtn.setOnClickListener(this);
		colorGreenBtn.setOnClickListener(this);
		colorBlueBtn.setOnClickListener(this);
		undoBtn.setOnClickListener(this);
		redoBtn.setOnClickListener(this);
		eraseBtn.setOnClickListener(this);
		clearBtn.setOnClickListener(this);

		redoBtn.setEnabled(false);
		undoBtn.setEnabled(false);

		setCurrentPaint();

		return rootView;
	}

	private void setCurrentPaint() {
		currentPaint = new Paint();
		currentPaint.setDither(true);
		currentPaint.setColor(0xFFFF0000);
		currentPaint.setStyle(Paint.Style.STROKE);
		currentPaint.setStrokeJoin(Paint.Join.ROUND);
		currentPaint.setStrokeCap(Paint.Cap.ROUND);
		currentPaint.setStrokeWidth(6);
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			currentDrawingPath = new DrawingPath();
			currentDrawingPath.paint = currentPaint;
			currentDrawingPath.path = new Path();
			currentDrawingPath.path.moveTo(motionEvent.getX(),
					motionEvent.getY());
			currentDrawingPath.path.lineTo(motionEvent.getX(),
					motionEvent.getY());
			drawingSurface.addDrawingPath(currentDrawingPath, false);
		} else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
			currentDrawingPath.path.lineTo(motionEvent.getX(),
					motionEvent.getY());
			drawingSurface.addDrawingPath(currentDrawingPath, false);
		} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
			currentDrawingPath.path.lineTo(motionEvent.getX(),
					motionEvent.getY());
			drawingSurface.addDrawingPath(currentDrawingPath, true);
			drawingSurface.clearTempStack();
			undoBtn.setEnabled(true);
			redoBtn.setEnabled(false);
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == colorRedBtn.getId()) {
			currentPaint = new Paint();
			currentPaint.setDither(true);
			currentPaint.setColor(0xFFFF0000);
			currentPaint.setStyle(Paint.Style.STROKE);
			currentPaint.setStrokeJoin(Paint.Join.ROUND);
			currentPaint.setStrokeCap(Paint.Cap.ROUND);
			currentPaint.setStrokeWidth(6);
		}
		if (v.getId() == colorGreenBtn.getId()) {
			currentPaint = new Paint();
			currentPaint.setDither(true);
			currentPaint.setColor(0xFF0000FF);
			currentPaint.setStyle(Paint.Style.STROKE);
			currentPaint.setStrokeJoin(Paint.Join.ROUND);
			currentPaint.setStrokeCap(Paint.Cap.ROUND);
			currentPaint.setStrokeWidth(6);
		}
		if (v.getId() == colorBlueBtn.getId()) {
			currentPaint = new Paint();
			currentPaint.setDither(true);
			currentPaint.setColor(0xFF00FF00);
			currentPaint.setStyle(Paint.Style.STROKE);
			currentPaint.setStrokeJoin(Paint.Join.ROUND);
			currentPaint.setStrokeCap(Paint.Cap.ROUND);
			currentPaint.setStrokeWidth(6);
		}
		if (v.getId() == undoBtn.getId()) {
			drawingSurface.undo();
			if (drawingSurface.hasMoreUndo() == false) {
				undoBtn.setEnabled(false);
			}
			redoBtn.setEnabled(true);
		}
		if (v.getId() == redoBtn.getId()) {
			drawingSurface.redo();
			if (drawingSurface.hasMoreRedo() == false) {
				redoBtn.setEnabled(false);
			}
			undoBtn.setEnabled(true);
		}
		if (v.getId() == eraseBtn.getId()) {
			currentPaint = new Paint();
			currentPaint.setDither(true);
			currentPaint.setColor(0xFF00FF00);
			currentPaint.setAlpha(0xFF);
			currentPaint.setStyle(Paint.Style.STROKE);
			currentPaint.setStrokeJoin(Paint.Join.ROUND);
			currentPaint.setStrokeCap(Paint.Cap.ROUND);
			currentPaint.setStrokeWidth(6);
			currentPaint.setXfermode(new PorterDuffXfermode(
					PorterDuff.Mode.CLEAR));
		}
		if (v.getId() == clearBtn.getId()) {
			drawingSurface.clear();
			redoBtn.setEnabled(false);
			undoBtn.setEnabled(false);
		}
	}

}
