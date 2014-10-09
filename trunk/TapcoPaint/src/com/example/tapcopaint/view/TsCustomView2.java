package com.example.tapcopaint.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.tapcopaint.R;
import com.example.tapcopaint.utils.FilterLog;
import com.example.tapcopaint.utils.ImageCache;

public class TsCustomView2 extends View implements OnTouchListener {
	private static final String TAG = "TsCustomView";
	FilterLog log = new FilterLog(TAG);
	private static final float TOUCH_TOLERANCE = 4;
	private Canvas canvas;

	private List<TsPath> listTsPaths = new ArrayList<TsPath>();

	private List<Path> listPaths = new ArrayList<Path>();
	private List<Path> listPathsRedo = new ArrayList<Path>();

	private List<Bitmap> listBitmap = new ArrayList<Bitmap>();
	private List<Bitmap> listBitmapRedo = new ArrayList<Bitmap>();
	private Path path;
	private Bitmap imageBackground;

	private Bitmap bitmapPaint;
	private Bitmap bgBitmapPaint;
	private int w, h;
	private float mX, mY;

	private Paint paint;
	private int id;
	private boolean isUndo;

	private boolean isEarse;

	private TsPath tsPath;

	private AQuery aQuery;

	private Stack<String> stackBitmaps = new Stack<String>();

	public TsCustomView2(Context context) {
		super(context);
		log.d("log>>> " + "TsView contructor");
		setFocusable(true);
		setFocusableInTouchMode(true);
		setOnTouchListener(this);
		// imageBackground =
		// BitmapFactory.decodeResource(context.getResources(), id);
		path = new Path();
		tsPath = new TsPath();
		aQuery = new AQuery(context);
	}

	public TsCustomView2(Context context, Paint paint, int id) {
		// super(context);
		this(context);
		this.paint = paint;
		this.id = id;

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// log.d("log>>> " + "onSizeChanged w:" + w + ";h:" + h);
		super.onSizeChanged(w, h, oldw, oldh);
		this.w = w;
		this.h = h;
		bitmapPaint = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmapPaint);
		imageBackground = BitmapFactory.decodeResource(getResources(), id);
		imageBackground = Bitmap.createScaledBitmap(imageBackground,
				getWidth(), getHeight(), true);

		listTsPaths.add(new TsPath(tsPath));
		mImageCache = new ImageCache(getContext());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// draw a background
		// canvas.drawBitmap(imageBackground, 0, 0, null);
		// draw a bitmap paint with Paint.DITHER_FLAG

		// if (stackBitmaps.size() == 0) {
		// // bitmapPaint = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		// } else {
		// bitmapPaint = mImageCache.get(stackBitmaps.peek());
		// }
		// log.d("log>>> " + "onDraw on:"+bitmapPaint);
		//
		// if (bitmapPaint != null) {
		// canvas.drawBitmap(bitmapPaint, 0, 0, new Paint(Paint.DITHER_FLAG));
		// } else {
		// log.d("log>>> " + "bitmapPaint is NULL");
		// }
		// log.d("log>>> " + " ");
		// log.d("log>>> " + "onDraw " + bitmapPaint.toString());
		canvas.drawBitmap(bitmapPaint, 0, 0, new Paint(Paint.DITHER_FLAG));
		// canvas.drawColor(Color.GREEN);
		// if(stackBitmaps.size() > 0) {
		// if (stackBitmaps.size() == 2) {
		// canvas.drawColor(Color.GREEN);
		//
		// }
		//
		// if (stackBitmaps.size() == 1) {
		// canvas.drawColor(Color.BLUE);
		//
		// if(isUndo) {
		//
		// path = new Path();
		// path.moveTo(0, 0);
		// path.lineTo(300, 400);
		// canvas.drawPath(path, paint);
		// }
		// }
		// }

		// canvas.drawColor(Color.GREEN);
		// canvas.drawBitmap(bitmapPaint, 0, 0, new Paint(Paint.DITHER_FLAG));
		log.d("log>>> " + bitmapPaint);

		// if(stackBitmaps.size() > 0) {
		// canvas.drawBitmap(mImageCache.get(stackBitmaps.peek()), 0, 0, new
		// Paint(Paint.DITHER_FLAG));
		// }

		 if(!isUndo) {
			 canvas.drawPath(path, paint);
		 } else {
			 if (stackBitmaps.size() == 2) {
				 
				  path = new Path();
				  path.moveTo(0, 0);
				  path.lineTo(300, 400);
				  canvas.drawPath(path, paint);
			 }
			 
			 if (stackBitmaps.size() == 1) {
				 
				  path = new Path();
				  path.moveTo(20, 20);
				  path.lineTo(400, 400);
				  canvas.drawPath(path, paint);
			 }
		 }
		
		

