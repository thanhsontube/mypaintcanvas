package com.example.tapcopaint.usingsurface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawingSurface extends SurfaceView implements
		SurfaceHolder.Callback {

	private Context mContext;

	private int backgroundResource = -1;
	private Bitmap backgroundBitmap;

	private Boolean _run;
	protected DrawThread thread;

	private CommandManager commandManager;

	public DrawingSurface(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;

		getHolder().addCallback(this);

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
			Canvas canvas = null;
			while (_run) {
				try {
					canvas = mSurfaceHolder.lockCanvas(null);
//					if (backgroundBitmap != null) {
//						canvas.drawBitmap(backgroundBitmap, 0, 0, new Paint(
//								Paint.DITHER_FLAG));
//					}
					
//					Paint transPainter = new Paint();
//					transPainter.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//					transPainter.setAlpha(0x00);
//					canvas.drawRect(0, 0, getWidth(), getHeight(), transPainter);
					
					canvas.drawColor(0, Mode.CLEAR);
//					canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
					
					commandManager.executeAll(canvas);
				} finally {
					mSurfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}

	}

	public void addDrawingPath(DrawingPath drawingPath, boolean save) {
		commandManager.addCommand(drawingPath, save);
	}

	public void clearTempStack() {
		commandManager.clearTempStack();
	}

	public boolean hasMoreRedo() {
		return commandManager.hasMoreRedo();
	}

	public void redo() {
		commandManager.redo();
	}

	public void undo() {
		commandManager.undo();
	}

	public void clear() {
		commandManager.clear();
	}

	public boolean hasMoreUndo() {
		return commandManager.hasMoreRedo();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void setBackgroundResource(int res) {
		backgroundResource = res;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (backgroundResource != -1) {
			backgroundBitmap = BitmapFactory.decodeResource(
					mContext.getResources(), backgroundResource);
			backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap,
					getWidth(), getHeight(), true);
		}

		thread.setRunning(true);
		thread.start();
	}

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

}
