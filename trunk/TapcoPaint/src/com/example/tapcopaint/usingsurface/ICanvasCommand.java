package com.example.tapcopaint.usingsurface;

import android.graphics.Canvas;

public interface ICanvasCommand {
	public void draw(Canvas canvas);

	public void undo();
}
