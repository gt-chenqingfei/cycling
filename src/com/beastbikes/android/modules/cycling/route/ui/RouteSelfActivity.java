package com.beastbikes.android.modules.cycling.route.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.route.biz.RouteManager;
import com.beastbikes.android.modules.cycling.route.dto.RouteDTO;
import com.beastbikes.android.modules.preferences.ui.BaseEditTextActivity;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.cache.CacheManager;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.android.utils.ConnectivityUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.AsyncImageView;

import java.util.ArrayList;
import java.util.List;

@Alias("我的路书")
@LayoutResource(R.layout.route_self_activity)
@MenuResource(R.menu.route_make_line_menu)
public class RouteSelfActivity extends SessionFragmentActivity implements
        OnClickListener, OnItemClickListener, OnItemLongClickListener,
        OnSharedPreferenceChangeListener, RequestQueueManager {

    public static final int EXTRA_RESULT = 8;

    public static final String SP_USE_ROUTE_ID = "use_route_id";

    public static final String EXTRA_ROUTE_ID = "route_id";

    @IdResource(R.id.route_make_first_btn)
    private Button makeRouteBtn;

    @IdResource(R.id.route_self_list_view)
    private ListView routeLv;

    @IdResource(R.id.task_self_no_task_view)
    private ViewGroup noRouteView;

    private List<RouteDTO> rsfs = new ArrayList<RouteDTO>();
    private RouteSelfAdapter adapter;

    private RouteManager routeManager;

    private SharedPreferences sp;

    private RouteDTO currRoute;
    private LoadingDialog loadingDialog;

    private String sourceName;
    private int selectPosition;

    private RequestQueue requestQueue;

    @Override
    public final RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.requestQueue = RequestQueueFactory.newRequestQueue(this);

        this.routeManager = new RouteManager(this);
        this.makeRouteBtn.setOnClickListener(this);
        this.adapter = new RouteSelfAdapter(this, rsfs);
        this.routeLv.setAdapter(adapter);

        this.routeLv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        this.routeLv.setOnItemClickListener(this);
        this.registerForContextMenu(this.routeLv);
        this.routeLv.setOnItemLongClickListener(this);

        this.sp = getSharedPreferences(getPackageName(), 0);
        this.sp.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.fetchMyRoutes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.route_make_first_btn:
                Intent intent = new Intent(this, RoutePlanActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        RouteDTO route = (RouteDTO) parent.getAdapter().getItem(position);
        if (null == route)
            return;

        final Intent intent = new Intent(this, RoutePlanActivity.class);
        intent.putExtra(EXTRA_ROUTE_ID, String.valueOf(route.getId()));
        intent.putExtra(RoutePlanActivity.EXTRA_EDIT, false);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        RouteDTO route = (RouteDTO) parent.getAdapter().getItem(position);
        if (null == route)
            return true;

        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        MenuInflater inflater = new MenuInflater(v.getContext());
        inflater.inflate(R.menu.route_self_delete_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
                .getMenuInfo();
        selectPosition = menuInfo.position;
        if (selectPosition >= this.adapter.getCount())
            return true;

        currRoute = (RouteDTO) this.adapter.getItem(selectPosition);
        if (null == currRoute)
            return true;

        switch (item.getItemId()) {
            case R.id.route_self_route_delete:
                String routeId = currRoute.getId();
                if (TextUtils.isEmpty(routeId))
                    return true;

                this.deleteMyRouteById(routeId, selectPosition);

                SpeedxAnalytics.onEvent(this, "删除我的路线", null);
                break;
            case R.id.route_self_route_rename:
                this.sourceName = currRoute.getName();
                Intent intent = new Intent(this, BaseEditTextActivity.class);
                intent.putExtra(BaseEditTextActivity.EXTRA_VALUE, this.sourceName);
                startActivityForResult(intent, EXTRA_RESULT);

                SpeedxAnalytics.onEvent(this, "重命名我的路线", null);
                break;
            case R.id.route_self_route_edit:
                final Intent editIntent = new Intent(this, RoutePlanActivity.class);
                editIntent.putExtra(EXTRA_ROUTE_ID,
                        String.valueOf(currRoute.getId()));
                startActivity(editIntent);

                AVAnalytics.onEvent(this, "编辑我的路线");
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.route_make_line_menu_item: {
                // Intent intent = new Intent(this, RouteMapMakeActivity.class);
                SpeedxAnalytics.onEvent(this, "", "click_my_page_my_road_book_new");
                Intent intent = new Intent(this, RoutePlanActivity.class);
                startActivity(intent);

                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (null != key && key.equals(SP_USE_ROUTE_ID)) {
            String spRouteId = this.sp.getString(SP_USE_ROUTE_ID, "");
            for (RouteDTO rd : this.rsfs) {
                if (spRouteId.equals(rd.getId())) {
                    rd.setUse(true);
                } else {
                    rd.setUse(false);
                }
            }
            this.adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EXTRA_RESULT:
                if (RESULT_OK == resultCode) {
                    Bundle bundle = data.getExtras();
                    String name = bundle
                            .getString(BaseEditTextActivity.EXTRA_VALUE);
                    currRoute.setName(name);

                    updateRouteName(name);
                }
                break;
        }
    }

    /**
     * 获取我的路线
     */
    private void fetchMyRoutes() {
        this.loadingDialog = new LoadingDialog(this, getString(R.string.route_elevation_activity_loading), true);
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<RouteDTO>>() {
                    @Override
                    protected void onPreExecute() {
                        if (null != loadingDialog && !loadingDialog.isShowing()) {
                            loadingDialog.show();
                        }
                    }

                    @Override
                    protected List<RouteDTO> doInBackground(String... params) {
                        try {
                            return routeManager.getMyRoutes();
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<RouteDTO> result) {
                        if (null != loadingDialog && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        if (null == result || result.isEmpty()) {
                            routeLv.setVisibility(View.GONE);
                            noRouteView.setVisibility(View.VISIBLE);
                            return;
                        }

                        noRouteView.setVisibility(View.GONE);
                        routeLv.setVisibility(View.VISIBLE);
                        rsfs.clear();
                        rsfs.addAll(result);
                        if (sp.contains(SP_USE_ROUTE_ID)) {
                            String spRouteId = sp
                                    .getString(SP_USE_ROUTE_ID, "");
                            for (RouteDTO rd : rsfs) {
                                if (spRouteId.equals(rd.getId())) {
                                    rd.setUse(true);
                                } else {
                                    rd.setUse(false);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();

                    }

                });
    }

    /**
     * 根据routeId删除路线
     *
     * @param routeId
     * @param poistion
     */
    private void deleteMyRouteById(final String routeId, final int poistion) {
        this.loadingDialog = new LoadingDialog(this, getString(R.string.loading_msg_deleted), false);
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                if (null != loadingDialog) {
                    loadingDialog.show();
                }
            }

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    return routeManager.deleteMyRouteById(params[0]);
                } catch (BusinessException e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                }
                if (result) {
                    rsfs.remove(poistion);
                    adapter.notifyDataSetChanged();
                    if (rsfs.size() == 0) {
                        routeLv.setVisibility(View.GONE);
                        noRouteView.setVisibility(View.VISIBLE);
                    }
                    return;
                }

                final NetworkInfo ni = ConnectivityUtils.getActiveNetwork(RouteSelfActivity.this);
                if (null == ni || !ni.isConnected()) {
                    Toasts.show(RouteSelfActivity.this,
                            R.string.network_not_awesome);
                } else {
                    Toasts.show(RouteSelfActivity.this,
                            R.string.delete_err);
                }
            }

        }, routeId);
    }

    /**
     * 重命名
     *
     * @param name
     */
    private void updateRouteName(String name) {
        if (TextUtils.isEmpty(name))
            return;

        if (this.sourceName.equals(name))
            return;

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    return routeManager.updateRouteNameById(currRoute.getId(),
                            params[0]);
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    rsfs.remove(selectPosition);
                    rsfs.add(selectPosition, currRoute);
                    adapter.notifyDataSetChanged();
                }

            }

        }, name);
    }

    private final class RouteSelfAdapter extends BaseAdapter {

        private final List<RouteDTO> list;
        private final RouteSelfActivity activity;

        public RouteSelfAdapter(RouteSelfActivity activity, List<RouteDTO> list) {
            this.list = list;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public Object getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final RouteSelfViewHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.route_self_list_item, null);
                vh = new RouteSelfViewHolder(convertView, RouteSelfActivity.this);
            } else {
                vh = (RouteSelfViewHolder) convertView.getTag();
            }

            vh.bind(this.list.get(position));

            return convertView;
        }

    }

    private final class RouteSelfViewHolder extends ViewHolder<RouteDTO> {

        @IdResource(R.id.route_self_item_route_title)
        private TextView title;

        @IdResource(R.id.route_self_item_route_image)
        private AsyncImageView routeImg;

        @IdResource(R.id.route_self_item_distance)
        private TextView distance;

        @IdResource(R.id.route_self_item_unit)
        private TextView unit;


        @IdResource(R.id.route_self_item_use)
        private TextView use;

        @IdResource(R.id.route_self_item_route_default_image)
        private ImageView defaultImg;

        RequestQueueManager rqm;

        protected RouteSelfViewHolder(View v, RequestQueueManager rqm) {
            super(v);
            this.rqm = rqm;
        }

        @Override
        public void bind(final RouteDTO t) {
            if (null == t)
                return;

            this.title.setText(t.getName());

            double distance = t.getTotalDistance() / 1000;
            String unit = "km";
            if (!LocaleManager.isDisplayKM(getContext())) {
                distance = LocaleManager.kilometreToMile(distance);
                unit = "mi";
            }

            this.distance.setText(String.format("%.0f", distance));
            this.unit.setText(unit);

            CacheManager cm = CacheManager.getInstance();
            String routeUrl = t.getMapURL();

            if (TextUtils.isEmpty(routeUrl)) {
                this.routeImg.setScaleType(ScaleType.CENTER);
                this.defaultImg.setVisibility(View.VISIBLE);
            } else {
                this.routeImg.setImageUrl(routeUrl,
                        new ImageLoader(this.rqm.getRequestQueue(), cm) {

                            @Override
                            public ImageContainer get(String requestUrl,
                                                      final ImageListener imageListener,
                                                      int maxWidth, int maxHeight) {
                                return super.get(requestUrl,
                                        new ImageListener() {

                                            @Override
                                            public void onErrorResponse(
                                                    VolleyError arg0) {
                                                routeImg.setScaleType(ScaleType.CENTER);
                                                imageListener
                                                        .onErrorResponse(arg0);
                                            }

                                            @Override
                                            public void onResponse(
                                                    ImageContainer arg0,
                                                    boolean arg1) {
                                                routeImg.setScaleType(ScaleType.CENTER_CROP);
                                                imageListener.onResponse(arg0,
                                                        arg1);
                                            }

                                        }, maxWidth, maxHeight);
                            }

                            @Override
                            protected void onGetImageError(String cacheKey,
                                                           VolleyError error) {
                                super.onGetImageError(cacheKey, error);
                                routeImg.setScaleType(ScaleType.CENTER);
                            }

                            @Override
                            protected void onGetImageSuccess(String cacheKey,
                                                             Bitmap response) {
                                super.onGetImageSuccess(cacheKey, response);
                                routeImg.setScaleType(ScaleType.CENTER_CROP);
                                defaultImg.setVisibility(View.GONE);
                            }

                        });
            }

            if (t.isUse()) {
                this.use.setText(R.string.route_self_activity_used);
                this.use.setTextColor(getResources().getColor(
                        R.color.route_self_used));
                this.use.setBackgroundResource(R.drawable.route_map_used_bg);
            } else {
                this.use.setText(R.string.route_self_activity_use);
                this.use.setTextColor(getResources().getColor(
                        R.color.route_self_use));
                this.use.setBackgroundResource(R.drawable.route_map_use_bg);
                this.use.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        sp.edit().putString(SP_USE_ROUTE_ID, t.getId())
                                .apply();

                        Toasts.show(RouteSelfActivity.this,
                                R.string.route_self_activity_use_success);

                        AVAnalytics
                                .onEvent(
                                        RouteSelfActivity.this,
                                        getString(R.string.route_self_activity_make_route_to_map));
                    }
                });
            }
        }

    }

}
