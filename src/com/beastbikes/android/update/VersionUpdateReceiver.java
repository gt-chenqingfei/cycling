package com.beastbikes.android.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by chenqingfei on 16/7/19.
 */
public class VersionUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.DOWNLOAD_COMPLETE".equals(intent.getAction())) {
            try {
                Intent install = new Intent(Intent.ACTION_VIEW);
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                install.setDataAndType(downloadManager.getUriForDownloadedFile(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)),
                        "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
