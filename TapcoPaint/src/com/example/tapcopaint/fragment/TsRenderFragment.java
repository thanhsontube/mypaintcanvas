package com.example.tapcopaint.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tapcopaint.R;
import com.example.tapcopaint.base.BaseFragment;
import com.example.tapcopaint.view.TsSurfaceRender;

public class TsRenderFragment extends BaseFragment {

    private TsSurfaceRender tsSurfaceRender;
    private int id;

    @Override
    protected String generateTitle() {
        return "Paint";
    }

    public static TsRenderFragment newInstance(int id) {
        TsRenderFragment f = new TsRenderFragment();
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
        View rootView = (ViewGroup) inflater.inflate(R.layout.surface_render, container, false);
        tsSurfaceRender = (TsSurfaceRender) rootView.findViewById(R.id.paint_tssurfaceRender);
        tsSurfaceRender.setImage(getActivity(), id);
        return rootView;
    }

}
