package com.example.tapcopaint.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.tapcopaint.R;
import com.example.tapcopaint.adapter.ColorAdapter;
import com.example.tapcopaint.utils.PaintUtil;

public class ColorPickerDialog extends DialogFragment implements OnSeekBarChangeListener {

    private String color;
    private List<String> list = new ArrayList<String>();
    private ColorAdapter adapter;

    private SeekBar seekBarR, seekBarG, seekBarB;
    private TextView txtR, txtG, txtB;
    private ImageView imgPreview;

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

        imgPreview = (ImageView) dialog.findViewById(R.id.color_pick_img_preview);

        seekBarR = (SeekBar) dialog.findViewById(R.id.color_pick_seekbar_r);
        seekBarG = (SeekBar) dialog.findViewById(R.id.color_pick_seekbar_g);
        seekBarB = (SeekBar) dialog.findViewById(R.id.color_pick_seekbar_b);

        seekBarR.setOnSeekBarChangeListener(this);
        seekBarG.setOnSeekBarChangeListener(this);
        seekBarB.setOnSeekBarChangeListener(this);

        txtR = (TextView) dialog.findViewById(R.id.color_pick_txt_value_r);
        txtG = (TextView) dialog.findViewById(R.id.color_pick_txt_value_g);
        txtB = (TextView) dialog.findViewById(R.id.color_pick_txt_value_b);

        GridView lv = (GridView) dialog.findViewById(R.id.color_pick_grid);
        adapter = new ColorAdapter(getActivity(), list);
        lv.setAdapter(adapter);
        initTop();
        initGrid();
        return dialog;
    }

    void initTop() {
        int[] intColor = PaintUtil.getRGB(color);
        if (intColor == null || intColor.length == 0) {
            return;
        }
        txtR.setText("R:" + intColor[0]);
        txtG.setText("G:" + intColor[1]);
        txtB.setText("B:" + intColor[2]);

        seekBarR.setProgress(intColor[0]);
        seekBarG.setProgress(intColor[1]);
        seekBarB.setProgress(intColor[2]);

    }

    void initGrid() {
        list.clear();
        String[] arrayColor = getActivity().getResources().getStringArray(R.array.array_color_picker);
        for (String string : arrayColor) {
            list.add(string);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == seekBarR) {
            txtR.setText("R:" + progress);
        }
        if (seekBar == seekBarG) {
            txtG.setText("G:" + progress);
        }
        if (seekBar == seekBarB) {
            txtB.setText("B:" + progress);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }
}
