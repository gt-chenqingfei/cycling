package com.beastbikes.android.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.beastbikes.android.Constants;
import com.beastbikes.android.PushFactory;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.embapi.SchemaInterceptor;
import com.beastbikes.android.home.view.NavigationView;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnAPI;
import com.beastbikes.android.locale.locationutils.UtilsLocationCallBack;
import com.beastbikes.android.locale.locationutils.UtilsLocationManager;
import com.beastbikes.android.modules.cycling.SyncService;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedService;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dao.entity.Club;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.social.im.biz.RongCloudManager;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.modules.user.dto.WaterMark;
import com.beastbikes.android.modules.user.ui.WatermarkGalleryActivity;
import com.beastbikes.android.update.biz.UpdateManager;
import com.beastbikes.android.update.dto.VersionInfo;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.android.utils.ServicesUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.DensityUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by chenqingfei on 16/1/12.
 */
public class HomeManager implements Constants, SharedPreferences.OnSharedPreferenceChangeListener
        , UtilsLocationCallBack, RequestQueueManager {
    private final Logger logger = LoggerFactory.getLogger(HomeManager.class);
    private HomeActivity context;
    private SharedPreferences sp;
    private SharedPreferences defaultSp;
    private ClubManager clubManager;
    private AVUser user;
    private NavigationView nav;
    private RequestQueue requestQueue;
    private boolean hasLocation = false;

    @Override
    public final RequestQueue getRequestQueue() {
        return this.requestQueue;
    }


    public interface ProfileTabDotChangeListener {
        public void onProfileTabDotChange(int count);
    }

    public interface ClubTabDotChangeListener {
        public void onClubTabDotChanged(int count);
    }

    public interface OnClubStatusChangeListener {
        public void onClubStatusChanged(int status, boolean isChange);
    }

    public interface OnTabChangeListener {
        void onTabChange(String tab);
    }

    private ProfileTabDotChangeListener profileDotChangeListener;

    private ClubTabDotChangeListener clubDotChangeListener;

    private OnClubStatusChangeListener onClubStatusChangeListener;

    private OnTabChangeListener onTabChangeListener;

    public void setOnTabChangeListener(OnTabChangeListener listener) {
        this.onTabChangeListener = listener;
    }

    public void setProfileDotChangeListener(ProfileTabDotChangeListener l) {
        this.profileDotChangeListener = l;
        refreshProfileTabDot();
    }

    public void setClubDotChangeListener(ClubTabDotChangeListener l) {
        this.clubDotChangeListener = l;
    }

    public void setOnClubStatusChangeListener(OnClubStatusChangeListener l) {
        this.onClubStatusChangeListener = l;
    }

    /**
     * 销毁
     */
    public void destroy() {
        if (sp != null) {
            sp.unregisterOnSharedPreferenceChangeListener(this);
        }
        if (defaultSp != null) {
            this.defaultSp.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    public void onCreate() {
        if (RongCloudManager.getInstance() != null) {
            RongCloudManager.getInstance().connectRongCloud(user.getObjectId());
        }
        if (!ServicesUtils.isServiceRunning(context, SyncService.class.getName())) {
            try {
                context.startService(new Intent(context, SyncService.class));
            } catch (Exception e) {

            }
        }
        if (ClubFeedService.getInstance() != null) {
            ClubFeedService.getInstance().checkSchedule(context);
        }

        String pushData = context.getIntent().getStringExtra(PUSH_START_ACTIVITY_DATA);
        if (!TextUtils.isEmpty(pushData)) {
            this.startActivity4Push(pushData);
        }
        String rongCloudData = context.getIntent().getStringExtra(RongCloudManager.RONG_CLOUD_PUSH_KEY);
        if (!TextUtils.isEmpty(rongCloudData)) {
            this.startActivity4Rong(rongCloudData);
        }

        setJpushAlias();
        UtilsLocationManager.getInstance().getLocation(context, this);
        getClubRelation();
        getUserInfo();
        getClubUnReadCount();
    }

    public void onResume() {
        SchemaInterceptor.interceptUrlSchema(context.getIntent().getData(), context);

        refreshProfileTabDot();
        refreshCyclingState();
        refreshCyclingActivityDot();
        refreshSettingDot();
    }

    public HomeManager(HomeActivity context, NavigationView nav) {
        this.context = context;
        this.nav = nav;

        clubManager = new ClubManager(context);
        user = AVUser.getCurrentUser();
        if (user == null) {
            return;
        }

        this.requestQueue = RequestQueueFactory.newRequestQueue(context);
        this.sp = context.getSharedPreferences(user.getObjectId(), 0);
        this.sp.registerOnSharedPreferenceChangeListener(this);
        this.defaultSp = PreferenceManager.getDefaultSharedPreferences(context);
        this.defaultSp.registerOnSharedPreferenceChangeListener(this);
        getDynamicSticker();
    }

    /**
     * push 跳转页面
     *
     * @param pushData
     */
    private void startActivity4Push(String pushData) {
        try {
            JSONObject data = new JSONObject(pushData);

            if (data == null) {
                return;
            }


            Intent intent = PushFactory.getInstance().buildPushIntent(context, data);
            context.startActivity(intent);

        } catch (Exception e) {
            logger.error("startActivity4Push error" + e.toString());
        }
    }

    private void startActivity4Rong(String rongCloudData) {
        try {
            if (RongIM.getInstance().getCurrentConnectionStatus().
                    equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
                RongIM.getInstance().startConversationList(context, null);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 获取与俱乐部关系及跳转
     */

    private void getClubRelation() {
        if (null == context)
            return;
        context.getAsyncTaskQueue().add(new AsyncTask<Void, Void, Club>() {

            @Override
            protected Club doInBackground(Void... params) {
                Club club = null;
                try {
                    if (user != null) {
                        if (null == clubManager) {
                            clubManager = new ClubManager(context);
                        }
                        club = clubManager.getMyClubRelation(user.getObjectId());
                        if (!TextUtils.isEmpty(user.getClubId())) {
                            clubManager.getClubInfo(user.getClubId());
                        }
                    }
                    return club;
                } catch (BusinessException e) {
                    return null;
                }
            }
        });
    }

    /**
     * 获取俱乐部小红点逻辑
     */
    private void getClubUnReadCount() {
        if (context == null)
            return;
        context.getAsyncTaskQueue().add(new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return clubManager.getUnReadCount();
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                if (null == result)
                    return;

                int clubMsg = result.optInt("clubMsg")
                        | sp.getInt(PUSH.PREF_KEY.DOT_CLUB_MSG_TOTAL_COUNT, 0);
                int clubApply = result.optInt("clubApply")
                        | sp.getInt(PUSH.PREF_KEY.DOT_CLUB_MORE, 0);

                int follow = result.optInt("follow")
                        | sp.getInt(PUSH.PREF_KEY.DOT_FOLLOW, 0);
                int medal = result.optInt("medal")
                        | sp.getInt(PUSH.PREF_KEY.DOT_CYCLING_ACTIVITY, 0);

                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(PUSH.PREF_KEY.DOT_CLUB_MSG_TOTAL_COUNT, clubMsg);
                editor.putInt(PUSH.PREF_KEY.DOT_CLUB_MORE, clubApply);
                editor.putInt(PUSH.PREF_KEY.DOT_FOLLOW, follow);
                editor.putInt(PUSH.PREF_KEY.DOT_CYCLING_ACTIVITY, medal);

                editor.apply();

                refreshClubTabDot();
            }

        });
    }

    /**
     * 刷新俱乐部小红点
     */
    private void refreshClubTabDot() {
        if (null == context)
            return;

        if (AVUser.getCurrentUser() != null && TextUtils.isEmpty(AVUser.getCurrentUser().getClubId())) {
            return;
        }

        int clubMore = sp.getInt(PUSH.PREF_KEY.DOT_CLUB_MORE, 0);
        int clubTotalCout = sp.getInt(PUSH.PREF_KEY.DOT_CLUB_MSG_TOTAL_COUNT, 0);
        int clubActivity = sp.getInt(PUSH.PREF_KEY.DOT_CLUB_ACTIVITY, 0);

        int totalCount = clubMore + clubTotalCout + clubActivity;

        if (clubDotChangeListener != null) {
            clubDotChangeListener.onClubTabDotChanged(totalCount);
        }
    }

    /**
     * 刷新用户信息的小红点
     */
    private void refreshProfileTabDot() {
        int rcMessageCount = sp.getInt(PREF_RONGCLOUD_NEW_MESSAGE_COUNT, 0);
        int messageCount = sp.getInt(PREF_FRIEND_NEW_MESSAGE_COUNT, 0);
        int followCount = sp.getInt(PUSH.PREF_KEY.DOT_FOLLOW, 0);


        if (profileDotChangeListener != null) {
            profileDotChangeListener.onProfileTabDotChange(rcMessageCount + messageCount + followCount);
        }
    }

    private void refreshCyclingActivityDot() {
        int unread = sp.getInt(PUSH.PREF_KEY.DOT_CYCLING_ACTIVITY, 0);
        nav.setDot(R.id.nav_item_activity, unread, null, unread > 0 ? View.VISIBLE : View.GONE);
    }

    private void refreshSettingDot() {
        int version = defaultSp.getInt(PREF_DOT_VERSION_UPDATE, 0);
        boolean tag = defaultSp.getBoolean(PREF_DOT_VERSION_UPDATE_GUIDE + "1" + version, true);
        int currentVersion = UpdateManager.getCurrentVersion(context);

        boolean isShow = (version > currentVersion) && tag;

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(DensityUtil.dip2px(context, 8),
                DensityUtil.dip2px(context, 8));
        nav.setDot(R.id.nav_item_setting, 0, lp, isShow ? View.VISIBLE : View.GONE);
    }

    private void refreshCyclingState() {
        nav.notifyCyclingState();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.contains(PREF_HOME_TAB_CHANGE)) {
            if (onTabChangeListener != null) {
                String tab = sharedPreferences.getString(key, "");
                if (!TextUtils.isEmpty(tab)) {
                    onTabChangeListener.onTabChange(sharedPreferences.getString(key, ""));
                }
            }
        }

        if (key.contains(PUSH.PREF_KEY.DOT_CYCLING_ACTIVITY)) {
            refreshCyclingActivityDot();
            return;
        }
        if (key.contains(PREF_DOT_VERSION_UPDATE)) {
            refreshSettingDot();
            return;
        }
        if (key.contains(PREF_RONGCLOUD_NEW_MESSAGE_COUNT)
                || key.contains(PREF_FRIEND_NEW_MESSAGE_COUNT)
                || key.contains(PUSH.PREF_KEY.DOT_FOLLOW)
                ) {
            refreshProfileTabDot();
            return;
        }

        if (key.contains(PUSH.PREF_KEY.DOT_CLUB_ACTIVITY)
                || key.contains(PUSH.PREF_KEY.DOT_CLUB_MORE)
                || key.contains(PUSH.PREF_KEY.DOT_CLUB_MSG_TOTAL_COUNT)
                ) {
            refreshClubTabDot();
        }

        if (key.contains(PREF_HOME_NAV_CYCLING_STATE)) {
            refreshCyclingState();
            return;
        } else if (key.contains(PREF_CLUB_STATUS)) {
//            if (onClubStatusChangeListener != null) {
//                int status = sharedPreferences.getInt(key, 0);
//
////                Log.d("TAG", "onSharedPreferenceChanged status: " + status);
//                boolean isChange = false;
//                if (status == ClubInfoCompact.CLUB_STATUS_QUIT) {
//                    isChange = true;
//                    status = ClubInfoCompact.CLUB_STATUS_NONE;
//                }
//                onClubStatusChangeListener.onClubStatusChanged(status, isChange);
//
//            }
        } else if (key.contains(PUSH.PREF_KEY.NOTIFY_CLUB_TRANSFER)
                || key.contains(PUSH.PREF_KEY.NOTIFY_CLUB_TRANSFER_MASTER)
                ) {
            getClubRelation();
        } else if (key.contains(PUSH.PREF_KEY.NOTIFY_CLUB_APPLY_PASS)) {
            clubManager.updateClubStatus2ApplyPass();
        } else if (key.contains(PUSH.PREF_KEY.NOTIFY_CLUB_APPLY_REFUSE)) {
            clubManager.updateClubStatus2ApplyRefuse();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && !hasLocation) {
            hasLocation = true;
            GoogleMapCnAPI googleMapCnAPI = new GoogleMapCnAPI();
            googleMapCnAPI.geoCode(this.getRequestQueue(), location.getLatitude(), location.getLongitude(), null);
            updateDeviceInfo(location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    public void onLocationFail() {
        updateDeviceInfo(0, 0);
    }

    private void getUserInfo() {
        if (context == null)
            return;
        context.getAsyncTaskQueue().add(new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    AVUser avUser = AVUser.getCurrentUser();
                    if (avUser == null)
                        return null;
                    ProfileDTO profileDTO = new UserManager(context).getProfileByUserId(avUser.getObjectId());
                    if (profileDTO != null) {
                        RongCloudManager.getInstance()
                                .setRongCloudUserInfo(profileDTO);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }


    private void setJpushAlias() {
        context.getAsyncTaskQueue().add(new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String alias = JPushInterface.getRegistrationID(context);
                    try {
                        logger.info("Jpush RegistrationID = [" + alias + "]");
                        JPushInterface.setAliasAndTags(context, alias, null, mAliasCallback);
                    } catch (Exception e) {
                        logger.error(e.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    private void updateDeviceInfo(final double lat, final double lon) {
        if (context == null)
            return;

        if (JPushInterface.isPushStopped(context)) {
            JPushInterface.resumePush(context);
        }
        context.getAsyncTaskQueue().add(new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String alias = JPushInterface.getRegistrationID(context);
                    new UserManager(context).updateDeviceInfo(lat, lon, alias);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Jpush Set tag and alias success";
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Jpush Failed to set alias and tags due to timeout. Try again after 60s.";
                    // 延迟 60 秒来调用 Handler 设置别名
//                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Jpush Failed with errorCode = " + code;
            }
            logger.info(logs);
        }
    };

    public ClubInfoCompact getClubInfoCompact() {
        ClubInfoCompact clubInfoCompact = null;
        if (clubManager != null) {
            try {
                if (AVUser.getCurrentUser() != null) {
                    clubInfoCompact = clubManager.getMyClub(AVUser.getCurrentUser().getObjectId());
                }
            } catch (BusinessException e) {
                e.printStackTrace();
            }
        }
        return clubInfoCompact;
    }

    public void checkUpdate(final UpdateManager.CheckUpdateCallback callback) {
        context.getAsyncTaskQueue().add(new AsyncTask<Void, Void, VersionInfo>() {

            @Override
            protected VersionInfo doInBackground(Void... voids) {
                return new UpdateManager(context).checkUpdate();
            }

            @Override
            protected void onPostExecute(VersionInfo info) {
                super.onPostExecute(info);
                if (info != null && callback != null) {
                    callback.onUpdateAvailable(info);
                }
            }
        });
    }


    private void getDynamicSticker() {

        boolean isLoaded = defaultSp.getBoolean(Constants.PREF_WATER_MARK_LOAD, false);
        if (isLoaded)
            return;

        String mCurrentLanguage = Locale.getDefault().getLanguage();
        String URL;
        if (mCurrentLanguage.equals("zh")) {
            URL = WatermarkGalleryActivity.WATERMARK_URL_ZH + System.currentTimeMillis();
        } else {
            URL = WatermarkGalleryActivity.WATERMARK_URL_EN + System.currentTimeMillis();
        }
        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null || response.length() == 0) {
                    return;
                }
                defaultSp.edit().putBoolean(Constants.PREF_WATER_MARK_LOAD, true).commit();
                for (int i = 0; i < response.length(); i++) {


                    JSONArray imagesJsonArray = response.optJSONObject(i).optJSONArray("images");

                    if (imagesJsonArray != null && imagesJsonArray.length() > 0) {
                        for (int j = 0; j < imagesJsonArray.length(); j++) {
                            JSONObject jsonObject1 = imagesJsonArray.optJSONObject(j);
                            if (jsonObject1 == null)
                                continue;
                            String whiteURL = jsonObject1.optString("white_url");
                            String blackURL = jsonObject1.optString("black_url");

                            Picasso.with(context).load(whiteURL).fetch();
                            Picasso.with(context).load(blackURL).fetch();
                        }
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        req.setShouldCache(false);
        requestQueue.add(req);
    }

}
