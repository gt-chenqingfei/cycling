package com.beastbikes.android.ble.otadownload;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class OTADownLoadService extends IntentService {

    public OTADownLoadService() {
        super("OTADownLoadService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        OTAManage manage = new OTAManage(this);
        manage.init();
    }


}
