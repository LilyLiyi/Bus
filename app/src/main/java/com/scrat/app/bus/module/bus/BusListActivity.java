package com.scrat.app.bus.module.bus;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.scrat.app.bus.R;
import com.scrat.app.bus.common.BaseActivity;
import com.scrat.app.core.utils.ActivityUtils;

/**
 * Created by yixuanxuan on 16/5/20.
 */
public class BusListActivity extends BaseActivity {
    private BusListFragment mFragment;
    private Toolbar mToolbar;

    private static final String sExtraKeyBusId = "bus_id";
    private static final String sExtraKeyBusName = "bus_name";

    public static void show(Context ctx, String busId, String busName) {
        Intent i = new Intent(ctx, BusListActivity.class);
        i.putExtra(sExtraKeyBusId, busId);
        i.putExtra(sExtraKeyBusName, busName);
        ctx.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        if (mFragment == null) {
            String busId = getIntent().getStringExtra(sExtraKeyBusId);
            String busName = getIntent().getStringExtra(sExtraKeyBusName);
            mToolbar.setTitle(busName);
            mFragment = BusListFragment.newInstance(busId);
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mFragment, R.id.contentFrame);
        }

        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
