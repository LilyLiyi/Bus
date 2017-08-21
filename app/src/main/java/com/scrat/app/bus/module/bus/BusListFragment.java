package com.scrat.app.bus.module.bus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.scrat.app.bus.R;
import com.scrat.app.bus.common.BaseFragment;
import com.scrat.app.bus.databinding.FrgHomeBinding;
import com.scrat.app.bus.model.BusStopInfo;
import com.scrat.app.bus.module.ConfigSharePreferences;
import com.scrat.app.bus.report.ViewReport;
import com.scrat.app.bus.utils.L;

import java.util.List;

/**
 * Created by yixuanxuan on 16/5/15.
 */
public class BusListFragment extends BaseFragment implements BusListContract.View {
    private BusListContract.Presenter mPresenter;
    private BusListAdapter mAdapter;
    private boolean mIsRefreshing;
    private boolean autoRefresh;
    private FrgHomeBinding binding;

    private static final String sKeyId = "bus_id";

    public static BusListFragment newInstance(String busId) {
        BusListFragment fragment = new BusListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(sKeyId, busId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String busId = getArguments().getString(sKeyId);
        mPresenter = new BusListPresenter(this, busId);
        mAdapter = new BusListAdapter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.changeOrder();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FrgHomeBinding.inflate(inflater, container, false);
        binding.refreshImg.getBackground().setAlpha(100);
        binding.refreshImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.init();
            }
        });
        binding.refreshBtn.getBackground().setAlpha(100);
        binding.refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.init();
            }
        });
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.list.setLayoutManager(manager);
        binding.list.setAdapter(mAdapter);
        binding.list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mIsRefreshing;
            }
        });

        binding.refreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.init();
            }
        });

        binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                binding.refreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });

        return binding.getRoot();
    }

    public void setAutoRefresh(boolean autoRefresh) {
        seconds = 0;
        this.autoRefresh = autoRefresh;
        refreshCheck();
    }

    private volatile int seconds;
    private static final int REFRESH_SECOND = 10;

    private void refreshCheck() {
        if (isDestroyed()) {
            return;
        }

        if (autoRefresh) {
            binding.refreshBtn.setVisibility(View.VISIBLE);
            binding.refreshImg.setVisibility(View.GONE);
            binding.refreshBtn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    L.d("seconds=" + seconds);
                    seconds++;
                    binding.refreshBtn.setText((REFRESH_SECOND - seconds) + "");
                    if (REFRESH_SECOND <= seconds) {
                        mPresenter.init();
                        seconds = 0;
                    }
                    refreshCheck();
                }
            }, 1000);
        } else {
            binding.refreshBtn.setVisibility(View.GONE);
            binding.refreshImg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.init();
        autoRefresh = ConfigSharePreferences.getInstance().isAutoRefreshBushList();
        refreshCheck();
        ViewReport.reportView(getContext(), "bus_list");
    }

    @Override
    public void onPause() {
        super.onPause();
        autoRefresh = false;
    }

    @Override
    public void onDestroy() {
        mPresenter = null;
        super.onDestroy();
    }

    @Override
    public void showBusStop(List<BusStopInfo> list, String beginTime, String endTime) {
        String from;
        String to;
        if (list.size() == 0) {
            String unknown = getString(R.string.unknown);
            from = unknown;
            to = unknown;
        } else {
            from = list.get(0).getBusStopName();
            to = list.get(list.size() - 1).getBusStopName();
        }
        mAdapter.setFrom(from);
        mAdapter.setTo(to);
        mAdapter.setList(list);
        if (!isDestroyed()) {
            // fix dump
            ((BusListActivity) getActivity()).setTitle(beginTime, endTime);
        }
    }

    @Override
    public void showNoBusOnline() {
        if (getView() == null)
            return;

        showMsg(getString(R.string.no_bus_online));
    }

    @Override
    public void onLoadDataError() {
        if (getView() == null)
            return;

        showMsg(getString(R.string.server_error));
    }

    @Override
    public void showLoading() {
        if (getView() == null)
            return;

        super.showLoading();
        loading(true);
        mIsRefreshing = true;
    }

    @Override
    public void hideLoading() {
        if (getView() == null)
            return;

        super.hideLoading();
        loading(false);
        mIsRefreshing = false;
    }

    private void loading(final boolean show) {
        if (getView() == null)
            return;

        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(show);
            }
        });
    }
}
