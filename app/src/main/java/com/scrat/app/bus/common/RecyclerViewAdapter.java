package com.scrat.app.bus.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.scrat.app.core.common.BaseRecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yixuanxuan on 2017/1/27.
 */

public abstract class RecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {
    private static final int TYPE_CONTENT = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;

    protected List<T> list;
    private View header;
    private View footer;

    protected abstract BaseRecyclerViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType);

    public RecyclerViewAdapter() {
        list = new ArrayList<>();
    }

    public void clearData() {
        this.list.clear();
        notifyDataSetChanged();
    }

    public void replaceData(List<T> list) {
        this.list.clear();

        if (list == null)
            return;

        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void appendData(List<T> list) {
        if (list == null)
            return;

        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void appendData(T t) {
        if (t == null)
            return;

        list.add(t);
        notifyDataSetChanged();
    }

    public void setHeader(View v) {
        header = v;
        notifyDataSetChanged();
    }

    public void setFooter(View v) {
        footer = v;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (header != null && position == 0)
            return TYPE_HEADER;

        if (footer != null && header == null && list.size() == position)
            return TYPE_FOOTER;

        if (header != null && footer != null && list.size() + 1 == position)
            return TYPE_FOOTER;

        return TYPE_CONTENT;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            default:
            case TYPE_CONTENT:
                return onCreateContentViewHolder(parent, viewType);
            case TYPE_HEADER:
                return new BaseRecyclerViewHolder(header);
            case TYPE_FOOTER:
                return new BaseRecyclerViewHolder(footer);
        }
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        if (header != null && position == 0) {
            onBindHeaderViewHolder(holder);
            return;
        }

        int pos = position;
        if (header != null) {
            pos--;
        }
        if (footer != null && pos >= list.size()) {
            onBindFooterViewHolder(holder);
            return;
        }

        T t = list.get(pos);
        if (t == null)
            return;

        onBindContentViewHolder(holder, pos, t);
    }

    protected void onBindContentViewHolder(BaseRecyclerViewHolder holder, int position, T t) {

    }

    protected void onBindHeaderViewHolder(BaseRecyclerViewHolder holder) {

    }

    protected void onBindFooterViewHolder(BaseRecyclerViewHolder holder) {

    }

    private int getRealPos(final int position) {
        int pos = position;
        if (header != null)
            pos--;
        return pos;
    }

    @Override
    public int getItemCount() {
        int count = list.size();
        if (header != null) {
            count++;
        }
        if (footer != null) {
            count++;
        }
        return count;
    }
}
