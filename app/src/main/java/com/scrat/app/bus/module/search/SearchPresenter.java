package com.scrat.app.bus.module.search;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.scrat.app.bus.BusApp;
import com.scrat.app.bus.R;
import com.scrat.app.bus.model.BusInfo;
import com.scrat.app.bus.net.NetApi;
import com.scrat.app.core.net.GsonParser;
import com.scrat.app.core.net.ResponseCallback;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by yixuanxuan on 2017/1/27.
 */

public class SearchPresenter implements SearchContract.Presenter {
    private SearchContract.SearchView mView;
    private Call mCall;

    public SearchPresenter(SearchContract.SearchView view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void search(String content) {
        if (TextUtils.isEmpty(content)) {
            mView.onSearchContentEmptyError();
            return;
        }

        if (mCall != null && !mCall.isCanceled()) {
            mCall.cancel();
        }

        mView.onSearching();
        mCall = NetApi.getBusInfo(content, new ResponseCallback<List<BusInfo>>() {

            @Override
            protected void onRequestSuccess(List<BusInfo> list) {
                if (list == null || list.isEmpty()) {
                    mView.onEmptyResult();
                    return;
                }

                mView.onSearchSuccess(list);
            }

            @Override
            protected List<BusInfo> parseResponse(Response response) {
                return GsonParser.getInstance().getGson().fromJson(response.body().charStream(), new TypeToken<List<BusInfo>>(){}.getType());
            }

            @Override
            protected void onRequestFailure(Exception e) {
                mView.onSearchFail(BusApp.getContext().getString(R.string.server_error));
            }
        });
    }
}
