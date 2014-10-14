package com.example.tapcopaint.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.tapcopaint.R;
import com.example.tapcopaint.view.SquareView;

public class ColorAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> list;

    public ColorAdapter(Context context, List<String> list) {
        super(context, 0, list);
        this.list = list;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final Holder holder;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_color_pick, parent, false);
            holder = new Holder();
            holder.img = (SquareView) v.findViewWithTag("icon");
            holder.img.setTag(position);
            v.setTag(holder);
        } else {
            holder = (Holder) v.getTag();
        }

        holder.img.setBackgroundColor(Color.parseColor(list.get(position)));

        return v;
    }

    static class Holder {
        SquareView img;
    }

}
