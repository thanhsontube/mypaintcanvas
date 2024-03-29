package com.example.tapcopaint.base;

import java.util.Collection;
import java.util.Stack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;

import com.example.tapcopaint.utils.FilterLog;

public abstract class BaseFragmentActivity extends FragmentActivity implements OnBackStackChangedListener {

    public static final String FRAGMENT_KEY = "fragment-key";
    protected static final String SAVE_KEY_STACK = "tag_stack";
    private static final String TAG = "BaseFragmentActivity";
    FilterLog log = new FilterLog(TAG);

    protected abstract Fragment createFragmentMain(Bundle savedInstanceState);

    protected abstract int getFragmentContainerId();

    protected final Stack<String> mFragmentTagStack = new Stack<String>();

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.d("log>>>" + "onCreate savedInstanceState:" + savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(getFragmentContainerId(), createFragmentMain(savedInstanceState), FRAGMENT_KEY)
                    .setTransition(FragmentTransaction.TRANSIT_NONE).commit();
        } else {
            mFragmentTagStack.addAll((Collection<String>) savedInstanceState.getSerializable(SAVE_KEY_STACK));
            restoreFragmentsState();
        }
        getSupportFragmentManager().addOnBackStackChangedListener(this);

    }

    /**
     * add new fragment
     * 
     * @param f
     * @param isTransit
     */
    public void showFragment(Fragment f, boolean isTransit) {
        final String tag = String.format("%s:%d", getClass().getName(), mFragmentTagStack.size());
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        if (mFragmentTagStack.size() > 0) {
            final Fragment ff = fm.findFragmentByTag(mFragmentTagStack.peek());
            ft.hide(ff);
        } else {
            final Fragment ff = fm.findFragmentByTag(FRAGMENT_KEY);
            ft.hide(ff);
        }
        if (fm.findFragmentByTag(tag) == null) {
            ft.add(getFragmentContainerId(), f, tag);
            ft.show(f);
        } else {
            ft.replace(getFragmentContainerId(), f, tag);
            ft.show(f);
        }
        if (isTransit) {
            ft.addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        } else {
            ft.addToBackStack(null);
        }
        ft.commit();
        mFragmentTagStack.add(tag);
    }

    @Override
    public void onBackStackChanged() {
        final FragmentManager fm = getSupportFragmentManager();

        if (fm.getBackStackEntryCount() == mFragmentTagStack.size()) {
            return;
        }

        if (mFragmentTagStack.size() > 0) {
            final FragmentTransaction ft = fm.beginTransaction();
            final String tag = mFragmentTagStack.pop();
            if (fm.findFragmentByTag(tag) != null) {
                ft.remove(fm.findFragmentByTag(tag));
            }
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fm = getSupportFragmentManager();
        final Fragment f;
        if (mFragmentTagStack.size() > 0) {
            f = fm.findFragmentByTag(mFragmentTagStack.peek());
        } else {
            f = fm.findFragmentByTag(FRAGMENT_KEY);
        }

        if (f instanceof OnBackPressListener && ((OnBackPressListener) f).onBackPress()) {
            return;
        }
        super.onBackPressed();
    }

    /**
     * if a fragment implements this interface, when user do back, get onBackPressed() on that fragment
     * 
     * @author SonNT28
     * 
     */
    public interface OnBackPressListener {

        boolean onBackPress();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_KEY_STACK, mFragmentTagStack);
    }

    protected void restoreFragmentsState() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        if (mFragmentTagStack.size() == 0) {
            ft.show(fm.findFragmentByTag(FRAGMENT_KEY));
        } else {
            ft.hide(fm.findFragmentByTag(FRAGMENT_KEY));
            for (int i = 0; i < mFragmentTagStack.size(); i++) {
                String tag = mFragmentTagStack.get(i);
                Fragment f = fm.findFragmentByTag(tag);
                if (i + 1 < mFragmentTagStack.size()) {
                    ft.hide(f);
                } else {
                    ft.show(f);
                }
            }
        }
        ft.commit();
    }

}
