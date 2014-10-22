package com.example.tapcopaint.zoom;

import com.example.tapcopaint.utils.FilterLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TsImageView extends ImageView {
    private static final String TAG = "TsImageView";
    private Matrix matrix;
    private int viewW;
    private int viewH;
    FilterLog log = new FilterLog(TAG);

    private ScaleType scaleType;

    // contructor

    public TsImageView(Context context) {
        super(context);
        init(context);
    }

    public TsImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public TsImageView(Context context, AttributeSet attributeSet, int defaultStyle) {
        super(context, attributeSet, defaultStyle);
        init(context);
    }

    private void init(Context context) {
        log.d("log>>> " + "init");
        super.setClickable(true);
        matrix = new Matrix();
        if (scaleType == null) {
            scaleType = ScaleType.FIT_CENTER;
        }
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        log.d("log>>> " + "onMeasure");
//        Drawable drawable = getDrawable();
//        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
//            setMeasuredDimension(0, 0);
//            return;
//        }
//
//        int drawableWidth = drawable.getIntrinsicWidth();
//        int drawableHeight = drawable.getIntrinsicHeight();
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//
//        viewW = setViewSize(widthMode, widthSize, drawableWidth);
//        viewH = setViewSize(heightMode, heightSize, drawableHeight);
//
//        //
//        // Set view dimensions
//        //
//        setMeasuredDimension(viewW, viewH);
        
        viewW = widthMeasureSpec;
        viewH = heightMeasureSpec;
        fitImageToView();

    }

//    @Override
//    public void setScaleType(ScaleType scaleType) {
//        super.setScaleType(scaleType);
//        this.scaleType = scaleType;
//    }
//
//    @Override
//    public ScaleType getScaleType() {
//        return scaleType;
//    }

    //
    // @Override
    // protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // log.d("log>>> " + "onMeasure");
    // Drawable drawable = getDrawable();
    // if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
    // setMeasuredDimension(0, 0);
    // return;
    // }
    //
    // int drawableWidth = drawable.getIntrinsicWidth();
    // int drawableHeight = drawable.getIntrinsicHeight();
    // int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    // int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    // int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    // int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    // viewW = setViewSize(widthMode, widthSize, drawableWidth);
    // viewH = setViewSize(heightMode, heightSize, drawableHeight);
    //
    // //
    // // Set view dimensions
    // //
    // setMeasuredDimension(viewW, viewH);
    //
    // //
    // // Fit content within view
    // //
    // // fitImageToView();
    //
    // }
    //
    private void fitImageToView() {
        log.d("log>>> " + "scaleType:" + scaleType + ";viewW:" + viewW);
        Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicHeight() == 0 || drawable.getIntrinsicWidth() == 0) {
            return;
        }

        int drawableW = drawable.getIntrinsicWidth();
        int drawableH = drawable.getIntrinsicWidth();

        // scale
        float scaleX = (float) (viewW / drawableW);
        float scaleY = (float) (viewH / drawableH);
        if (scaleType == null) {
            scaleType = ScaleType.FIT_CENTER;
        }
        switch (scaleType) {
        case CENTER:
            scaleX = scaleY = 1;
            break;

        case CENTER_CROP:
            scaleX = scaleY = Math.max(scaleX, scaleY);
            break;

        case CENTER_INSIDE:
            scaleX = scaleY = Math.min(1, Math.min(scaleX, scaleY));

        case FIT_CENTER:
            scaleX = scaleY = Math.min(scaleX, scaleY);
            break;

        case FIT_XY:
            break;

        default:
            //
            // FIT_START and FIT_END not supported
            //
            throw new UnsupportedOperationException("TouchImageView does not support FIT_START or FIT_END");

        }

        float redundantXSpace = viewW - (scaleX * drawableW);
        float redundantYSpace = viewH - (scaleY * drawableH);
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);
        setImageMatrix(matrix);

    }

    //
    private int setViewSize(int mode, int size, int drawableWidth) {
        int viewSize;
        switch (mode) {
        case MeasureSpec.EXACTLY:
            viewSize = size;
            break;

        case MeasureSpec.AT_MOST:
            viewSize = Math.min(drawableWidth, size);
            break;

        case MeasureSpec.UNSPECIFIED:
            viewSize = drawableWidth;
            break;

        default:
            viewSize = size;
            break;
        }
        return viewSize;
    }

//    @Override
//    public void setImageResource(int resId) {
//        super.setImageResource(resId);
//        fitImageToView();
//    }
//
//    @Override
//    public void setImageBitmap(Bitmap bm) {
//        super.setImageBitmap(bm);
//        fitImageToView();
//    }
//
//    @Override
//    public void setImageDrawable(Drawable drawable) {
//        super.setImageDrawable(drawable);
//        fitImageToView();
//    }
//
//    @Override
//    public void setImageURI(Uri uri) {
//        super.setImageURI(uri);
//        fitImageToView();
//    }
    
    @Override
    public void setImageResource(int resId) {
        // TODO Auto-generated method stub
        super.setImageResource(resId);
        fitImageToView();
    }

}
