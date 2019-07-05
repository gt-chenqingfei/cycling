package com.beastbikes.android.modules.cycling.route.ui;

import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.beastbikes.android.modules.cycling.route.biz.RouteManager;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.BaseActivity;
import com.beastbikes.framework.ui.android.utils.Toasts;

public class RouteSaveByWebActivity extends BaseActivity {

    public static final String EXTRA_ROUTE_ID = "route_id";
    private RouteManager routeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (null == intent) {
            finish();
            return;
        }

        String routeId = intent.getStringExtra(EXTRA_ROUTE_ID);
        if (TextUtils.isEmpty(routeId)) {
            finish();
            return;
        }

        this.routeManager = new RouteManager(this);
        this.useActivityRoute(routeId);
    }

    /**
     * 保存Route
     *
     * @param routeId
     */
    private void useActivityRoute(final String routeId) {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(String... params) {
                try {
                    return routeManager.postFavoriteRoute(params[0]);
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                if (null == result) {
                    Toasts.show(getApplicationContext(), "保存失败");
                    finish();
                    return;
                }

                if (result.has("code") && result.optInt("code") == 0) {
                    Toasts.show(getApplicationContext(),
                            result.optString("message"));
                } else {
                    Toasts.show(getApplicationContext(),
                            result.optString("message"));
                }

                finish();
            }

        }, routeId);
    }
}
