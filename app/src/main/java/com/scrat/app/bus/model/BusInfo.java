package com.scrat.app.bus.model;

/**
 * Created by yixuanxuan on 16/4/19.
 */
public class BusInfo {
    private String i;
    private String n;

    public BusInfo setBusId(String id) {
        i = id;
        return this;
    }

    public BusInfo setName(String name) {
        n = name;
        return this;
    }

    public String getBusId() {
        return i;
    }

    public String getBusName() {
        return n;
    }

    @Override
    public String toString() {
        return "BusInfo{" +
                "i='" + i + '\'' +
                ", n='" + n + '\'' +
                '}';
    }
}
