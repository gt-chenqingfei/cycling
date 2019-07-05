package com.beastbikes.android.home.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.home.HomeActivity;
import com.beastbikes.android.home.HomeManager;
import com.beastbikes.android.home.view.TabViewHolder;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;

import java.util.ArrayList;

/**
 * Created by secret on 16/7/19.
 */
public class HomePagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener, HomeManager.OnTabChangeListener {

    private final FragmentActivity mFragmentActivity;
    private final ViewPager mViewPager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
    private final ArrayList<TabViewHolder> mHolders = new ArrayList<>();

    private ClubManager mClubManager;

    private OnTabChangeListener mOnTabChangeListener;

    static final class TabInfo {
        private final Class<?> clss;
        private final Bundle args;

        TabInfo(Class<?> _class, Bundle _args) {
            clss = _class;
            args = _args;
        }
    }

    public HomePagerAdapter(FragmentActivity activity, ViewPager pager) {
        super(activity.getSupportFragmentManager());
        mFragmentActivity = activity;
        mViewPager = pager;
        mViewPager.setAdapter(this);
        mViewPager.addOnPageChangeListener(this);

        mClubManager = new ClubManager(activity);

    }

    /**
     * 添加tab项
     * @param holder
     * @param clss
     * @param args
     */
    public void addTab(TabViewHolder holder, Class<?> clss, Bundle args) {
        TabInfo info = new TabInfo(clss, args);
        holder.setOnTabChangeListener(this);
        mTabs.add(info);
        mHolders.add(holder);
        notifyDataSetChanged();
        mFragmentActivity.setTitle(R.string.activity_fragment_title);
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        return Fragment.instantiate(mFragmentActivity, info.clss.getName(), info.args);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        this.setTabState(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabChange(String tag) {
        int size = mHolders.size();

        for (int i = 0; i < size; i++) {
            if (TextUtils.equals(mHolders.get(i).getTag(), tag)) {
                mViewPager.setCurrentItem(i,false);
                break;
            }
        }
    }

    /**
     * set dots for bottom tabs
     * @param position the position of selected tab
     * @param count the count of dot
     */
    public void setTabDots(int position, int count) {
        if (null == mHolders) {
            return;
        }

        mHolders.get(position).setDotText(count);

    }

    /**
     * set state of bottom tabs
     * @param position the position of selected tab
     */
    private void setTabState(int position) {

        HomeActivity.currentPage = position;
        if (null == mHolders) {
            return;
        }

        if (null != mOnTabChangeListener) {
            mOnTabChangeListener.onTabChanged(position);
        }

        mHolders.get(position).setSelected(true);
        mHolders.get((position + 1) % 3).setSelected(false);
        mHolders.get((position + 2) % 3).setSelected(false);

        this.setTitle(position);

    }

    public void setOnTabChangedListener(OnTabChangeListener onTabChangedListener) {
        this.mOnTabChangeListener = onTabChangedListener;
    }

    /**
     * set HomeActivity's title
     * @param position
     */
    private void setTitle(int position) {
        switch (position) {
            case 0:
                mFragmentActivity.setTitle(R.string.activity_fragment_title);
                break;

            case 1:
                AVUser avUser = AVUser.getCurrentUser();
                if (null != avUser) {
                    if (TextUtils.isEmpty(avUser.getClubId())) {
                        mFragmentActivity.setTitle(R.string.club_info_title);
                    } else {
                        try {
                            ClubInfoCompact info = mClubManager.getMyClub(avUser.getObjectId());
                            if (info != null && info.getStatus() != ClubInfoCompact.CLUB_STATUS_NONE) {
                                mFragmentActivity.setTitle(info.getName());
                            }
                        } catch (Exception e) {
                            mFragmentActivity.setTitle(R.string.club_info_title);
                        }
                    }
                }
                break;

            case 2:
                mFragmentActivity.setTitle("");
                break;
        }
    }

    public interface OnTabChangeListener {

        void onTabChanged(int position);

    }

}
