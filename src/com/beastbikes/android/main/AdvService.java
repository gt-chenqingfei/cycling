package com.beastbikes.android.main;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chenqingfei on 2016/3/03.
 *
 */
public class AdvService extends Service {

    private static final Logger logger = LoggerFactory
            .getLogger(AdvService.class);

    private final DownloadTask downloadTask = new DownloadTask();

    private final Thread thread = new Thread(downloadTask);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        logger.info("AdvService onCreate");
        thread.start();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        logger.info("AdvService onDestroy");
    }

    public void stopTask(){
        logger.info("AdvService stop download task!");
        this.stopSelf();
    }

    private final class DownloadTask implements Runnable {

        @Override
        public void run() {
            logger.info("AdvService start download task!");
            new  AdviertiseManager(AdvService.this).adviertiseLoadPre();
            stopTask();
        }
    }

}
