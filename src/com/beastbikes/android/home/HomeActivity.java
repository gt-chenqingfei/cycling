package com.beastbikes.android.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.home.adapter.HomePagerAdapter;
import com.beastbikes.android.home.view.HeaderViewHolder;
import com.beastbikes.android.home.view.NavigationView;
import com.beastbikes.android.home.view.TabViewHolder;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.ad.ui.AdBannerView;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.activity.ui.CyclingActivity;
import com.beastbikes.android.modules.cycling.activity.ui.CyclingFragment;
import com.beastbikes.android.modules.cycling.club.ui.ClubFragment;
import com.beastbikes.android.modules.cycling.club.ui.widget.MyFrameLayout;
import com.beastbikes.android.modules.cycling.ranking.ui.RankActivity;
import com.beastbikes.android.modules.cycling.route.ui.RoutesActivity;
import com.beastbikes.android.modules.cycling.sections.ui.CompetitionSectionActivity;
import com.beastbikes.android.modules.preferences.ui.SettingActivity;
import com.beastbikes.android.modules.shop.ui.BikeShopListActivity;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.MedalDTO;
import com.beastbikes.android.modules.user.ui.MedalInfoActivity;
import com.beastbikes.android.modules.user.ui.MedalsActivity;
import com.beastbikes.android.modules.user.ui.ProfileFragment;
import com.beastbikes.android.update.biz.UpdateManager;
import com.beastbikes.android.update.dto.VersionInfo;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.utils.Utils;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.WebActivity;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.io.Serializable;
import java.util.List;

