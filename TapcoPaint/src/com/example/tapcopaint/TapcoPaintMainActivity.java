package com.example.tapcopaint;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.example.tapcopaint.base.BaseFragmentActivity;
import com.example.tapcopaint.fragment.MainFragment;
import com.example.tapcopaint.fragment.MainFragment.IMainFragmentListener;
import com.example.tapcopaint.fragment.TsPaintFragment;
import com.example.tapcopaint.fragment.TsRenderFragment;

public class TapcoPaintMainActivity extends BaseFragmentActivity implements IMainFragmentListener {
    boolean isTest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapco_paint_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tapco_paint_main, menu);
        return true;
    }

    @Override
    protected Fragment createFragmentMain(Bundle savedInstanceState) {
        MainFragment f = new MainFragment();
        return f;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.main_ll_main;
    }

    @Override
    public void onIMainFragmentitemClick(int dto) {
        // PaintFragment f = PaintFragment.newInstance(dto);

        if (isTest) {

            TsRenderFragment f = TsRenderFragment.newInstance(dto);

            showFragment(f, true);
        } else {
            TsPaintFragment f = TsPaintFragment.newInstance(dto);
            showFragment(f, true);
        }

    }

}
