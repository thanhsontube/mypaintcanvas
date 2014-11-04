package com.example.tapcopaint.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class CommandManager {
    private List<DrawingPath> currentStack;
    private List<DrawingPath> redoStack;
    private DrawingPath mCurrentDP;
    private float tx = 1.f;
    private float ty = 1.f;

    public CommandManager() {
        currentStack = Collections.synchronizedList(new ArrayList<DrawingPath>());
        redoStack = Collections.synchronizedList(new ArrayList<DrawingPath>());
    }

    public void addCommand(DrawingPath command, boolean save) {
        redoStack.clear();
        currentStack.add(command);
    }

    public void drawCurrent(DrawingPath dp) {
        mCurrentDP = dp;
    }

    public void undo() {
        final int length = currentStackLength();
        if (length > 0) {
            final DrawingPath undoCommand = currentStack.get(length - 1);
            currentStack.remove(length - 1);
            undoCommand.undo();
            redoStack.add(undoCommand);
        }
    }

    public void clear() {
        currentStack.clear();
        redoStack.clear();
    }

    public void earse() {
        final int length = currentStackLength();
        if (length > 0) {
            final DrawingPath undoCommand = currentStack.get(length - 1);
            undoCommand.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
    }

    public int currentStackLength() {
        final int length = currentStack.toArray().length;
        return length;
    }

    public void executeAll(Canvas canvas) {
        if (currentStack != null) {
            synchronized (currentStack) {
                final Iterator<DrawingPath> i = currentStack.iterator();
                while (i.hasNext()) {
                    final DrawingPath drawingPath = (DrawingPath) i.next();
                    drawingPath.draw(canvas, tx, ty);
                }
            }
        }
        if (mCurrentDP != null) {
            mCurrentDP.draw(canvas, tx, ty);
        }
    }

    public boolean hasMoreRedo() {
        return redoStack.toArray().length > 0;
    }

    public void scale(float x, float y) {
        tx = x;
        ty = y;
    }

    public boolean hasMoreUndo() {
        return currentStack.toArray().length > 0;
    }

    public void redo() {
        final int length = redoStack.toArray().length;
        if (length > 0) {
            final DrawingPath redoCommand = redoStack.get(length - 1);
            redoStack.remove(length - 1);
            currentStack.add(redoCommand);
        }
    }

    public List<DrawingPath> getCurrentStack() {
        return currentStack;
    }
}
