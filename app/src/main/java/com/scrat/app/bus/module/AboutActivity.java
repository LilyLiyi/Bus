package com.scrat.app.bus.module;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.scrat.app.bus.R;
import com.scrat.app.bus.common.BaseActivity2;
import com.scrat.app.bus.databinding.ActivityAboutBinding;
import com.scrat.app.bus.utils.Utils;

import java.util.Locale;

/**
 * Created by yixuanxuan on 2017/2/4.
 */

public class AboutActivity extends BaseActivity2 {
    public static void show(Context ctx) {
        Intent i = new Intent(ctx, AboutActivity.class);
        ctx.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAboutBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_about);
        String verName = Utils.getVersionName();
        int verCode = Utils.getVersionCode();
        String verInfo = String.format(Locale.getDefault(), "%s(%d)", verName, verCode);
        binding.ver.setText(verInfo);
    }

    public void back(View v) {
        finish();
    }
}
