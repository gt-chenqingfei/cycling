package com.beastbikes.android.modules.cycling.club.biz;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeed;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedPost;
import com.beastbikes.android.modules.cycling.club.dto.ImageInfo;
import com.beastbikes.framework.android.utils.AlgorithmUtils;
import com.beastbikes.framework.android.utils.ConnectivityUtils;
import com.beastbikes.framework.business.BusinessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClubFeedService extends Thread {

    public static final String KEY_CLUBFEED_CACHE = "clubfeed_cache";
    public static final String KEY_CLUBPHOTO_CACHE = "clubphoto_cache";
    public static final String KEY_CLUBFEED_CACHE_POST = "clubfeed_cache_post";
    private static final long CLUBFEED_POST_INTERVAL = 1 * 1000L;
    private static final int ACTION_QUEUE_POST_START = 1;
    private static final int ACTION_QUEUE_POST_STOP = 2;


    private static final Logger logger = LoggerFactory
            .getLogger(ClubFeedService.class);

    private static ClubFeedService instance;
    private List<ClubFeed> clubFeedPostWaitQueue;
    private Handler clubFeedPostHandler;
    private ClubFeedManager feedManager;

    private final BroadcastReceiver connReceiver = new ClubFeedReceiver();

    public interface ClubFeedPostNotifyListener {
        public void onClubFeedNotify(final String clubId);
    }

    public interface ProgressCallback {
        public void onUpLoadProgress(int progress, int total, final String clubId);
    }

    private ClubFeedPostNotifyListener listener;
    private ProgressCallback callback;

    public void setClubFeedPostNotifyListener(ClubFeedPostNotifyListener l) {
        this.listener = l;
    }

    public void setProgressCallback(ProgressCallback cb) {
        this.callback = cb;
    }

    private ClubFeedService() {
        // register receiver to handle network state change
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        BeastBikes.getInstance().registerReceiver(this.connReceiver, filter);

        this.start();
    }

    public static ClubFeedService getInstance() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    public void unInit(){
        feedManager = null;
    }

    private ClubFeedManager getFeedManager(Context context) {

        if (feedManager == null) {
            feedManager = new ClubFeedManager(context);
        }

        return feedManager;
    }

    public static void init() {

        if (instance == null) {
            synchronized (ClubFeedService.class) {
                if (instance == null) {
                    instance = new ClubFeedService();
                }
            }
        }
    }

    public void clubFeedPostQueueIn(ClubFeed post, String clubId) {

        if (post == null)
            throw new RuntimeException("ClubFeed is null");

        feedManager = getFeedManager(BeastBikes.getInstance());

        clubFeedPostWaitQueue = feedManager.getClubFeedCache(clubId, KEY_CLUBFEED_CACHE_POST);
        if (clubFeedPostWaitQueue == null) {
            clubFeedPostWaitQueue = new ArrayList<ClubFeed>();
        }
        clubFeedPostWaitQueue.add(0, post);
        logger.info("clubFeedPostQueueIn clubId =" + clubId + ",queueSize=" + clubFeedPostWaitQueue.size());
        feedManager.clubFeedCacheUpdate(clubFeedPostWaitQueue, KEY_CLUBFEED_CACHE_POST, clubId);

        startSchedule();
    }

    public void clubFeedPostQueueOut(int fid, String clubId) {

        for (int i = 0; clubFeedPostWaitQueue != null && i < clubFeedPostWaitQueue.size(); i++) {
            if (clubFeedPostWaitQueue.get(i).getFid() == fid) {
                clubFeedPostWaitQueue.remove(i);
                break;
            }
        }
        feedManager = getFeedManager(BeastBikes.getInstance());
        feedManager.clubFeedCacheUpdate(clubFeedPostWaitQueue, KEY_CLUBFEED_CACHE_POST, clubId);

    }

    public void checkSchedule(Context context) {
        AVUser user = AVUser.getCurrentUser();
        if (user == null || context == null)
            return;
        SharedPreferences userSp = context.getSharedPreferences(user.getObjectId(), 0);
        String clubId = userSp.getString(Constants.PREF_CLUB_ID, null);
        if (!TextUtils.isEmpty(clubId)) {
            feedManager = getFeedManager(BeastBikes.getInstance());
            clubFeedPostWaitQueue = feedManager.getClubFeedCache(clubId, KEY_CLUBFEED_CACHE_POST);
        }
        startSchedule();
        //logger.info("clubFeedPostQueueIn clubId =" + clubId + ",queueSize=" + clubFeedPostWaitQueue.size());
    }

    public List<ClubFeed> getClubFeedPostQueue() {
        return clubFeedPostWaitQueue;
    }

    public void startSchedule() {
        if (clubFeedPostHandler != null) {
            clubFeedPostHandler.sendEmptyMessage(ACTION_QUEUE_POST_START);
        }
    }

    public void stopSchedule() {
        if (clubFeedPostHandler != null) {
            clubFeedPostHandler.removeMessages(ACTION_QUEUE_POST_START);
        }
    }

    private void excutePost() {
        ImageUploader imageUploader = new ImageUploader();
        for (int i = 0; clubFeedPostWaitQueue != null && i < clubFeedPostWaitQueue.size(); i++) {
            String clubId = "";
            ClubFeed feed = clubFeedPostWaitQueue.get(i);
            clubId = feed.getPost().getClubId();
            if (feed.getState() == ClubFeed.STATE_DOING) {
                ClubFeedPost postForm = feed.getPost();
                List<ImageInfo> infos = null;
                int total = 0;
                if (feed.getImageTxt() != null) {
                    infos = feed.getImageTxt().getImageList();
                    total = infos.size();
                }
                for (int j = 0; infos != null && j < infos.size(); j++) {
                    ImageInfo info = infos.get(j);

                    if (info == null || TextUtils.isEmpty(info.getUrl()) || info.getUrl().contains("http://")) {
                        continue;
                    }

                    final File file = new File(info.getUrl());

                    if (file.exists()) {
                        try {
                            String fileName = AlgorithmUtils.md5(UUID.randomUUID().toString());
                            ImageInfo savedImage = imageUploader.
                                    withFile(file, fileName, ImageUploader.TYPE_CLUB_FEED).saveSync();
                            if (savedImage != null) {
                                info.setUrl(savedImage.getUrl());
                                info.setId(savedImage.getId());
                            }
                        } catch (Exception e1) {
                            continue;
                        }
                        if (callback != null) {
                            logger.info("uploading progress =" + j + "size =" + total);
                            int pos = j + 1;
                            callback.onUpLoadProgress(pos, total, clubId);
                        }
                    }
                }
                postForm.setPostImageList(infos);

                try {
                    feedManager = getFeedManager(BeastBikes.getInstance());
                    if (feed != null) {
                        if (postForm.getType() == ClubFeedPost.TYPE_ABLUM) {
                            boolean ret = feedManager.postClubPhotos(postForm.getCompleteListJsonStr(), postForm.getContent());
//                            if (ret) {
                            clubFeedPostWaitQueue.remove(feed);
                            feedManager.clubFeedCacheUpdate(clubFeedPostWaitQueue, KEY_CLUBFEED_CACHE_POST, postForm.getClubId());
//                            }
                        } else {

                            ClubFeed feedRet = feedManager.postClubFeed(postForm.getClubId(), postForm.getContent(),
                                    postForm.getSportIdentify(), postForm.getCompleteListJsonStr(),
                                    postForm.isNeedSync());
                            if (feedRet != null) {
                                clubFeedPostWaitQueue.remove(feed);
                                feedManager.clubFeedCacheUpdate(clubFeedPostWaitQueue, KEY_CLUBFEED_CACHE_POST, postForm.getClubId());
                            } else {
                                logger.error(ClubFeedService.class.getSimpleName(), "clubfeed post error feed is null");
                            }
                        }
                    }
                } catch (BusinessException e) {
                    logger.error(ClubFeedService.class.getSimpleName(), "schedule excute error" + e);
                }

            }
            if (listener != null && !TextUtils.isEmpty(clubId)) {
                listener.onClubFeedNotify(clubId);
                logger.info("schedule notify to ui");
            }
        }
    }

    @Override
    public void run() {
        Looper.prepare();
        logger.info("schedule run begin  !");
        clubFeedPostHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ACTION_QUEUE_POST_START: {
                        logger.info("schedule start !");
                        excutePost();
                        break;
                    }
                    case ACTION_QUEUE_POST_STOP: {
                        logger.info("schedule stop !");
                        Looper.myLooper().quit();
                        break;
                    }
                }
            }
        };
        Looper.loop();
        logger.info("schedule run end !");
    }

    private final class ClubFeedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (TextUtils.isEmpty(action))
                return;

            switch (ConnectivityUtils.getActiveNetworkType(context)) {
                case ConnectivityManager.TYPE_WIFI:
                case ConnectivityManager.TYPE_ETHERNET:
                    if (AVUser.getCurrentUser() != null) {
                        checkSchedule(BeastBikes.getInstance());
                    }
                    break;
            }
        }

    }
}