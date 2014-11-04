package com.example.tapcopaint.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.example.tapcopaint.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GridPictureAdapter extends ArrayAdapter<String> {
    // private List<Integer> list;
    private List<String> list;
    private Context context;
    private AQuery aQuery;
    private ImageLoader imageLoader;

    // public GridPictureAdapter(Context context, List<Integer> list) {
    // super(context, 0, list);
    // this.list = list;
    // this.context = context;
    //
    // }

    public GridPictureAdapter(Context context, List<String> list) {
        super(context, 0, list);
        this.list = list;
        this.context = context;
        aQuery = new AQuery(context);
        imageLoader = ImageLoader.getInstance();

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

        String dto = list.get(position);
        // holder.img.setImageResource(dto);
        // aQuery.id(holder.img).image(new File(dto), 0);
        // imageLoader.loadImage("file:///" + dto, null);
        imageLoader.displayImage("file:///" + dto, holder.img);
        return v;
    }

    static class Holder {
        ImageView img;
    }

}
