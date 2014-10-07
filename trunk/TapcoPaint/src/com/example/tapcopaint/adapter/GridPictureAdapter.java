package com.example.tapcopaint.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.tapcopaint.R;

public class GridPictureAdapter extends ArrayAdapter<Integer> {
    private List<Integer> list;
    private Context context;

    public GridPictureAdapter(Context context, List<Integer> list) {
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
            v = inflater.inflate(R.layout.row_grid_picture, parent, false);
            holder = new Holder();
            holder.img = (ImageView) v.findViewWithTag("icon");
            holder.img.setTag(position);
            v.setTag(holder);
        } else {
            holder = (Holder) v.getTag();
        }

        int dto = list.get(position);
        holder.img.setImageResource(dto);

        return v;
    }

    static class Holder {
        ImageView img;
    }

}
