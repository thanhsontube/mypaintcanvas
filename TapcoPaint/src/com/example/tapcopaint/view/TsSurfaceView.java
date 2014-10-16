package com.example.tapcopaint.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.tapcopaint.utils.FilterLog;

public class TsSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = "TsSurfaceView";
	FilterLog log = new FilterLog(TAG);
	int id = -1;
	Bitmap bitmapBackGround;
	Bitmap bitmapPaint;

	private Canvas mCanvas;

	private boolean mDrawing = false;

	public void setId(int id) {
		this.id = id;
	}

	CommandManager commandManager;
	private Boolean _run;
	protected DrawThread thread;

	public TsSurfaceView(Context context) {
		super(context);
	}

	public TsSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		getHolder().addCallback(this);
		setZOrderOnTop(true);
		getHolder().setFormat(PixelFormat.TRANSPARENT);
		setWillNotDraw(false);

		commandManager = new CommandManager();
		thread = new DrawThread(getHolder());
	}

	class DrawThread extends Thread {
		private SurfaceHolder mSurfaceHolder;

		public DrawThread(SurfaceHolder surfaceHolder) {
			mSurfaceHolder = surfaceHolder;

		}

		public void setRunning(boolean run) {
			_run = run;
		}

		@Override
		public void run() {
			mCanvas = null;
			while (_run) {
				if (mDrawing) {
					try {
						mCanvas = mSurfaceHolder.lockCanvas(null);
						mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
						commandManager.executeAll(mCanvas);
					} finally {
						mSurfaceHolder.unlockCanvasAndPost(mCanvas);
					}
				}
			}
		}
	}

	Path path;
	Paint paint;

	public void onMyDraw(Path path, Paint paint) {
		this.paint = paint;
		this.path = path;
	}

	public void addDrawingPath(DrawingPath drawingPath, boolean save) {
		commandManager.addCommand(drawingPath, save);
	}

	public void drawCurrent(DrawingPath drawingPath) {
		commandManager.drawCurrent(drawingPath);
	}

	public boolean hasMoreRedo() {
		return commandManager.hasMoreRedo();
	}

	public void redo() {
		commandManager.redo();
		redrawSurface();
	}

	public void undo() {
		commandManager.undo();
		redrawSurface();
	}

	public void clear() {
		path = null;
		commandManager.clear();
		redrawSurface();
	}

	public void earse() {
		commandManager.earse();
	}

	public boolean hasMoreUndo() {
		return commandManager.hasMoreRedo();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		log.v("log>>> surfaceCreated:" + id);
		if (id != -1) {
			bitmapBackGround = BitmapFactory.decodeResource(getResources(), id);
			bitmapBackGround = Bitmap.createScaledBitmap(bitmapBackGround,
					getWidth(), getHeight(), true);
		}
		bitmapPaint = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		thread.setRunning(true);
		if (!thread.isInterrupted()) {
			thread.start();
		}
	}

	public void setRunning(boolean isRun) {
		this._run = isRun;
	}

	public void setDrawing(boolean isDrawing) {
		mDrawing = isDrawing;
	}

	public void stopThread() {
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				thread.interrupt();
				retry = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	private void redrawSurface() {
		mDrawing = true;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mDrawing = false;
			}
		}, 300);
	}

	public void saveBitmap() {
		Bitmap bm = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas();
		c.setBitmap(bm);
		List<DrawingPath> currentStack = commandManager.getCurrentStack();
		for (DrawingPath dp : currentStack) {
			c.drawPath(dp.path, dp.paint);
		}
		try {
			this.setDrawingCacheEnabled(true);
			FileOutputStream fos = new FileOutputStream(new File(
					Environment.getExternalStorageDirectory() + "/tmp.png"));
			bm.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
