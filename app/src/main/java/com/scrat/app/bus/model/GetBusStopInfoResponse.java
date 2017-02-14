package com.scrat.app.bus.model;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by yixuanxuan on 16/4/19.
 */
public class GetBusStopInfoResponse {
    private String rn;
    private String d;
    private String c;
    private String ft;
    private String lt;
    private List<BusStopInfo> l;

    public String getBusName() {
        return rn;
    }

    public List<BusStopInfo> getBusStopInfoList() {
        return l;
    }

    public String getBeginTime() {
        return getTime(ft);
    }

    public String getEndTime() {
        return getTime(lt);
    }

    private String getTime(String t) {
        if (TextUtils.isEmpty(t)) {
            return "";
        }

        if (t.length() < 4) {
            return t;
        }

        return t.substring(0, 2) + ":" + t.substring(2, t.length());
    }

    @Override
    public String toString() {
        return "GetBusStopInfoResponse{" +
                "rn='" + rn + '\'' +
                ", d='" + d + '\'' +
                ", c='" + c + '\'' +
                ", ft='" + ft + '\'' +
                ", lt='" + lt + '\'' +
                ", l=" + l +
                '}';
    }
}
