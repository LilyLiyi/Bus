package com.scrat.app.bus.common.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Created by yixuanxuan on 2017/1/28.
 */

public class SQLiteManager extends SQLiteOpenHelper {
    private static volatile SQLiteManager instance;
    private Context mContext;

    private static DatabaseConfig mConfig;
    private static volatile SQLiteDatabase db;

    public SQLiteManager(Context context, String name, int version) {
        super(context, name, null, version);
        mContext = context;
    }

    public static synchronized SQLiteDatabase getDB(Context context, DatabaseConfig config) {
        if (db == null) {
            mConfig = config;
            if (instance == null) {
                instance = new SQLiteManager(context, config.getDatabaseName(), config.getDatabaseVersion());
            }
            db = instance.getWritableDatabase();
        }
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<Class<? extends SQLiteTable>> classes = mConfig.getTables(mContext);
        for (Class<? extends SQLiteTable> clazz : classes) {
            try {
                Constructor<? extends SQLiteTable> con = clazz.getConstructor(Context.class);
                SQLiteTable table = con.newInstance(mContext);
                table.onCreate(db);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        List<Class<? extends SQLiteTable>> classes = mConfig.getTables(mContext);
        for (Class<? extends SQLiteTable> clazz : classes) {
            try {
//                Logger.i("now onUpgrade Sqlite!");
                Constructor<? extends SQLiteTable> con = clazz.getConstructor(Context.class);
                SQLiteTable table = con.newInstance(mContext);
                table.onUpdate(db, oldVersion, newVersion);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * DAO表接口
     */
    public interface SQLiteTable {
        String DESC = "DESC";
        String ASC = "ASC";
        String INTEGER_PRIMARY_KEY = "INTEGER PRIMARY KEY";
        String FLOAT = "FLOAT";
        String TEXT = "TEXT";
        String INT = "INT";
        String LONG = "LONG";
        String UNIQUE = "UNIQUE";

        void onCreate(SQLiteDatabase database);

        void onUpdate(SQLiteDatabase database, int oldVersion, int newVersion);
    }

}