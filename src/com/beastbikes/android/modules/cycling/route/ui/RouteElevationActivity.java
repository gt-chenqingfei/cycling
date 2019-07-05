package com.beastbikes.android.modules.cycling.route.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.route.view.RouteElevationView;
import com.beastbikes.android.modules.user.ui.ProgressDialog;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@LayoutResource(R.layout.route_map_elevation_activity)
public class RouteElevationActivity extends SessionFragmentActivity implements
        OnClickListener ,RequestQueueManager {

    public static final String EXTRA_DISTANCE = "distance";

    public static final String EXTRA_NODES = "nodes";

    private static final Logger logger = LoggerFactory.getLogger(RouteElevationActivity.class);

    @IdResource(R.id.route_map_elevation_chart)
    private LinearLayout chart;

    @IdResource(R.id.route_map_elevation_back)
    private TextView backTv;

    @IdResource(R.id.route_map_elevation_distance)
    private TextView distanceTv;

    private ArrayList<Double> listElevation;

    private ProgressDialog dialog;
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
        this.requestQueue = RequestQueueFactory.newRequestQueue(this);
        this.dialog = new ProgressDialog(this);
        this.dialog.setMessage(getString(R.string.route_elevation_activity_loading));
        this.listElevation = new ArrayList<Double>();

        this.backTv.setOnClickListener(this);
        this.fetchElevation();
    }

    private void fetchElevation() {
        this.dialog.show();
        this.dialog.setCancelable(false);
        final Context ctx = this;
        final StringBuilder sb = new StringBuilder(
                "http://maps.google.cn/maps/api/elevation/json?path=");
        final Intent intent = getIntent();
        if (null != intent && intent.hasExtra(EXTRA_NODES)) {
            final String latLngStr = intent.getStringExtra(EXTRA_NODES);
            sb.append(latLngStr).append("&samples=200");
            int a = (int) intent.getDoubleExtra(EXTRA_DISTANCE, 10);
            distanceTv.setText(String.valueOf(a));
            logger.error("the elevation request url is : " + sb.toString());
        }

        JsonObjectRequest request = new JsonObjectRequest(sb.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final JSONArray arrayLatLng;
                        if ("OK".equals(response.optString("status"))) {
                            arrayLatLng = response.optJSONArray("results");
                            for (int i = 0; i < arrayLatLng.length(); i++) {
                                try {
                                    JSONObject obj = (JSONObject) arrayLatLng
                                            .get(i);
                                    listElevation.add(obj.optDouble("elevation"));
                                } catch (JSONException e) {
                                    logger.error("get elevation error", e);
                                }
                            }
                            final double min = Math.min(0, listMin(listElevation));
                            final double max = listMax(listElevation);
                            RouteElevationView v = new RouteElevationView(RouteElevationActivity.this);
                            v.setLabelsData(listElevation.size());
                            v.setData(listElevation);
                            v.chartRender(max, min, listElevation.size());
                            chart.addView(v, new LinearLayout.LayoutParams(
                                    LayoutParams.MATCH_PARENT,
                                    LayoutParams.MATCH_PARENT));
                            dialog.dismiss();
                            logger.info("the min elevation is " + String.valueOf(min) + " and the max is : " + String.valueOf(max));
                        } else {
                            dialog.dismiss();
                            Toasts.show(ctx, getString(R.string.route_elevation_activity_error));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toasts.show(ctx, getString(R.string.route_elevation_activity_error));
                finish();
                logger.error("get elevation error", error.getMessage());
            }
        });

        this.getRequestQueue().add(request);
    }

    /**
     * 获取List中的最小值
     *
     * @param list
     */
    private double listMax(List<Double> list) {
        try {
            double maxDevation = 0.0;
            int totalCount = list.size();
            if (totalCount >= 1) {
                double max = list.get(0);
                for (int i = 0; i < totalCount; i++) {
                    double temp = list.get(i);
                    if (temp > max) {
                        max = temp;
                    }
                }
                maxDevation = max;
            }
            return maxDevation;
        } catch (Exception ex) {
            logger.error("get the max elevation error", ex);
        }
        return 50;
    }

    /**
     * 获取List中的最小值
     *
     * @param list
     */
    private double listMin(List<Double> list) {
        try {
            double mixDevation = 0.0;
            int size = list.size();
            if (size >= 1) {
                double min = list.get(0);
                for (int i = 0; i < size; i++) {
                    double temp = list.get(i);
                    if (min > temp) {
                        min = temp;
                    }
                }
                mixDevation = min;
            }
            return mixDevation;
        } catch (Exception ex) {
            logger.error("get the min elevation error", ex);
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.route_map_elevation_back:
                this.finish();
                break;
            default:
                break;
        }
    }
}
