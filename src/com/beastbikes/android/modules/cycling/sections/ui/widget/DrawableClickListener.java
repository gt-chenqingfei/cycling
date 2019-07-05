package com.beastbikes.android.modules.cycling.sections.ui.widget;

/**
 * Created by caoxiao on 16/4/5.
 */
public interface DrawableClickListener {

    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    public void onClick(DrawablePosition target);
}
