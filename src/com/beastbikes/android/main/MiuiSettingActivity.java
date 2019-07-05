package com.beastbikes.android.main;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.home.HomeActivity;
import com.beastbikes.android.modules.social.im.biz.RongCloudManager;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.BaseActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@LayoutResource(R.layout.miui_setting_activity)
public class MiuiSettingActivity extends BaseActivity implements
        OnClickListener {

    public static final int RC_GOTO_MIUI_SETTING_PAGE = 2;

    private static final Logger logger = LoggerFactory
            .getLogger(MiuiSettingActivity.class);

    @IdResource(R.id.miui_setting_close)
    private ImageView closeIv;

    @IdResource(R.id.miui_setting_activity_desc_title)
    private TextView descTv;

    @IdResource(R.id.miui_setting_desc_icon)
    private ImageView iconIv;

    @IdResource(R.id.miui_setting_go_open)
    private Button goSettingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.closeIv.setOnClickListener(this);
        this.goSettingBtn.setOnClickListener(this);

        this.getSystemProperty();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.miui_setting_close:
                this.goHome();
//                finish();
                break;
            case R.id.miui_setting_go_open:
                this.goMiui6FollowSetting();
//                finish();
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.goHome();
    }

    @Override
    public void finish() {
//        this.goHome();
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOTO_MIUI_SETTING_PAGE) {
            this.goHome();
            this.finish();
        }
    }

    protected void goHome() {

        AVUser user = AVUser.getCurrentUser();
        if (null != user) {

            android.webkit.CookieManager manager = android.webkit.CookieManager.getInstance();
            manager.setCookie(Constants.UrlConfig.DEV_SPEEDX_HOST_DOMAIN,
                    "sessionid=" + user.getSessionToken());

        }

        Intent homeIntent = new Intent(this, HomeActivity.class);
        Intent intent = getIntent();
        if (null != intent) {
            String pushData = intent
                    .getStringExtra(Constants.PUSH_START_ACTIVITY_DATA);
            if (!TextUtils.isEmpty(pushData)) {
                homeIntent.putExtra(Constants.PUSH_START_ACTIVITY_DATA,
                        pushData);
            }
            //rongcloud
            String rongPush = intent.getStringExtra(RongCloudManager.RONG_CLOUD_PUSH_KEY);
            if (!TextUtils.isEmpty(rongPush)) {
                Log.e("rongPush", rongPush);
                homeIntent.putExtra(RongCloudManager.RONG_CLOUD_PUSH_KEY,
                        rongPush);
            } else {
                Log.e("rongPush", "null");
            }
            if(null != getIntent()) {
                Uri schemaData = getIntent().getData();
                if (schemaData != null) {
                    homeIntent.setData(schemaData);
                }
            }
        }
        this.startActivity(homeIntent);
        this.finish();
    }

    /**
     * go open
     */
    private void goMiui6FollowSetting() {
        try {
            Intent miuiIntent = new Intent();
            miuiIntent
                    .setClassName("com.miui.securitycenter",
                            "com.miui.permcenter.autostart.AutoStartManagementActivity");
            startActivity(miuiIntent);
        } catch (Exception e) {
            PackageInfo info = null;
            PackageManager pm = getPackageManager();

            try {
                info = pm.getPackageInfo(getPackageName(), 0);
            } catch (NameNotFoundException e1) {
                return;
            }

            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.putExtra("extra_package_uid", info.applicationInfo.uid);
            intent.putExtra("extra_pkgname", getPackageName());

            try {
                startActivity(intent);
            } catch (Exception e2) {
                Intent intentSetting = new Intent();
                intentSetting
                        .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri rui = Uri.fromParts("package", getPackageName(), null);
                intentSetting.setData(rui);
                startActivity(intentSetting);
            }

        }
    }

    private void getSystemProperty() {
        String line = null;
        BufferedReader input = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("getprop " + "ro.miui.ui.version.code");
            inputStream = p.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            input = new BufferedReader(inputStreamReader, 1024);
            line = input.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error("Unable to read sysprop " + "ro.miui.ui.version.code");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error("Exception while closing InputStream");
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != p) {
                p.destroy();
            }

        }
        int code = 1;
        if (!TextUtils.isEmpty(line)) {
            code = Integer.parseInt(line);
        }
        if (code > 3) {
            this.iconIv.setImageResource(R.drawable.ic_setting_miui_7_bg);
            this.descTv.setText(R.string.miui_setting_desc_2);
        } else {
            this.iconIv.setImageResource(R.drawable.ic_setting_miui_6_bg);
            this.descTv.setText(R.string.miui_setting_desc_1);
        }
        logger.trace("Miui version name " + line);
    }

}
