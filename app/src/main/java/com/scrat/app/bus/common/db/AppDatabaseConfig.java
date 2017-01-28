package com.scrat.app.bus.common.db;

import android.content.Context;

import com.scrat.app.bus.data.SearchHistoryDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yixuanxuan on 2017/1/28.
 */

public class AppDatabaseConfig implements DatabaseConfig {
    private static final String DB_NAME = "bus.db";
    private static final int DB_VER = 1;

    private static class SingletonHolder {
        private static AppDatabaseConfig instance = new AppDatabaseConfig();
    }

    public static AppDatabaseConfig getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public String getDatabaseName() {
        return DB_NAME;
    }

    @Override
    public int getDatabaseVersion() {
        return DB_VER;
    }

    @Override
    public List<Class<? extends SQLiteManager.SQLiteTable>> getTables(Context context) {
        List<Class<? extends SQLiteManager.SQLiteTable>> ret = new ArrayList<>();
        ret.add(SearchHistoryDao.class);
        return ret;
    }
}
