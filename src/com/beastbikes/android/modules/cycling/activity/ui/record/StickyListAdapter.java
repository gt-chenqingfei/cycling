package com.beastbikes.android.modules.cycling.activity.ui.record;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.widget.CircleIndicator;
import com.beastbikes.android.widget.slidingup_pannel.SlidingUpPanelLayout;
import com.beastbikes.android.widget.stickylistlibrary.stickylistheaders.StickyListHeadersAdapter;
import com.beastbikes.android.widget.stickylistlibrary.stickylistheaders.StickyListHeadersListView;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.DensityUtil;


public class StickyListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private View mContainer;
    private Context mContext;
    private RecordAnalysis mHeader;
    private ActivityDTO mData;
    private CircleIndicator mIndicator;
    private ViewPager mViewPager;

    public StickyListAdapter(Context context, PagerAdapter adapter, SlidingUpPanelLayout
            slidingUpPanelLayout, StickyListHeadersListView stickyList) {
        this.mContext = context;
        this.mContainer = LayoutInflater.from(context).inflate(R.layout.record_sticky_item, null);

        mViewPager = (ViewPager) this.mContainer.findViewById(R.id.sticky_list_view_pager);
        mIndicator = (CircleIndicator) this.mContainer.findViewById(R.id.indicator);

        mViewPager.setAdapter(adapter);

        int screenHeight = DensityUtil.getHeight(context);
        int mHeaderHeight = DensityUtil.dip2px(context, 60 + 9);
        int bottomHeight = DensityUtil.dip2px(context, 90 + 15);
        mViewPager.setPageMargin(DensityUtil.dip2px(context, 10));
        mViewPager.getLayoutParams().height = screenHeight - mHeaderHeight - bottomHeight;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return this.mContainer;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            this.mHeader = new RecordAnalysis(this.mContext);
            this.mHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, RecordDataCompareActivity.class);
                    mContext.startActivity(intent);
                }
            });
            convertView = this.mHeader;
        }
        this.mHeader.onDataChanged(mData);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    public void notifyDataChanged(ActivityDTO object) {
        this.mData = object;
        if (this.mHeader != null) {
            this.mHeader.onDataChanged(mData);
        }
    }

    public void notifyBottomDots() {
        mIndicator.setViewPager(mViewPager);
    }

}
