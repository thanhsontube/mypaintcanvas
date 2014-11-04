package com.example.tapcopaint.rotation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tapcopaint.R;
import com.example.tapcopaint.base.BaseFragment;

public class RotationFragment extends BaseFragment {

    @Override
    protected String generateTitle() {
        return "rotation";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("", "log>>>:" + "onCreate savedInstanceState:" + savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("", "log>>>:" + "RotationFragment onCreateView");
        View rootView = (ViewGroup) inflater.inflate(R.layout.rotation_fragment, container, false);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key", "sonnt");
    }

}
