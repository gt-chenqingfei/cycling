package com.beastbikes.android.modules.cycling.route.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.locale.googlemaputils.GooglePlaceAPICallBack;
import com.beastbikes.android.locale.googlemaputils.GooglePlaceAPIUtils;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.route.dto.GooglePlaceAddressDTO;
import com.beastbikes.android.modules.cycling.route.dto.PoiInfoDTO;
import com.beastbikes.android.modules.cycling.sections.ui.widget.CustomEditText;
import com.beastbikes.android.modules.cycling.sections.ui.widget.DrawableClickListener;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.List;

@LayoutResource(R.layout.route_map_search_geo_activity)
public class RouteMapSearchGeoActivity extends SessionFragmentActivity
        implements OnClickListener, OnGetPoiSearchResultListener,
        OnItemClickListener, RequestQueueManager, GooglePlaceAPICallBack {

    public static final int EXTRA_SEARCH_RESULT_CODE = 80;

    public static boolean isLandscape = true;

    @IdResource(R.id.route_map_search_back)
    private ImageView goBackIv;

    //    @IdResource(R.id.route_map_search_edittext)
    private CustomEditText searchEt;

    @IdResource(R.id.route_map_search_result)
    private ListView poiInfoLv;

    // 兴趣点搜索
    private PoiSearch poiSearch;

    private PoiInfoAdapter adapter;
    private List<PoiInfo> pis = new ArrayList<>();

    private String search;
    private int searchCount = 0;

    private GooglePlaceAPIUtils googlePlaceAPIUtils;

    private RequestQueue requestQueue;

    private boolean isChineseVersion = true;

    private List<GooglePlaceAddressDTO> googlePlaceList = new ArrayList<>();

    private GooglePoiInfoAdapter googlePoiInfoAdapter;

    private GoogleOnItemClickListener googleOnItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        isChineseVersion = LocaleManager.isChineseTimeZone();

        if (isChineseVersion) {
            this.adapter = new PoiInfoAdapter(pis);
            this.poiInfoLv.setAdapter(adapter);
            this.poiInfoLv.setOnItemClickListener(this);
            this.poiSearch = PoiSearch.newInstance();
            this.poiSearch.setOnGetPoiSearchResultListener(this);
        } else {
            this.googleOnItemClickListener = new GoogleOnItemClickListener();
            this.googlePoiInfoAdapter = new GooglePoiInfoAdapter(googlePlaceList);
            this.poiInfoLv.setAdapter(googlePoiInfoAdapter);
            this.poiInfoLv.setOnItemClickListener(googleOnItemClickListener);
            this.requestQueue = RequestQueueFactory.newRequestQueue(this);
            this.googlePlaceAPIUtils = new GooglePlaceAPIUtils(this, getAsyncTaskQueue(), this.getRequestQueue(), this);
        }

        this.goBackIv.setOnClickListener(this);
        searchEt = (CustomEditText) findViewById(R.id.route_map_search_edittext);
        this.searchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0) {
                    search = s.toString();
                    if (isChineseVersion) {
                        if (poiSearch != null)
                            poiSearch.searchInCity(new PoiCitySearchOption().city("").keyword(search));
                    } else {
                        if (googlePlaceAPIUtils != null)
                            googlePlaceAPIUtils.getResultByGoogle(search);
                    }
                }

                if (TextUtils.isEmpty(searchEt.getText().toString())) {
                    searchEt.setCompoundDrawables(null, null, null, null);
                } else {
                    Drawable rightDrawable = getResources().getDrawable(R.drawable.ic_section_search_clear_black);
                    rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                    searchEt.setCompoundDrawables(null, null, rightDrawable, null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchEt.setDrawableClickListener(new DrawableClickListener() {

            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        searchEt.setText("");
                        break;
                    default:
                        break;
                }
            }

        });
        searchEt.setFocusable(true);
        searchEt.setFocusableInTouchMode(true);
    }

    @Override
    public RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

    @Override
    protected void onResume() {
        if (!isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    @Override
    public void placeAPIonConnectionFailed() {

    }

    @Override
    public void getAutocompletePredictionFail(Status status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toasts.show(RouteMapSearchGeoActivity.this, getResources().getString(R.string.get_poi_no_result));
            }
        });
    }

    @Override
    public void getGooglePlaceAddressDTOList(List<GooglePlaceAddressDTO> googlePlaceAddressList) {
        if (googlePlaceAddressList == null || googlePlaceAddressList.size() == 0) {

            return;
        }
        googlePlaceList.clear();
        googlePlaceList.addAll(googlePlaceAddressList);
        if (googlePoiInfoAdapter != null)
            googlePoiInfoAdapter.notifyDataSetChanged();
        poiInfoLv.setBackgroundResource(R.drawable.bg_shadow);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.route_map_search_back:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        PoiInfo info = (PoiInfo) parent.getAdapter().getItem(position);
        if (null == info)
            return;

        Intent intent = getIntent();
        intent.putExtra(RoutePlanActivity.EXTRA_POIINFO, new PoiInfoDTO(info));
        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, result.getName() + ": " + result.getAddress(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toasts.show(this, getResources().getString(R.string.get_poi_no_result));
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            this.searchCount = 0;
            List<PoiInfo> infos = result.getAllPoi();
            if (!infos.isEmpty()) {
                this.pis.clear();
                this.pis.addAll(infos);
                this.adapter.notifyDataSetChanged();
            }
            poiInfoLv.setBackgroundResource(R.drawable.bg_shadow);
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            if (this.searchCount > 2) {
                this.searchCount = 0;
                return;
            }

            if (result.getSuggestCityList().size() > 0)
                poiSearch.searchInCity(new PoiCitySearchOption().city(
                        result.getSuggestCityList().get(0).city)
                        .keyword(search));

            this.searchCount += 1;
        }
    }

    private final class PoiInfoAdapter extends BaseAdapter {

        public final List<PoiInfo> list;

        public PoiInfoAdapter(List<PoiInfo> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final PoiInfoViewHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.route_map_search_list_item, null);
                vh = new PoiInfoViewHolder(convertView);
            } else {
                vh = (PoiInfoViewHolder) convertView.getTag();
            }

            vh.bind(this.list.get(position));

            return convertView;
        }

    }

    private final class PoiInfoViewHolder extends ViewHolder<PoiInfo> {

        @IdResource(R.id.route_map_search_item_name)
        private TextView nameTv;

        @IdResource(R.id.route_map_search_item_address)
        private TextView addressTv;

        protected PoiInfoViewHolder(View v) {
            super(v);
        }

        @Override
        public void bind(PoiInfo t) {
            if (null == t)
                return;

            this.nameTv.setText(t.name);
            this.addressTv.setText(t.address);
        }
    }

    private final class GooglePoiInfoViewHolder extends ViewHolder<GooglePlaceAddressDTO> {

        @IdResource(R.id.route_map_search_item_name)
        private TextView nameTv;

        @IdResource(R.id.route_map_search_item_address)
        private TextView addressTv;

        protected GooglePoiInfoViewHolder(View v) {
            super(v);
        }

        @Override
        public void bind(GooglePlaceAddressDTO t) {
            if (null == t)
                return;

            this.nameTv.setText(t.getName());
            this.addressTv.setText(t.getAddress());
        }
    }

    private final class GooglePoiInfoAdapter extends BaseAdapter {

        public final List<GooglePlaceAddressDTO> list;

        public GooglePoiInfoAdapter(List<GooglePlaceAddressDTO> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final GooglePoiInfoViewHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.route_map_search_list_item, null);
                vh = new GooglePoiInfoViewHolder(convertView);
            } else {
                vh = (GooglePoiInfoViewHolder) convertView.getTag();
            }

            vh.bind(this.list.get(position));

            return convertView;
        }

    }

    private class GoogleOnItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            GooglePlaceAddressDTO info = (GooglePlaceAddressDTO) parent.getAdapter().getItem(position);
            if (null == info)
                return;
            PoiInfoDTO poiInfoDTO = new PoiInfoDTO(info);
            Intent intent = getIntent();
            intent.putExtra(RoutePlanActivity.EXTRA_POIINFO, poiInfoDTO);
            RouteMapSearchGeoActivity.this.setResult(RESULT_OK, intent);
            RouteMapSearchGeoActivity.this.finish();
        }
    }
}
