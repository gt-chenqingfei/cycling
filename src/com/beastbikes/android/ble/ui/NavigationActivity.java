package com.beastbikes.android.ble.ui;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.ble.CentralService;
import com.beastbikes.android.ble.Invocation;
import com.beastbikes.android.ble.biz.CentralInvocation;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by icedan on 15/12/7.
 * 导航页面
 */
@TargetApi(18)
@LayoutResource(R.layout.navigation_activity)
public class NavigationActivity extends SessionFragmentActivity implements OnClickListener, Constants {
    private static final Logger logger = LoggerFactory.getLogger("DiscoveryActivity");
    @IdResource(R.id.navigation_activity_straight)
    private TextView straightTv;

    @IdResource(R.id.navigation_activity_turn_left)
    private TextView turnLeftTv;

    @IdResource(R.id.navigation_activity_turn_right)
    private TextView turnRightTv;

    @IdResource(R.id.navigation_activity_turn)
    private TextView turnTV;

    private Invocation manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.straightTv.setOnClickListener(this);
        this.turnLeftTv.setOnClickListener(this);
        this.turnRightTv.setOnClickListener(this);
        this.turnTV.setOnClickListener(this);

        final Intent service = new Intent(CentralService.ACTION_CENTRAL_CONTROL);
        service.setPackage(getPackageName());
        this.startService(service);
        this.bindService(service, connection, BIND_AUTO_CREATE);
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (manager != null) {
            this.unbindService(connection);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigation_activity_straight:
                if (this.manager != null)
                    this.manager.writeNavigationRequest(4, 10);
                break;
            case R.id.navigation_activity_turn_left:
                if (this.manager != null)
                    this.manager.writeNavigationRequest(1, 10);
                break;
            case R.id.navigation_activity_turn_right:
                if (this.manager != null)
                    this.manager.writeNavigationRequest(2, 10);
                break;
            case R.id.navigation_activity_turn:
                if (this.manager != null)
                    this.manager.writeNavigationRequest(3, 10);
                break;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            logger.info("onServiceConnected");
            CentralService mService = ((CentralService.ICentralBinder) binder).getService();
            manager = mService.getInvocation();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            logger.info("onServiceDisconnected");
        }
    };
}
