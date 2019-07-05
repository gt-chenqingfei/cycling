package com.beastbikes.android.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by caoxiao on 16/4/9.
 */
public class PullRefreshListView4ScrollView extends PullRefreshListView {

    public PullRefreshListView4ScrollView(Context context) {
        super(context);
    }

    public PullRefreshListView4ScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
