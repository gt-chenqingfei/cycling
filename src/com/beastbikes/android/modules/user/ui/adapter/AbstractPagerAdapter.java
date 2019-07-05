package com.beastbikes.android.modules.user.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 *
 * Created by secret on 16/9/29.
 */
public abstract class AbstractPagerAdapter<T> extends PagerAdapter {

    protected ArrayList<T> list;

    private SparseArray<View> viewSparseArray;

    public AbstractPagerAdapter(ArrayList<T> list) {
        this.list = list;
        viewSparseArray = new SparseArray<>(list.size());
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = viewSparseArray.get(position);
        if (null == view) {
            view = onCreateView(position);
            viewSparseArray.put(position, view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewSparseArray.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public T getItem(int position) {
        return list.get(position);
    }

    protected abstract View onCreateView(int position);

    public void resetView() {
        viewSparseArray.clear();
    }
}
