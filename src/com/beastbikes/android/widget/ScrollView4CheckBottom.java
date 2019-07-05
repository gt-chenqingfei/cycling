package com.beastbikes.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by caoxiao on 16/4/10.
 */
public class ScrollView4CheckBottom extends ScrollView {
    private ScrollViewLoadMoreListener listener;

    public ScrollView4CheckBottom(Context context) {
        super(context);
    }

    public ScrollView4CheckBottom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewLoadMoreListener(ScrollViewLoadMoreListener listener) {
        this.listener = listener;
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (t + getHeight() >= computeVerticalScrollRange() && listener != null) {
            listener.loadMore();
        }
    }

    public interface ScrollViewLoadMoreListener {
        void loadMore();
    }
}
