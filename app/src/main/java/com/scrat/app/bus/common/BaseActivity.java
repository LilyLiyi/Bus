package com.scrat.app.bus.common;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by yixuanxuan on 16/5/12.
 */
@Deprecated
public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    public void showToask(String content) {
        Snackbar.make(getWindow().getDecorView(), content, Snackbar.LENGTH_LONG).show();
    }
}
