package com.scrat.app.bus.common.db;

import android.content.Context;

import java.util.List;

/**
 * Created by yixuanxuan on 2017/1/28.
 */

public interface DatabaseConfig {
    /**
     * 获取数据库名
     */
    String getDatabaseName();

    /**
     * 获取数据库版本号
     */
    int getDatabaseVersion();

    /**
     * 获取数据库库中所有表的类
     */
    List<Class<? extends SQLiteManager.SQLiteTable>> getTables(Context context);
}