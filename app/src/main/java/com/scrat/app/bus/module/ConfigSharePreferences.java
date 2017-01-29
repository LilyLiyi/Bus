package com.scrat.app.bus.module;

import com.scrat.app.bus.BusApp;
import com.scrat.app.bus.common.BaseSharedPreferences;

/**
 * Created by yixuanxuan on 16/6/22.
 */
public class ConfigSharePreferences extends BaseSharedPreferences {

    private static class SingletonHolder {
        private static ConfigSharePreferences instance = new ConfigSharePreferences();
    }

    public static ConfigSharePreferences getInstance() {
        return SingletonHolder.instance;
    }

    private static final String FILE_NAME = "conf";
    public ConfigSharePreferences() {
        super(BusApp.getContext(), FILE_NAME);
    }

    private static final String KEY_CARD_ID = "card_id";
    public void setCardId(String cardId) {
        setString(KEY_CARD_ID, cardId);
    }
    public String getCardId() {
        return getString(KEY_CARD_ID, "");
    }

    private static final String KEY_LAST_SEARCH_BUS_NAME = "last_search_bus_name";
    public void setLastSearchBusName(String bus) {
        setString(KEY_LAST_SEARCH_BUS_NAME, bus);
    }
    public String getLastSearchBusName() {
        return getString(KEY_LAST_SEARCH_BUS_NAME, "");
    }

}
