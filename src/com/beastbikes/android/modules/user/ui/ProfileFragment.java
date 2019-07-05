package com.beastbikes.android.modules.user.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.ble.biz.BleManager;
import com.beastbikes.android.ble.dao.entity.BleDevice;
import com.beastbikes.android.ble.ui.DiscoveryActivity;
import com.beastbikes.android.ble.ui.SpeedForceActivity;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.route.ui.RouteSelfActivity;
import com.beastbikes.android.modules.cycling.sections.ui.FavorSegmentActivity;
import com.beastbikes.android.modules.message.biz.MessageManager;
import com.beastbikes.android.modules.preferences.ui.SettingActivity;
import com.beastbikes.android.modules.preferences.ui.UserSettingActivity;
import com.beastbikes.android.modules.social.im.biz.FriendManager;
import com.beastbikes.android.modules.social.im.biz.RongCloudManager;
import com.beastbikes.android.modules.social.im.dto.FriendDTO;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.HistogramDTO;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.modules.user.dto.UserDetailDTO;
import com.beastbikes.android.modules.user.ui.binding.widget.AddFriendRemarksDialog;
import com.beastbikes.android.modules.user.widget.HistogramView;
import com.beastbikes.android.utils.IntentUtils;
import com.beastbikes.android.utils.RxBus;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.cache.CacheManager;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.android.res.annotation.StringResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;
import com.umeng.onlineconfig.OnlineConfigAgent;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

