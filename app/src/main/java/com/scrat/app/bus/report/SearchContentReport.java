package com.scrat.app.bus.report;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yixuanxuan on 2017/1/30.
 */

public class SearchContentReport {
    private static final String SEARCH_CONTENT = "search_content";

    private static final String SEARCH_TEXT = "searchText";
    private static final String CLICK = "click";
    private static final String FAIL = "fail";

    private SearchContentReport() {
    }

    public static void reportSearchText(Context ctx, String content) {
        Map<String, String> m = new HashMap<>();
        m.put(SEARCH_TEXT, content);
        MobclickAgent.onEventValue(ctx, SEARCH_CONTENT, m, 1);
    }

    public static void reportClick(Context ctx, String busName) {
        Map<String, String> m = new HashMap<>();
        m.put(CLICK, busName);
        MobclickAgent.onEventValue(ctx, SEARCH_CONTENT, m, 1);
    }

    public static void reportFail(Context ctx, String content) {
        Map<String, String> m = new HashMap<>();
        m.put(FAIL, content);
        MobclickAgent.onEventValue(ctx, SEARCH_CONTENT, m, 1);
    }
}
