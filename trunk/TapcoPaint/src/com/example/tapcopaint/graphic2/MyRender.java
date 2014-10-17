package com.example.tapcopaint.graphic2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import com.androidquery.AQuery;
import com.example.tapcopaint.utils.FilterLog;

public class MyRender extends SurfaceRenderer {
    private static final String TAG = "MyRender";

    FilterLog log = new FilterLog(TAG);

    int id;

    protected MyRender(Context c) {
        super(c);
    }

    public MyRender(Context c, int id) {
        this(c);
        this.id = id;
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
        log.d("log>>> " + "start");

    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    @Override
    public void suspend(boolean b) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void drawBase() {
        // TODO Auto-generated method stub
        drawOriginalImage(context_, id, viewPort_);

    }

    @Override
    protected void drawLayer() {
        // TODO Auto-generated method stub
        drawLayer(context_, viewPort_);

    }

    @Override
    protected void drawFinal() {
        // TODO Auto-generated method stub

    }

    private final Rect dstRect_ = new Rect(0, 0, 0, 0);
    private final Rect srcRect_ = new Rect(0, 0, 0, 0);
    private final Point dstSize_ = new Point();

    /**
     * draw imagebackground
     */
    public void drawOriginalImage(Context context, int id, ViewPort viewPort) {
        // log.d("log>>> " + "drawOriginalImage id:" + id);
        AQuery aQuery = new AQuery(context);
        Bitmap bitmap;
        if (id == -1) {
            bitmap = Bitmap.createBitmap(viewPort.getPhysicalWidth(), viewPort.getPhysicalHeight(),
                    Bitmap.Config.ARGB_8888);

        } else {
            bitmap = aQuery.getCachedImage(id);
        }
        // Bitmap bitmap = aQuery.getCachedImage(R.drawable.pic2);

        srcRect_.set(0, 0, 300, 300);
        dstRect_.set(0, 0, viewPort.getPhysicalWidth(), viewPort.getPhysicalHeight());
        Canvas canvas = new Canvas(viewPort.bitmap_);
        canvas.drawBitmap(bitmap, null, dstRect_, null);
    }

    /**
     * draw layer
     * 
     */
    public void drawLayer(Context context, ViewPort viewPort) {

    }
    
    

}
