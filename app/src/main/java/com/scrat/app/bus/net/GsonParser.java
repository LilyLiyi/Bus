package com.scrat.app.bus.net;

import com.google.gson.Gson;

/**
 * Created by yixuanxuan on 16/3/31.
 */
public class GsonParser {
    private static class SingletonHolder {
        private static GsonParser instance = new GsonParser();
    }

    public static GsonParser getInstance(){
        return SingletonHolder.instance;
    }

    private Gson gson;

    private GsonParser() {
        gson = new Gson();
    }

    public Gson getGson() {
        return gson;
    }

}
