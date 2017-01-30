package com.scrat.app.bus.report;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yixuanxuan on 2017/1/30.
 */

public class ClickReport {
    private static final String CLICK = "click";
    private static final String ITEM = "item";

    private ClickReport() {
    }

    public static void reportClick(Context ctx, String key) {
        Map<String, String> m = new HashMap<>();
        m.put(ITEM, key);
        MobclickAgent.onEventValue(ctx, CLICK, m, 1);
    }
}
