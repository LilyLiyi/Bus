package com.scrat.app.bus.common.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yixuanxuan on 2017/1/28.
 */

public abstract class BaseDao<T> implements SQLiteManager.SQLiteTable {
    public static final String[] ALL_COLS = new String[]{"*"};
    protected Context mContext;
    private String mTableName;
    private DatabaseConfig mConfig;
    private List<Class<? extends IPatcher<T>>> mPatcherList;

    public BaseDao(String tableName, Context context, DatabaseConfig config) {
        mContext = context;
        mTableName = tableName;
        mConfig = config;
    }

    /**
     * 获取sqlite数据
     */
    public SQLiteDatabase getDatabase() {
        return SQLiteManager.getDB(mContext, mConfig);
    }

    /**
     * 从游标中取一个对
     */
    protected abstract T findByCursor(Cursor cursor, int i);

    @Override
    public final void onUpdate(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (this.mPatcherList != null) {
            for (Class<? extends IPatcher<T>> patcherClazz : mPatcherList) {
                IPatcher<T> inst;
                try {
                    inst = patcherClazz.newInstance();
                    int max = inst.getSupportMaxVersion();
                    if (oldVersion <= max) {
                        inst.execute(this, database, getContext());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据游标获取列表
     */
    public List<T> findListByCursor(Cursor c) {
        List<T> list = new ArrayList<>();
        try {
            if (c != null && c.getCount() > 0) {
                int i = 0;
                while (c.moveToNext()) {
                    T t = findByCursor(c, i);
                    list.add(t);
                    i++;
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return list;
    }

    /**
     * 根据游标获取指定个数的列
     */
    public List<T> findListByCursor(Cursor cursor, int size) {
        List<T> ret = new ArrayList<>();
        cursor.moveToFirst();
        int n = 0;
        try {
            while (!cursor.isAfterLast() && (n < size)) {
                T t = findByCursor(cursor, n);
                ret.add(t);
                cursor.moveToNext();
                n++;
            }
        } finally {
            cursor.close();
        }
        return ret;
    }

    public List<T> findListByCursor(Cursor cursor, RowMapper<T> mapper) {
        List<T> ret = new ArrayList<T>();
        cursor.moveToFirst();
        int n = 0;
        while (!cursor.isAfterLast()) {
            T t = findByCursor(cursor, n);
            if (mapper != null) {
                mapper.map(t, n);
            }
            ret.add(t);
            cursor.moveToNext();
            n++;
        }
        cursor.close();
        return ret;
    }

    public void findByCursor(Cursor cursor, OnEachListener<T> listener) {
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            T t = findByCursor(cursor, 0);
            if (listener != null) {
                listener.onEach(t);
            }
            cursor.moveToNext();
        }
        cursor.close();
    }

    /**
     * 根据表明和排序获取指定列的列
     *
     * @param orderBy 排序id desc createAt asc 无需增加order by
     */
    public List<T> findAll(String orderBy, String... rows) {
        Cursor cursor = getDatabase().query(mTableName, rows, null, null, null, null, orderBy);
        return findListByCursor(cursor);
    }

    public List<T> findAll(SQLiteDatabase database, String orderBy) {
        Cursor cursor = database.query(getTableName(), ALL_COLS, null, null, null, null, orderBy);
        return findListByCursor(cursor);
    }

    public List<T> findAll(String orderBy, int start, int count, String... rows) {
        Cursor cursor = getDatabase().query(mTableName, rows, null, null, null, null, orderBy, start + "," + count);
        return findListByCursor(cursor);
    }

    /**
     * 删除全部
     */
    public void deleteAll() {
        getDatabase().delete(mTableName, null, null);
    }

    protected void deleteAll(SQLiteDatabase database) {
        database.delete(mTableName, null, null);
    }

    public String asString(Object object) {
        if (object != null) {
            return object.toString();
        }
        return null;
    }

    protected void dropTable(SQLiteDatabase database) {
        String sql = "DROP TABLE IF EXISTS " + mTableName;
        database.execSQL(sql);
    }

    /**
     * 执行sql语句
     */
    protected void execSQLs(SQLiteDatabase database, List<String> sqls) {
        if (sqls == null)
            return;

        for (String sql : sqls) {
            database.execSQL(sql);
        }
    }

    public List<T> findAll() {
        Cursor cursor = getDatabase().query(mTableName, ALL_COLS, null, null, null, null, null);
        return findListByCursor(cursor);
    }

    public int countAll() {
        Cursor cursor = getDatabase().query(mTableName, new String[]{"COUNT(1)"}, null, null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }
        return 0;
    }

    /**
     * 创建表
     */
    protected void createTable(SQLiteDatabase database, Map<String, String> columns) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(mTableName).append("(");
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            sql.append(entry.getKey()).append(" ").append(entry.getValue());
            sql.append(",");
        }
        sql.deleteCharAt(sql.lastIndexOf(","));
        sql.append(")");
        database.execSQL(sql.toString());
    }

    protected long save(ContentValues values) {
        return getDatabase().insert(mTableName, null, values);
    }

    protected long save(SQLiteDatabase database, ContentValues values) {
        return database.insert(getTableName(), null, values);
    }

    public void addColumns(SQLiteDatabase database, Map<String, String> columns) {
        if (columns == null)
            return;

        Object[] keys = columns.keySet().toArray();
        for (Object key : keys) {
            String sql = "ALTER TABLE " + mTableName + " ADD `" + key.toString() + "` " + columns.get(key);
            database.execSQL(sql);
        }
    }

    public String getTableName() {
        return mTableName;
    }

    protected Cursor query(String[] columns, String selection, String[] selectionArgs, String orderBy, String limit) {
        return getDatabase().query(getTableName(), columns, selection, selectionArgs, null, null, orderBy, limit);
    }

    protected Cursor query(String[] columns, String selection, String[] selectionArgs, String limit) {
        return getDatabase().query(getTableName(), columns, selection, selectionArgs, null, null, null, limit);
    }

    /**
     * 查找对象
     */
    protected T find(SQLiteDatabase database, String[] columns, String selection, String[] selectionArgs) {
        T ret = null;
        Cursor cursor = database.query(getTableName(), columns, selection, selectionArgs, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                ret = findByCursor(cursor, 0);
            }
        } finally {
            cursor.close();
        }
        return ret;
    }

    protected T find(String[] columns, String selection, String[] selectionArgs) {
        return find(getDatabase(), columns, selection, selectionArgs);
    }

    protected List<T> findList(String[] columns, String selection, String[] selectionArgs, String orderBy) {
        Cursor cursor = getDatabase().query(getTableName(), columns, selection,
                selectionArgs, null, null, orderBy);
        return findListByCursor(cursor);
    }

    protected List<T> findList(String[] columns, String selection, String[] selectionArgs, String orderBy, String limit) {
        Cursor cursor = getDatabase().query(getTableName(), columns, selection, selectionArgs, null, null, orderBy, limit);
        return findListByCursor(cursor);
    }

    protected int count(String selection, String[] selectionArgs) {
        Cursor cursor = getDatabase().query(getTableName(), new String[]{"count(1)"}, selection, selectionArgs, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }
        return 0;
    }

    protected Context getContext() {
        return mContext;
    }

    public interface RowMapper<T> {
        void map(T t, int index);
    }

    public interface OnEachListener<T> {
        void onEach(T t);
    }

    /**
     * 注册补丁类,此类必须拥有用无参数的构造函数
     */
    public BaseDao<T> registerPatcher(Class<? extends IPatcher<T>> patcherClazz) {
        if (mPatcherList == null) {
            mPatcherList = new ArrayList<>();
        }
        mPatcherList.add(patcherClazz);
        return this;
    }

    protected final boolean exist(SQLiteDatabase database, String selection, String[] args) {
        Cursor cursor = database.query(getTableName(), ALL_COLS, selection, args, null, null, null);
        try {
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    protected final boolean exist(String selection, String[] args) {
        return exist(getDatabase(), selection, args);
    }

    protected int getIntFromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    protected String getStringFromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

    protected Long getLongFromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getLong(columnIndex);
    }


}
