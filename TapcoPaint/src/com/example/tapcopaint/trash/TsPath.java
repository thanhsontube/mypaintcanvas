package com.example.tapcopaint.trash;

import java.util.ArrayList;

import android.graphics.Path;

public class TsPath extends Path {
    private ArrayList<Float> points = new ArrayList<Float>();

    public void addPath(TsPath src) {
        this.points.addAll(src.getPoints());
        super.rewind();
        super.moveTo(this.points.get(0), this.points.get(1));
        for (int i = 2; i < this.points.size(); i += 2) {
            super.lineTo(this.points.get(i), this.points.get(i + 1));
        }
    }

    public TsPath() {
        super();
    }

    public TsPath(TsPath src) {
        super(src);
    }

    @Override
    public void moveTo(float x, float y) {
        this.points.add(x);
        this.points.add(y);
        super.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y) {
        this.points.add(x);
        this.points.add(y);
        super.lineTo(x, y);
    }

    public ArrayList<Float> getPoints() {
        return this.points;
    }
}
