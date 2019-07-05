package com.beastbikes.framework.ui.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class FlatListView extends ListView {

    public FlatListView(Context context) {
        this(context, null);
    }

    public FlatListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlatListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
