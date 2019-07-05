package com.beastbikes.android.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.authentication.ui.AuthenticationActivity;
import com.beastbikes.android.main.tutorial.TutorialPage;
import com.beastbikes.android.main.tutorial.TutorialPage1;
import com.beastbikes.android.main.tutorial.TutorialPage2;
import com.beastbikes.android.main.tutorial.TutorialPage3;
import com.beastbikes.android.main.tutorial.TutorialPage4;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@Alias("引导页")
@LayoutResource(R.layout.tutorial_activity)
public class TutorialActivity extends BaseFragmentActivity implements
        OnPageChangeListener, View.OnClickListener {

    public static final String PREF_HAS_SHOWN = "has_shown_tutorials";

    private static final String KEY_SELECTED_INDEX = "selected-index";

    @IdResource(R.id.activity_tutorial_pager)
    private ViewPager pager;

    @IdResource(R.id.activity_tutorial_page_indicator)
    private ViewGroup indicator;

    @IdResource(R.id.tutorial_page_open_speedx)
    private Button open;

    private int selectedIndex;

    private TutorialPageAdapter adapter;

    private static final Class<?>[] PAGES = {
    /* page 1 */TutorialPage1.class,
    /* page 2 */TutorialPage2.class,
    /* page 3 */TutorialPage3.class,
	/* page 4 */TutorialPage4.class,};

    private int currentPageScrollStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.pager.setOnPageChangeListener(this);
        this.adapter = new TutorialPageAdapter();
        this.pager.setAdapter(this.adapter);

        this.open.setOnClickListener(this);

        if (indicator.getChildCount() > 0) {
            this.indicator.getChildAt(0).setSelected(true);
        }

    }

    public void isShowTutorial() {
        final SharedPreferences sp = getSharedPreferences(getPackageName(), 0);
        sp.edit().putBoolean(PREF_HAS_SHOWN, true).apply();
        AVUser avUser = AVUser.getCurrentUser();
        Intent intent = new Intent();
        if (null != avUser) {
            this.finish();
            return;
        }
        intent.setClass(this, AuthenticationActivity.class);
        this.startActivity(intent);

        SharedPreferences mPerferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        //第一次初始化需要显示引导页的同时，如果当前时区为大陆，则修改默认显示公里
        mPerferences.edit().putInt(Constants.KM_OR_MI, Constants.DISPLAY_KM).apply();
        this.finish();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_none);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tutorial_page_open_speedx:
                this.isShowTutorial();
                break;
        }
    }

    private final class TutorialPageAdapter extends PagerAdapter {

        private final List<TutorialPage> pages = new ArrayList<TutorialPage>();

        public TutorialPageAdapter() {
            final Context ctx = TutorialActivity.this;

            for (int i = 0; i < PAGES.length; i++) {
                View.inflate(ctx, R.layout.tutorial_page_indicator, indicator);
                View v = indicator.getChildAt(i);

                try {
                    Class<?> clazz = PAGES[i];
                    Constructor<?> ctor = clazz.getConstructor(Context.class);
                    TutorialPage tp = (TutorialPage) ctor.newInstance(ctx);
                    pages.add(tp);
                    v.setBackgroundResource(R.drawable.tutorial_page_indicator);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TutorialPage tp = this.pages.get(position);
            if (null == tp.getParent()) {
                container.addView(tp);
            }
            return tp;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return this.pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object item) {
            return view == item;
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        currentPageScrollStatus = state;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
//        if (position == this.adapter.getCount() - 1
//                && positionOffsetPixels == 0 && currentPageScrollStatus == 1 && this.fromAbout) {
//            isShowTutorial();
//        }
    }

    @Override
    public void onPageSelected(int index) {
        this.pager.setCurrentItem(index, true);
        this.selectedIndex = index;

        for (int i = 0, n = this.indicator.getChildCount(); i < n; i++) {
            View v = this.indicator.getChildAt(i);
            v.setSelected(i == index);
        }

        TutorialPageAdapter adapter = (TutorialPageAdapter) pager.getAdapter();
        for (int i = 0, n = adapter.getCount(); i < n; i++) {
            TutorialPage tp = adapter.pages.get(i);
            if (i == index) {
                tp.onEnterPage();
            } else {
                tp.onLeavePage();
            }
        }

        if (index == this.adapter.getCount() - 1) {
            this.open.setVisibility(View.VISIBLE);
        } else {
            this.open.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.selectedIndex = savedInstanceState.getInt(KEY_SELECTED_INDEX, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SELECTED_INDEX, this.selectedIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
