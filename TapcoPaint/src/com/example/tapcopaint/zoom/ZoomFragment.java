package com.example.tapcopaint.zoom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ZoomControls;

import com.example.tapcopaint.R;
import com.example.tapcopaint.base.BaseFragment;

public class ZoomFragment extends BaseFragment {
    ZoomControls zoomControls;
    ImageView img;

    @Override
    protected String generateTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (ViewGroup) inflater.inflate(R.layout.zoom_fragment, container, false);
        zoomControls = (ZoomControls) rootView.findViewById(R.id.zoomControls1);
        img = (ImageView) rootView.findViewById(R.id.imageView1);
        enableZoom();
        return rootView;
    }

    private void enableZoom() {
        zoomControls.setIsZoomInEnabled(true);
        zoomControls.setIsZoomOutEnabled(true);
        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int w = img.getWidth();
                int h = img.getHeight();

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w + 10, h + 10);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);

                img.setLayoutParams(params);

            }
        });
        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int w = img.getWidth();
                int h = img.getHeight();

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w - 10, h - 10);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);

                img.setLayoutParams(params);

            }
        });
    }

}
