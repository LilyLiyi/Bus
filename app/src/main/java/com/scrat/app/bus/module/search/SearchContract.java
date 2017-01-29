package com.scrat.app.bus.module.search;

import com.scrat.app.bus.model.BusInfo;

import java.util.List;

/**
 * Created by yixuanxuan on 16/5/21.
 */
public interface SearchContract {
    interface Presenter {
        void search(String content);
    }

    interface SearchView {
        void setPresenter(Presenter presenter);

        void onEmptyResult();
        void onSearchContentEmptyError();
        void onSearching();
        void onSearchFail(String msg);
        void onSearchSuccess(List<BusInfo> list);
    }
}
