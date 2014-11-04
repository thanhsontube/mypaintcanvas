package com.example.tapcopaint.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.example.tapcopaint.R;
import com.example.tapcopaint.adapter.GridPictureAdapter;
import com.example.tapcopaint.base.BaseFragment;
import com.example.tapcopaint.utils.FilterLog;

public class MainFragment extends BaseFragment implements OnItemClickListener {

    private static final String TAG = "MainFragment";
    private GridView gridView;
    GridPictureAdapter adapter;
    private List<Integer> list = new ArrayList<Integer>();
    private List<String> listPath = new ArrayList<String>();
    FilterLog log = new FilterLog(TAG);

    @Override
    protected String generateTitle() {
        return "Selection";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (ViewGroup) inflater.inflate(R.layout.main_fragment, container, false);
        list.add(R.drawable.pic1);
        list.add(R.drawable.pic2);
        list.add(R.drawable.pic3);
        list.add(R.drawable.pic4);
        list.add(R.drawable.pic5);
        list.add(R.drawable.pic6);

        File f = new File(Environment.getExternalStorageDirectory(), "tapco");
        if (!f.exists()) {
            Toast.makeText(getActivity(), "Please Create Folder [tapco] in Sdcard and put some image in it",
                    Toast.LENGTH_SHORT).show();
        }

        listPath.clear();
        for (File s : f.listFiles()) {
            listPath.add(s.getPath());
            log.d("log>>> " + "s:" + s);
        }

        adapter = new GridPictureAdapter(getActivity(), listPath);

        gridView = (GridView) rootView.findViewWithTag("gridview");
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        mListener.onIMainFragmentitemClick(listPath.get(arg2));
    }

    private IMainFragmentListener mListener;

    public interface IMainFragmentListener {

        void onIMainFragmentitemClick(String path);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IMainFragmentListener) {
            mListener = (IMainFragmentListener) activity;
        }
    }

}