		// if (listPaths.size() > 0) {
		// if (isUndo) {
		//
		// bitmapPaint = getmImageCache().get(String.valueOf(listPaths.size() -
		// 1));
		// int i = 0;
		// for (Path p : listPaths) {
		// Bitmap bitmap = getmImageCache().get(String.valueOf(i));
		// i++;
		// log.d("log>>> " + "bitmapPaintLOG:" + bitmap);
		// }
		// }
		// } else {
		// if (isUndo) {
		//
		// bitmapPaint = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		// }
		// }
		//
		// if (listPaths.size() > 0 && !isUndo) {
		// bitmapPaint = getmImageCache().get(String.valueOf(listPaths.size() -
		// 1));
		// }
		//
		// log.d("log>>> " + "bitmapPaint:" + bitmapPaint);
		// if (bitmapPaint != null) {
		//
		// canvas.drawBitmap(bitmapPaint, 0, 0, new Paint(Paint.DITHER_FLAG));
		// } else {
		// log.d("log>>> " + "bitmapPaint is NULL");
		// }
		//
		// canvas.drawPath(path, paint);

	}

	private void touchStart(float x, float y) {
		isUndo = false;
		// log.d("log>>> " + "touchStart");
		// log.d("log>>> " + "bitmapPaint:" + bitmapPaint);
		// path.reset();
		path.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touchMove(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touchUp(float x, float y) {
		// log.d("log>>> " + "touchUp");
		// log.d("log>>> " + "bitmapPaint:" + bitmapPaint);
		t++;
		path.lineTo(mX, mY);
		listPaths.add(path);
		canvas.drawPath(path, paint);
		// path.reset();

		new LazyImageSetTask2(bitmapPaint).execute();
		// commit the path to our offscreen

	}

	public void undo() {

		// log.d("log>>> " + "undo: listTsPaths:" + listTsPaths.size());
		// if (listPaths.size() == 0) {
		// Toast.makeText(getContext(), "Stack empty",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }

		// canvas.drawBitmap(bgBitmapPaint, 0, 0, new Paint(Paint.DITHER_FLAG));

		if (stackBitmaps.size() == 0) {
			Toast.makeText(getContext(), "Stack empty", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		Bitmap bitmap = getmImageCache().get(stackBitmaps.peek());
		log.d("log>>> " + " ");
		log.d("log>>> " + "undo remove key:" + bitmap.toString());
		stackBitmaps.pop();
		log.d("log>>> " + "undo end remove key:" + bitmap.toString());
		log.d("log>>> " + " ");
		bitmap.recycle();

		isUndo = true;
		// listPathsRedo.add(listPaths.get(listPaths.size() - 1));
		// listPaths.remove(listPaths.size() - 1);

		if (stackBitmaps.size() > 0) {
			log.d("log>>> " + "restore bitmap " + stackBitmaps.peek());
			bitmapPaint = mImageCache.get(stackBitmaps.peek());
		} else {
			log.d("log>>> " + "no bitmap in stackBitmap");
		}

		invalidate();
	}

	public void earse() {
		log.d("log>>> " + "earse");
		isEarse = true;
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		// invalidate();
	}

	public void clear() {
		paint.setXfermode(null);
		isEarse = false;
		listPaths.clear();
		listPathsRedo.clear();
		listTsPaths.clear();
		tsPath.reset();
		canvas.drawBitmap(imageBackground, 0, 0, null);
		invalidate();
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

	public ImageCache getmImageCache() {
		return mImageCache;
	}

	public void setmImageCache(ImageCache mImageCache) {
		this.mImageCache = mImageCache;
	}

	private ImageCache mImageCache;

	int t = 0;

	class LazyImageSetTask extends AsyncTask<Void, Void, Bitmap> {
		final Bitmap bitmap;
		final String key;

		public LazyImageSetTask(Bitmap src, String pos) {

			if (t == 1) {
				this.bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.pic4);
			} else {
				this.bitmap = Bitmap.createBitmap(src);

			}

			// this.bitmap = src;
			this.key = pos;
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			// log.d("log>>> " + "doInBackground key:" + key + ";bitmap:" +
			// bitmap);
			log.d("log>>> " + "doInBackground key:" + bitmap.toString()
					+ ";bitmap:" + bitmap);

			synchronized (mImageCache) {
				mImageCache.put(bitmap.toString(), bitmap);
			}
			// bitmap.recycle();
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			log.d("log>>> " + "--- current list ---");
			for (String key : stackBitmaps) {
				log.d(key);
			}
			log.d("log>>> " + "--- end current list ---");
			// canvas.drawPath(path, paint);
			// path.reset();
		}
	}

	class LazyImageSetTask2 extends AsyncTask<Void, Void, Bitmap> {
		Bitmap bitmap;

		public LazyImageSetTask2(Bitmap src) {
			
			this.bitmap = Bitmap.createBitmap(src);

			if (t == 2) {
				this.bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.pic4);
			} 
			
			if (t == 3) {
				this.bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.pic5);
			} 
			
			if (t == 4) {
				this.bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.pic2);
			} 
			
			
		}

		@Override
		protected Bitmap doInBackground(Void... params) {

			// log.d("log>>> " + " ");
			// log.d("log>>> " + "doInBackground add stackBitmaps with key:" +
			// bitmap.toString());
			stackBitmaps.add(bitmap.toString());
			// log.d("log>>> " + "doInBackground end add stackBitmaps with key:"
			// + bitmap.toString());
			// log.d("log>>> " + " ");

			// log.d("log>>> " + " ");
			// log.d("log>>> " + "doInBackground save bitmap with key:" +
			// bitmap.toString() + ";bitmap:" + bitmap);
			synchronized (mImageCache) {
				mImageCache.put(bitmap.toString(), bitmap);
			}
			// log.d("log>>> " + "doInBackground end save bitmap with key:" +
			// bitmap.toString() + ";bitmap:" + bitmap);
			// log.d("log>>> " + " ");

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {

			log.d("log>>> " + " ");
			log.d("log>>> " + "--- onPostExecute current list ---");
			for (String key : stackBitmaps) {
				log.d("log>>> " + key);
				log.d("log>>> " + mImageCache.get(key));
			}
			log.d("log>>> " + "--- onPostExecute end current list ---");
			log.d("log>>> " + " ");

			// log.d("log>>> " + " ");
			// log.d("log>>> " + "--- onPostExecute draw again ---");
			// log.d("log>>> " + "get " + stackBitmaps.peek());
			bitmapPaint = mImageCache.get(stackBitmaps.peek());
			invalidate();
			// log.d("log>>> " + "--- onPostExecute end draw again ---");
			// log.d("log>>> " + " ");

		}
	}

}
