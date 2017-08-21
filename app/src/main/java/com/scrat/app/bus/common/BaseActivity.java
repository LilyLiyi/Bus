package com.scrat.app.bus.common;

import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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

    public void showSnackbar(String content) {
        if (isFinish()) {
            return;
        }
        Snackbar.make(getWindow().getDecorView(), content, Snackbar.LENGTH_LONG).show();
    }

    public void showToast(String content) {
        if (isFinish()) {
            return;
        }
        Toast.makeText(getBaseContext(), content, Toast.LENGTH_LONG).show();
    }

    private boolean isFinish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isDestroyed()) {
                return true;
            }
        }
        if (isFinishing()) {
            return true;
        }
        return false;
    }
}
