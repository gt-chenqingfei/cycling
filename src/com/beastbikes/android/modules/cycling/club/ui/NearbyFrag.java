package com.beastbikes.android.modules.cycling.club.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.AbsListView;
import android.widget.ListView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.utils.IntentUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.lib.frag.FragBaseList;
import com.beastbikes.framework.ui.android.lib.list.BaseListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 15/11/30.
 */
public class NearbyFrag extends FragBaseList<String, ClubInfoCompact, ListView> implements BDLocationListener {

    private ClubManager clubManager;
    private int page = 1;
    private final int pageCount = 20;
    private String clubCity;
    private LoadingDialog loadingDialog;
    private List<ClubInfoCompact> nearbyList;
    private NoNearByCallBack noNearByCallBack;

    private LocationClient client;
    private LocationClientOption option;

    private static final int NO_GPS_TIMEOUT = 3 * 1000;

    private boolean canNotGetCity = false;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public NearbyFrag() {
        super();
    }

    public void setParams(NoNearByCallBack noNearByCallBack, String clubCity){
        this.noNearByCallBack = noNearByCallBack;
        this.clubCity = clubCity;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        clubManager = new ClubManager(getActivity());
        pullView.disablePullDown();
        loadingDialog = new LoadingDialog(getActivity(),
                getString(R.string.pull_to_refresh_refreshing_label), true);
        loadingDialog.show();
        nearbyList = new ArrayList<>();
        if (TextUtils.isEmpty(clubCity)||clubCity.equals("null")) {
            this.option = new LocationClientOption();
            this.option.setOpenGps(true);
            this.option.setCoorType("bd09ll");
            this.option.setAddrType("all");
            this.option.setPriority(LocationClientOption.NetWorkFirst);

            this.client = new LocationClient(getActivity());
            this.client.registerLocationListener(this);
            this.client.setLocOption(this.option);
            this.client.start();
            this.client.requestLocation();

            mainHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (TextUtils.isEmpty(clubCity)) {
                        canNotGetCity = true;
                        client.stop();
                        fetchClubCityList();
                    }
                }
            }, NO_GPS_TIMEOUT);
        } else {
            fetchClubCityList();
        }
    }

    @Override
    protected void onItemClick(ClubInfoCompact item) {
        super.onItemClick(item);

        IntentUtils.goClubFeedInfoActivity(getContext(), item);
    }

    @Override
    public void loadNormal() {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadMore(String maxId) {
        // TODO Auto-generated method stub
        super.loadMore(maxId);
        page++;
        fetchClubCityList();
    }

    /**
     * 获取所有同城俱乐部
     */
    private void fetchClubCityList() {
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<ClubInfoCompact>>() {
                    @Override
                    protected List<ClubInfoCompact> doInBackground(
                            String... params) {
                        try {
                            if (!canNotGetCity) {
                                return clubManager.getClubList(ClubManager.CLUB_ORDERBY.NONE,
                                        clubCity, null,
                                        page, pageCount);
                            } else {
                                return clubManager.getClubList(ClubManager.CLUB_ORDERBY.RECOMMEND,
                                        "", "",
                                        page, pageCount);
                            }

                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<ClubInfoCompact> result) {
                        if (null != loadingDialog)
                            loadingDialog.dismiss();

                        if (null == result || result.isEmpty()) {
                            if (null != getActivity()) {
                                if (page == 1 && null != noNearByCallBack) {
                                    noNearByCallBack.noNearByCallBack();
                                }
                                if (page > 1) {
                                    onLoadFailed(getActivity().getString(
                                            R.string.club_discover_load_end));
                                }
                                pullView.disablePull();

                            }
                            return;
                        }
                        nearbyList.addAll(result);
                        onLoadSucessfully(nearbyList);
                    }
                });
    }

    @Override
    protected BaseListAdapter<ClubInfoCompact> adapterToDisplay(AbsListView view) {
        // TODO Auto-generated method stub
        return new ClubSearchAdapter(null, view);
    }

    public interface NoNearByCallBack {
        void noNearByCallBack();
    }

    @Override
    public void onReceiveLocation(BDLocation bd) {
        if (null == bd || TextUtils.isEmpty(bd.getCity()))
            return;

        mainHandler.removeMessages(NO_GPS_TIMEOUT);

        this.clubCity = bd.getCity();

        fetchClubCityList();
    }

}
