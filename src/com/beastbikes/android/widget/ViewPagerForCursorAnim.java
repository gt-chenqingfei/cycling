package com.beastbikes.android.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author icedan 实现滑动ViewPager同时滑动Cursor滚动条，如果需要滚动Cursor必须调用setViewAndIndex
 */
public class ViewPagerForCursorAnim extends ViewPager {

    private View cursor;

    private int currentIndex;

    private int cursorWidth;

    public ViewPagerForCursorAnim(Context context) {
        super(context);
    }

    public ViewPagerForCursorAnim(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPageScrolled(int position, float positionOffset,
                                  int positionOffsetPixels) {
        this.cursorAnim(position, positionOffset, positionOffsetPixels);
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    public void setViewAndIndex(View cursor, int currentIndex, int cursorWidth) {
        this.cursor = cursor;
        this.currentIndex = currentIndex;
        this.cursorWidth = cursorWidth;
    }

    private void cursorAnim(int position, float positionOffset,
                            float positionOffsetPixels) {
        if (null != this.cursor) {
            LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) this.cursor
                    .getLayoutParams();

            if (this.currentIndex == position) {
                lp.leftMargin = (int) (positionOffset * cursorWidth + currentIndex
                        * cursorWidth);
                this.cursor.setLayoutParams(lp);
            } else if (this.currentIndex > position) {
                lp.leftMargin = (int) (currentIndex * cursorWidth - (1 - positionOffset)
                        * cursorWidth);
                this.cursor.setLayoutParams(lp);
            }

        }
    }

}
