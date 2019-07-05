package com.beastbikes.android.modules.cycling.club.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.utils.IntentUtils;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.lib.view.search.MySearchBar;
import com.beastbikes.framework.ui.android.lib.view.search.MySearchListener;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.io.Serializable;
import java.util.List;

public class ClubDiscoverFrag extends SessionFragment implements
        OnClickListener, MySearchListener,
        NearbyFrag.NoNearByCallBack, SharedPreferences.OnSharedPreferenceChangeListener {

    private View applayTip;

    private LinearLayout searchbarLL;

    private TextView recommendTab;

    private TextView nearbyTab;

    private LinearLayout fragContainer;

    private int DIALOG_CLUB_CANCEL_WARNING = 100;
    private static final int REQ_CREATE = 101;

//    private ClubDiscoverFrag frag;

    private ClubInfoCompact myClubInfo;

    private MySearchBar searchBar;

    private LoadingDialog loadingDialog;

    private ClubManager clubManager;

    private FragmentManager fm;

    private RecommendFrag recommendFrag;

    private NearbyFrag nearbyFrag;

    private final int pageCount = 20;

    public static boolean SHOW_MENU = true;

    public int status;
    public String clubId;
    private View mView;

    private SharedPreferences mUserSp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
            return mView;
        }
        LayoutInflater layoutInflater = LayoutInflater.from(this.getActivity());
        mView = layoutInflater.inflate(R.layout.activity_club_discovery, container, false);

        applayTip = mView.findViewById(R.id.club_discover_apply_tip);
        searchbarLL = (LinearLayout) mView.findViewById(R.id.searchbarLL);
        recommendTab = (TextView) mView.findViewById(R.id.recommend_tab);
        nearbyTab = (TextView) mView.findViewById(R.id.nearby_tab);
        fragContainer = (LinearLayout)mView.findViewById(R.id.frag_container);

        return mView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        clubManager = new ClubManager(getActivity());
        searchBar = new MySearchBar(getActivity(), getString(R.string.club_search_search_btn), getString(R.string.club_search_hint));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        searchbarLL.addView(searchBar, layoutParams);
        searchBar.setSearchBarListener(this);
        fm = getChildFragmentManager();
        showFragment(1);

        recommendTab.setOnClickListener(this);
        nearbyTab.setOnClickListener(this);

        AVUser user = AVUser.getCurrentUser();
        if (null != user)
            this.mUserSp = getContext().getSharedPreferences(user.getObjectId(), 0);

        this.mUserSp.registerOnSharedPreferenceChangeListener(this);
        this.getMyClub();
    }

    @Override
    public void onResume() {
        super.onResume();
//        this.getMyClub();
    }

    private void refreshClubStatus(ClubInfoCompact info) {
        switch (info.getStatus()) {
            case ClubInfoCompact.CLUB_STATUS_APPLY:
                applayTip.setVisibility(View.VISIBLE);
                applayTip.setOnClickListener(this);
                break;
            default:
                applayTip.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.contains(Constants.PREF_CLUB_STATUS)) {
            int status = sharedPreferences.getInt(s, 0);

            if (status == ClubInfoCompact.CLUB_STATUS_APPLY_REFUSED) {
                onMyApplyTipDismiss();
                this.mUserSp.edit().putInt(Constants.PREF_CLUB_STATUS, ClubInfoCompact.CLUB_STATUS_NONE).apply();
            } else if (status == ClubInfoCompact.CLUB_STATUS_APPLY) {
                this.getMyClub();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != this.mUserSp) {
            this.mUserSp.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.club_discover_apply_tip: {

                switch (myClubInfo.getStatus()) {
                    case ClubInfoCompact.CLUB_STATUS_CREATE:
                        Intent it = new Intent(getActivity(), ClubCreateActivity.class);
                        startActivity(it);
                        break;
                    case ClubInfoCompact.CLUB_STATUS_APPLY:
                        IntentUtils.goClubFeedInfoActivity(getContext(), myClubInfo);
                        break;
                }
            }
            case R.id.recommend_tab:
                showFragment(1);
                break;
            case R.id.nearby_tab:
                showFragment(2);
                break;
        }
    }

    private void getMyClub() {
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, ClubInfoCompact>() {

                    @Override
                    protected ClubInfoCompact doInBackground(String... params) {
                        AVUser user = AVUser.getCurrentUser();
                        if (null == user)
                            return null;

                        try {
                            return clubManager.getMyClub(user.getObjectId());
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(ClubInfoCompact result) {
                        if (null != searchBar)
                            searchBar.requestFocus();
                        if (null == result) {
                            onMyApplyTipDismiss();
                            return;
                        }
                        clubId = result.getObjectId();
                        status = result.getStatus();
                        onMyClubInfo(result);
                    }

                });
    }

    @Override
    public List<?> loadHistoryData(String searchKey) {
        return null;
    }

    @Override
    public List<?> loadIntelligenceData(String searchKey, String keyword) {
        return null;
    }

    @Override
    public CharSequence getHistoryCharSequence(Object resultValue) {
        return null;
    }

    @Override
    public CharSequence getIntelligenceCharSequence(Object resultValue) {
        return null;
    }

    @Override
    public void recordHistory(String searchKey, String keyword) {

    }

    @Override
    public void clearHistory(String searchKey) {

    }

    @Override
    public String getSearchKey() {
        return null;
    }

    @Override
    public void goSearch(String searchKey, String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            loadingDialog = new LoadingDialog(getActivity(),
                    getString(R.string.club_search_loading_msg), false);
            loadingDialog.show();
            fetchClubCityList(keyword);
            SpeedxAnalytics.onEvent(getActivity(), "搜索俱乐部", "search_club");
        }
    }

    @Override
    public void onHistoryItemClicked(Object historyItem) {

    }

    @Override
    public void onIntelligenceItemClicked(Object intelligenceItem) {

    }


    public void onMyClubInfo(ClubInfoCompact info) {

        if (null == info)
            return;
        myClubInfo = info;
        refreshClubStatus(info);
    }


    public void onMyApplyTipDismiss() {
        applayTip.setVisibility(View.GONE);
    }

    /**
     * 搜索俱乐部
     */
    private void fetchClubCityList(final String keyword) {
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<ClubInfoCompact>>() {
                    @Override
                    protected List<ClubInfoCompact> doInBackground(
                            String... params) {
                        try {
                            return clubManager.getClubList(ClubManager.CLUB_ORDERBY.NONE,
                                    null, keyword,
                                    1, pageCount);

                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<ClubInfoCompact> result) {
                        if (null != loadingDialog)
                            loadingDialog.dismiss();

                        if (!TextUtils.isEmpty(keyword)) {
                            if (null == result || result.isEmpty()) {
                                Toasts.show(
                                        getActivity(),
                                        getString(R.string.club_search_fail_msg));
                                return;
                            }
                            Intent intent = new Intent(getActivity(),
                                    ClubSearchActivity.class);
                            intent.putExtra(ClubSearchFrag.EXTRA_SEARCH_RESULT,
                                    (Serializable) result);
                            intent.putExtra(ClubSearchFrag.EXTRA_SEARCH_KEY,
                                    keyword);
                            ClubDiscoverFrag.this.startActivity(intent);
                        }
                    }
                });
    }

    public void showFragment(int index) {
        if (getActivity() == null)
            return;

        FragmentTransaction ft = fm.beginTransaction();

        switch (index) {
            case 1:
                SpeedxAnalytics.onEvent(getContext(), "推荐俱乐部", "comment_club");
                // 如果fragment1已经存在则将其显示出来
                recommendTab.setSelected(true);
                nearbyTab.setSelected(false);
                if (nearbyFrag != null) {
                    ft.hide(nearbyFrag);
                }
                if (recommendFrag != null) {
                    ft.show(recommendFrag);
                    // 否则是第一次切换则添加fragment1，注意添加后是会显示出来的，replace方法也是先remove后add
                }
                else {
                    recommendFrag = new RecommendFrag();
                    ft.add(R.id.frag_container, recommendFrag);
                }
                break;
            case 2:
                SpeedxAnalytics.onEvent(getContext(), "同城俱乐部", "same_city_club");
                nearbyTab.setSelected(true);
                recommendTab.setSelected(false);
                if(recommendFrag != null) {
                    ft.hide(recommendFrag);
                }
                if (nearbyFrag != null) {
                    ft.show(nearbyFrag);
                }
                else {
                    String city = "";
                    if(AVUser.getCurrentUser() != null){
                        city = AVUser.getCurrentUser().getCity();
                    }
                    nearbyFrag = new NearbyFrag();
                    nearbyFrag.setParams(this, city);
                    ft.add(R.id.frag_container, nearbyFrag);
                }

                break;
        }
        ft.commitAllowingStateLoss();
    }



    @Override
    public void noNearByCallBack() {
//        hasNearby = false;
//        showFragment(2);
        
        //// FIXME: 16/7/12  没有附近俱乐部会显示创建俱乐部按钮
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CREATE && resultCode == Activity.RESULT_OK) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

}
