package com.beastbikes.android.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.qiniu.QiNiuManager;
import com.beastbikes.android.modules.qiniu.QiNiuUploadCallBack;
import com.beastbikes.android.update.biz.UpdateManager;
import com.beastbikes.android.update.ui.VersionUpdateActivity;
import com.beastbikes.android.utils.LogUtil;
import com.beastbikes.android.utils.ZipUtils;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.utils.ConnectivityUtils;
import com.beastbikes.framework.android.utils.PackageUtils;
import com.beastbikes.framework.android.utils.TelephonyUtils;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

@Alias("关于页")
@LayoutResource(R.layout.activity_about)
public class AboutActivity extends BaseFragmentActivity implements
        OnClickListener, QiNiuUploadCallBack, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "AboutActivity";

    private static final int RC_GOTO_TUTORIAL_PAGE = 1;

    private static final long VERSION_CLICK_DURACTION = 30 * DateUtils.SECOND_IN_MILLIS;
    private long validAfter = 0;
    private Vector<Long> versionClickCounter = new Vector<Long>();

    @IdResource(R.id.activity_about_version_textview)
    private TextView versionTv;

    @IdResource(R.id.activity_about_list_content)
    private ViewGroup viewContent;

    @IdResource(R.id.activity_about_tutorial)
    private TextView tutorialTv;

    @IdResource(R.id.activity_about_image)
    private LinearLayout about_image;

    @IdResource(R.id.activity_about_enter_weibo_tv)
    private TextView enterWeiboTV;

    @IdResource(R.id.activity_about_update_group)
    private View versionUpdateGroup;

    @IdResource(R.id.activity_about_update_tv)
    private TextView versionUpdateTV;

    private SharedPreferences defautSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        for (int i = 0; i < viewContent.getChildCount(); i++) {
            View v = viewContent.getChildAt(i);
            if (!LocaleManager.isChineseTimeZone()) {
                if (v.getId() == R.id.about_view
                        || v.getId() == R.id.activity_about_tutorial
                        || v.getId() == R.id.about_activity_email_lay) {
                    v.setVisibility(View.VISIBLE);
                } else {
                    v.setVisibility(View.GONE);
                }
            } else {
                if (v.getId() == R.id.about_activity_email_lay) {
                    v.setVisibility(View.GONE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }
            }
        }

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams layoutParams = about_image.getLayoutParams();
        layoutParams.height = width * 629 / 720;
        this.about_image.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams versionTv_layoutParams = versionTv.getLayoutParams();
        versionTv_layoutParams.height = width * 629 / 720 * 3 / 5;
        this.versionTv.setLayoutParams(versionTv_layoutParams);
        this.versionTv.setText("V" + PackageUtils.getVersionName(this));
        this.versionTv.setOnClickListener(this);

        this.tutorialTv.setOnClickListener(this);
        this.enterWeiboTV.setOnClickListener(this);

        defautSp = PreferenceManager.getDefaultSharedPreferences(this);
        defautSp.registerOnSharedPreferenceChangeListener(this);
        refreshVersionUpdateDot();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
        defautSp.unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_about_version_textview:
                final long curTime = SystemClock.elapsedRealtime();
                if (curTime <= validAfter) {
                    return;
                }
                for (int i = versionClickCounter.size() - 1; i >= 0; i--) {
                    if (versionClickCounter.get(i) < curTime
                            - VERSION_CLICK_DURACTION) {
                        versionClickCounter.remove(i);
                    }
                }
                versionClickCounter.add(curTime);
                if (versionClickCounter.size() >= 5) {
                    exportLog();

                    versionClickCounter.clear();
                    validAfter = SystemClock.elapsedRealtime()
                            + VERSION_CLICK_DURACTION;
                }
                break;
            case R.id.activity_about_tutorial:
                startActivityForResult(new Intent(this, TutorialActivity.class),
                        RC_GOTO_TUTORIAL_PAGE);
                break;
            case R.id.activity_about_enter_weibo_tv:
                final Intent intent = new Intent(this, BrowserActivity.class);
                intent.setData(Uri.parse("http://weibo.com/beastbikes?is_hot=1"));
                startActivity(intent);
                break;
            case R.id.activity_about_update_group:
                final Intent it = new Intent(this, VersionUpdateActivity.class);
                startActivity(it);
                break;
            default:
                break;
        }
    }

    private void exportLog() {
        final PackageManager pm = getPackageManager();

        try {
            final ApplicationInfo ai = pm.getApplicationInfo(
                    this.getPackageName(), PackageManager.GET_META_DATA);

            if (LogUtil.exportLog(ai.dataDir + File.separator + "files"
                    + File.separator + "log", Environment
                    .getExternalStorageDirectory().getAbsolutePath()
                    + "/beast")) {
                Toasts.show(this, "export log success");
            } else {
                Toasts.show(this, "export log failed");
            }
        } catch (NameNotFoundException e) {
            return;
        }

        this.uploadLog(new Date());
    }


    @SuppressLint("SimpleDateFormat")
    private void uploadLog(final Date date) {
        switch (ConnectivityUtils.getActiveNetworkType(this)) {
            case ConnectivityManager.TYPE_BLUETOOTH:
            case ConnectivityManager.TYPE_ETHERNET:
            case ConnectivityManager.TYPE_WIFI:
                break;
            default:
                Log.i(TAG, "Ignore log uploading, require WIFI/Ethernet network");
                return;
        }

        final SimpleDateFormat sdfSecond = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        final String suffix = ".log";
        final File log = new File(getFilesDir(), "log");
        final File[] logs = log.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(suffix);
            }
        });

        if (null == logs || logs.length < 1)
            return;

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -2);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long minTime = c.getTimeInMillis();

        final File outFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/beast/" + sdfSecond.format(date) + ".zip");
        final List<File> files = new ArrayList<>();
        for (File file : logs) {
            if (file.lastModified() > minTime) {
                files.add(file);
            }
        }

        final AVUser user = AVUser.getCurrentUser();
        final String sn = null != user ? user.getObjectId() : TelephonyUtils.getDeviceId(this);
        String prefix = LogUtil.logPath + String.valueOf(sn) + "_" + ".zip";
        String second = sdfSecond.format(new Date());
        prefix = LogUtil.stringInsert(prefix, second, prefix.length() - 5);
        QiNiuManager qiNiuManager = new QiNiuManager(AboutActivity.this);
        qiNiuManager.setQiNiuUploadCallBack(this);

        try {
            ZipUtils.zipFiles(files, outFile);
            qiNiuManager.uploadFile(prefix, outFile, prefix);
        } catch (IOException e) {
            for (File file : files) {
                second = sdfSecond.format(new Date(file.lastModified()));
                prefix = LogUtil.stringInsert(prefix, second, prefix.length() - 5);
                qiNiuManager.uploadFile(prefix, file, prefix);
            }
            e.printStackTrace();
        }

    }


    @Override
    public void onComplete(String key) {
        Log.e("log", key);
        Toasts.show(AboutActivity.this, "Upload log success");
    }

    @Override
    public void onError() {
        Toasts.show(AboutActivity.this, "Upload log error");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.PREF_DOT_VERSION_UPDATE)) {
            refreshVersionUpdateDot();
        }
    }

    private void refreshVersionUpdateDot() {
        SharedPreferences defaultSp = PreferenceManager.getDefaultSharedPreferences(this);
        int version = defaultSp.getInt(Constants.PREF_DOT_VERSION_UPDATE, 0);
        defaultSp.edit().putBoolean(Constants.PREF_DOT_VERSION_UPDATE_GUIDE + "2" + version, false).apply();

        int currentVersion = UpdateManager.getCurrentVersion(this);
        if (version > currentVersion) {
            //versionUpdateTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dot_red_bg, 0, 0, 0);
            versionUpdateTV.setText(R.string.version_update_has_new);
            this.versionUpdateGroup.setOnClickListener(this);
        } else {
            versionUpdateTV.setText(R.string.version_update_not_has_new);
            //versionUpdateTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }
}
