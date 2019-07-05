package com.beastbikes.android.modules.cycling.activity.ui.record;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * Created by chenqingfei on 15/12/3.
 */
public abstract class RecordBase<K> extends LinearLayout {

    protected Context mContext;
    private K mData;

    public RecordBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public RecordBase(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public void onCreateView() {

    }

    public void onDataChanged(K k) {
        this.mData = k;
    }

    public abstract int getLayRes();

    private void initView() {
        int layoutRes = getLayRes();
        LayoutInflater.from(this.getContext()).inflate(layoutRes, this);
        onCreateView();
    }


}