package com.example.tapcopaint.graphic2;

import java.util.Stack;

import android.content.Context;
import android.graphics.Canvas;

import com.example.tapcopaint.utils.FilterLog;
import com.example.tapcopaint.view.DrawingPath;

public class StackPathManager {
    private static final String TAG = "StackPathManager";
    private Stack<DrawingPath> currentStack;
    private Stack<DrawingPath> redoStack;

    private DrawingPath currentPath = null;
    FilterLog log = new FilterLog(TAG);

    public StackPathManager(Context context) {
        currentStack = new Stack<DrawingPath>();
        redoStack = new Stack<DrawingPath>();
    }

    public void addStack(DrawingPath path) {
        redoStack.clear();
        currentStack.add(path);
    }

    public void restoreAll(Canvas canvas) {
        // log.d("log>>> " + "currentPath:" + currentPath + ";currentStack:" + currentStack.size());

        DrawingPath drawingPath;
        synchronized (currentStack) {
            for (int i = 0; i < currentStack.size(); i++) {
                drawingPath = currentStack.get(i);
                drawingPath.draw(canvas);
            }
        }
        // if (currentPath != null) {
        // synchronized (currentPath) {
        // currentPath.draw(canvas);
        // }
        // }
    }

    public void clear() {
        currentStack.clear();
        redoStack.clear();
    }

    public DrawingPath getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(DrawingPath currentPath) {
        this.currentPath = currentPath;
    }

    public int getSize() {
        return currentStack.size();
    }

}
