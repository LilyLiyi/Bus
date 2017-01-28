package com.scrat.app.core.common;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by yixuanxuan on 16/5/14.
 */
@Deprecated
public abstract class BaseRecyclerViewAdapter<Item, Holder extends BaseRecyclerViewHolder> extends RecyclerView.Adapter<Holder> {

    protected abstract Holder onCreateRecycleItemView(ViewGroup parent, int viewType);
    protected abstract void onBindItemViewHolder(Holder holder, int position, Item item);

    protected static final int sViewTypeHeader = 0;
    protected static final int sViewTypeContent = 1;
    protected static final int sViewTypeFooter = 2;

    private List<Item> mList;

    private boolean mHasFooter;
    private boolean mHasHeader;

    public BaseRecyclerViewAdapter(boolean hasHeader, boolean hasFooter) {
        mHasHeader = hasHeader;
        mHasFooter = hasFooter;
    }

    public BaseRecyclerViewAdapter() {
    }

    @Override
    public int getItemViewType(int position) {
        if (!mHasHeader && !mHasFooter) // 无Header, 无Footer
            return sViewTypeContent;

        if (mHasHeader && !mHasFooter) {// 只有Header
            if (position == 0)
                return sViewTypeHeader;
            return sViewTypeContent;
        }

        if (!mHasHeader && mHasFooter) { // 只有Footer
            if (mList.size() == position)
                return sViewTypeFooter;
            return sViewTypeContent;
        }

        // 有Footer 有Header
        if (position == 0)
            return sViewTypeHeader;
        if (mList.size() + 1 == position)
            return sViewTypeFooter;
        return sViewTypeContent;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (mHasHeader && position == 0) {
            onBindHeaderViewHolder(holder);
            return;
        }

        int realPos = getRealPosition(position);
        Item item = getItem(realPos);
        if (item == null) {
            if (mHasFooter) {
                onBindFooterViewHolder(holder);
            }
            return;
        }

        onBindItemViewHolder(holder, realPos, item);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == sViewTypeFooter)
            return onCreateFooterViewHolder(parent);

        if (viewType == sViewTypeHeader)
            return onCreateHeaderViewHolder(parent);

        return onCreateRecycleItemView(parent, viewType);
    }

    protected Holder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    protected Holder onCreateFooterViewHolder(ViewGroup parent) {
        return null;
    }

    protected void onBindFooterViewHolder(Holder holder) {
    }

    protected void onBindHeaderViewHolder(Holder holder) {
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mList != null) {
            count = mList.size();
        }

        if (mHasFooter) {
            count++;
        }

        if (mHasHeader) {
            count++;
        }

        return count;
    }

    public void clear() {
        if (mList == null)
            return;

        mList.clear();
        notifyDataSetChanged();
    }

    private int getRealPosition(int position) {
        int realPos = position;
        if (mHasHeader) {
            realPos--;
        }
        return realPos;
    }

    public void setList(List<Item> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void addItem(Item item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void addItem(Item item, int location) {
        mList.add(location, item);
        notifyDataSetChanged();
    }

    public void addList(List<Item> list) {
        if (list == null || list.size() == 0)
            return;

        if (mList == null) {
            setList(list);
            return;
        }

        mList.addAll(list);
        notifyDataSetChanged();
    }

    private Item getItem(int realPos) {
        if (mList == null)
            return null;

        if (realPos + 1 > mList.size())
            return null;

        return mList.get(realPos);
    }
}
