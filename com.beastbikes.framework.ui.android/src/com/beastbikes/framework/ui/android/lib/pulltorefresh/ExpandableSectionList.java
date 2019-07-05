package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.beastbikes.framework.ui.android.R;
import com.beastbikes.framework.ui.android.lib.list.BaseSectionListAdapter;

public class ExpandableSectionList extends ExpandableListView implements
        OnScrollListener {

    private static final String TAG = "section";

    private static final int INVALID_LAYOUT_ID = -1;

    private boolean isCollapsable = true;

    private int left = 0;
    private int top = 0;
    private int right = 0;
    private int bottom = 0;
    private boolean headerShow = false;
    private int indicatorGroupId = -1;
    private int indicatorGroupHeight;
    private int scrollOffset;

    private MotionEvent lastEvent = null;
    private OnScrollListener mockScrollListener;
    private boolean hasTouched = false;

    public ExpandableSectionList(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SectionHeader);

        if (a.hasValue(R.styleable.SectionHeader_header_layout)) {

            int layout = a.getResourceId(
                    R.styleable.SectionHeader_header_layout, INVALID_LAYOUT_ID);
            if (layout != INVALID_LAYOUT_ID) {

                LayoutInflater inflater = LayoutInflater.from(context);
                View v = inflater.inflate(layout, this, false);

                v.setOnTouchListener(new OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                this.setPinnedHeaderView(v);

                if (a.hasValue(R.styleable.SectionHeader_is_collapsable)) {
                    isCollapsable = a.getBoolean(
                            R.styleable.SectionHeader_is_collapsable, true);
                }
            }
        }

        a.recycle();
        super.setOnScrollListener(this);
    }

    private BaseSectionListAdapter<?, ?> mAdapter;
    private View mHeaderView;

    private int mHeaderViewWidth;

    private int mHeaderViewHeight;

    public void setPinnedHeaderView(View view) {
        mHeaderView = view;

        if (mHeaderView != null) {
            setFadingEdgeLength(0);
            // mHeaderView.setVisibility(View.GONE);
        }
        requestLayout();
    }

    @Override
    public void setAdapter(ExpandableListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (BaseSectionListAdapter<?, ?>) adapter;
        if (mAdapter != null) {
            mAdapter.expandAll();
            mAdapter.registerDataSetObserver(new DataSetObserver() {
                public void onChanged() {
                    indicatorGroupId = -1;
                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mHeaderView != null) {
            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
            configHeader(0);
        }
    }

    protected void configHeader(int scrollOffset) {
        if (mHeaderView == null)
            return;

        /**
         * calculate point (0,0)
         */
        int itemPos = this.pointToPosition(10, scrollOffset + 1);

        if (itemPos != AdapterView.INVALID_POSITION) {
            long pos = this.getExpandableListPosition(itemPos);
            int childPos = ExpandableListView.getPackedPositionChild(pos);
            int groupPos = ExpandableListView.getPackedPositionGroup(pos);

            headerShow = (childPos == -1 && groupPos == -1);

            if (childPos == AdapterView.INVALID_POSITION) {
                int offset = headerShow ? 1 : 0;
                View groupView = this.getChildAt(itemPos
                        - this.getFirstVisiblePosition() + offset);
                if (groupView != null) {
                    indicatorGroupHeight = groupView.getHeight();
                }
            }
            // get an error data, so return now
            if (indicatorGroupHeight == 0) {
                return;
            }

            // update the data of indicator group view
            if (groupPos != indicatorGroupId) {
                if (!headerShow) {
                    Log.d(TAG, "refresh group view " + groupPos);
                    mAdapter.getGroupView(groupPos,
                            this.isGroupExpanded(groupPos), mHeaderView, null);

                }
                indicatorGroupId = groupPos;
            }
        }

        if (indicatorGroupId == -1) {
            headerShow = true;
            return;
        }

        /**
         * calculate point (0,indicatorGroupHeight)
         */
        int showHeight = indicatorGroupHeight;
        int nEndPos = this.pointToPosition(10, scrollOffset
                + indicatorGroupHeight + 1);

        if (nEndPos != AdapterView.INVALID_POSITION) {
            long pos = this.getExpandableListPosition(nEndPos);
            int groupPos = ExpandableListView.getPackedPositionGroup(pos);

            if (groupPos != indicatorGroupId) {
                View viewNext = this.getChildAt(nEndPos
                        - this.getFirstVisiblePosition());
                showHeight = viewNext.getTop() - scrollOffset;
            }
        }

        left = 0;
        top = -(indicatorGroupHeight - showHeight);
        right = mHeaderViewWidth;
        bottom = mHeaderViewHeight + top;

        mHeaderView.layout(left, scrollOffset + top, right, scrollOffset
                + bottom);

        this.scrollOffset = scrollOffset;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.lastEvent = ev;
        hasTouched = true;
        if (mHeaderView != null && isCollapsable) {
            // mHeaderView.setVisibility(View.VISIBLE);


            int x = (int) ev.getX();
            int y = (int) ev.getY();
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (isGroupExpanded(indicatorGroupId) && x > left && x < right
                            && y > top && y < bottom) {
                        collapseGroup(indicatorGroupId);
                        return true;
                    }

                    break;
                default:
                    break;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    public void singleClick() {
        if (lastEvent != null) {
            lastEvent.setAction(MotionEvent.ACTION_DOWN);
            super.dispatchTouchEvent(lastEvent);

            lastEvent.setAction(MotionEvent.ACTION_CANCEL);
            super.dispatchTouchEvent(lastEvent);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!headerShow && mHeaderView != null && scrollOffset >= 0
                && hasTouched) {
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            lastEvent = null;
        }
        if (mockScrollListener != null) {
            mockScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (mHeaderView != null) {
            configHeader(0);
        }
        if (mockScrollListener != null) {
            mockScrollListener.onScroll(view, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        this.mockScrollListener = l;
    }

}