@Alias("个人详情页")
@StringResource(R.string.profile_fragment_title)
@MenuResource(R.menu.friends_setting_menu)
@LayoutResource(R.layout.profile_fragment)
public class ProfileFragment extends SessionFragment implements
        OnClickListener, Constants, OnSharedPreferenceChangeListener, UserManager.LoadLocalProfileListener {

    public static final Logger logger = LoggerFactory.getLogger(ProfileFragment.class);
    public static final String PREF_LAST_DATE = "message.lastdate";
    public static final int RESULT_CODE = 77;
    public static final int RESULT_NOTIFICATION_CODE = 78;

    // 监听通知栏
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    @IdResource(R.id.profile_fragment_avatar)
    private CircleImageView avatar;
    @IdResource(R.id.profile_fragment_gender_iv)
    private ImageView genderIv;
    @IdResource(R.id.profile_fragment_remark_name)
    private TextView remarkTv;
    @IdResource(R.id.profile_fragment_nickname_name)
    private TextView nickNameTv;
    @IdResource(R.id.profile_fragment_location)
    private TextView location;
    @IdResource(R.id.profile_fragment_id_tv)
    private TextView userIdTv;

    @IdResource(R.id.profile_fragment_friend_btn)
    private FrameLayout friendBtn;

    @IdResource(R.id.profile_fragment_friend_btn_icon)
    private ImageView friendBtnIcon;

    @IdResource(R.id.profile_fragment_friend_btn_text)
    private TextView profile_fragment_friend_btn_text;

    @IdResource(R.id.profile_fragment_statistic_item_total_distance)
    private ViewGroup totalDistance;
    private TextView totalDistanceValue;

    @IdResource(R.id.profile_fragment_statistic_item_total_elapsed_time)
    private ViewGroup totalElapsedTime;
    private TextView totalElapsedTimeValue;

    @IdResource(R.id.profile_fragment_statistic_item_total_calories)
    private ViewGroup totalCalories;
    private TextView totalCaloriesValue;

    @IdResource(R.id.profile_fragment_follower_view)
    private ViewGroup followerView;
    @IdResource(R.id.profile_fragment_statistic_item_fans)
    private TextView followValue;

    @IdResource(R.id.profile_fragment_medal_view)
    private ViewGroup medalView;
    @IdResource(R.id.profile_fragment_statistic_item_medal_value)
    private TextView medalValueTv;

    @IdResource(R.id.profile_fragment_fans_view)
    private ViewGroup fansView;
    @IdResource(R.id.profile_fragment_statistic_item_follower)
    private TextView fansValueTv;

    @IdResource(R.id.dot_follower)
    private TextView dotFollower;

    @IdResource(R.id.profile_fragment_statistic_item_total_times)
    private ViewGroup totalTimes;
    private TextView totalTimesValue;

    @IdResource(R.id.profile_fragment_detail_item_activities)
    private ViewGroup detailedActivities;
    private TextView detailedActivitiesDesciption;

    @IdResource(R.id.profile_fragment_cycling_details)
    private LinearLayout profileFragmentCyclingDetails;

    @IdResource(R.id.profile_fragment_detail_item_statistics)
    private View detailedStatistics;

    @IdResource(R.id.profile_fragment_detail_item_route_favorites)
    private ViewGroup routeFavoritesVG;

    @IdResource(R.id.profile_fragment_detail_item_route_book)
    private ViewGroup routeBookVG;

    @IdResource(R.id.profile_fragment_detail_item_grid)
    private ViewGroup gridView;

    @IdResource(R.id.profile_fragment_detail_item_club)
    private ViewGroup clubView;
    private TextView clubDesc;

    private AddFriendRemarksDialog addfriendremarksDialog;

    @IdResource(R.id.profile_fragment_same_grid_count_tv)
    private TextView sameGridCountTv;

    @IdResource(R.id.profile_fragment_detail_item_speedforce)
    private ViewGroup speedForceVG;

    @IdResource(R.id.profile_fragment_histogram_user_view)
    private ViewGroup userHistogramParentView;
    private TextView messageCountTv;

    private HistogramView userHistogramView;
    private UserManager userManager;
    private MessageManager messageManager;
    private FriendManager friendManager;
    private LoadingDialog loadingDialog;
    private SharedPreferences userSp;
    private BeastBikes app;

    private ProfileDTO profile;

    private boolean refresh = true;

    // 是否是登录用户
    private boolean isLoginUser;

    // 好友关系状态
    private int status;

    private Intent intent;

    //是否需要好友关系的菜单
    private boolean isFriendSetting = false;
    private Menu menu;

    private boolean isMine;
    private String nickName;

    @Override
    public void onAttach(Activity activity) {
        this.userManager = new UserManager(activity);
        this.messageManager = new MessageManager(activity);
        this.friendManager = new FriendManager(activity);
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity().getLocalClassName().contains("HomeActivity")) {
            isMine = true;
            setHasOptionsMenu(true);
        }

        String openMedal = OnlineConfigAgent.getInstance().getConfigParams(this.getActivity(), UMENG_OPEN_MEDAL);
        logger.info("open_medal", openMedal);

        this.avatar.setOnClickListener(this);
        /**
         * 控制scrollview滑到顶部
         */
        this.avatar.setFocusable(true);
        this.avatar.setFocusableInTouchMode(true);
        this.avatar.requestFocus();

        this.profileFragmentCyclingDetails.setOnClickListener(this);

        TextView totalDistanceLabel = (TextView) this.totalDistance
                .findViewById(R.id.profile_fragment_statistic_item_name);
        if (LocaleManager.isDisplayKM(ProfileFragment.this.getActivity())) {
            totalDistanceLabel.setText(getResources().getString(R.string.activity_param_label_distance) + LocaleManager.LocaleString.profile_fragment_statistic_item_total_distance);
        } else {
            totalDistanceLabel.setText(getResources().getString(R.string.activity_param_label_distance) + LocaleManager.LocaleString.profile_fragment_statistic_item_total_distance_mi);
        }
        this.totalDistanceValue = (TextView) this.totalDistance
                .findViewById(R.id.profile_fragment_statistic_item_value);
        this.totalDistanceValue
                .setText(R.string.activity_param_distance_default_value);

        TextView totalElapsedTimeLabel = (TextView) this.totalElapsedTime
                .findViewById(R.id.profile_fragment_statistic_item_name);
        totalElapsedTimeLabel
                .setText(R.string.profile_fragment_statistic_item_total_elapsed_time);
        this.totalElapsedTimeValue = (TextView) this.totalElapsedTime
                .findViewById(R.id.profile_fragment_statistic_item_value);
        this.totalElapsedTimeValue
                .setText(R.string.activity_param_elapsed_time_default_short_value);

        this.totalCaloriesValue = (TextView) this.totalCalories.
                findViewById(R.id.profile_fragment_statistic_item_value);
        TextView totalCaloriesLabel = (TextView) this.totalCalories.
                findViewById(R.id.profile_fragment_statistic_item_name);
        totalCaloriesLabel.setText(R.string.activity_param_label_calorie);
        this.totalCaloriesValue.setText(R.string.activity_param_distance_default_value);

        this.totalTimesValue = (TextView) this.totalTimes.
                findViewById(R.id.profile_fragment_statistic_item_value);
        TextView totalTimesLabel = (TextView) this.totalTimes.
                findViewById(R.id.profile_fragment_statistic_item_name);
        totalTimesLabel.setText(R.string.activity_param_label_times);
        this.totalTimesValue.setText(R.string.activity_param_distance_default_value);

        this.detailedActivities.setOnClickListener(this);
        ImageView detailedActivitiesIcon = (ImageView) this.detailedActivities
                .findViewById(R.id.profile_fragment_detail_item_icon);
        detailedActivitiesIcon
                .setImageResource(R.drawable.ic_profile_activities);
        TextView detailedActivitiesSubject = (TextView) this.detailedActivities
                .findViewById(R.id.profile_fragment_detail_item_subject);
        detailedActivitiesSubject
                .setText(R.string.profile_fragment_detailed_item_activities);
        this.detailedActivitiesDesciption = (TextView) this.detailedActivities
                .findViewById(R.id.profile_fragment_detail_item_desc);

        this.detailedStatistics.setOnClickListener(this);

        this.gridView.setOnClickListener(this);
        ImageView gridIcon = (ImageView) this.gridView
                .findViewById(R.id.profile_fragment_detail_item_icon);
        gridIcon.setImageResource(R.drawable.ic_profile_grid_icon);
        TextView gridTitle = (TextView) this.gridView
                .findViewById(R.id.profile_fragment_detail_item_subject);
        gridTitle
                .setText(R.string.grid_explore_label);
        TextView gridDesc = (TextView) this.gridView
                .findViewById(R.id.profile_fragment_detail_item_desc);
        gridDesc.setVisibility(View.GONE);
        TextView gridDot = (TextView) this.gridView
                .findViewById(R.id.profile_fragment_detail_item_dot);
        gridDot.setVisibility(View.GONE);
        this.gridView.setVisibility(View.GONE);


        this.clubView.setOnClickListener(this);
        ImageView clubIcon = (ImageView) this.clubView
                .findViewById(R.id.profile_fragment_detail_item_icon);
        clubIcon.setImageResource(R.drawable.ic_profile_club);
        TextView clubTitle = (TextView) this.clubView
                .findViewById(R.id.profile_fragment_detail_item_subject);
        clubTitle
                .setText(R.string.profile_fragment_club_title);
        this.clubDesc = (TextView) this.clubView
                .findViewById(R.id.profile_fragment_detail_item_desc_left);

        this.clubView
                .findViewById(R.id.profile_fragment_detail_item_desc).setVisibility(View.GONE);
        clubDesc.setText(R.string.did_not_join_club);
        clubDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        TextView clubDot = (TextView) this.clubView
                .findViewById(R.id.profile_fragment_detail_item_dot);
        clubDot.setVisibility(View.GONE);

        this.speedForceVG.setOnClickListener(this);
        ImageView speedForceIcon = (ImageView) this.speedForceVG.findViewById(R.id.profile_fragment_detail_item_icon);
        TextView speedForceTitle = (TextView) this.speedForceVG.findViewById(R.id.profile_fragment_detail_item_subject);
        TextView speedForceDesc = (TextView) this.speedForceVG.findViewById(R.id.profile_fragment_detail_item_desc);
        speedForceTitle.setText(R.string.profile_fragment_detail_item_speed_force_title);
        speedForceIcon.setImageResource(R.drawable.ic_speed_force_icon);
        speedForceDesc.setVisibility(View.GONE);

        this.routeBookVG.setOnClickListener(this);
        ImageView routeBookIcon = (ImageView) this.routeBookVG
                .findViewById(R.id.profile_fragment_detail_item_icon);
        routeBookIcon.setImageResource(R.drawable.ic_nav_route_plan);
        TextView routeBookName = (TextView) this.routeBookVG
                .findViewById(R.id.profile_fragment_detail_item_subject);
        routeBookName
                .setText(R.string.profile_fragment_detailed_item_my_route);
        TextView routeBookDesc = (TextView) this.routeBookVG
                .findViewById(R.id.profile_fragment_detail_item_desc);
        routeBookDesc.setVisibility(View.GONE);

        this.routeFavoritesVG.setOnClickListener(this);
        this.routeFavoritesVG.setVisibility(View.GONE);
        ImageView routeFavoritesIcon = (ImageView) this.routeFavoritesVG
                .findViewById(R.id.profile_fragment_detail_item_icon);
        routeFavoritesIcon.setImageResource(R.drawable.ic_profile_collect_icon);
        TextView routeFavoritesName = (TextView) this.routeFavoritesVG
                .findViewById(R.id.profile_fragment_detail_item_subject);
        routeFavoritesName
                .setText(R.string.profile_fragment_detailed_item_my_favorites);
        TextView routeFavoritesDesc = (TextView) this.routeFavoritesVG
                .findViewById(R.id.profile_fragment_detail_item_desc);
        routeFavoritesDesc.setVisibility(View.GONE);

        this.medalView.setOnClickListener(this);

        this.followerView.setOnClickListener(this);
        this.fansView.setOnClickListener(this);
        this.friendBtn.setOnClickListener(this);

        intent = getActivity().getIntent();
        if (null == intent) {
            return;
        }


        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            return;
        }

        this.isLoginUser = user.getObjectId().equals(getUserId());
        this.app = (BeastBikes) getActivity().getApplication();

        if (this.isLoginUser) {
            this.remarkTv.setText(user.getDisplayName());
            if (LocaleManager.isChineseTimeZone()) {
                this.routeBookVG.setVisibility(View.VISIBLE);
            } else {
                this.routeBookVG.setVisibility(View.GONE);
            }
        } else {
            this.routeBookVG.setVisibility(View.GONE);
        }

        this.userSp = getActivity().getSharedPreferences(user.getObjectId(), 0);
        this.userSp.registerOnSharedPreferenceChangeListener(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || !isLoginUser) {
            this.speedForceVG.setVisibility(View.GONE);
        }

        this.fetchUserDetail(getUserId());

        this.userHistogramView = new HistogramView(getActivity());
        this.userHistogramParentView.addView(userHistogramView);

        this.fetchUserProfile(this.getUserId(), this);
        fetchUserDiagram();

        if (this.isLoginUser) {
            this.fetchMessage();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final AVUser user = AVUser.getCurrentUser();
        // this.fetchUserProfile(this.getUserId(),false);
        if (this.isLoginUser) {
            if (this.app.isMapStyleEnabled()) {
                this.gridView.setVisibility(View.VISIBLE);
            } else {
                this.gridView.setVisibility(View.GONE);
            }
            CharSequence title = getActivity().getTitle();
            if (!TextUtils.isEmpty(title) && title.equals(user.getDisplayName())) {
                this.remarkTv.setText(user.getDisplayName());
            }
            if (!TextUtils.isEmpty(user.getAvatar())) {
                Picasso.with(getActivity()).load(user.getAvatar()).fit().placeholder(R.drawable.ic_avatar).
                        error(R.drawable.ic_avatar).centerCrop().into(avatar);
            }
        } else {
            isFriendSetting = true;
            nickName = intent.getStringExtra(ProfileActivity.EXTRA_NICK_NAME);
            String remarks = intent.getStringExtra(ProfileActivity.EXTRA_REMARKS);
            refreshTitle(nickName, remarks);

            String avatarUrl = intent.getStringExtra(ProfileActivity.EXTRA_AVATAR);
            if (!TextUtils.isEmpty(avatarUrl)) {
                Picasso.with(getActivity()).load(avatarUrl).fit().placeholder(R.drawable.ic_avatar).
                        error(R.drawable.ic_avatar).centerCrop().into(avatar);
            }

            String city = intent.getStringExtra(ProfileActivity.EXTRA_CITY);
            if (!TextUtils.isEmpty(city)) {
                location.setText(city);
            }

        }

        if (user != null && this.isLoginUser) {
            this.refreshDot();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != this.userSp) {
            this.userSp.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        if (isFriendSetting) {
            super.onCreateOptionsMenu(menu, inflater);
        } else {
            if (isMine) {
                inflater.inflate(R.menu.setting_activity, menu);
                MenuItem menuItem = menu.getItem(0);
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.profile_fragment_message_menu, null);
                ImageView messageView = (ImageView) view.findViewById(R.id.profile_fragment_menu_message_icon);
                this.messageCountTv = (TextView) view.findViewById(R.id.profile_fragment_menu_message_count);
                messageView.setOnClickListener(this);
                refreshDot();
                menuItem.setActionView(view);
            }
        }
    }

    private void hintDeleteFriendMenu(boolean isShow) {
        if (!isFriendSetting)
            return;
        if (menu != null) {
            MenuItem deleteFriendMenu = menu.getItem(1);
            if (deleteFriendMenu != null)
                deleteFriendMenu.setVisible(isShow);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_setting_action_button_setting:
                final Intent intent = new Intent(getActivity(), UserSettingActivity.class);
                intent.putExtra(UserSettingActivity.EXTRA_FROM_SETTING, true);
                startActivityForResult(intent, SettingActivity.RC_GOTO_USER_SETTING_PAGE);
                break;
            case R.id.activity_friends_update_remarks:
                this.addfriendremarksDialog = new AddFriendRemarksDialog(this.getActivity(), null,
                        getString(R.string.friends_add_friendremarks_hint), new UpdateRemarksDialogClickListener());
                addfriendremarksDialog.setCancelable(false);
                addfriendremarksDialog.show();
                break;
            case R.id.activity_friends_delete_friend:// 取消关注
                this.showUnfollowDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        final Activity ctx = getActivity();
        switch (v.getId()) {
            case R.id.profile_fragment_detail_item_activities: {// 查看骑行记录列表
                final Intent it = new Intent(ctx, CyclingRecordActivity.class);
                it.putExtra(CyclingRecordActivity.EXTRA_USER_ID, getUserId());
                String avatarUrl = "";
                String nickName = "";

                if (null != this.profile) {
                    avatarUrl = profile.getAvatar();
                    nickName = profile.getNickname();
                } else if (this.intent != null) {
                    nickName = this.intent.getStringExtra(ProfileActivity.EXTRA_NICK_NAME);
                    avatarUrl = this.intent.getStringExtra(ProfileActivity.EXTRA_AVATAR);
                }
                if (TextUtils.isEmpty(nickName) && TextUtils.isEmpty(avatarUrl)) {
                    if (AVUser.getCurrentUser() != null) {
                        nickName = AVUser.getCurrentUser().getDisplayName();
                        avatarUrl = AVUser.getCurrentUser().getAvatar();
                    }
                }

                it.putExtra(CyclingRecordActivity.EXTRA_AVATAR_URL, avatarUrl);
                it.putExtra(CyclingRecordActivity.EXTRA_NICK_NAME, nickName);

                it.putExtra(CyclingRecordActivity.EXTRA_REFRESH, refresh);
                startActivityForResult(it, RESULT_CODE);

                if (this.isLoginUser)
                    SpeedxAnalytics.onEvent(ctx, "查看我的骑行纪录列表", "click_my_page_ridding_history");
                else
                    SpeedxAnalytics.onEvent(ctx, "查看别人的骑行纪录列表", null);
                break;
            }
            case R.id.profile_fragment_detail_item_statistics: {// 个人总成绩
                final Intent intent = new Intent(ctx, PersonalRecordActivity.class);
                intent.putExtra(PersonalRecordActivity.EXTRA_USER_ID, getUserId());
                startActivity(intent);
                break;
            }
            case R.id.profile_fragment_medal_view: {//勋章入口
                final Intent intent = new Intent(getActivity(), MedalsActivity.class);
                if (null != profile) {
                    intent.putExtra(SessionFragmentActivity.EXTRA_USER_ID, getUserId());
                    intent.putExtra(MedalsActivity.EXTRA_MEDAL_COUNT, profile.getMedalNum());
                }
                startActivity(intent);
                SpeedxAnalytics.onEvent(getActivity(), "", "click_medal");
                break;
            }
            case R.id.profile_fragment_detail_item_grid:// 我的格子
                if (null == profile) {
                    return;
                }

                Intent gridIntent = new Intent(this.getActivity(), GridExploreActivity.class);
                gridIntent.putExtra(GridExploreActivity.EXTRA_USER_ID, getUserId());
                gridIntent.putExtra(GridExploreActivity.EXTRA_PROFILE, profile);
                startActivity(gridIntent);
                break;
            case R.id.profile_fragment_detail_item_speedforce: { // 我的中控
                SpeedxAnalytics.onEvent(getActivity(), "", "click_my_page_my_device");
                this.startNotificationListener();
                break;
            }
            case R.id.profile_fragment_avatar: {// 查看头像大图
                if (this.profile == null)
                    return;

                Intent intent = new Intent(ctx, AvatarViewer.class);
                intent.putExtra(AvatarViewer.EXTRA_USER_ID,
                        this.profile.getUserId());
                intent.putExtra(AvatarViewer.EXTRA_USER_AVATAR_URL,
                        this.profile.getAvatar());
                startActivity(intent);
                break;
            }
            case R.id.profile_fragment_friend_btn:// 添加好友／发消息
                if (null == profile) {
                    return;
                }
                String nickName = profile.getNickname();
                if (!TextUtils.isEmpty(profile.getRemarks())) {
                    nickName = profile.getRemarks();
                }

                if (this.isLoginUser) {
                    final Intent intent = new Intent(getActivity(), UserSettingActivity.class);
                    intent.putExtra(UserSettingActivity.EXTRA_FROM_SETTING, true);
                    startActivityForResult(intent, SettingActivity.RC_GOTO_USER_SETTING_PAGE);
                    break;
                }

                if (FriendDTO.FRIEND_STATUS_ADD == this.status ||
                        FriendDTO.FRIEND_STATUS_FANS == this.status) {// 添加好友
                    if (isAdded()) {
                        follow();
                    }
                } else if (FriendDTO.FRIEND_STATUS_FOLLOW_AND_FANS == this.status
                        || FriendDTO.FRIEND_STATUS_FOLLOW == this.status) {// 发送消息
                    if (null != RongIM.getInstance() && null != profile) {
                        SpeedxAnalytics.onEvent(getActivity(), "", "click_profile_messenger");
                        RongIM.getInstance().startPrivateChat(getActivity(),
                                profile.getUserId(), nickName);
                    }
                }
                break;
            case R.id.profile_fragment_detail_item_route_book:// 我的路书
                SpeedxAnalytics.onEvent(getActivity(), "", "click_my_page_my_road_book");
                startActivity(new Intent(getContext(), RouteSelfActivity.class));
                break;
            case R.id.profile_fragment_follower_view:// 关注
                Intent followIntent = new Intent(getActivity(), FollowActivity.class);
                followIntent.putExtra(FollowActivity.EXTRA_USER_ID, getUserId());
                startActivity(followIntent);
                break;
            case R.id.profile_fragment_fans_view:// 粉丝
                userSp.edit().putInt(PUSH.PREF_KEY.DOT_FOLLOW, 0).apply();
                Intent fansIntent = new Intent(getActivity(), FansActivity.class);
                fansIntent.putExtra(FansActivity.EXTRA_USER_ID, getUserId());
                startActivity(fansIntent);
                break;
            case R.id.profile_fragment_detail_item_route_favorites:// 收藏路段
                Intent segmentIntent = new Intent(getContext(), FavorSegmentActivity.class);
                segmentIntent.putExtra(FavorSegmentActivity.EXTRA_USER_ID, getUserId());
                startActivity(segmentIntent);
                break;
            case R.id.profile_fragment_detail_item_club:// 俱乐部
                if (null == profile) {
                    return;
                }

                if (!TextUtils.isEmpty(profile.getClubId())) {
                    IntentUtils.goClubFeedInfoActivity(getContext(), profile.getClubId());
                }
                break;
            case R.id.profile_fragment_menu_message_icon:// 我的消息
                if (RongIM.getInstance().getCurrentConnectionStatus().equals(
                        RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
                    RongIM.getInstance().startConversationList(getActivity(), null);
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Bundle bundle = data.getExtras();
                        this.refresh = bundle.getBoolean(CyclingRecordActivity.EXTRA_REFRESH);
                        logger.trace("activity record result");
                        break;
                }
                break;
            case RESULT_NOTIFICATION_CODE: // 通知栏使用权返回
                this.startSpeedForceActivity();
                break;
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        switch (key) {
            case PREF_FRIEND_NEW_MESSAGE_COUNT:
            case PREF_RONGCLOUD_NEW_MESSAGE_COUNT:
            case PUSH.PREF_KEY.DOT_FOLLOW:
                this.refreshDot();
                break;
            case PUSH.PREF_KEY.NOTIFY_FOLLOW:
                fetchUserProfile(getUserId(), this);
                break;
            case PREF_UPDATE_USERINFO:
                fetchUserProfileFromLocal(getUserId());
                break;
        }
    }

    /**
     * 刷新小红点
     */
    private void refreshDot() {
        if (null == userSp || null == this.messageCountTv) {
            return;
        }

        int rcMessageCount = userSp.getInt(PREF_RONGCLOUD_NEW_MESSAGE_COUNT, 0);
        int messageCount = userSp.getInt(PREF_FRIEND_NEW_MESSAGE_COUNT, 0);
        int followerCout = userSp.getInt(PUSH.PREF_KEY.DOT_FOLLOW, 0);

        rcMessageCount += messageCount;

        if (rcMessageCount > 0) {
            this.messageCountTv.setVisibility(View.VISIBLE);
        } else {
            this.messageCountTv.setVisibility(View.GONE);
        }

        if (rcMessageCount > 99) {
            this.messageCountTv.setText("99+");
        } else {
            this.messageCountTv.setText(String.valueOf(rcMessageCount));
        }

        if (followerCout > 0) {
            this.dotFollower.setVisibility(View.VISIBLE);
        } else {
            this.dotFollower.setVisibility(View.GONE);
        }

        if (followerCout > 99) {
            this.dotFollower.setText("99+");
        } else {
            this.dotFollower.setText(String.valueOf(followerCout));
        }

    }

    /**
     * 刷新好友状态
     */
    private void refreshButton() {
        this.friendBtn.setVisibility(View.VISIBLE);
        if (isLoginUser) {
            this.profile_fragment_friend_btn_text.setText(R.string.profile_fragment_edit_user_info);
        } else {
            switch (this.status) {
                case FriendDTO.FRIEND_STATUS_ADD:// 已关注
                case FriendDTO.FRIEND_STATUS_FANS:// 被关注
                    this.profile_fragment_friend_btn_text.setText(R.string.friends_add_friend);
                    this.profile_fragment_friend_btn_text.setTextColor(Color
                            .parseColor("#ffffff"));
                    this.friendBtnIcon.setImageResource(R.drawable.ic_add_friend_icon);
                    this.friendBtnIcon.setVisibility(View.VISIBLE);
                    hintDeleteFriendMenu(false);
                    break;
                case FriendDTO.FRIEND_STATUS_FOLLOW:// 已关注
                case FriendDTO.FRIEND_STATUS_FOLLOW_AND_FANS:// 相互关注
                    this.profile_fragment_friend_btn_text
                            .setText(R.string.friends_send_message);
                    this.profile_fragment_friend_btn_text.setTextColor(Color
                            .parseColor("#ffffff"));
                    friendBtnIcon.setImageResource(R.drawable.ic_message);
                    this.friendBtnIcon.setVisibility(View.VISIBLE);
                    hintDeleteFriendMenu(true);
                    break;
            }
        }
    }

    /**
     * 获取系统消息个数
     */
    private void fetchMessage() {
        if (null == userSp) {
            return;
        }

        long lastDate = this.userSp.getLong(PREF_LAST_DATE, 0);

        this.getAsyncTaskQueue().add(new AsyncTask<Long, Void, Integer>() {

            @Override
            protected Integer doInBackground(Long... params) {
                try {
                    long lastDate = params[0];

                    return messageManager.getMessageCount(lastDate);
                } catch (BusinessException e) {
                    return -1;
                }
            }

            @Override
            protected void onPostExecute(Integer result) {
                userSp.edit().putInt(PREF_FRIEND_NEW_MESSAGE_COUNT, result).apply();
                refreshDot();
            }

        }, lastDate);
    }

    /**
     * 获取用户信息
     *
     * @param userId userId
     */
    private void fetchUserProfile(final String userId, final UserManager.LoadLocalProfileListener listener) {
        if (TextUtils.isEmpty(userId))
            return;

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, ProfileDTO>() {

            @Override
            protected ProfileDTO doInBackground(String... params) {
                try {
                    final ProfileDTO dto = userManager.getProfileByUserId(params[0], listener);
                    if (dto == null)
                        return null;

                    if (isLoginUser && RongCloudManager.getInstance() != null) {
                        RongCloudManager.getInstance().setRongCloudUserInfo(dto);
                    }
                    CacheManager.getInstance().putString(params[0], dto.getAvatar());

                    return dto;
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ProfileDTO dto) {
                if (dto == null || getActivity() == null) {
                    return;
                }

                profile = dto;
                refreshView4UserInfo(dto);
            }

        }, userId);
    }

    /**
     * 从本地获取用户信息
     *
     * @param userId userId
     */
    private void fetchUserProfileFromLocal(final String userId) {
        if (TextUtils.isEmpty(userId))
            return;

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, ProfileDTO>() {

            @Override
            protected ProfileDTO doInBackground(String... params) {
                try {
                    return userManager.getProfileFromLocal(params[0]);

                } catch (BusinessException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ProfileDTO dto) {
                if (dto == null) {
                    return;
                }
                profile = dto;
                refreshView4UserInfo(dto);
            }

        }, userId);
    }

    /**
     * 刷新用户信息
     *
     * @param dto ProfileDto
     */
    private void refreshView4UserInfo(ProfileDTO dto) {
        if (null == dto) {
            return;
        }

        String avatarUrl = dto.getAvatar();
        if (!TextUtils.isEmpty(avatarUrl)) {
            Picasso.with(getActivity()).load(avatarUrl)
                    .fit().placeholder(R.drawable.ic_avatar).
                    error(R.drawable.ic_avatar).centerCrop().into(avatar);
        } else {
            avatar.setImageResource(R.drawable.ic_avatar);
        }

        if (1 == dto.getSex()) {
            genderIv.setImageResource(R.drawable.ic_gender_male);
        } else {
            genderIv.setImageResource(R.drawable.ic_gender_female);
        }

        String address = dto.getLocation();
        if (!TextUtils.isEmpty(address)) {
            location.setText(address);
        }

        this.fansValueTv.setText(dto.getFansNum() + "");
        this.followValue.setText(dto.getFollowNum() + "");
        this.medalValueTv.setText(dto.getMedalNum() + "");

        nickName = dto.getNickname();
        refreshTitle(dto.getNickname(), dto.getRemarks());

        if (app.isMapStyleEnabled()) {
            if (dto.getSameNum() <= 0) {
                sameGridCountTv.setVisibility(View.GONE);
            } else {
                sameGridCountTv.setVisibility(View.VISIBLE);
                if (isAdded())
                    sameGridCountTv.setText(String.format(getResources().getString(R.string.profile_fragment_same_grid_count), dto.getSameNum()));
            }
        }

        this.userIdTv.setText("ID:" + dto.getSpeedxId());

        if (!TextUtils.isEmpty(dto.getClubId())) {
            this.clubDesc.setText(dto.getClubName());
            clubDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right_icon, 0);
        } else {
            this.clubDesc.setText(R.string.did_not_join);
            clubDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        this.status = dto.getFollowStatu();
        this.refreshButton();

    }

    /**
     * 获取用户骑行数据
     *
     * @param userId userId
     */
    private void fetchUserDetail(final String userId) {
        if (TextUtils.isEmpty(userId))
            return;

        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, UserDetailDTO>() {

                    @Override
                    protected UserDetailDTO doInBackground(String... params) {
                        try {
                            return userManager.getUserDetailByUserId(params[0]);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(UserDetailDTO dto) {
                        if (null == dto) {
                            return;
                        }

                        final double td = dto.getTotalDistance();

                        if (td <= 0) {
                            totalDistanceValue.setText("0.00");
                        } else {
                            if (LocaleManager.isDisplayKM(ProfileFragment.this.getActivity())) {
                                totalDistanceValue.setText(String.format("%.2f", td / 1000));
                            } else {
                                totalDistanceValue.setText(String.format("%.2f",
                                        LocaleManager.kilometreToMile(td / 1000)));
                            }
                        }

                        final long tet = dto.getTotalElapsedTime();
                        if (tet <= 0) {
                            totalElapsedTimeValue.setText("00:00");
                        } else {
                            final long h = tet / 3600, m = (tet % 3600) / 60;
                            totalElapsedTimeValue.setText(String.format("%02d:%02d", h, m));
                            if (null != profile) {
                                profile.setTotalElapsedTime(tet);
                                profile.setTotalDistance(td);
                            }
                        }

                        final long lat = dto.getLatestActivityTime();
                        long currentTime = System.currentTimeMillis();
                        long diffTime = Math.abs(currentTime - lat);

                        if (lat <= 0) {
                            detailedActivitiesDesciption
                                    .setVisibility(View.GONE);
                        } else if (diffTime < 5000 * 60) {
                            detailedActivitiesDesciption.setText(R.string.feedback_activity_just_now);
                        } else {
                            final CharSequence span = DateUtils
                                    .getRelativeTimeSpanString(lat);
                            detailedActivitiesDesciption.setText(span);
                        }

                        if (dto.getTotalCount() > 0) {
                            totalTimesValue.setText(String.format("%d",
                                    dto.getTotalCount()));
                        }

                        if (dto.getTotalCalories() > 0) {
                            totalCaloriesValue.setText(String.format("%d",
                                    Math.round(dto.getTotalCalories())));
                        }
                    }

                }, userId);
    }

    /**
     * 刷新昵称
     *
     * @param nickName nickName
     * @param remarks  remarks
     */
    private void refreshTitle(String nickName, String remarks) {
        if (!TextUtils.isEmpty(remarks)) {
            this.remarkTv.setText(remarks);
            if (!TextUtils.isEmpty(nickName)) {
                String showName = "(" + nickName + ")";
                if (getActivity() != null) {
                    this.nickNameTv.setText(showName);
                }
            }
        } else {
            if (!TextUtils.isEmpty(nickName) && getActivity() != null) {
                this.remarkTv.setText(nickName);
            }
        }
    }

    @Override
    public void onLoadLocalProfile(final ProfileDTO localProfile) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshView4UserInfo(localProfile);
            }
        });

    }

    class UpdateRemarksDialogClickListener implements AddFriendRemarksDialog.OnMessageDialogClickListener {

        @Override
        public void onMessageDialogClickOk(String text) {
            updateSocialInfo(text);
            addfriendremarksDialog.dismiss();
        }
    }

    /**
     * 取消关注提示窗
     */
    private void showUnfollowDialog() {
        final String userId = getUserId();
        if (TextUtils.isEmpty(userId) || this.isLoginUser) {
            return;
        }

        final MaterialDialog dialog = new MaterialDialog(getActivity());
        dialog.setMessage(R.string.msg_unfollow_prompt_dialog);
        dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollow(userId);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 修改备注
     *
     * @param remarks remarks
     */
    private void updateSocialInfo(final String remarks) {
        if (TextUtils.isEmpty(getUserId()) || TextUtils.isEmpty(remarks)) {
            return;
        }

        this.loadingDialog = new LoadingDialog(getActivity(), null, true);
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                if (null != loadingDialog) {
                    loadingDialog.show();
                }
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                return friendManager.updateSocialInfo(getUserId(), remarks);
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                }
                if (result == null)
                    return;
                if (result.optInt("code") == 0) {
                    //修改成功
//                    updateUserRemarks.updateUserRemarksSuccess(result.optJSONObject("result").optString("remarks"));
                    refreshTitle(nickName, result.optJSONObject("result").optString("remarks"));

                    //通知粉丝列表页更新
                    RxBus.getDefault().post(new ProfileEvent(getUserId(), result.optJSONObject("result").optString("remarks")));
                } else {
                    Toasts.showOnUiThread(getActivity(), result.optString("message"));
                }
            }
        });
    }

    /**
     * 关注
     */
    private void follow() {
        final String userId = getUserId();
        if (TextUtils.isEmpty(getUserId()) || this.isLoginUser) {
            return;
        }

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return userManager.follow(userId);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    Toasts.show(getActivity(), R.string.lable_follow_success_msg);
                    int fansNum = Integer.valueOf(fansValueTv.getText().toString());
                    fansValueTv.setText(String.valueOf(fansNum + 1));
                    status = status + 2;
                    refreshButton();
                }
            }
        });
    }

    /**
     * 取消关注
     */
    private void unfollow(final String userId) {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return userManager.unfollow(userId);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    Toasts.show(getActivity(), R.string.lable_unfollow_success_msg);
                    int fansNum = Integer.valueOf(fansValueTv.getText().toString());
                    if (fansNum > 0) {
                        fansValueTv.setText(String.valueOf(fansNum - 1));
                    }
                    status = status - 2;
                    refreshButton();
                }
            }
        });
    }

    /**
     * 获取个人月排行柱状图
     */
    private void fetchUserDiagram() {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, HistogramDTO>() {
            @Override
            protected HistogramDTO doInBackground(String... params) {
                return userManager.getDiagram(0, getUserId(), 30);
            }

            @Override
            protected void onPostExecute(HistogramDTO histogramDTO) {
                userHistogramView.setHistogramDTO(histogramDTO);
            }
        });
    }

    /**
     * 检查是否开启通知栏监听
     */
    private boolean isNotificationListenerEnabled() {
        String pkgName = getActivity().getPackageName();
        final String flat = Settings.Secure.getString(getActivity().getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 我的中控页面跳转分发
     */
    private void startNotificationListener() {
        boolean isListener = isNotificationListenerEnabled();
        if (isListener) {// 通知栏已开启
            this.startSpeedForceActivity();
            return;
        }

        // 系统设置的通知使用权是关闭的
        if (!this.userSp.contains(BLE.PREF_BLE_MESSAGE_ON_KEY) || this.userSp.getInt(BLE.PREF_BLE_MESSAGE_ON_KEY, 0) == 1) {
            // 如果是第一次进入中控页面或者中控的消息通知是到开的，则再次提示用户开启通知栏使用权
            try {
                Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intent, RESULT_NOTIFICATION_CODE);
            } catch (ActivityNotFoundException e) {
                this.startSpeedForceActivity();
                logger.error("跳转到系统设置通知使用权页面错误, " + e);
            }
            return;
        }

        this.startSpeedForceActivity();
    }

    /**
     * 搜索并连接中控
     */
    private void startSpeedForceActivity() {
        final BleManager bleManager = new BleManager(getActivity());
        List<BleDevice> devices = bleManager.getBleDevices();
        if (null != devices && devices.size() > 0) {
            startActivity(new Intent(getActivity(), SpeedForceActivity.class));
        } else {
            startActivity(new Intent(getActivity(), DiscoveryActivity.class));
        }
        SpeedxAnalytics.onEvent(getActivity(), "查看我的中控", null);
    }

    public static class ProfileEvent {

        public String userId;

        public String mark;

        public ProfileEvent(String userId, String mark) {

            this.userId = userId;
            this.mark = mark;

        }

    }
}
