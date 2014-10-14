package com.example.tapcopaint.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * Created by IntelliJ IDEA. User: almondmendoza Date: 15/11/2010 Time: 12:23 AM To change this template use File |
 * Settings | File Templates.
 */
public class CommandManager {
    private List<DrawingPath> currentStack;
    private List<DrawingPath> redoStack;
    private List<DrawingPath> tmpStack;

    public CommandManager() {
        currentStack = Collections.synchronizedList(new ArrayList<DrawingPath>());
        redoStack = Collections.synchronizedList(new ArrayList<DrawingPath>());
        tmpStack = Collections.synchronizedList(new ArrayList<DrawingPath>());
    }

    public void addCommand(DrawingPath command, boolean save) {
        if (save) {
            redoStack.clear();
            currentStack.add(command);
        } else {
            tmpStack.add(command);
        }
    }

    public void clearTempStack() {
        tmpStack.clear();
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
        tmpStack.clear();
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
                    drawingPath.draw(canvas);
                }
            }
        }
        if (tmpStack != null) {
            synchronized (tmpStack) {
                final Iterator<DrawingPath> i = tmpStack.iterator();
                while (i.hasNext()) {
                    final DrawingPath drawingPath = (DrawingPath) i.next();
                    drawingPath.draw(canvas);
                }
            }
        }
    }

    public boolean hasMoreRedo() {
        return redoStack.toArray().length > 0;
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
}
