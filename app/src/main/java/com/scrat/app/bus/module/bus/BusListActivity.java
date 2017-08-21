package com.scrat.app.bus.module.bus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.scrat.app.bus.R;
import com.scrat.app.bus.common.BaseActivity;
import com.scrat.app.bus.module.ConfigSharePreferences;
import com.scrat.app.bus.utils.ActivityUtils;

/**
 * Created by yixuanxuan on 16/5/20.
 */
public class BusListActivity extends BaseActivity {
    private BusListFragment mFragment;

    private static final String sExtraKeyBusId = "bus_id";
    private static final String sExtraKeyBusName = "bus_name";

    public static void show(Context ctx, String busId, String busName) {
        Intent i = new Intent(ctx, BusListActivity.class);
        i.putExtra(sExtraKeyBusId, busId);
        i.putExtra(sExtraKeyBusName, busName);
        ctx.startActivity(i);
    }

    private Toolbar toolbar;
    private String busName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_base);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        if (mFragment == null) {
            String busId = getIntent().getStringExtra(sExtraKeyBusId);
            busName = getIntent().getStringExtra(sExtraKeyBusName);
            toolbar.setTitle(busName);
            mFragment = BusListFragment.newInstance(busId);
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mFragment, R.id.contentFrame);
        }

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bus_list, menu);
        MenuItem item = menu.findItem(R.id.auto_refresh);
        item.setChecked(ConfigSharePreferences.getInstance().isAutoRefreshBushList());
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(!item.isChecked());
                ConfigSharePreferences.getInstance().setAutoRefreshBusList(item.isChecked());
                mFragment.setAutoRefresh(item.isChecked());
                if (item.isChecked()) {
                    showToast("已开启自动刷新");
                } else {
                    showToast("已关闭自动刷新");
                }
                return false;
            }
        });
        return true;
    }

    public void setTitle(String beginTime, String endTime) {
        if (TextUtils.isEmpty(beginTime)) {
            toolbar.setTitle(busName);
        } else {
            toolbar.setTitle(busName + "（" + beginTime + "-" + endTime + "）");
        }
    }
}
