package com.beastbikes.framework.ui.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.beastbikes.framework.ui.android.R;

public class ScrollableViewPager extends ViewPager {

    private final boolean scrollable;

    public ScrollableViewPager(Context context) {
        this(context, null);
    }

    public ScrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.ScrollableViewPager);
        this.scrollable = ta.getBoolean(
                R.styleable.ScrollableViewPager_scrollable, false);
        ta.recycle();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.scrollable) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

}
