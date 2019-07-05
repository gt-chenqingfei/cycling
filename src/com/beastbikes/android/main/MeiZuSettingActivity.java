package com.beastbikes.android.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

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

@LayoutResource(R.layout.meizu_setting_activity)
public class MeiZuSettingActivity extends BaseActivity implements
        OnClickListener {

    private static final Logger logger = LoggerFactory
            .getLogger(MeiZuSettingActivity.class);

    @IdResource(R.id.miui_setting_desc_icon)
    private ImageView iconIv;

    @IdResource(R.id.miui_setting_go_open)
    private Button goSettingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.goSettingBtn.setOnClickListener(this);

        this.getSystemProperty();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.miui_setting_close:
                this.goHome();
                break;
            case R.id.miui_setting_go_open:
                this.goMeizuFollowSetting();
//                finish();
                break;
        }
    }

    @Override
    public void finish() {
//        this.goHome();
        super.finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.goHome();
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
    private void goMeizuFollowSetting() {
        try {
            Intent miuiIntent = new Intent();
            miuiIntent
                    .setClassName("com.meizu.safe",
                            "com.meizu.safe.powerui.AppPowerManagerActivity");
            startActivity(miuiIntent);
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setClassName("com.mediatek.batterywarning", "com.mediatek.batterywarning.BatteryWarningActivity");
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

        this.goHome();
    }

    private void getSystemProperty() {
        String line = null;
        BufferedReader input = null;
        InputStreamReader inputStreamReader = null;
        InputStream inputStream = null;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("getprop " + "ro.build.display.id");
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
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != p) {
                p.destroy();
            }
        }
        String key = "Flyme OS ";
        String codeStr = "1";
        if (TextUtils.isEmpty(line))
            return;
        if (line.contains(key) && line.length() > key.length() + 1) {
            codeStr = line.substring(key.length(), key.length() + 1);
        }

        int code = 1;
        if (TextUtils.isDigitsOnly(codeStr)) {
            code = Integer.parseInt(codeStr);
        }
        if (code < 5) {
            this.iconIv.setImageResource(R.drawable.bg_meizu_4);
        } else {
            this.iconIv.setImageResource(R.drawable.bg_meizu_5);
        }
        logger.trace("Miui version name " + line);
    }

}
