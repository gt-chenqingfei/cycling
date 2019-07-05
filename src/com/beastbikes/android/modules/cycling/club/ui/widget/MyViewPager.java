package com.beastbikes.android.modules.cycling.club.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.beastbikes.android.widget.SwipeRefreshAndLoadLayout;

import com.beastbikes.android.widget.slidingup_pannel.SlidingUpPanelLayout;
import com.beastbikes.android.widget.stickylistlibrary.stickylistheaders.StickyListHeadersListView;

/**
 * Created by caoxiao on 15/12/27.
 */
public class MyViewPager extends ViewPager {

    private SwipeRefreshAndLoadLayout swipeRefreshAndLoadLayout;
    private StickyListHeadersListView stickyList;
    private SlidingUpPanelLayout slidingUpPanelLayout;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public MyViewPager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public void setNestedpParent(SwipeRefreshAndLoadLayout swipeRefreshAndLoadLayout, StickyListHeadersListView stickyList) {
        this.swipeRefreshAndLoadLayout = swipeRefreshAndLoadLayout;
        this.stickyList = stickyList;
    }

    public void setNestedParent(SlidingUpPanelLayout slidingUpPanelLayout, StickyListHeadersListView stickyList) {
//        this.slidingUpPanelLayout = slidingUpPanelLayout;
        this.stickyList = stickyList;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (swipeRefreshAndLoadLayout != null) {
            swipeRefreshAndLoadLayout.requestDisallowInterceptTouchEvent(true);
        }

        if (stickyList != null) {
            stickyList.requestDisallowInterceptTouchEvent(true);
        }

        if (slidingUpPanelLayout != null) {
            slidingUpPanelLayout.requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (swipeRefreshAndLoadLayout != null) {
            swipeRefreshAndLoadLayout.requestDisallowInterceptTouchEvent(true);
        }

        if (stickyList != null) {
            stickyList.requestDisallowInterceptTouchEvent(true);
        }

        if (slidingUpPanelLayout != null) {
            slidingUpPanelLayout.requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (swipeRefreshAndLoadLayout != null) {
            swipeRefreshAndLoadLayout.requestDisallowInterceptTouchEvent(true);
        }
        if (stickyList != null) {
            stickyList.requestDisallowInterceptTouchEvent(true);
        }
        if (slidingUpPanelLayout != null) {
            slidingUpPanelLayout.requestDisallowInterceptTouchEvent(true);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (swipeRefreshAndLoadLayout != null) {
                    swipeRefreshAndLoadLayout.setEnabled(false);
                }
                if (stickyList != null) {
                    stickyList.setCanTouch(false);
                }
                if (slidingUpPanelLayout != null) {
                    slidingUpPanelLayout.setEnabled(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (swipeRefreshAndLoadLayout != null) {
                    swipeRefreshAndLoadLayout.setEnabled(true);
                }
                if (stickyList != null) {
                    stickyList.setCanTouch(true);
                }
                if (slidingUpPanelLayout != null) {
                    slidingUpPanelLayout.setEnabled(true);
                }
                break;

        }
        return super.onTouchEvent(event);
    }
}
