package com.beastbikes.android.modules.cycling.route.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.route.biz.RouteManager;
import com.beastbikes.android.modules.cycling.route.dto.CityDTO;
import com.beastbikes.android.modules.cycling.route.dto.RouteDTO;
import com.beastbikes.android.modules.user.ui.ProgressDialog;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.StringResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by chenqingfei on 16/1/14.
 */

@Alias("路线首页")
@StringResource(R.string.routes_fragment_title)
@LayoutResource(R.layout.route_fragment)
public class RoutesFrag extends SessionFragment implements
        AdapterView.OnItemClickListener, BDLocationListener {


    private static final float RESOLUTION = 640f / 380f;

    private static final String TAG = "RoutesFragment";

    // City ID of Beijing
    private static final String DEFAULT_CITY_CITY_ID = "131";

    @IdResource(R.id.route_list_view)
    private ListView routeList;

    @IdResource(R.id.network_err)
    private ImageView err;

    private final BDLocation myLocation = new BDLocation();
    private final Set<CityDTO> cities = new LinkedHashSet<CityDTO>();
    private final List<RouteDTO> routes = new ArrayList<RouteDTO>();

    private RouteAdapter routeAdapter;
    private RouteManager routeManager;
    private LocationClient client;
    private LocationClientOption opts;

    private ProgressDialog dialog;

    private String cityId = DEFAULT_CITY_CITY_ID;

    private boolean fetched;

    private boolean isShowKM;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(R.string.discovery_fragment_good_route);
        this.routeManager = new RouteManager(getActivity());
        final CityDTO dto = new CityDTO();
        dto.setCityId(DEFAULT_CITY_CITY_ID);
        dto.setName(getString(R.string.beijing));
        this.cities.add(dto);

        this.setHasOptionsMenu(true);
        this.isShowKM = LocaleManager.isDisplayKM(this.getActivity());
        this.routeAdapter = new RouteAdapter();
        this.routeList.setAdapter(this.routeAdapter);
        this.routeList.setOnItemClickListener(this);

        if (!fetched && routeList != null) {
            if (TextUtils.isEmpty(cityId))
                this.cityId = DEFAULT_CITY_CITY_ID;

            this.fetchCitiesAndRoutes(this.cityId);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getActivity().setTitle(R.string.discovery_fragment_good_route);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.opts = new LocationClientOption();
        this.opts.setAddrType("all");
        this.opts.setOpenGps(true);
        this.client = new LocationClient(getActivity());
        this.client.setLocOption(this.opts);
        this.client.registerLocationListener(this);
        this.client.start();
        this.client.requestLocation();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (null != this.client && this.client.isStarted()) {
            this.client.stop();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        final Context ctx = getActivity();
        synchronized (this.cities) {
            for (final CityDTO city : this.cities) {
                final String id = city.getCityId();
                final String title = city.getName();
                final MenuItem mi = menu.add(title);

                String display = Build.FINGERPRINT;
                String version = Build.VERSION.RELEASE;
                if (version.equals("4.2.2")) {
                    if (!display.contains("Xiaomi")) {
                        mi.setCheckable(true);
                        mi.setChecked(id.equals(this.cityId));
                    }
                } else {
                    mi.setCheckable(true);
                    mi.setChecked(id.equals(this.cityId));
                }
                mi.setTitleCondensed("aa");
                mi.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        dialog = new ProgressDialog(ctx);
                        dialog.setMessage(R.string.activity_record_detail_activity_loading);
                        fetchCitiesAndRoutes(id);
                        return true;
                    }
                });
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        SpeedxAnalytics.onEvent(getActivity(), "点击路线总次数", null);

        final RouteDTO dto = (RouteDTO) parent.getItemAtPosition(position);
        if (null == dto)
            return;

        final Intent intent = new Intent(getActivity(), RouteActivity.class);
        intent.putExtra(RouteActivity.EXTRA_ROUTE, dto);
        startActivity(intent);

    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (null == location)
            return;

        this.myLocation.setAddrStr(location.getAddrStr());
        this.myLocation.setAltitude(location.getAltitude());
        this.myLocation.setCoorType(location.getCoorType());
        this.myLocation.setLatitude(location.getLatitude());
        this.myLocation.setLongitude(location.getLongitude());
        this.myLocation.setLocType(location.getLocType());

        final String cityCode = location.getCityCode();
        if (!DEFAULT_CITY_CITY_ID.equals(cityCode)) {
            this.fetchCitiesAndRoutes(cityCode);
        }

        if (null != this.client) {
            this.client.stop();
        }
    }

    private void fetchCitiesAndRoutes(String cityId) {
        final Context context = getActivity();
        if (TextUtils.isEmpty(cityId)) {
            this.cityId = DEFAULT_CITY_CITY_ID;
        } else {
            this.cityId = cityId;
        }

        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<RouteDTO>>() {

                    @Override
                    protected void onPreExecute() {
                        if (dialog != null) {
                            dialog.show();
                        }
                    }

                    @Override
                    protected List<RouteDTO> doInBackground(String... params) {
                        String cityId = params[0];
                        if (TextUtils.isEmpty(cityId)) {
                            cityId = DEFAULT_CITY_CITY_ID;
                        }

                        try {
                            final List<CityDTO> cities = routeManager.getRouteCities();
                            if (cities != null && !cities.isEmpty()) {
                                synchronized (RoutesFrag.this.cities) {
                                    RoutesFrag.this.cities.clear();
                                    RoutesFrag.this.cities.addAll(cities);
                                }
                            }
                        } catch (BusinessException e) {
                            Log.e(TAG, "Load cities error", e);
                        }

                        try {
                            if (cities.size() < 1)
                                cityId = DEFAULT_CITY_CITY_ID;

                            Set<String> cityIds = new LinkedHashSet<String>();
                            for (CityDTO city : cities) {
                                String id = city.getCityId();
                                cityIds.add(id);
                            }

                            if (!cityIds.contains(cityId))
                                cityId = DEFAULT_CITY_CITY_ID;

                            return routeManager.getRoutesByCityId(cityId);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<RouteDTO> list) {
                        if (dialog != null)
                            dialog.dismiss();

                        if (null == list || list.isEmpty()) {
                            err.setVisibility(View.VISIBLE);
                            routes.clear();
                            routeAdapter.notifyDataSetChanged();
                            fetched = false;
                        }

                        Activity context = getActivity();//fix java.lang.NullPointerException
                        if (context != null) {
                            context.invalidateOptionsMenu();
                        }

                        if (null != list) {
                            synchronized (routes) {
                                err.setVisibility(View.GONE);
                                routes.clear();
                                routes.addAll(list);
                                routeAdapter.notifyDataSetChanged();
                                fetched = true;
                            }
                        }
                    }

                }, cityId);
    }

    private final class RouteAdapter extends BaseAdapter {
        private final WindowManager wm = getActivity().getWindowManager();
        private final Display display = wm.getDefaultDisplay();
        private final DisplayMetrics dm = new DisplayMetrics();

        public RouteAdapter() {
            this.display.getMetrics(dm);
        }

        @Override
        public int getCount() {
            return routes.size();
        }

        @Override
        public Object getItem(int position) {
            return routes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder<RouteDTO> holder;

            if (null == convertView) {
                convertView = View.inflate(parent.getContext(),
                        R.layout.routes_fragment_route_list_item, null);
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(dm.widthPixels,
                        (int) (this.dm.widthPixels / RESOLUTION));
                convertView.setLayoutParams(lp);
                holder = new RouteViewHoder(convertView);
            } else {
                holder = RouteViewHoder.as(convertView);
            }

            holder.bind((RouteDTO) getItem(position));
            return convertView;
        }

    }

    private final class RouteViewHoder extends ViewHolder<RouteDTO> {

        @IdResource(R.id.route_loading)
        private ViewGroup loading;

        @IdResource(R.id.route_fragment_route_list_item_cover)
        private ImageView cover;

        @IdResource(R.id.route_fragment_route_list_item_name)
        private TextView name;

        @IdResource(R.id.route_fragment_route_list_item_english_name)
        private TextView englishName;

        @IdResource(R.id.route_fragment_route_list_item_follower)
        private TextView follower;

        @IdResource(R.id.route_fragment_route_list_item_difficulty_coefficient)
        private RatingBar difficulty;

        @IdResource(R.id.route_fragment_route_list_item_total_distance)
        private TextView totalDistance;

        @IdResource(R.id.route_fragment_route_list_item_distance_to_me)
        private TextView distanceToMe;

        protected RouteViewHoder(View v) {
            super(v);
        }

        @Override
        public void bind(final RouteDTO t) {
            this.name.setText(t.getName());
            this.englishName.setText(t.getEnglishName());
            this.follower.setText(String.format(
                    getString(R.string.routes_fragment_followed),
                    t.getNumberOfFollowers()));
            this.difficulty.setRating(Math.round(t.getDifficultyCoefficient()));
            if (isShowKM) {
                this.totalDistance.setText(String.format("%.0f km", t.getTotalDistance() / 1000));
            } else {
                this.totalDistance.setText(String.format("%.0f mi", LocaleManager.kilometreToMile(t.getTotalDistance()) / 1000));
            }

            if (!TextUtils.isEmpty(t.getCoverURL())) {
                Picasso.with(getContext()).load(t.getCoverURL()).fit().error(R.drawable.transparent).
                        placeholder(R.drawable.transparent).centerCrop().into(this.cover);
            } else {
                this.cover.setImageResource(R.drawable.transparent);
            }

            if (null != myLocation && t.getOriginLatitude() != 0) {
                LatLng startLl = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                LatLng endLl = new LatLng(t.getOriginLatitude(), t.getOriginLongitude());
                double distance = DistanceUtil.getDistance(startLl, endLl) / 1000;
                if (isShowKM) {
                    this.distanceToMe.setText(String.format("<%.0f km", distance));
                } else {
                    this.distanceToMe.setText(String.format("<%.0f mi", LocaleManager.kilometreToMile(distance)));
                }
            }
        }
    }

}
