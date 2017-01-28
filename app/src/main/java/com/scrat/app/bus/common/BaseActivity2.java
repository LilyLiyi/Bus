package com.scrat.app.bus.common;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.scrat.app.bus.BusApp;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by yixuanxuan on 2017/1/27.
 */

public class BaseActivity2 extends AppCompatActivity {
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void back(View v) {
        onBackPressed();
    }

    protected void showMsg(int resId) {
        if (getWindow() == null || getWindow().getDecorView() == null) {
            Toast.makeText(BusApp.getContext(), resId, Toast.LENGTH_LONG).show();
            return;
        }

        Snackbar.make(getWindow().getDecorView(), resId, Snackbar.LENGTH_LONG).show();
    }

    protected void showMsg(String content) {
        if (getWindow() == null || getWindow().getDecorView() == null) {
            Toast.makeText(BusApp.getContext(), content, Toast.LENGTH_LONG).show();
            return;
        }

        Snackbar.make(getWindow().getDecorView(), content, Snackbar.LENGTH_LONG).show();
    }
}
