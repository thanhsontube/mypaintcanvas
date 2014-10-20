package com.example.tapcopaint.zoom;

import android.widget.ImageView.ScaleType;

public class ZoomVariables {

    public float scale;
    public float focusX;
    public float focusY;
    public ScaleType scaleType;

    public ZoomVariables(float scale, float focusX, float focusY, ScaleType scaleType) {
        this.scale = scale;
        this.focusX = focusX;
        this.focusY = focusY;
        this.scaleType = scaleType;
    }

}
