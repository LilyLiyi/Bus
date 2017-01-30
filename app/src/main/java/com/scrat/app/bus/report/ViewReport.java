package com.scrat.app.bus.report;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yixuanxuan on 2017/1/30.
 */

public class ViewReport {
    private static final String VIEW = "view";
    private static final String PAGE = "page";

    private ViewReport() {
    }

    public static void reportView(Context ctx, String content) {
        Map<String, String> m = new HashMap<>();
        m.put(PAGE, content);
        MobclickAgent.onEventValue(ctx, VIEW, m, 1);
    }
}
