package com.scrat.app.bus.module.search;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.scrat.app.bus.R;
import com.scrat.app.bus.common.BaseActivity2;
import com.scrat.app.bus.common.RecyclerViewAdapter;
import com.scrat.app.bus.data.SearchHistoryDao;
import com.scrat.app.bus.databinding.SearchActivityBinding;
import com.scrat.app.bus.databinding.SearchHeaderBinding;
import com.scrat.app.bus.model.BusInfo;
import com.scrat.app.bus.module.bus.BusListActivity;
import com.scrat.app.bus.common.BaseRecyclerViewHolder;

import java.util.List;

/**
 * Created by yixuanxuan on 2017/1/27.
 */

public class SearchBusActivity extends BaseActivity2 implements SearchContract.SearchView {
    public static void show(Activity activity) {
        Intent i = new Intent(activity, SearchBusActivity.class);
        activity.startActivity(i);
    }

    private SearchActivityBinding mBinding;
    private SearchHeaderBinding mHeaderBinding;
    private MyAdapter mAdapter;
    private SearchContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.search_activity);
        initView();
        new SearchPresenter(this);
    }

    private void initView() {
        View header = getLayoutInflater().inflate(R.layout.search_header, null);
        header.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        header.setPadding(0, 10, 0, 10);
        mHeaderBinding = DataBindingUtil.bind(header);
        mAdapter = new MyAdapter(this);
        mAdapter.setHeader(header);
        mBinding.list.setLayoutManager(new LinearLayoutManager(this));
        mBinding.list.setHasFixedSize(true);
        mBinding.list.setAdapter(mAdapter);

        mBinding.searchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_SEARCH)
                    return false;

                search(mBinding.searchContent.getText().toString());
                return true;
            }
        });
        mBinding.searchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (start == 0 && after == 0) {
                    mBinding.clearBtn.setVisibility(View.GONE);
                    resetList();
                } else {
                    mBinding.clearBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = s.toString();
                if (TextUtils.isEmpty(content))
                    return;

                search(content);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void resetList() {
        mAdapter.clearData();
        mHeaderBinding.title.setText(R.string.search_content_required);
    }

    private void search(String content) {
        mPresenter.search(content);
    }

    @Override
    public void setPresenter(SearchContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onEmptyResult() {
        mHeaderBinding.title.setText(R.string.no_result);
        mAdapter.clearData();
    }

    @Override
    public void onSearchContentEmptyError() {
        showMsg(R.string.search_content_required);
    }

    @Override
    public void onSearching() {
        mHeaderBinding.title.setText(R.string.loading);
    }

    @Override
    public void onSearchFail(String msg) {
        showMsg(msg);
    }

    @Override
    public void onSearchSuccess(List<BusInfo> list) {
        String searchResultFormat = getString(R.string.search_result);
        mHeaderBinding.title.setText(String.format(searchResultFormat, list.size()));
        mAdapter.replaceData(list);
    }

    public void clearInput(View v) {
        mBinding.searchContent.setText("");
    }

    private static class MyAdapter extends RecyclerViewAdapter<BusInfo> {
        private Activity mActivity;
        private SearchHistoryDao mDao;

        private MyAdapter(Activity activity) {
            mActivity = activity;
            mDao = new SearchHistoryDao(activity);
        }

        @Override
        protected BaseRecyclerViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_search_result, parent, false);
            return new BaseRecyclerViewHolder(view);
        }

        @Override
        protected void onBindContentViewHolder(BaseRecyclerViewHolder holder, int position, final BusInfo busInfo) {
            holder.setText(R.id.tv_name, busInfo.getBusName());
            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDao.saveOrUpdate(busInfo);
                    BusListActivity.show(mActivity, busInfo.getBusId(), busInfo.getBusName());
                    mActivity.finish();
                }
            });
        }
    }
}
