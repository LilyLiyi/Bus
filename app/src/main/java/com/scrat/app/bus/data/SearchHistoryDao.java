package com.scrat.app.bus.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.scrat.app.bus.common.db.AppDatabaseConfig;
import com.scrat.app.bus.common.db.BaseDao;
import com.scrat.app.bus.model.BusInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yixuanxuan on 2017/1/28.
 */

public class SearchHistoryDao extends BaseDao<BusInfo> {

    public SearchHistoryDao(Context context) {
        super(BusHistoryEntry.TABLE_NAME, context.getApplicationContext(), AppDatabaseConfig.getInstance());
    }

    @Override
    protected BusInfo findByCursor(Cursor cursor, int i) {
        BusInfo info = new BusInfo();
        info.setBusId(getStringFromCursor(cursor, BusHistoryEntry.BUS_ID));
        info.setName(getStringFromCursor(cursor, BusHistoryEntry.NAME));
        return info;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Map<String, String> cols = new HashMap<>();
        cols.put(BusHistoryEntry._ID, INTEGER_PRIMARY_KEY);
        cols.put(BusHistoryEntry.BUS_ID, TEXT + " " + UNIQUE);
        cols.put(BusHistoryEntry.NAME, TEXT);
        cols.put(BusHistoryEntry.CREATE_AT, LONG);
        cols.put(BusHistoryEntry.UPDATE_AT, LONG);
        createTable(database, cols);
    }

    private ContentValues getInsertCountValues(BusInfo info) {
        ContentValues values = new ContentValues();
        values.put(BusHistoryEntry.BUS_ID, info.getBusId());
        values.put(BusHistoryEntry.NAME, info.getBusName());
        long nowTs = new Date().getTime();
        values.put(BusHistoryEntry.CREATE_AT, nowTs);
        values.put(BusHistoryEntry.UPDATE_AT, nowTs);
        return values;
    }

    public void saveOrUpdate(BusInfo info) {
        if (save(info))
            return;

        update(info);
    }

    public void update(String busId) {
        ContentValues values = new ContentValues();
        values.put(BusHistoryEntry.UPDATE_AT, new Date().getTime());
        getDatabase().update(getTableName(), values, BusHistoryEntry.BUS_ID + "=?", new String[]{busId});
    }

    public void delete(String busId) {
        getDatabase().delete(getTableName(), BusHistoryEntry.BUS_ID + "=?", new String[]{busId});
    }

    @Override
    public List<BusInfo> findAll() {
        return findAll(getDatabase(), BusHistoryEntry.UPDATE_AT + " " + DESC + "," + BusHistoryEntry.CREATE_AT + " " + DESC);
    }

    private boolean save(BusInfo info) {
        ContentValues values = getInsertCountValues(info);
        return save(values) > 0;
    }

    private void update(BusInfo info) {
        update(info.getBusId());
    }

    private static abstract class BusHistoryEntry implements BaseColumns {
        private static final String TABLE_NAME = "search_history";
        private static final String BUS_ID = "bus_id";
        private static final String NAME = "name";
        private static final String CREATE_AT = "create_at";
        private static final String UPDATE_AT = "update_at";
    }
}