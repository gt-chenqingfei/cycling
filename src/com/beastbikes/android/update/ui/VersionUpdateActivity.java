package com.beastbikes.android.update.ui;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.update.biz.UpdateManager;
import com.beastbikes.android.update.dto.VersionInfo;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

/**
 * Created by chenqingfei on 16/7/19.
 */
@LayoutResource(R.layout.activity_version_update)
public class VersionUpdateActivity extends SessionFragmentActivity implements View.OnClickListener {
    @IdResource(R.id.activity_version_update_version_code)
    TextView tvVersionCode;
    @IdResource(R.id.activity_version_update_release_time)
    TextView tvReleaseTime;

    @IdResource(R.id.activity_version_update_btn)
    TextView tvUpdate;

    @IdResource(R.id.activity_version_update_content)
    TextView tvContent;

    private VersionInfo versionInfo;
    private DownloadManager downloadManager;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        checkUpdate();

        SharedPreferences defaultSp = PreferenceManager.getDefaultSharedPreferences(this);
        int version = defaultSp.getInt(Constants.PREF_DOT_VERSION_UPDATE, 0);
        defaultSp.edit().putBoolean(Constants.PREF_DOT_VERSION_UPDATE_GUIDE + version, false).apply();
    }

    public void checkUpdate() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, VersionInfo>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = new LoadingDialog(VersionUpdateActivity.this, "", true);
                loadingDialog.show();
            }

            @Override
            protected VersionInfo doInBackground(Void... voids) {
                return new UpdateManager(VersionUpdateActivity.this).checkUpdate();
            }

            @Override
            protected void onPostExecute(VersionInfo info) {
                super.onPostExecute(info);
                if (!isFinishing() && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                if (info != null) {
                    versionInfo = info;
                    tvContent.setText(info.getChangeLog());
                    tvVersionCode.setText(info.getVersionName());
                    tvUpdate.setOnClickListener(VersionUpdateActivity.this);
                    tvReleaseTime.setText(info.getReleaseDate());
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == tvUpdate) {
            startDownload();
        }
    }

    public void startDownload() {
        if (versionInfo != null) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(versionInfo.getDownloadLink());
            intent.setData(content_url);
            startActivity(intent);
        }

    }
}
