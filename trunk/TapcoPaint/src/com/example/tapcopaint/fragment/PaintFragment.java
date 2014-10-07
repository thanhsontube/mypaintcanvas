package com.example.tapcopaint.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tapcopaint.R;
import com.example.tapcopaint.base.BaseFragment;

public class PaintFragment extends BaseFragment {

    private int id;
    private ImageView img;

    @Override
    protected String generateTitle() {
        return "Paint";
    }

    public static PaintFragment newInstance(int id) {
        PaintFragment f = new PaintFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (ViewGroup) inflater.inflate(R.layout.paint_fragment, container, false);
        img = (ImageView) rootView.findViewWithTag("icon");
        img.setImageResource(id);
        return rootView;
    }

}
