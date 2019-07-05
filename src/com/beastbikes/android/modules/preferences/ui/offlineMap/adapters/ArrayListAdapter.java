package com.beastbikes.android.modules.preferences.ui.offlineMap.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class ArrayListAdapter<T> extends BaseAdapter {

    protected Context context;
    protected List<T> datas;
    protected LayoutInflater inflater;

    public ArrayListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (datas == null) {
            return 0;
        }
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        if (datas == null || position < 0 || position > datas.size() - 1) {
            return null;
        }
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public boolean hasDownloadItem() {
        if (null == datas || 0 == datas.size()) {
            return false;
        } else {
            return true;
        }
    }

    public void setDatas(List<T> ds) {
        this.datas = ds;
        notifyDataSetChanged();
    }

    public void setArrayDatas(T[] array) {
        List<T> list = new ArrayList<T>();

        if (array != null && array.length > 0) {
            for (T t : array) {
                list.add(t);
            }
        }
        setDatas(list);
    }

    public void setItem(T one, int pos) {
        if (datas == null || pos < 0 || pos >= datas.size()) {
            return;

        } else {
            synchronized (datas) {
                datas.set(pos, one);
            }
        }

        notifyDataSetChanged();
    }

    public void addItems(List<T> list) {
        if (list == null || list.isEmpty())
            return;

        if (datas == null) {
            datas = new ArrayList<T>();
        }

        synchronized (datas) {
            this.datas.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void addItems(T[] array) {
        if (array == null || array.length < 1)
            return;

        if (datas == null) {
            datas = new ArrayList<T>();
        }

        synchronized (datas) {
            for (T value : array) {
                this.datas.add(value);
            }
        }
        notifyDataSetChanged();
    }

    public void addItemsPre(List<T> list) {
        if (list == null || list.isEmpty())
            return;

        if (datas == null) {
            datas = new ArrayList<T>();
        }

        synchronized (datas) {
            this.datas.addAll(0, list);
        }
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        if (item == null)
            return;

        if (datas == null) {
            datas = new ArrayList<T>();
        }

        synchronized (datas) {
            this.datas.add(item);
        }

        notifyDataSetChanged();
    }

    public void removeItem(T item) {
        if (item == null || datas == null)
            return;

        synchronized (datas) {
            this.datas.remove(item);
        }
        notifyDataSetChanged();
    }

    public void removeItem(int pos) {
        if (datas == null || pos < 0 || pos >= datas.size()) {
            return;

        } else {
            synchronized (datas) {
                this.datas.remove(pos);
            }
        }

        notifyDataSetChanged();
    }

    public void removeAll() {
        if (datas != null) {
            synchronized (datas) {
                datas.clear();
            }
        }

        notifyDataSetChanged();
    }

}