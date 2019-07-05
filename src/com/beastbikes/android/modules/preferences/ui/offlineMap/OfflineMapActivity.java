package com.beastbikes.android.modules.preferences.ui.offlineMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.preferences.ui.offlineMap.adapters.OfflineExpandableListAdapter;
import com.beastbikes.android.modules.preferences.ui.offlineMap.adapters.OfflineMapHotCitiesAdapter;
import com.beastbikes.android.modules.preferences.ui.offlineMap.adapters.OfflineMapManagerAdapter;
import com.beastbikes.android.modules.preferences.ui.offlineMap.interfaces.OnOfflineItemStatusChangeListener;
import com.beastbikes.android.modules.preferences.ui.offlineMap.models.OfflineMapItem;
import com.beastbikes.android.widget.AutoExpandableListView;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Alias("离线地图")
@LayoutResource(R.layout.activity_offline_map)
public class OfflineMapActivity extends BaseFragmentActivity implements
        MKOfflineMapListener, OnOfflineItemStatusChangeListener {

    private static final String SHARE_NAME = OfflineMapActivity.class.getName();

    private static final String TAB_INDEX = "tab_index";

    private SharedPreferences sp;

    @IdResource(R.id.activity_offlinemap_tab_content)
    private ViewPager viewPager;

    @IdResource(R.id.activity_offlinemap_tab_title1)
    private TextView tvTabTitle1;

    @IdResource(R.id.activity_offlinemap_tab_title2)
    private TextView tvTabTitle2;

    @IdResource(R.id.activity_offlinemap_tab_cursor)
    private ImageView cursor;

    private AutoExpandableListView lvWholeCountry;

    private ListView lvHotCitits;

    private ListView lvDown;

    private TextView tvNoDownloadItem;

    private final MKOfflineMap offlineMap = new MKOfflineMap();

    private final List<OfflineMapItem> itemsDown = Collections.synchronizedList(new ArrayList<OfflineMapItem>()); // 下载或下载中城市
    private final List<OfflineMapItem> itemsAll = Collections.synchronizedList(new ArrayList<OfflineMapItem>()); // 所有城市，与热门城市及下载管理对象相同

    private final List<View> views = new ArrayList<View>(2);

    private final List<OfflineMapItem> itemsProvince = Collections.synchronizedList(new ArrayList<OfflineMapItem>());
    private final List<OfflineMapItem> childHotCitys = Collections.synchronizedList(new ArrayList<OfflineMapItem>());
    private final List<List<OfflineMapItem>> itemsProvinceCity = Collections.synchronizedList(new ArrayList<List<OfflineMapItem>>());

    private OfflineMapManagerAdapter downAdapter;
    private OfflineMapHotCitiesAdapter hotAdapter;
    private OfflineExpandableListAdapter allCountryAdapter;

    private boolean isResumed = false;

    private boolean isWake = false;

    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.sp = getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isResumed) {
            isResumed = true;
            loadData();
        }
    }

    @Override
    protected void onDestroy() {
        offlineMap.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        this.sp.edit().putInt(TAB_INDEX, this.currIndex).commit();
        super.onPause();
    }

    private void loadData() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                int num = offlineMap.importOfflineData();
                if (num > 0) {
                    Toasts.show(OfflineMapActivity.this, "成功导入" + num + "个离线包");
                }

                List<MKOLSearchRecord> all = null;
                try {
                    all = offlineMap.getOfflineCityList();
                } catch (Exception e) {
                    new BusinessException(e);
                }
                if (all == null || all.isEmpty()) {
                    Toasts.show(OfflineMapActivity.this, "未取到离线地图城市数据！");
                    return null;
                }

                List<MKOLSearchRecord> hotCity = offlineMap.getHotCityList();
                HashSet<Integer> hotCityIds = new HashSet<Integer>();
                if (!hotCity.isEmpty()) {
                    for (MKOLSearchRecord r : hotCity) {
                        hotCityIds.add(r.cityID);
                    }
                }

                // cityType 0:全国；1：省份；2：城市,如果是省份，可以通过childCities得到子城市列表

                // 全国概略图、直辖市、港澳 子城市列表
                ArrayList<MKOLSearchRecord> childMunicipalities = new ArrayList<MKOLSearchRecord>();

                for (MKOLSearchRecord province : all) {
                    OfflineMapItem item = new OfflineMapItem();
                    item.setCityInfo(province);

                    List<MKOLSearchRecord> childs = province.childCities;
                    if (childs != null && !childs.isEmpty()) {
                        // 省

                        List<OfflineMapItem> itemList = new ArrayList<OfflineMapItem>();
                        for (MKOLSearchRecord itemCity : childs) {
                            OfflineMapItem c = new OfflineMapItem();
                            c.setCityInfo(itemCity);
                            itemList.add(c);

                            itemsAll.add(c);
                            if (hotCityIds.contains(itemCity.cityID)) {
                                // 添加到热门城市，保证与省份下的城市是一个对象
                                childHotCitys.add(c);
                            }
                        }
                        itemsProvinceCity.add(itemList);
                        itemsProvince.add(item);

                    } else {
                        // 全国概略图、直辖市、港澳
                        childMunicipalities.add(province);
                    }
                }

                // 构建一个省份，放全国概略图、直辖市、港澳
                if (!childMunicipalities.isEmpty()) {
                    MKOLSearchRecord proMunicipalities = new MKOLSearchRecord();
                    proMunicipalities.cityName = "全国概略图、直辖市、港澳";
                    proMunicipalities.childCities = childMunicipalities;
                    proMunicipalities.cityType = 1;

                    List<OfflineMapItem> itemList = new ArrayList<OfflineMapItem>();
                    for (MKOLSearchRecord itemCity : childMunicipalities) {
                        OfflineMapItem c = new OfflineMapItem();
                        c.setCityInfo(itemCity);
                        itemList.add(c);

                        proMunicipalities.size += itemCity.size;
                        itemsAll.add(c);
                        if (hotCityIds.contains(itemCity.cityID)) {
                            // 添加到热门城市，保证与省份下的城市是一个对象
                            childHotCitys.add(c);
                        }
                    }

                    OfflineMapItem item = new OfflineMapItem();
                    item.setCityInfo(proMunicipalities);
                    itemsProvinceCity.add(0, itemList);
                    itemsProvince.add(0, item);
                }

                // 刷新状态
                List<MKOLUpdateElement> updates = offlineMap.getAllUpdateInfo();
                if (updates != null && updates.size() > 0) {
                    for (MKOLUpdateElement element : updates) {
                        setElement(element, false);
                    }

                    if (itemsDown != null && itemsDown.size() > 1) {
                        Collections.sort(itemsDown, OfflineMapItem.TIME_DESC);
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                refreshDownList();
//				setListViewHeight(lvWholeCountry);
                hotAdapter.setDatas(childHotCitys);
                allCountryAdapter.setDatas(itemsProvince, itemsProvinceCity);
            }

        });
    }

    /**
     * 返回itemsDown.add
     *
     * @param element
     * @param ischeck
     * @return
     */
    private OfflineMapItem setElement(MKOLUpdateElement element, boolean ischeck) {
        OfflineMapItem ret = null;

        if (element == null || itemsAll == null) {
            return null;
        }

        if (element.status == MKOLUpdateElement.FINISHED
                || element.ratio == 100) {
            element.status = MKOLUpdateElement.FINISHED;
            // 记录下载完成的时间
            this.sp.edit()
                    .putLong(String.valueOf(element.cityID),
                            System.currentTimeMillis()).commit();
        }

        for (OfflineMapItem item : itemsAll) {
            if (element.cityID == item.getCityId()) {
                item.setDownInfo(element);

                // 设置下载完成的时间，如果没有则设置为0
                item.setFinishTime(sp.getLong(String.valueOf(element.cityID),
                        0L));

                // 过滤已下载数据
                if (item.getStatus() != MKOLUpdateElement.UNDEFINED) {
                    if (ischeck) {
                        if (!itemsDown.contains(item)
                                && item.getStatus() == MKOLUpdateElement.FINISHED) {
                            if (itemsDown.add(item)) {
                                ret = item;
                            }
                        }

                    } else {
                        if (item.getStatus() == MKOLUpdateElement.FINISHED) {
                            if (itemsDown.add(item)) {
                                ret = item;
                            }
                        }

                    }
                }
                if (!isWake && item.getStatus() == MKOLUpdateElement.WAITING) {
                    int id = item.getCityId();
                    if (id > 0) {
                        offlineMap.start(id);
                    }
                    isWake = true;
                }
                break;
            }
        }

        return ret;
    }

    /**
     * 刷新下载列表
     */
    private void refreshDownList() {
        final boolean noDownloaded = null == itemsDown || itemsDown.isEmpty();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvNoDownloadItem.setVisibility(noDownloaded ? View.VISIBLE : View.GONE);
                downAdapter.setDatas(itemsDown);
                downAdapter.notifyDataSetChanged();
                hotAdapter.setDatas(childHotCitys);
                hotAdapter.notifyDataSetChanged();
                allCountryAdapter.setDatas(itemsProvince, itemsProvinceCity);
                allCountryAdapter.notifyDataSetChanged();
            }
        });
    }

    @SuppressLint("InflateParams")
    private void initView() {
        offlineMap.init(this);

        // 初始化cursor
        bmpW = BitmapFactory.decodeResource(getResources(),
                R.drawable.offline_map_tab_cursor).getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);// 设置动画初始位置

        final LayoutInflater inflater = LayoutInflater.from(this);

        View cities = inflater.inflate(R.layout.activity_offline_map_cities,
                null, false);
        lvWholeCountry = (AutoExpandableListView) cities
                .findViewById(R.id.offlinemap_fragment_cities_lvWholeCountry);

