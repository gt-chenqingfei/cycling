package com.beastbikes.android.ble.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.ble.biz.CentralSessionHandler;
import com.beastbikes.android.ble.biz.entity.CentralSession;
import com.beastbikes.android.ble.dto.CadenceDTO;
import com.beastbikes.android.ble.ui.widget.ShapeIndicatorView;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.android.utils.DimensionUtils;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@LayoutResource(R.layout.activity_speedx_cadence_setting)
@MenuResource(R.menu.activity_speedx_cadence_menu)
public class SpeedXCadenceSettingActivity extends SessionFragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    public static final String EXTRA_SELECT_INDEX = "index";

    @IdResource(R.id.activity_speedx_cadence_setting_tablayout)
    private TabLayout tabLayout;
    @IdResource(R.id.activity_speedx_cadence_setting_viewpager)
    private ViewPager viewBanner;
    @IdResource(R.id.activity_speedx_cadence_setting_btn)
    private Button selectBtn;
    @IdResource(R.id.activity_speddx_cadence_setting_tab_indicator)
    private ShapeIndicatorView indicatorView;

    private List<CadenceDTO> list;
    private CadenceViewPagerAdapter adapter;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.index = getIntent().getIntExtra(EXTRA_SELECT_INDEX, 0);
        this.selectBtn.setOnClickListener(this);

        list = new ArrayList<>();
        String[] arrays = getResources().getStringArray(R.array.select_cadence_array);
        String[] titles = getResources().getStringArray(R.array.select_cadence_title);
        String[] descs = getResources().getStringArray(R.array.select_cadence_desc);
        for (int i = 0; i < arrays.length; i++) {
            CadenceDTO cadence = new CadenceDTO();
            cadence.setTitle(titles[i]);
            cadence.setDesc(descs[i]);
            cadence.setData(arrays[i]);
            if (index == i) {
                cadence.setSelected(true);
            } else {
                cadence.setSelected(false);
            }
            list.add(cadence);
        }


        adapter = new CadenceViewPagerAdapter(this, list);
        viewBanner.addOnPageChangeListener(this);
        viewBanner.setAdapter(adapter);
        viewBanner.setCurrentItem(index);
        this.onPageSelected(index);
        viewBanner.setOffscreenPageLimit(list.size());
//        tabLayout.setupWithViewPager(viewBanner);
        tabLayout.setTabsFromPagerAdapter(adapter);
        indicatorView.setupWithTabLayout(tabLayout);
        indicatorView.setupWithViewPager(viewBanner);
        setTabWidth();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_cadence_help:// 跳转帮助页
                final Intent browserIntent = new Intent(this, BrowserActivity.class);
                browserIntent.setData(Uri.parse("https://hybrid.speedx.com/cadence-notice"));
                startActivity(browserIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_speedx_cadence_setting_btn:// 选择踏频
                if (!selectBtn.isSelected()) {
                    CentralSession session = CentralSessionHandler.getInstance().getConnectSession();
                    if (null == session) {
                        Toasts.show(this, getString(R.string.toast_bluetooth_disconnect_try_again));
                        break;
                    }

                    Intent intent = getIntent();
                    intent.putExtra(EXTRA_SELECT_INDEX, viewBanner.getCurrentItem());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (null != list && list.size() > position) {
            CadenceDTO cadence = this.list.get(position);
            if (null != cadence) {
                this.selectBtn.setSelected(cadence.isSelected());
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 通过反射设置tab宽度及指示条宽度(通过设置margin)
     */
    private void setTabWidth() {
        Class<?> tabLayoutClazz = tabLayout.getClass();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        try {
            Field tabStrip = tabLayoutClazz.getDeclaredField("mTabStrip");
            tabStrip.setAccessible(true);
            LinearLayout ll_tab = (LinearLayout) tabStrip.get(tabLayout);
            int margin = DimensionUtils.dip2px(this, 14f);
            int tabWidth = (width - margin * 9) / 5;
            for (int i = 0; i < ll_tab.getChildCount(); i++) {
                View child = ll_tab.getChildAt(i);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                params.setMargins(margin, 0, margin, 0);
                params.width = tabWidth;
                child.setLayoutParams(params);
                child.invalidate();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private class CadenceViewPagerAdapter extends PagerAdapter {

        private List<CadenceDTO> list;
        private LayoutInflater inflater;
        private SparseArray<View> viewSparseArray;

        public CadenceViewPagerAdapter(Context context, List<CadenceDTO> list) {
            this.list = list;
            this.viewSparseArray = new SparseArray<>(list.size());
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = viewSparseArray.get(position);
            if (null == view) {
                view = inflater.inflate(R.layout.layout_cadence_viewpager_item, null);
                viewSparseArray.put(position, view);
            }

            TextView titleTv = (TextView) view.findViewById(R.id.cadence_item_title);
            TextView descTv = (TextView) view.findViewById(R.id.cadence_item_desc);
            TextView dataTv = (TextView) view.findViewById(R.id.cadence_item_value);
            ImageView selectIv = (ImageView) view.findViewById(R.id.cadence_item_select_iv);
            CadenceDTO cadence = this.list.get(position);
            if (null != cadence) {
                titleTv.setText(cadence.getTitle());
                descTv.setText(cadence.getDesc());
                dataTv.setText(cadence.getData());
                selectIv.setVisibility(cadence.isSelected() ? View.VISIBLE : View.GONE);
            }

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(this.viewSparseArray.get(position));
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return this.list.get(position).getTitle();
        }
    }
}
