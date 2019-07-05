package com.beastbikes.android.modules.shop.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.locale.locationutils.UtilsLocationManager;
import com.beastbikes.android.modules.shop.biz.BikeShopManager;
import com.beastbikes.android.modules.shop.dto.BikeShopListDTO;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.WebActivity;
import com.beastbikes.framework.ui.android.lib.frag.FragBaseList;
import com.beastbikes.framework.ui.android.lib.list.BaseListAdapter;
import com.beastbikes.framework.ui.android.lib.view.search.MySearchListener;

import java.util.ArrayList;
import java.util.List;


public class BikeShopListFrag extends FragBaseList<String, BikeShopListDTO, ListView> implements
        MySearchListener, View.OnClickListener {

    public static final String EXTRA_TYPE = "type";

    public static final int WEB_REQUEST_CODE = 0x1;

    private BikeShopManager bikeShopManager;
    private List<BikeShopListDTO> bikeShopList = null;
    private LoadingDialog loadingDialog;
    private double lat;
    private double lon;

    private String type = "location";
    private String userId;

    @Override
    public void onResume() {
        super.onResume();

        if ("mine".equals(type)) {
            fetchBikeShopList(null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        if (bundle.getInt(EXTRA_TYPE) == R.id.bike_shop_tab_mine) {
            type = "mine";
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (AVUser.getCurrentUser() != null) {
            userId = AVUser.getCurrentUser().getObjectId();
        }

        bikeShopManager = new BikeShopManager(getActivity());
        pullView.disablePullDown();
        pullView.enablePullUp();
        loadingDialog = new LoadingDialog(getActivity(),
                getString(R.string.pull_to_refresh_refreshing_label), true);
        loadingDialog.show();
        bikeShopList = new ArrayList<>();

        SharedPreferences sharedPreferences = getActivity().
                getSharedPreferences(UtilsLocationManager.getInstance().getClass().getName(), 0);
        this.lat = Float.parseFloat(sharedPreferences.getString(Constants.BLE.PREF_LOCATION_LAT, "0"));
        this.lon = Float.parseFloat(sharedPreferences.getString(Constants.BLE.PREF_LOCATION_LON, "0"));

        pullView.setPullToRefreshEnabled(false);

        if ("mine".equals(type)) {
            View view = View.inflate(getContext(), R.layout.bike_shop_footer_view, null);
            view.findViewById(R.id.bike_shop_add_tv).setOnClickListener(this);
            this.addFooterView(view);
        } else {
            fetchBikeShopList(null);
        }
    }


    @Override
    protected void onItemClick(final BikeShopListDTO item) {
        super.onItemClick(item);


        if (item != null) {
            if (item.getStatus() == BikeShopListDTO.STATUS_PASS || item.getStatus() == BikeShopListDTO.STATUS_UNTREATED) {
                SpeedxAnalytics.onEvent(getContext(), "查看车店详情", "open_bicycle_detail");
                Intent intentShopDetail = new Intent(getContext(), BikeShopDetailActivity.class);
                intentShopDetail.putExtra(BikeShopDetailActivity.INTENT_SHOP_ID, item.getShopId());
                intentShopDetail.putExtra(BikeShopDetailActivity.SHOW_ENTER_CLUB, true);
                intentShopDetail.putExtra(BikeShopDetailActivity.EXTRA_TYPE, type);
                startActivity(intentShopDetail);
            } else if (item.getStatus() == BikeShopListDTO.STATUS_FAIL) {
                final MaterialDialog dialog = new MaterialDialog(getContext());
                dialog.setTitle(R.string.dialog_bike_shop_fail_title);
                dialog.setMessage(item.getReason());
                dialog.setPositiveButton(R.string.club_release_activites_dialog_ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        final StringBuilder sb = new StringBuilder(Constants.UrlConfig.DEV_SPEEDX_HOST)
                                .append("/app/shop/auth.html?shopId=" + item.getShopId() + "#shop");
                        final Uri browserUri = Uri.parse(sb.toString());
                        final Intent browserIntent = new Intent(getContext(),
                                BrowserActivity.class);
                        browserIntent.setData(browserUri);
                        browserIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                        browserIntent.setPackage(getContext().getPackageName());
                        browserIntent.putExtra(WebActivity.EXTRA_TITLE,
                                getString(R.string.bike_shop_edit));
                        browserIntent.putExtra(WebActivity.EXTRA_CAN_GOBACK, true);
                        browserIntent.putExtra(WebActivity.EXTRA_ENTER_ANIMATION,
                                R.anim.activity_in_from_right);
                        browserIntent.putExtra(WebActivity.EXTRA_EXIT_ANIMATION,
                                R.anim.activity_out_to_right);
                        browserIntent.putExtra(WebActivity.EXTRA_NONE_ANIMATION,
                                R.anim.activity_none);
                        startActivity(browserIntent);

                    }
                }).setNegativeButton(R.string.club_release_activites_dialog_cencle, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).show();

            }
        }
    }

    @Override
    public void onItemLongClick(final BikeShopListDTO item) {
        super.onItemLongClick(item);
        if ("location".equals(type)) {
            return;
        }
        if (item != null && item.getOwnerId().equals(userId)) {
            final MaterialDialog deleteDialog = new MaterialDialog(getContext());
            deleteDialog.setCanceledOnTouchOutside(true);
            View deleteView = LayoutInflater.from(getContext()).
                    inflate(R.layout.activity_record_delete_dialog, null);
            TextView deleteTv = (TextView) deleteView.findViewById(R.id.delete_dialog_delete_item);
            deleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                    onBikeShopDelWarning(item);
                }
            });
            deleteDialog.setContentView(deleteView).show();
        }
    }

    private void onBikeShopDelWarning(final BikeShopListDTO item) {
        final MaterialDialog dialog = new MaterialDialog(getContext());
        dialog.setMessage(R.string.dialog_sure_bike_shop_delete);
        dialog.setPositiveButton(R.string.delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                delBikeShop(item.getShopId());
            }
        }).setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void loadNormal() {

    }

    @Override
    public void loadMore(String maxId) {
        super.loadMore(maxId);
    }

    /**
     * 删除车店
     */
    private void delBikeShop(final long shopId) {
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, Boolean>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        loadingDialog = new LoadingDialog(getActivity(),
                                getString(R.string.club_info_waiting), true);
                        loadingDialog.show();
                    }

                    @Override
                    protected Boolean doInBackground(
                            String... params) {
                        try {
                            return bikeShopManager.deleteBikeShop(shopId);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (loadingDialog != null && null != getActivity() && !getActivity().isFinishing()) {
                            loadingDialog.dismiss();
                        }
                        fetchBikeShopList("");
                    }

                });
    }

    /**
     * 获取所有俱乐部推荐
     */
    private void fetchBikeShopList(final String key) {
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<BikeShopListDTO>>() {
                    @Override
                    protected List<BikeShopListDTO> doInBackground(
                            String... params) {
                        try {

                            return bikeShopManager.getBikeShopList(lon, lat, -1, key, lat, lon, type);

                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<BikeShopListDTO> result) {
                        if (loadingDialog != null && null != getActivity() && !getActivity().isFinishing())
                            loadingDialog.dismiss();
                        if (result == null || result.isEmpty()) {
                            Activity activity = getActivity();
                            if (null == activity)
                                return;
                            if ("mine".equals(type)) {
                                addEmptyView();
                            } else {
                                onLoadFailed(activity.getString(R.string.bike_shop_load_fail));
                            }
                            return;
                        }
                        if ("mine".equals(type)) {
                            onLoadSucessfully(result);
                        } else {
                            if (TextUtils.isEmpty(key)) {
                                if (bikeShopList.size() <= 0) {
                                    bikeShopList.addAll(result);
                                }
                                onLoadSucessfully(bikeShopList);
                            } else {
                                onLoadSucessfully(result);
                            }
                        }
                    }
                });
    }

    private void addEmptyView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bike_shop_empty_view, null);
        view.findViewById(R.id.bike_shop_add).setOnClickListener(this);
        pullView.addEmptyView(view);
    }

    @Override
    protected BaseListAdapter<BikeShopListDTO> adapterToDisplay(AbsListView view) {
        return new BikeShopListAdapter(null, view, type);
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
        onLoadSucessfully(bikeShopList);
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
            fetchBikeShopList(keyword);
        }
    }

    @Override
    public void onHistoryItemClicked(Object historyItem) {

    }

    @Override
    public void onIntelligenceItemClicked(Object intelligenceItem) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bike_shop_add || v.getId() == R.id.bike_shop_add_tv) {

            final StringBuilder sb = new StringBuilder(Constants.UrlConfig.DEV_SPEEDX_HOST)
                    .append("/app/shop/auth.html#shop");
            final Uri browserUri = Uri.parse(sb.toString());
            final Intent browserIntent = new Intent(getActivity(),
                    BrowserActivity.class);
            browserIntent.setData(browserUri);
            browserIntent.addCategory(Intent.CATEGORY_DEFAULT);
            browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
            browserIntent.setPackage(getContext().getPackageName());
            browserIntent.putExtra(WebActivity.EXTRA_TITLE,
                    getString(R.string.bike_shop_add));
            browserIntent.putExtra(WebActivity.EXTRA_CAN_GOBACK, true);
            browserIntent.putExtra(WebActivity.EXTRA_ENTER_ANIMATION,
                    R.anim.activity_in_from_right);
            browserIntent.putExtra(WebActivity.EXTRA_EXIT_ANIMATION,
                    R.anim.activity_out_to_right);
            browserIntent.putExtra(WebActivity.EXTRA_NONE_ANIMATION,
                    R.anim.activity_none);
            startActivityForResult(browserIntent, WEB_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //FIXME 添加车店成功后,刷新我的车店列表,需要js联合调试
        if (requestCode == WEB_REQUEST_CODE && requestCode == Activity.RESULT_OK) {

            this.fetchBikeShopList("");
        }
    }
}