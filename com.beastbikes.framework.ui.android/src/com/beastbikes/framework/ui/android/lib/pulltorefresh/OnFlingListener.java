package com.beastbikes.framework.ui.android.lib.pulltorefresh;

public interface OnFlingListener {

    boolean onFlingToLeft(float fromX, float fromY, float toX, float toY);

    boolean onFlingToRight(float fromX, float fromY, float toX, float toY);

    boolean onTouchedAfterFlinged(float x, float y);
}
