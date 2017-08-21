package com.scrat.app.bus;

import android.app.Application;
import android.content.Context;

/**
 * Created by yixuanxuan on 16/4/19.
 */
public class BusApp extends Application {
    private static BusApp mApp;

    public static Context getContext() {
        return mApp.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        if (BuildConfig.DEBUG) {
            com.squareup.leakcanary.LeakCanary.install(this);
        }
    }

}
