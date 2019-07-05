package com.beastbikes.android.modules.cycling.ranking.ui;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.home.HomeActivity;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnAPI;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnBean;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnCallBack;
import com.beastbikes.android.locale.locationutils.UtilsLocationCallBack;
import com.beastbikes.android.locale.locationutils.UtilsLocationManager;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.ranking.biz.RankingManager;
import com.beastbikes.android.modules.cycling.ranking.ui.widget.RankMenuPopupWindow;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 16/1/6.
 */
@MenuResource(R.menu.rank_fragment_menu)
public class RankFragment extends SessionFragment implements RequestQueueManager, Constants {

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private View mView;
    //地区范围
    private String geoCode = "CN.22.2038349";
    private int region_code;
    private TextView drop_down_tv;
    private View drop_down;
    private boolean isInitEare = true;
    private TimeCount time;
    private RequestQueue requestQueue;

    @Override
    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity() != null) {
            getActivity().setTitle(R.string.ranking_fragment_title);
        }

        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
            return mView;
        }
        setHasOptionsMenu(true);
        LayoutInflater layoutInflater = LayoutInflater.from(this.getActivity());
        mView = layoutInflater.inflate(R.layout.activity_ranking, container, false);
        tabLayout = (TabLayout) mView.findViewById(R.id.activity_main_tablayout);
        viewPager = (ViewPager) mView.findViewById(R.id.activity_main_viewpager);

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.requestQueue = RequestQueueFactory.newRequestQueue(getContext());
        initRegion();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //初始化地区
    private void initRegion() {
        this.region_code = HomeActivity.REGION_RANK_CODE;
        this.isInitEare = true;
        AVUser avUser = AVUser.getCurrentUser();
        if (avUser == null)
            return;
        geoCode = avUser.getGeoCode();
        if (TextUtils.isEmpty(geoCode) || geoCode == null) {
            time = new TimeCount(3000, 1000);
            time.start();
            UtilsLocationManager.getInstance().getLocation(getActivity(), new UtilsLocationCallBack() {
                @Override
                public void onLocationChanged(Location location) {
                    GoogleMapCnAPI googleMapCnAPI = new GoogleMapCnAPI();
                    googleMapCnAPI.geoCode(getRequestQueue(),
                            location.getLatitude(), location.getLongitude(), new GoogleMapCnCallBack() {
                                @Override
                                public void onGetGeoCodeInfo(GoogleMapCnBean googleMapCnBean) {
                                    if (null == googleMapCnBean) {
                                        return;
                                    }

                                    String area = googleMapCnBean.getCityName();
                                    if (isInitEare) {
                                        if (time != null)
                                            time.cancel();
                                        initArea(area);
                                    }
                                }

                                @Override
                                public void onGetGeoInfoError(VolleyError volleyError) {

                                }
                            });
                }

                @Override
                public void onLocationFail() {

                }
            });
        } else {
            initView();
        }
    }


    //获取地区码
    private void initArea(final String area) {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(Void... params) {
                RankingManager manager = new RankingManager(getActivity());
                return manager.getGeoCode(area);
            }

            protected void onPostExecute(JSONObject result) {
                if (null == result) {
                    geoCode = "CN.22.2038349";
                    return;
                } else if (result.optInt("code") == 0) {
                    geoCode = result.optString("result");
                } else {
                    Toasts.show(
                            getActivity(),
                            result.optString("message"));
                    geoCode = "CN.22.2038349";
                }
                if (isAdded()) {
                    initView();
                }
            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        initDropdownView();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setActionView(this.drop_down);
    }

    private void initDropdownView() {
        List<String> areaData = new ArrayList<>();
        areaData.add(getString(R.string.ranking_fragment_menu_safety_net));
        areaData.add(getString(R.string.ranking_fragment_menu_whole_country));
        areaData.add(getString(R.string.ranking_fragment_menu_whole_area));
        this.drop_down = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_rank_area, null);
        this.drop_down_tv = (TextView) this.drop_down.
                findViewById(R.id.fragment_rank_area_tv);
        this.drop_down_tv.setText(areaData.get(region_code));
        this.drop_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RankMenuPopupWindow.createPopupWindow(getActivity(),
                        drop_down_tv, geoCode).
                        showAsDropDown(drop_down, 0, 0);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (getActivity() != null) {
                getActivity().setTitle(R.string.ranking_fragment_title);
            }
        }
    }

    public void initView() {
        String str;
        if (region_code == Constants.RANK_REGION_NET_CODE) {
            str = "";
        } else if (region_code == Constants.RANK_REGION_COUNTRY) {
            str = geoCode.split("\\.")[0];
        } else {
            str = geoCode.split("\\.")[0] + "." + geoCode.split("\\.")[1];
        }

        List<String> titles = new ArrayList<>();
        List<SessionFragment> rankFragments = new ArrayList<>();
        titles.add(getResources().getString(R.string.week));
        titles.add(getResources().getString(R.string.month));
        titles.add(getResources().getString(R.string.year));
        titles.add(getResources().getString(R.string.total));

        Bundle bundle = new Bundle();
        bundle.putString(RANK_GEO, str);

        SessionFragment weekFragment = new WeeklyRankFragment();
        weekFragment.setArguments(bundle);
        rankFragments.add(weekFragment);

        SessionFragment monthlyRankFragment = new MonthlyRankFragment();
        monthlyRankFragment.setArguments(bundle);
        rankFragments.add(monthlyRankFragment);

        SessionFragment yearlyRankFragment = new YearlyRankFragment();
        yearlyRankFragment.setArguments(bundle);
        rankFragments.add(yearlyRankFragment);

        SessionFragment totallyRankFragment = new TotallyRankFragment();
        totallyRankFragment.setArguments(bundle);
        rankFragments.add(totallyRankFragment);

        for (int i = 0; i < titles.size(); i++) {
            this.tabLayout.addTab(this.tabLayout.newTab().setText(titles.get(i)));//创建每一个选项卡
        }
        RankFragmentAdapter adapter =
                new RankFragmentAdapter(getChildFragmentManager(), rankFragments, titles);//将标题与fragment传入适配器
        this.viewPager.setOffscreenPageLimit(4);
        this.viewPager.setAdapter(adapter);
        this.tabLayout.setupWithViewPager(this.viewPager);
        this.tabLayout.setTabsFromPagerAdapter(adapter);
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            isInitEare = false;
            AVUser user = AVUser.getCurrentUser();
            if (null != user) {
                String city = user.getCity();
                initArea(city);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示

        }
    }

    public class RankFragmentAdapter extends FragmentStatePagerAdapter {
        private List<SessionFragment> fragments;
        private List<String> titles;

        public RankFragmentAdapter(FragmentManager fm, List<SessionFragment> fragments, List<String> titles) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }


        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


}