@LayoutResource(R.layout.activity_home)
public class HomeActivity extends SessionFragmentActivity implements
        NavigationView.NavigationCallback, Constants, HomeManager.ProfileTabDotChangeListener,
        HomeManager.ClubTabDotChangeListener, UpdateManager.CheckUpdateCallback, HomePagerAdapter.OnTabChangeListener {
    public static final String ACTION_FINISH_HOME_ACTIVITY = "action_finish_home_activity";
    public static final int DELLAY_MILLIS_BACK_TO_HOME = 2 * 1000;
    public static final String PAGE_TAG_CYCLING = "cycling";
    public static final String PAGE_TAG_CLUB_FEED = "feed";
    public static final String PAGE_TAG_PROFILE = "profile";

    // 排行榜默认排行 0:全球 1:全国 2:区域
    public static int REGION_RANK_CODE = 1;

    public static int currentPage = 0;

    @IdResource(R.id.id_lv_left_menu)
    private ListView mLvLeftMenu;

    @IdResource(R.id.activity_home_content_main_viewPager)
    private ViewPager mMainViewPager;

    @IdResource(R.id.activity_home_content_main_tabs)
    private LinearLayout mTabs;

    private NavigationView nav;
    private HeaderViewHolder navHeader;
    private HomeManager homeManager;
    private long backHomeDelayMillis;
    private SharedPreferences userSp;
    private SharedPreferences defaultSp;

    private HomeActivityReceiver receiver;

    private HomePagerAdapter mHomePagerAdapter;

    private ActivityManager activityManager;

    private UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVUser user = AVUser.getCurrentUser();
        if (user == null) return;

        navHeader = new HeaderViewHolder(this, mLvLeftMenu);
        nav = new NavigationView(this, mLvLeftMenu);
        nav.setHeaderView(navHeader.getView());
        nav.setup(this);
        nav.setNavigationSelectListener(this);
        nav.setCurrentItem(R.id.nav_item_cycling);

        mLvLeftMenu.postDelayed(new Runnable() {
            @Override
            public void run() {
                nav.setFooterView(new AdBannerView(HomeActivity.this, mLvLeftMenu.getWidth()));
            }
        }, 200);

        this.userSp = getSharedPreferences(user.getObjectId(), 0);
        this.defaultSp = PreferenceManager.getDefaultSharedPreferences(this);

        this.homeManager = new HomeManager(this, nav);
        this.homeManager.onCreate();
        this.homeManager.setProfileDotChangeListener(this);
        this.homeManager.setClubDotChangeListener(this);
        receiver = new HomeActivityReceiver();
        homeManager.checkUpdate(this);

        this.activityManager = new ActivityManager(this);

        this.mUserManager = new UserManager(this);

        this.initViewPager();
        this.registerReceiver();

        this.getMedalStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != this.homeManager) {
            this.homeManager.onResume();
        }
        this.navHeader.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver();
        if (homeManager != null) {
            homeManager.destroy();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP)
            return true;

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

            long millis = Utils.getCurrentTimeInMillis() - backHomeDelayMillis;
            if (millis > DELLAY_MILLIS_BACK_TO_HOME) {
                Toasts.show(this, R.string.activity_home_toast_backhome_dellay);
            } else {
//                moveTaskToBack(true);
                this.finish();
                return true;
            }
            backHomeDelayMillis = Utils.getCurrentTimeInMillis();

            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onNavigationItemSelected(int id) {

        switch (id) {
            case R.id.nav_item_ranking://排行榜
                this.startActivity(new Intent(HomeActivity.this, RankActivity.class));
                break;
            case R.id.nav_item_activity://热门活动
                SpeedxAnalytics.onEvent(this, "", "click_cycling_event");
                startActivityActivity();
                break;
            case R.id.nav_item_railway://竞赛路段
                this.startActivity(new Intent(HomeActivity.this, CompetitionSectionActivity.class));
                break;
            case R.id.nav_item_route://精品路线
                this.startActivity(new Intent(HomeActivity.this, RoutesActivity.class));
                break;
            case R.id.nav_item_store://附近车店
                this.startActivity(new Intent(HomeActivity.this, BikeShopListActivity.class));
                break;
            case R.id.nav_item_setting://设置
                this.startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                break;
            default:
                break;
        }
        return false;

    }

    /**
     * 刷新俱乐部红点
     *
     * @param count
     */
    @Override
    public void onClubTabDotChanged(int count) {
        if (null != mHomePagerAdapter) {
            mHomePagerAdapter.setTabDots(1, count);
        }
    }

    /**
     * 刷新用户信息红点
     *
     * @param count
     */
    @Override
    public void onProfileTabDotChange(int count) {
        if (null != mHomePagerAdapter) {
            mHomePagerAdapter.setTabDots(2, count);
        }
    }

    /**
     * 跳转活动
     */
    private void startActivityActivity() {

        //// FIXME: 16/8/29  测试微信支付
        final StringBuilder sb = new StringBuilder(Constants.UrlConfig.DEV_SPEEDX_HOST)
                .append("/app/activity/list.html");

//        final StringBuilder sb = new StringBuilder("http://hybrid.speedx.com/200k/");

        final Uri browserUri = Uri.parse(sb.toString());
        final Intent browserIntent = new Intent(this,
                BrowserActivity.class);
        browserIntent.setData(browserUri);
        browserIntent.addCategory(Intent.CATEGORY_DEFAULT);
        browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        browserIntent.setPackage(getPackageName());
        browserIntent.putExtra(WebActivity.EXTRA_TITLE,
                getString(R.string.task_title));
        browserIntent.putExtra(WebActivity.EXTRA_ENTER_ANIMATION,
                R.anim.activity_in_from_right);
        browserIntent.putExtra(WebActivity.EXTRA_EXIT_ANIMATION,
                R.anim.activity_out_to_right);
        browserIntent.putExtra(WebActivity.EXTRA_NONE_ANIMATION,
                R.anim.activity_none);
        Bundle bundle = new Bundle();
        bundle.putString("X-User-Id", getUserId());
        browserIntent.putExtra(WebActivity.EXTRA_HTTP_HEADERS, bundle);
        this.startActivity(browserIntent);
        userSp.edit().putInt(PUSH.PREF_KEY.DOT_CYCLING_ACTIVITY, 0).apply();
        SpeedxAnalytics.onEvent(this, "进入活动入口", "open_activity");
    }


    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        mHomePagerAdapter = new HomePagerAdapter(this, mMainViewPager);
        TabViewHolder holderCycling = new TabViewHolder(R.string.activity_fragment_title, R.drawable.tab_cycling_selector,
                PAGE_TAG_CYCLING, HomeActivity.this, mTabs, 3, null);
        TabViewHolder holderClubFeed = new TabViewHolder(R.string.club_info_title, R.drawable.tab_club_selector,
                PAGE_TAG_CLUB_FEED, HomeActivity.this, mTabs, 3, null);
        TabViewHolder holderProfile = new TabViewHolder(R.string.route_self, R.drawable.tab_profile_selector,
                PAGE_TAG_PROFILE, HomeActivity.this, mTabs, 3, null);

        mHomePagerAdapter.addTab(holderCycling, CyclingFragment.class, null);
        mHomePagerAdapter.addTab(holderClubFeed, ClubFragment.class, null);
        mHomePagerAdapter.addTab(holderProfile, ProfileFragment.class, null);
        mHomePagerAdapter.setOnTabChangedListener(this);

        mMainViewPager.setOffscreenPageLimit(3);
        mMainViewPager.setAdapter(mHomePagerAdapter);
        mMainViewPager.setCurrentItem(0);
        setTitle(R.string.activity_fragment_title);

        holderCycling.setSelected(true);
        currentPage = 0;
    }

    @Override
    public void onUpdateAvailable(final VersionInfo info) {


        if (info != null) {
            int versionTag = defaultSp.getInt(PREF_DOT_VERSION_UPDATE, 0);
            //非强制更新类型 每个版本只会提醒一次更新
            if (info.getVersionCode() == versionTag && info.getType() == VersionInfo.TYPE_NORMAL) {
                return;
            }
            final MaterialDialog dialog = new MaterialDialog(this);
            dialog.setTitle(String.format(getString(R.string.version_update_title), info.getVersionName()));
            dialog.setMessage(info.getChangeLog());
            dialog.setCanceledOnTouchOutside(false);
            if (info.getType() == VersionInfo.TYPE_NORMAL) {
                dialog.setNegativeButton(R.string.version_update_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        defaultSp.edit().putInt(PREF_DOT_VERSION_UPDATE, info.getVersionCode()).apply();
                    }
                });
            } else {
                dialog.setCancelable(false);
            }

            dialog.setPositiveButton(R.string.version_update_ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (info.getType() == VersionInfo.TYPE_NORMAL) {
                        dialog.dismiss();
                        defaultSp.edit().putInt(PREF_DOT_VERSION_UPDATE, info.getVersionCode()).apply();
                    }
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(info.getDownloadLink());
                    intent.setData(content_url);
                    startActivity(intent);
                }
            });
            dialog.show();

        }
    }

    @Override
    public void onTabChanged(int position) {
        if (position == 0) {
            checkCyclingState();
        }
    }

    /**
     * check cycling state
     */
    private void checkCyclingState() {

        boolean checkOn = userSp.getBoolean(Constants.PREF_CYCLING_STATE_CHECK_KEY, true);

        if (activityManager != null && checkOn) {
            LocalActivity activity = activityManager.getCurrentActivity();
            if (null != activity) {
                final Intent cyclingIntent = new Intent(this, CyclingActivity.class);
                startActivity(cyclingIntent);
            }
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyFrameLayout.ACTION_ON_VIEW_RESIZE);
        filter.addAction(ACTION_FINISH_HOME_ACTIVITY);
        registerReceiver(receiver, filter);
    }

    private void unregisterReceiver() {
        if (receiver != null && !receiver.isOrderedBroadcast())
            unregisterReceiver(receiver);
    }

    /**
     * 获取已经点亮的最新勋章
     */
    private void getMedalStatus() {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, List<MedalDTO>>() {

            @Override
            protected List<MedalDTO> doInBackground(String... params) {
                return mUserManager.getBadgeList(2, 1, 1000, getUserId());
            }

            @Override
            protected void onPostExecute(List<MedalDTO> medalDTOs) {
                if (null == medalDTOs || medalDTOs.isEmpty()) {
                    return;
                }

                //弹窗
                showMedalsDialog(medalDTOs);
            }
        });
    }

    /**
     * 调用MedalInfoActivity代替dialog
     */
    private void showMedalsDialog(List<MedalDTO> newActiveMedals) {
        Intent intent = new Intent(this, MedalInfoActivity.class);
        intent.putExtra(MedalInfoActivity.EXTRA_MEDAL_LIST, (Serializable) newActiveMedals);
        intent.putExtra(MedalsActivity.EXTRA_FROM_PUSH, true);
        startActivity(intent);
    }

    class HomeActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null)
                return;
            if (intent.getAction().equals(MyFrameLayout.ACTION_ON_VIEW_RESIZE)) {
                boolean isShow = intent.getBooleanExtra(MyFrameLayout.EXTRA_IS_SOFTKEYBOARD_SHOWN, false);
                if (mTabs != null) {
                    if (isShow) {
                        mTabs.setVisibility(View.GONE);
                    } else {
                        mTabs.setVisibility(View.VISIBLE);
                    }
                }
            } else if (TextUtils.equals(intent.getAction(), ACTION_FINISH_HOME_ACTIVITY)) {
                finish();
            }
        }
    }
}

