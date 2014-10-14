package com.example.tapcopaint.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.widget.GridView;

import com.example.tapcopaint.R;
import com.example.tapcopaint.adapter.ColorAdapter;

public class ColorPickerDialog extends DialogFragment {

    String color;
    private List<String> list = new ArrayList<String>();
    private ColorAdapter adapter;

    public static ColorPickerDialog newInstance(String color) {
        ColorPickerDialog f = new ColorPickerDialog();
        Bundle bundle = new Bundle();
        bundle.putString("color", color);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            color = getArguments().getString("color");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.color_picker_2);
        GridView lv = (GridView) dialog.findViewById(R.id.color_pick_grid);
        adapter = new ColorAdapter(getActivity(), list);
        lv.setAdapter(adapter);
        initGrid();
        return dialog;
    }

    void initGrid() {
        list.clear();
        String[] arrayColor = getActivity().getResources().getStringArray(R.array.array_color_picker);
        for (String string : arrayColor) {
            list.add(string);
        }
        adapter.notifyDataSetChanged();
    }
}
