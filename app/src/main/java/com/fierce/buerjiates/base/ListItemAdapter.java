package com.fierce.buerjiates.base;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/11/28.
 */

public abstract class ListItemAdapter<T> extends BaseAdapter {

    private Context mContext;
    private List<T> mList;

    public ListItemAdapter(Context context, List<T> list) {
        mList = list;
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
