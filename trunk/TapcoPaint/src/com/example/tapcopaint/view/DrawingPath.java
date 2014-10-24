package com.example.tapcopaint.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by IntelliJ IDEA. User: almondmendoza Date: 10/11/2010 Time: 12:44 AM Link:
 * http://www.tutorialforandroid.com/
 */
public class DrawingPath implements ICanvasCommand {
    public Path path;
    public Paint paint;
    
    

    public DrawingPath() {
        super();
        // TODO Auto-generated constructor stub
    }

    public DrawingPath(Path path, Paint paint) {
        super();
        this.path = path;
        this.paint = paint;
    }

    public void draw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    public void draw(Canvas canvas, float x, float y) {
        canvas.translate(x, y);
        canvas.drawPath(path, paint);
    }

    public void undo() {
        // Todo this would be changed later
    }
}
