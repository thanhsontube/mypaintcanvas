package com.example.tapcopaint.view;


import android.graphics.Canvas;

/**
 * Created by IntelliJ IDEA.
 * User: almondmendoza
 * Date: 14/11/2010
 * Time: 11:39 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ICanvasCommand {
    public void draw(Canvas canvas);
    public void undo();
}
