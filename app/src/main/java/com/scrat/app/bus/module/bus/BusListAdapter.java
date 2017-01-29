package com.scrat.app.bus.module.bus;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.scrat.app.bus.R;
import com.scrat.app.bus.model.BusStopInfo;
import com.scrat.app.bus.common.BaseRecyclerViewAdapter;
import com.scrat.app.bus.common.BaseRecyclerViewHolder;

import java.lang.ref.WeakReference;

/**
 * Created by yixuanxuan on 16/5/14.
 */
public class BusListAdapter extends BaseRecyclerViewAdapter<BusStopInfo, BaseRecyclerViewHolder> {
    private View.OnClickListener mOnHeaderClickListener;
    private String mFrom;
    private String mTo;

    public BusListAdapter(View.OnClickListener onClickListener) {
        super(true, false);
        mOnHeaderClickListener = new WeakReference<>(onClickListener).get();
    }

    public void setFrom(String from) {
        this.mFrom = from;
    }

    public void setTo(String to) {
        this.mTo = to;
    }

    @Override
    protected void onBindHeaderViewHolder(BaseRecyclerViewHolder holder) {
        holder.setText(R.id.tv_from, mFrom)
                .setText(R.id.tv_to, mTo);
    }

    @Override
    protected BaseRecyclerViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bus_route, parent, false);
        if (mOnHeaderClickListener != null) {
            view.setOnClickListener(mOnHeaderClickListener);
        }
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        return new BaseRecyclerViewHolder(view);
    }

    @Override
    protected BaseRecyclerViewHolder onCreateRecycleItemView(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bus, parent, false);
        return new BaseRecyclerViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position, BusStopInfo busStopInfo) {
        holder.setText(R.id.tv_name, busStopInfo.getBusStopName())
                .setVisibility(R.id.ic_curr_stop,
                        busStopInfo.isArrivaled() ? View.VISIBLE : View.INVISIBLE)
                .setVisibility(R.id.ic_leaving_stop,
                        busStopInfo.isLeaving() ? View.VISIBLE : View.INVISIBLE);
    }


}
