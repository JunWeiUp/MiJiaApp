package com.handongkeji.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2017/7/18 0018.
 */

public abstract class QPagerAdapter<T> extends PagerAdapter {
    protected List<T> mList;
    protected SparseArray<View> items;


    public QPagerAdapter() {
        this(null);
    }

    public QPagerAdapter(List<T> dataList) {
        this.mList = dataList == null ? new ArrayList<T>() : dataList;
        items = new SparseArray<>();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = items.get(position);
        T t = mList.get(position);
        if (view == null) {
            view = getView(LayoutInflater.from(container.getContext()));
            items.put(position, view);
        }
        bindView(t,view);
        container.addView(view);
        return view;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public abstract View getView(LayoutInflater inflater);

    public abstract void bindView(T t,View itemView);
}
