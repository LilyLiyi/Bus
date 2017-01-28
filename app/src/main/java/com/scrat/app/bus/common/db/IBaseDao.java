package com.scrat.app.bus.common.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by yixuanxuan on 2017/1/28.
 */

public interface IBaseDao<T> {
    SQLiteDatabase getDatabase();

    String getTableName();

    void deleteAll();

    int countAll();

    List<T> findAll();
}
