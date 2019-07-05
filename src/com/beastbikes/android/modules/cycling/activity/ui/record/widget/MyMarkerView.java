package com.beastbikes.android.modules.cycling.activity.ui.record.widget;

import android.content.Context;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 *
 * Created by secret on 16/10/20.
 */

public class MyMarkerView extends MarkerView {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

    }

//    @Override
//    public MPPointF getOffset() {
//        return new MPPointF(-(getWidth() / 2), -getHeight() / 2);
////        return super.getOffset();
//    }

    @Override
    public int getXOffset(float xpos) {
        return -getWidth() / 2;
    }

    @Override
    public int getYOffset(float ypos) {
        return -getHeight() / 2;
    }

}
