package com.beastbikes.android.modules.preferences.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.authentication.AuthenticationFactory;
import com.beastbikes.android.authentication.biz.AuthenticationManager;
import com.beastbikes.android.authentication.ui.AuthenticationActivity;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.main.AboutActivity;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.SyncService;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityState;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.activity.ui.CyclingTargetSettingActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedService;
import com.beastbikes.android.modules.preferences.ui.offlineMap.OfflineMapActivity;
import com.beastbikes.android.modules.setting.ui.LaboratoryActivity;
import com.beastbikes.android.modules.social.im.biz.RongCloudManager;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.modules.user.ui.binding.AccountManagementActivity;
import com.beastbikes.android.update.biz.UpdateManager;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.materialdesign.mdswitch.Switch;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.cache.CacheManager;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.beastbikes.leancloud.cache.APICache;
import com.squareup.picasso.Picasso;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

@Alias("设置")
@LayoutResource(R.layout.setting_fragment)
public class SettingActivity extends SessionFragmentActivity implements
        OnClickListener, MKOfflineMapListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "SettingActivity";

    public static final int RC_GOTO_USER_SETTING_PAGE = 2;

    @IdResource(R.id.setting_fragment_item_user)
    private ViewGroup grpUser;

    @IdResource(R.id.setting_fragment_item_user_avatar)
    private CircleImageView imgAvatar;

    @IdResource(R.id.setting_fragment_user_nickname)
    private TextView lblNickname;

    @IdResource(R.id.setting_fragment_user_location)
    private TextView lblLocation;

    @IdResource(R.id.setting_fragment_item_voice_banding)
    private TextView banding;

    @IdResource(R.id.setting_fragment_item_service_contact)
    private TextView serviceContact;

    @IdResource(R.id.settings_goal)
    private TextView settingGoal;

    @IdResource(R.id.setting_fragment_item_offline_map)
    private View lblOfflineMap;

    @IdResource(R.id.setting_fragment_item_quit)
    private View settingQuit;

    @IdResource(R.id.setting_fragment_laboratory)
    private LinearLayout laboratoryView;
    @IdResource(R.id.setting_fragment_item_laboratory)
    private Switch lblLaboratory;

    @IdResource(R.id.setting_fragment_item_clear_cache)
    private View lblClearCache;

    @IdResource(R.id.setting_fragment_item_about)
    private View lblAbout;

    @IdResource(R.id.setting_fragment_item_about_dot)
    private View lblAboutDot;

    @IdResource(R.id.stopwatchsettings)
    private View stopWatchSettings;

    @IdResource(R.id.setting_fragment_email)
    private TextView sendEmail;

    @IdResource(R.id.setting_fragment_item_feedback)
    private View lblFeedback;

    private UserManager userManager;
    private SharedPreferences userSp;
    private SharedPreferences defautSp;
    private String userId;

    @Override
    public void onGetOfflineMapState(int type, int cityId) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.userManager = new UserManager(this);
        this.grpUser.setOnClickListener(this);
        if (LocaleManager.isChineseTimeZone()) {
            this.lblOfflineMap.setVisibility(View.VISIBLE);
            this.lblOfflineMap.setOnClickListener(this);
        }

        final BeastBikes app = (BeastBikes) getApplication();
        this.userSp = getSharedPreferences(getUserId(), 0);
        userSp.registerOnSharedPreferenceChangeListener(this);
        defautSp = PreferenceManager.getDefaultSharedPreferences(this);
        defautSp.registerOnSharedPreferenceChangeListener(this);

        this.lblLaboratory.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean open) {
                app.setMapStyleEnabled(open);
                if (open) {
                    SpeedxAnalytics.onEvent(app, "地图样式优化开启", null);
                } else {
                    SpeedxAnalytics.onEvent(app, "地图样式优化关闭", null);
                }
            }
        });
        this.lblClearCache.setOnClickListener(this);
        this.lblAbout.setOnClickListener(this);
        this.stopWatchSettings.setOnClickListener(this);
        this.laboratoryView.setOnClickListener(this);
        this.banding.setOnClickListener(this);
        this.sendEmail.setOnClickListener(this);
        this.settingGoal.setOnClickListener(this);
        this.settingQuit.setOnClickListener(this);
        this.lblFeedback.setOnClickListener(this);
        this.serviceContact .setOnClickListener(this);

        AVUser user = AVUser.getCurrentUser();
        if (null != user) {
            if (!TextUtils.isEmpty(user.getAvatar())) {
                Picasso.with(this).load(user.getAvatar()).fit().centerCrop().error(R.drawable.ic_avatar).
                        placeholder(R.drawable.ic_avatar).into(this.imgAvatar);
            } else {
                this.imgAvatar.setImageResource(R.drawable.ic_avatar);
            }
        }
        refreshVersionUpdateDot();

    }

    @Override
    public void onResume() {
        super.onResume();

        final BeastBikes app = (BeastBikes) this.getApplication();
        this.lblLaboratory.setChecked(app.isMapStyleEnabled());
        userId = this.getUserId();
        if (!TextUtils.isEmpty(userId)) {
            this.fetchProfile(userId);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stopwatchsettings: {
                SpeedxAnalytics.onEvent(this, "骑行页设置", "setting_ridding_data");
                startActivity(new Intent(this, CyclingSettingActivity.class));
                break;
            }
            case R.id.setting_fragment_item_user: {
                SpeedxAnalytics.onEvent(this, "用户设置", null);
                final Intent intent = new Intent(this, UserSettingActivity.class);
                intent.putExtra(UserSettingActivity.EXTRA_FROM_SETTING, true);
                startActivityForResult(intent, RC_GOTO_USER_SETTING_PAGE);
                break;
            }
            case R.id.setting_fragment_item_offline_map:
                startActivity(new Intent(this, OfflineMapActivity.class));
                SpeedxAnalytics.onEvent(this, "离线地图", "enter_offline_map");
                break;
            case R.id.setting_fragment_item_clear_cache:// 清除缓存
                SpeedxAnalytics.onEvent(this, "清除缓存", null);
                this.clearCache();
                break;
            case R.id.setting_fragment_item_about:
                startActivity(new Intent(this, AboutActivity.class));
                SpeedxAnalytics.onEvent(this, "关于页面", null);
                break;
            case R.id.setting_fragment_laboratory:// 实验室设置
                SpeedxAnalytics.onEvent(this, "野兽实验室", "speedx_library");
                startActivity(new Intent(this, LaboratoryActivity.class));
                break;
            case R.id.setting_fragment_item_voice_banding:
                SpeedxAnalytics.onEvent(this, "账号绑定", "accout_bind");
                startActivity(new Intent(this, AccountManagementActivity.class));
                break;
            case R.id.setting_fragment_email:
                Intent data = new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:" + getResources().getString(R.string.contact_us_email_address)));
                startActivity(data);
                break;
            case R.id.settings_goal:
                SpeedxAnalytics.onEvent(this, "目标设置", "setting_ridding_goal");
                startActivity(new Intent(SettingActivity.this, CyclingTargetSettingActivity.class));
                break;
            case R.id.setting_fragment_item_feedback:// 反馈
                SpeedxAnalytics.onEvent(this, "用户反馈", null);
                if (RongCloudManager.getInstance().isRunning()) {
                    String targetId = RongCloudManager.PUBLIC_SERVICE_FEEDBACK_EN;
                    if (LocaleManager.isChineseLanunage()) {
                        targetId = RongCloudManager.PUBLIC_SERVICE_FEEDBACK;
                    }
                    RongIM.getInstance().startConversation(SettingActivity.this, Conversation.ConversationType.PUBLIC_SERVICE, targetId, getResources().getString(R.string.customerservice));
                } else {

                }
                break;
            case R.id.setting_fragment_item_service_contact:
                SpeedxAnalytics.onEvent(this, "服务管理", "service_manager");
                startActivity(new Intent(SettingActivity.this, ServiceManagerActivity.class));
                break;
            case R.id.setting_fragment_item_quit:
                SpeedxAnalytics.onEvent(this, "退出登录", null);
                ActivityManager am = new ActivityManager(this);
                if (am != null) {
                    LocalActivity activity = am.getCurrentActivity();
                    if (activity != null && (activity.getState() != ActivityState.STATE_NONE
                            || activity.getState() != ActivityState.STATE_COMPLETE)) {

                        final MaterialDialog dialog = new MaterialDialog(this);
                        dialog.setTitle(R.string.club_feed_del_hint);
                        dialog.setMessage(R.string.user_setting_activity_finish_cycling_tip);
                        dialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }).setNegativeButton(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }).show();

                    } else {
                        quit(this);
                    }
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        userSp.unregisterOnSharedPreferenceChangeListener(this);
        defautSp.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    private void clearCache() {
        final LoadingDialog dlg = new LoadingDialog(this, getString(R.string.setting_fragment_clearing_cache), true);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                dlg.show();
            }

            @Override
            protected void onPostExecute(Void result) {
                dlg.dismiss();
            }

            @Override
            protected Void doInBackground(Void... params) {
                final Context ctx = SettingActivity.this;

                // clear synced activities & samples
                try {
                    new ActivityManager(ctx).deleteSyncedActivities();
                } catch (BusinessException e) {
                    Log.e(TAG, "Clear local activity & samples error", e);
                }

                // clear image cache
                CacheManager.getInstance().clear(ctx);

                // clear API cache
                APICache.getInstance(ctx).clear();

                return null;
            }

        }.execute();
    }

    /**
     * 退出当前账号
     */
    public static void quit(Context context) {
        if (context == null)
            return;
        try {
            context.stopService(new Intent(context, SyncService.class));
        } catch (Exception e) {
//            logger.info("OPPO Service SecurityException");
        }
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        JPushInterface.stopPush(context);
        if (AVUser.getCurrentUser() != null) {
            UserManager um = new UserManager(context);
            if (um != null) {
                try {
                    new UserManager(context).deleteLocalUser(um.getLocalUser(AVUser.getCurrentUser().getObjectId()));
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
            }

//            if (AVUser.getCurrentUser().getSignType() != AVUser.SIGN_TYPE_EMAIL
//                    || AVUser.getCurrentUser().getSignType() != AVUser.SIGN_TYPE_PHONE)
//                SNS.logout(activity, SNSType.pareType(AVUser.getCurrentUser().getSignType()));
        }

        ClubFeedService.getInstance().unInit();
        new AuthenticationManager(context).logOut();
        AuthenticationFactory.removeALLAccount(context);//注销全部第三方登录
        RongCloudManager.getInstance().rongCloudUnInit();

        final Intent intent =new Intent(context, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (activity != null) {
            activity.finish();
        }

    }

    private void fetchProfile(final String userId) {
        final CacheManager cm = CacheManager.getInstance();
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, ProfileDTO>() {

            @Override
            protected ProfileDTO doInBackground(String... params) {
                try {
                    if (null == userManager)
                        return null;

                    final ProfileDTO dto = userManager.getProfileFromLocal(params[0]);
                    if (null != cm && null != dto)
                        cm.putString(params[0], dto.getAvatar());
                    return dto;
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ProfileDTO dto) {
                if (null == dto)
                    return;

                final String url = dto.getAvatar();
                final String nickname = dto.getNickname();
                final String location = dto.getLocation();

                if (!TextUtils.isEmpty(url)) {
                    Picasso.with(SettingActivity.this).load(url)
                            .fit().centerCrop().error(R.drawable.ic_avatar)
                            .placeholder(R.drawable.ic_avatar).into(imgAvatar);
                } else {
                    imgAvatar.setImageResource(R.drawable.ic_avatar);
                }

                if (!TextUtils.isEmpty(nickname)) {
                    lblNickname.setText(dto.getNickname());
                }
                if (!TextUtils.isEmpty(location)) {
                    lblLocation.setText(dto.getLocation());
                }
            }

        }, userId);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.PREF_UPDATE_USERINFO)) {
            if (!TextUtils.isEmpty(userId)) {
                this.fetchProfile(userId);
            }
        } else if (key.contains(Constants.PREF_DOT_VERSION_UPDATE)) {
            refreshVersionUpdateDot();
        }
    }

    private void refreshVersionUpdateDot() {

        SharedPreferences defaultSp = PreferenceManager.getDefaultSharedPreferences(this);
        int version = defaultSp.getInt(Constants.PREF_DOT_VERSION_UPDATE, 0);
        defaultSp.edit().putBoolean(Constants.PREF_DOT_VERSION_UPDATE_GUIDE+"1" + version, false).apply();

        int currentVersion = UpdateManager.getCurrentVersion(this);
        boolean tag = defaultSp.getBoolean(Constants.PREF_DOT_VERSION_UPDATE_GUIDE+"2"+version,true);

        lblAboutDot.setVisibility((version > currentVersion && tag) ? View.VISIBLE : View.GONE);

    }
}
