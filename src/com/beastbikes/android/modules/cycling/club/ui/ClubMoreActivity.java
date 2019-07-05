package com.beastbikes.android.modules.cycling.club.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.sharepopupwindow.CommonSharePopupWindow;
import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareLinkDTO;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

@Alias("俱乐部管理")
@LayoutResource(R.layout.activity_club_more)
public class ClubMoreActivity extends SessionFragmentActivity implements
        OnClickListener, Constants {

    /*
     * int level = 128为manager，其它为成员
     */
    public static final String EXTRA_CLUB_LEVLE = "level";
    public static final String EXTRA_CLUB_NOTICE = "notice";
    public static final int REQ_SELECT_MEMBER = 1009;

    @IdResource(R.id.activity_club_manager_items)
    private LinearLayout managerItems;

    @IdResource(R.id.activity_club_manager_history_notice)
    private ViewGroup historyVG;
    private TextView historyTv;
    private ImageView historyIcon;
    private TextView historyDot;
    private TextView historyNoTextDot;

    @IdResource(R.id.activity_club_manager_invite)
    private ViewGroup inviteVG;
    private TextView inviteTv;
    private ImageView inviteIcon;
    private TextView inviteDot;

    @IdResource(R.id.activity_club_manager_apply)
    private ViewGroup applyVG;
    private TextView applyTv;
    private ImageView applyIcon;
    private TextView applyDot;

    @IdResource(R.id.activity_club_manager_apply_view)
    private View applyView;

    @IdResource(R.id.activity_club_manager_member)
    private ViewGroup memberVG;
    private TextView memberTv;
    private ImageView memberIcon;
    private TextView memberDot;

    @IdResource(R.id.activity_club_manager_member_view)
    private View memberView;

    @IdResource(R.id.activity_club_manager_info_setting)
    private ViewGroup settingVG;
    private TextView settingTv;
    private ImageView settingIcon;
    private TextView settingDot;

    @IdResource(R.id.activity_club_manager_info_setting_view)
    private View settingView;

    @IdResource(R.id.activity_club_level)
    private ViewGroup levelVG;
    private TextView levelTv;
    private ImageView levelIcon;
    private TextView levelDot;

    @IdResource(R.id.activity_club_manager_ablum)
    private ViewGroup ablumVG;
    private TextView ablumTv;
    private ImageView ablumIcon;
    private TextView ablumDot;

    @IdResource(R.id.activity_club_manager_transfer_ll)
    private ViewGroup transferLL;

    @IdResource(R.id.activity_club_manager_transfer)
    private ViewGroup transferVG;
    private TextView transferTv;
    private ImageView transferIcon;
    private TextView transferDot;

    @IdResource(R.id.activity_club_manager_quit_club)
    private ViewGroup quitVg;

    private View view;

    private ClubManager manager;
    private SharedPreferences sp;
    private CommonSharePopupWindow share;

    private String clubId;

    private String clubName;

    private String notice = "";

    private int level = 0;
    private ClubInfoCompact clubInfoCompact;
    private LoadingDialog loadingDialog;
    private CommonShareLinkDTO commonShareLinkDTO;
    private String title = "俱乐部招人啦！！！";
    private String iconUrl = "http://img.wdjimg.com/mms/icon/v1/0/02/d7f68ce13acfe90d00b7527b2ec57020_256_256.png";
    private String targetUrl;
    private String desc = "我在野兽骑行找到了组织～快加入我们俱乐部一起愉快的玩耍吧！";


    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.manager = new ClubManager(this);
        this.sp = getSharedPreferences(getUserId(), 0);
        this.inviteVG.setOnClickListener(this);
        this.inviteTv = (TextView) this.inviteVG
                .findViewById(R.id.universal_item_lable);
        this.inviteTv.setText(R.string.activity_club_manager_item_invite);
        this.inviteIcon = (ImageView) this.inviteVG
                .findViewById(R.id.universal_item_icon);
        this.inviteIcon.setImageResource(R.drawable.ic_club_manager_invite);
        this.inviteDot = (TextView) this.inviteVG
                .findViewById(R.id.universal_item_dot);

        this.applyVG.setOnClickListener(this);
        this.applyTv = (TextView) this.applyVG
                .findViewById(R.id.universal_item_lable);
        this.applyTv.setText(R.string.activity_club_manager_item_apply);
        this.applyIcon = (ImageView) this.applyVG
                .findViewById(R.id.universal_item_icon);
        this.applyIcon.setImageResource(R.drawable.ic_club_manager_apply);
        this.applyDot = (TextView) this.applyVG
                .findViewById(R.id.universal_item_dot);
        this.applyDot.setVisibility(View.GONE);

        this.memberVG.setOnClickListener(this);
        this.memberTv = (TextView) this.memberVG
                .findViewById(R.id.universal_item_lable);
        this.memberTv.setText(R.string.activity_club_manager_item_member);
        this.memberIcon = (ImageView) this.memberVG
                .findViewById(R.id.universal_item_icon);
        this.memberDot = (TextView) this.memberVG
                .findViewById(R.id.universal_item_dot);
        this.memberDot.setVisibility(View.GONE);
        this.memberIcon.setImageResource(R.drawable.ic_club_manager_mumber);

        this.settingVG.setOnClickListener(this);
        this.settingTv = (TextView) this.settingVG
                .findViewById(R.id.universal_item_lable);
        this.settingTv.setText(R.string.activity_club_manager_item_setting);
        this.settingIcon = (ImageView) this.settingVG
                .findViewById(R.id.universal_item_icon);
        this.settingIcon.setImageResource(R.drawable.ic_club_manager_setting);
        this.settingDot = (TextView) this.settingVG
                .findViewById(R.id.universal_item_dot);
        this.settingDot.setVisibility(View.GONE);


        this.levelVG.setOnClickListener(this);
        this.levelTv = (TextView) this.levelVG
                .findViewById(R.id.universal_item_lable);
        this.levelTv.setText(R.string.clubfeed_level);
        this.levelIcon = (ImageView) this.levelVG
                .findViewById(R.id.universal_item_icon);
        this.levelIcon.setImageResource(R.drawable.ic_club_level);
        this.levelDot = (TextView) this.levelVG
                .findViewById(R.id.universal_item_dot);
        this.levelDot.setVisibility(View.GONE);

        this.ablumVG.setOnClickListener(this);
        this.ablumTv = (TextView) this.ablumVG
                .findViewById(R.id.universal_item_lable);
        this.ablumTv.setText(R.string.club_ablum);
        this.ablumIcon = (ImageView) this.ablumVG
                .findViewById(R.id.universal_item_icon);
        this.ablumIcon.setImageResource(R.drawable.ic_club_manager_ablum);
        this.ablumDot = (TextView) this.ablumVG
                .findViewById(R.id.universal_item_dot);
        this.ablumDot.setVisibility(View.GONE);

        transferLL.setVisibility(View.GONE);
        this.transferVG.setOnClickListener(this);
        this.transferTv = (TextView) transferVG.findViewById(R.id.universal_item_lable);
        this.transferTv.setText(getResources().getString(R.string.activity_club_manager_item_transfer));
        this.transferIcon = (ImageView) transferVG.findViewById(R.id.universal_item_icon);
        this.transferIcon.setImageResource(R.drawable.ic_transfer);
        this.transferDot = (TextView) transferVG.findViewById(R.id.universal_item_dot);
        this.transferDot.setVisibility(View.GONE);

        this.quitVg.setOnClickListener(this);

        this.historyTv = (TextView) historyVG.findViewById(R.id.universal_item_lable);
        this.historyIcon = (ImageView) historyVG.findViewById(R.id.universal_item_icon);
        this.historyDot = (TextView) historyVG.findViewById(R.id.universal_item_dot);
        this.historyNoTextDot = (TextView) historyVG.findViewById(R.id.universal_item_no_text_dot);
        this.historyTv.setText(R.string.club_notice_history);
        this.historyIcon.setImageResource(R.drawable.ic_club_manager_history);
        this.historyVG.setOnClickListener(this);

        final Intent intent = getIntent();
//        if (null != intent
//                && intent.hasExtra(ClubFeedInfoActivity.EXTRA_CLUB_ID)) {
//            this.clubId = intent
//                    .getStringExtra(ClubFeedInfoActivity.EXTRA_CLUB_ID);
//            this.notice = intent.getStringExtra(ClubFeedInfoActivity.EXTRA_CLUB_NOTICE);
//        }
        if (intent != null) {
            this.clubId = intent
                    .getStringExtra(ClubFeedInfoFrag.EXTRA_CLUB_ID);
            clubInfoCompact = (ClubInfoCompact) getIntent().getSerializableExtra(ClubMemberManagerActivity.EXTRA_CLUB_INFO);
            this.notice = clubInfoCompact.getNotice();
            targetUrl = "https://www.speedx.com/app/club/shareClub.html?clubId=" + clubInfoCompact.getObjectId();
        }

        this.view = LayoutInflater.from(this).inflate(
                R.layout.browser_activity, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.refreshDot();
        this.fetchMyClub();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SELECT_MEMBER) {
            if (data != null) {
                String memberId = data.getStringExtra(ClubMemberManagerActivity.EXTRA_CLUB_MEMBER);
                String memberName = data.getStringExtra(ClubMemberManagerActivity.EXTRA_CLUB_MEMBER_NAME);
                int isQuit = data.getIntExtra(ClubMemberManagerActivity.EXTRA_IS_QUIT, 0);
                doTransferClub(memberId, memberName, isQuit);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_club_manager_invite: {
                SpeedxAnalytics.onEvent(this, "邀请好友成功分享到外网","invite_friends");
                this.showShareWindow();
            }
            break;
            case R.id.activity_club_manager_apply: {
                final Intent intent = new Intent(this, ApplyManagerActivity.class);
                this.startActivity(intent);
            }
            break;
            case R.id.activity_club_manager_member: {
                final Intent intent = new Intent(this, ClubMemberManagerActivity.class);
                intent.putExtra(ClubFeedInfoFrag.EXTRA_CLUB_ID, this.clubId);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable(ClubMemberManagerActivity.EXTRA_CLUB_INFO, clubInfoCompact);
                intent.putExtras(bundle2);
                this.startActivity(intent);
            }
            break;
            case R.id.activity_club_manager_info_setting: {
                final Intent intent = new Intent(this,
                        ClubInfoSettingActivity.class);
                intent.putExtra(ClubFeedInfoFrag.EXTRA_CLUB_ID, this.clubId);
                this.startActivity(intent);
            }
            break;
            case R.id.activity_club_manager_quit_club:
                SpeedxAnalytics.onEvent(this, "", "click_exit_club");
                this.getClubInfo(clubInfoCompact.getObjectId());
//                this.quitClub();
                break;
            case R.id.activity_club_manager_history_notice:
                final Intent intent = new Intent(this,
                        ClubHistoryNoticeActivity.class);
                intent.putExtra(ClubFeedInfoFrag.EXTRA_CLUB_ID, this.clubId);
                intent.putExtra(ClubMoreActivity.EXTRA_CLUB_LEVLE, this.level);
                if (!TextUtils.isEmpty(notice)) {
                    intent.putExtra(ClubMoreActivity.EXTRA_CLUB_NOTICE, notice);
                }
                this.startActivity(intent);

                this.sp.edit().putInt(PUSH.PREF_KEY.DOT_CLUB_MORE, 0).commit();
                break;
            case R.id.activity_club_manager_transfer:
                if (clubInfoCompact.getMembers() > 1) {
                    getClubTransStatus(0);
                } else {
                    final MaterialDialog dialog = new MaterialDialog(this);
                    dialog.setMessage(R.string.club_member_transfer_check_tip);
                    dialog.setPositiveButton(R.string.club_release_activites_dialog_ok, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    }).show();
                }

                break;
            case R.id.activity_club_level: {
                final Intent it = new Intent(this,
                        ClubLevelActivity.class);
                it.putExtra(ClubLevelActivity.EXTRA_CLUBINFO, clubInfoCompact);
                this.startActivity(it);
                break;
            }
            case R.id.activity_club_manager_ablum: {
                SpeedxAnalytics.onEvent(this, "俱乐部相册",null);
                if (AVUser.getCurrentUser() == null)
                    return;
                if (!AVUser.getCurrentUser().getClubId().equals(clubInfoCompact.getObjectId())) {
                    Toasts.show(this, getResources().getString(R.string.cannotlookbeforejoin));
                    break;
                }
                final Intent albumIntent = new Intent(this, ClubGalleryActivity.class);
                albumIntent.putExtra(ClubGalleryActivity.EXTRA_CLUB_ID, clubInfoCompact.getObjectId());
                albumIntent.putExtra(ClubGalleryActivity.EXTRA_PHOTO_MAX_COUNT, clubInfoCompact.getMaxPhotoNum());
                albumIntent.putExtra(ClubGalleryActivity.EXTRA_PHOTO_COUNT, clubInfoCompact.getCurPhotoNum());
                albumIntent.putExtra(ClubGalleryActivity.EXTRA_CLUB_STATUS, clubInfoCompact.getStatus());
                albumIntent.putExtra(ClubGalleryActivity.EXTRA_CLUB_MANAGER_ID, clubInfoCompact.getManagerId());
                albumIntent.putExtra(ClubMoreActivity.EXTRA_CLUB_LEVLE, clubInfoCompact.getLevel());
                this.startActivityForResult(albumIntent, ClubFeedInfoFrag.REQ_BACK_TO_REFRESH);
            }
            break;

            default:
                break;
        }
    }

    /**
     * 刷新小红点
     */
    private void refreshDot() {
        int applyCount = this.sp.getInt(PUSH.PREF_KEY.DOT_CLUB_MORE, 0);
        if (applyCount > 0) {
            this.applyDot.setVisibility(View.VISIBLE);
            this.applyDot.setText(String.valueOf(applyCount));
        } else {
            this.applyDot.setVisibility(View.GONE);
        }

//        int share = this.sp.getInt(PUSH.PREF_KEY.CLUB_NOTICE, 0);
//        if (share > 0) {
//            this.historyNoTextDot.setVisibility(View.VISIBLE);
//            this.historyNoTextDot.setBackgroundResource(R.drawable.red_dot_bg);
//        } else {
//            this.historyNoTextDot.setVisibility(View.GONE);
//        }
    }

    private void fetchMyClub() {
        final String userId = this.getUserId();
        this.getAsyncTaskQueue().add(
                new AsyncTask<Void, Void, ClubInfoCompact>() {

                    @Override
                    protected ClubInfoCompact doInBackground(Void... params) {
                        if (null != manager) {
                            try {
                                return manager.getMyClub(userId);
                            } catch (BusinessException e) {
                                return null;
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(ClubInfoCompact result) {
                        if (null != result) {
                            clubName = result.getName();
                            if (!TextUtils.isEmpty(clubName)) {
                                title = getString(R.string.club) + clubName + getString(R.string.recruit);
                            }
                            level = result.getLevel();
                            if (level != 128) {
                                applyVG.setVisibility(View.GONE);
                                memberVG.setVisibility(View.GONE);
                                settingVG.setVisibility(View.GONE);
                                settingView.setVisibility(View.GONE);
                                memberView.setVisibility(View.GONE);
                                applyView.setVisibility(View.GONE);
                                transferLL.setVisibility(View.GONE);

                            } else {
                                managerItems.setVisibility(View.VISIBLE);
                                transferLL.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                }

        );
    }

    private int loadingMsg;
    private int quitSuccessTip;

    private void quitClub() {
        boolean isManager = level == 128;
        /*
         * 自己不是管理员可以退出，是管理员成员小于1也可以退
         */
        boolean isQuit = !(isManager && clubInfoCompact.getMembers() > 1);
        int message = R.string.activity_club_manager_dialog_title;
        loadingMsg = R.string.club_info_waiting;
        quitSuccessTip = R.string.activity_club_manager_quit_club_success;
        if (isManager) {

            loadingMsg = R.string.club_quit_waitting;
            quitSuccessTip = R.string.activity_club_manager_quit_club_success1;
            if (clubInfoCompact.getMembers() > 1) {
                message = R.string.activity_club_manager_quit_tip;
            }
        }

        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(message);
        if (isQuit) {
            dialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    postClubQuit(loadingMsg, quitSuccessTip);
                }
            }).setNegativeButton(R.string.cancel, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            }).show();
        }
        else {
            dialog.setNegativeButton(R.string.activity_alert_dialog_text_ok, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            }).show();
        }
    }

    /**
     * 获取俱乐部详情
     *
     * @param clubId
     */
    private void getClubInfo(final String clubId) {
        if (TextUtils.isEmpty(clubId)) {
            return;
        }

        getAsyncTaskQueue().add(
                new AsyncTask<String, Void, ClubInfoCompact>() {

                    @Override
                    protected ClubInfoCompact doInBackground(String... params) {
                        try {
                            return manager.getClubInfo(params[0]);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(ClubInfoCompact result) {
                        if (null == result)
                            return;

                        clubInfoCompact = result;

                        quitClub();
                    }

                }, clubId);
    }

    /**
     * 显示分享
     */
    private void showShareWindow() {
        if (null == this.share) {
            commonShareLinkDTO = new CommonShareLinkDTO(iconUrl, title, targetUrl, desc, desc + getString(R.string.weibo_offical), getString(R.string.club_invate_wechat));
            this.share = new CommonSharePopupWindow(ClubMoreActivity.this,
                    commonShareLinkDTO, "");
            this.share.setShareTitle(getResources().getString(R.string.activity_club_manager_invite_share_title));
        }
        this.share.showAtLocation(this.view, Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void postClubQuit(final int loadingMsg, final int quitSuccessTip) {

        final ClubMoreActivity ctx = ClubMoreActivity.this;
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadingDialog = new LoadingDialog(ctx, getString(loadingMsg), true);
                loadingDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return manager.postCmdClub(1, clubId, null, null);
                } catch (BusinessException e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (result.booleanValue()) {

                    RongIMClient.getInstance().clearConversations(new RongIMClient.ResultCallback() {
                        @Override
                        public void onSuccess(Object o) {
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                        }
                    }, Conversation.ConversationType.GROUP);

                    Toasts.show(ctx, R.string.activity_club_manager_quit_club_success);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toasts.show(ctx, R.string.activity_club_manager_quit_club_failed);
                }
            }
        });
    }

    private void getClubTransStatus(final int isQuit) {

        final ClubMoreActivity ctx = ClubMoreActivity.this;
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = new LoadingDialog(ctx, getString(R.string.club_info_waiting), true);
                loadingDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    // 0代表删除成员
                    if (null == manager) {
                        manager = new ClubManager(ClubMoreActivity.this);
                    }
                    return manager.getClubTransStatus();
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (!TextUtils.isEmpty(result)) {
                    showClubTransferDialog(result);
                } else {
                    Intent it = new Intent(ClubMoreActivity.this, ClubMemberManagerActivity.class);
                    it.putExtra(ClubMemberManagerActivity.EXTRA_CLUB_MEMBER_SELECT_MODE, true);
                    it.putExtra(ClubMemberManagerActivity.EXTRA_CLUB_ID, clubInfoCompact.getObjectId());
                    it.putExtra(ClubMemberManagerActivity.EXTRA_CLUB_INFO, clubInfoCompact);
                    it.putExtra(ClubMemberManagerActivity.EXTRA_IS_QUIT, isQuit);
                    startActivityForResult(it, REQ_SELECT_MEMBER);
                }
            }
        });
    }

    private void doTransferClub(final String memberId, final String memberName, final int isQuit) {

        final ClubMoreActivity ctx = ClubMoreActivity.this;
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = new LoadingDialog(ctx, getString(R.string.club_info_waiting), true);
                loadingDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return manager.transferClub(memberId, isQuit);
                } catch (BusinessException e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        });
    }

    private void showClubTransferDialog(String name) {
        if (TextUtils.isEmpty(name))
            return;
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setMessage(String.format(getString(R.string.club_more_activity_transfer_message), name));
        dialog.setTitle(R.string.club_more_activity_transfer_title);
        dialog.setPositiveButton(R.string.club_transfer_activity_cancel_transfer, new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                doCancelTransfer();

            }
        }).setNegativeButton(R.string.club_transfer_activity_tip_transfer, new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                doTipTransfer();
            }
        }).show();
    }

    private void doTipTransfer() {
        final ClubMoreActivity ctx = ClubMoreActivity.this;
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = new LoadingDialog(ctx, getString(R.string.club_info_waiting), true);
                loadingDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return manager.sendClubTransNotify();
                } catch (BusinessException e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                if (result) {
                    Toasts.show(ctx, R.string.club_transfer_activity_tip_transfer_ok);
                }
            }
        });
    }

    private void doCancelTransfer() {
        final ClubMoreActivity ctx = ClubMoreActivity.this;
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = new LoadingDialog(ctx, getString(R.string.club_info_waiting), true);
                loadingDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return manager.cancelClubTrans();
                } catch (BusinessException e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (result) {
                    Toasts.show(ctx, R.string.club_transfer_activity_cancel_transfer_ok);
                } else {
                    Toasts.show(ctx, R.string.club_transfer_activity_cancel_transfer_fail);
                }
            }
        });
    }
}
