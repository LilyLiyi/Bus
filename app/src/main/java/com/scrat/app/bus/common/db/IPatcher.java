package com.scrat.app.bus.common.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by yixuanxuan on 2017/1/28.
 */

public interface IPatcher<T> {
    /**
     * 原有数据库版本 小于等于这个值都将被执行
     */
    int getSupportMaxVersion();

    /**
     * 执行数据库补丁
     */
    void execute(BaseDao<T> baseDAO, SQLiteDatabase database, Context context);
}