//		lvWholeCountry.setOnGroupExpandListener(new OnGroupExpandListener() {
//			@Override
//			public void onGroupExpand(int groupPosition) {
//				setListViewHeight(lvWholeCountry);
//			}
//		});
//
//		lvWholeCountry
//				.setOnGroupCollapseListener(new OnGroupCollapseListener() {
//					@Override
//					public void onGroupCollapse(int groupPosition) {
//						setListViewHeight(lvWholeCountry);
//					}
//				});

        this.lvHotCitits = (ListView) cities
                .findViewById(R.id.offlinemap_fragment_cities_lvHotCities);
        this.views.add(cities);

        View downloaded = inflater.inflate(
                R.layout.activity_offline_map_download, null, false);
        this.lvDown = (ListView) downloaded
                .findViewById(R.id.offlinemap_fragment_download_lvDown);
        this.views.add(downloaded);

        final OnPageChangeListener pageChangeListener = new MyOnPageChangeListener();
        this.viewPager.setAdapter(new MyViewPagerAdapter(views));
        this.viewPager.setOnPageChangeListener(pageChangeListener);
        this.currIndex = sp.getInt(TAB_INDEX, 0);
        this.viewPager.setCurrentItem(this.currIndex);
        pageChangeListener.onPageSelected(this.currIndex);

        this.downAdapter = new OfflineMapManagerAdapter(this, offlineMap, this);
        this.lvDown.setAdapter(this.downAdapter);

        this.hotAdapter = new OfflineMapHotCitiesAdapter(this, offlineMap, this);
        this.lvHotCitits.setAdapter(hotAdapter);

        this.allCountryAdapter = new OfflineExpandableListAdapter(this, offlineMap,
                this);
        this.lvWholeCountry.setAdapter(allCountryAdapter);
        this.lvWholeCountry.setGroupIndicator(null);

        this.tvNoDownloadItem = (TextView) downloaded
                .findViewById(R.id.offlinemap_fragment_download_no_download);

        this.tvTabTitle1.setOnClickListener(new MyOnClickListener(0));
        this.tvTabTitle2.setOnClickListener(new MyOnClickListener(1));
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageSelected(int index) {
            Animation animation = new TranslateAnimation(one * currIndex, one
                    * index, 0, 0);// 显然这个比较简洁，只有一行代码。
            currIndex = index;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            cursor.startAnimation(animation);
            tvTabTitle1.setSelected(0 == index);
            tvTabTitle2.setSelected(1 == index);
        }

    }

    @Override
    public void statusChanged(OfflineMapItem item, boolean removed) {
        if (removed) {
            for (OfflineMapItem i : itemsAll) {
                if (i.getCityId() == item.getCityId()) {
                    i.setDownInfo(null);
                    break;
                }
            }

            for (int i = itemsDown.size() - 1; i >= 0; i--) {
                OfflineMapItem temp = itemsDown.get(i);
                if (temp.getCityId() == item.getCityId()) {
                    itemsDown.remove(i);
                    sp.edit().remove(String.valueOf(item.getCityId())).apply();
                    MKOLUpdateElement element = offlineMap.getUpdateInfo(item
                            .getCityId());
                    setElement(element, false);
                }
            }

            refreshDownList();
        } else {
            downAdapter.notifyDataSetChanged();
        }
        hotAdapter.notifyDataSetChanged();
        allCountryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetOfflineMapState(int type, int state) {
        final MKOLUpdateElement update = offlineMap.getUpdateInfo(state);
        if (null == update)
            return;

        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
                if (setElement(update, true) != null) {
                    if (itemsDown != null && itemsDown.size() > 1) {
                        Collections.sort(itemsDown, OfflineMapItem.TIME_DESC);
                    }
                }

                refreshDownList();
                break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                break;

            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                break;
        }
    }

    public void toDownloadPage() {
        viewPager.setCurrentItem(1);
    }

//	// 通过计算ExpandableListView的高度解决和ScrollView冲突的问题
//	private void setListViewHeight(ExpandableListView listView) {
//		ListAdapter listAdapter = listView.getAdapter();
//		int totalHeight = 0;
//		View first = listAdapter.getView(0, null, listView);
//		if (null != first) {
//			first.measure(0, 0);
//			totalHeight += first.getMeasuredHeight() * listAdapter.getCount();
//		}
//		ViewGroup.LayoutParams params = listView.getLayoutParams();
//		params.height = totalHeight
//				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//		listView.setLayoutParams(params);
//		listView.requestLayout();
//	}

    private class MyViewPagerAdapter extends PagerAdapter {

        private List<View> listViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.listViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(listViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(listViews.get(position), 0);
            return listViews.get(position);
        }

        @Override
        public int getCount() {
            return listViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    private class MyOnClickListener implements OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }

    }

}
