package com.scrat.app.bus.module.search;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scrat.app.bus.R;
import com.scrat.app.bus.common.BaseFragment;
import com.scrat.app.bus.common.RecyclerViewAdapter;
import com.scrat.app.bus.data.SearchHistoryDao;
import com.scrat.app.bus.databinding.FrgSearchBinding;
import com.scrat.app.bus.model.BusInfo;
import com.scrat.app.bus.module.bus.BusListActivity;
import com.scrat.app.core.common.BaseRecyclerViewHolder;

import java.util.Iterator;
import java.util.List;

/**
 * Created by yixuanxuan on 16/5/20.
 */
public class SearchFragment extends BaseFragment {

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    private FrgSearchBinding mBinding;
    private SearchHistoryDao mDao;
    private Adapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDao = new SearchHistoryDao(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frg_search, container, false);
        mBinding = DataBindingUtil.bind(root);

        initList();

        mBinding.guideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchBusActivity.show(getActivity());
            }
        });

        return root;
    }

    private void initList() {
        mBinding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new Adapter(getActivity(), mDao, mBinding);
        View header = LayoutInflater.from(getContext()).inflate(R.layout.header_search_history, null);
        header.setPadding(0, 35, 0, 10);
        mAdapter.setHeader(header);
        mBinding.list.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<BusInfo> list = mDao.findAll();
        mAdapter.replaceData(list);
        if (list.size() == 0) {
            mBinding.guideBtn.setVisibility(View.VISIBLE);
            mBinding.list.setVisibility(View.GONE);
        } else {
            mBinding.guideBtn.setVisibility(View.GONE);
            mBinding.list.setVisibility(View.VISIBLE);
        }
    }

    private static class Adapter extends RecyclerViewAdapter<BusInfo> {
        private Activity mActivity;
        private SearchHistoryDao mDao;
        private FrgSearchBinding mBinding;
        private Adapter(Activity activity, SearchHistoryDao dao, FrgSearchBinding binding) {
            mActivity = activity;
            mDao = dao;
            mBinding = binding;
        }

        @Override
        protected BaseRecyclerViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false);
            return new BaseRecyclerViewHolder(v);
        }

        @Override
        protected void onBindContentViewHolder(BaseRecyclerViewHolder holder, int position, final BusInfo info) {
            holder.setText(R.id.title, info.getBusName())
                    .setOnClickListener(R.id.close, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            remove(info.getBusId());
                        }
                    })
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDao.update(info.getBusId());
                            BusListActivity.show(mActivity, info.getBusId(), info.getBusName());
                        }
                    });
        }

        private void remove(String busId) {
            mDao.delete(busId);
            for (Iterator<BusInfo> iterator = list.iterator(); iterator.hasNext(); ) {
                BusInfo info = iterator.next();
                if (info.getBusId().equals(busId)) {
                    iterator.remove();
                    break;
                }
            }
            notifyDataSetChanged();
            if (list.size() == 0) {
                mBinding.guideBtn.setVisibility(View.VISIBLE);
                mBinding.list.setVisibility(View.GONE);
            }
        }
    }

}
