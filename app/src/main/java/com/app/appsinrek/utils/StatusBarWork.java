package com.app.appsinrek.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.app.appsinrek.R;


public class StatusBarWork {

    private final Activity mActivity;

    public StatusBarWork(Activity activity) {
        this.mActivity = activity;
    }

    @SuppressLint("ResourceAsColor")
    public void statusBarTransparent() {
        // to make space and white status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mActivity.getWindow().setStatusBarColor(R.color.Theme_1);
        }
    }
    public void statusBarBgBlack() {
        // to make space and white status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mActivity.getWindow().setStatusBarColor(mActivity.getResources().getColor(R.color.black));
        }
    }

    @SuppressLint("ResourceAsColor")
    public void statusBarBgColor(int color) {
        // to make space and white status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mActivity.getWindow().setStatusBarColor(R.color.Theme_1);
        }
    }
    public void setWindowData(){
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(mActivity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(mActivity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            mActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
    public void setWindowDataDrak(){
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(mActivity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(mActivity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            mActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
    public void setWindowFlag(Activity activity, final int bits, boolean on) {


        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
