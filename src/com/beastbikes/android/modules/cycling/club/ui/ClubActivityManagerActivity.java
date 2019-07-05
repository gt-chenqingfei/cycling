package com.beastbikes.android.modules.cycling.club.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubActivityManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubActUserList;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityInfo;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityLikeRead;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityListDTO;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityMember;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityUser;
import com.beastbikes.android.modules.cycling.task.biz.TaskManager;
import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.sharepopupwindow.CommonSharePopupWindow;
import com.beastbikes.android.widget.sharepopupwindow.dto.CommonShareLinkDTO;
import com.beastbikes.android.zxing.CaptureActivity;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 16/3/11.
 */
@Alias("俱乐部管理活动页")
@LayoutResource(R.layout.activity_club_act_manager)
public class ClubActivityManagerActivity extends SessionFragmentActivity implements View.OnClickListener {

    public static final int SCAN_CODE = 1;
    public static final int REQUEST_EDIT_ACTIVITY_CODE = 13;
    public static final String EXTRA_DATA = "data";

    private Logger logger = LoggerFactory.getLogger(ClubActivityManagerActivity.class);

    @IdResource(R.id.activity_club_act_manager_like_title)
    private TextView actLikeTitle;

    @IdResource(R.id.activity_club_act_manager_read_title)
    private TextView actReadTitle;

    private ClubActivityManager clubActivityManager;
    private ClubActivityListDTO clubActivityInfo;

    private int page = 1;
    private int count = 20;//第一次拉取20个

    @IdResource(R.id.activity_club_act_manager_member)
    private ImageView memberIV;

    @IdResource(R.id.activity_club_act_manager_member_layout1)
    private ViewGroup memberLayout1;
    private ImageView memberAvater1;

    @IdResource(R.id.activity_club_act_manager_member_layout2)
    private ViewGroup memberLayout2;
    private ImageView memberAvater2;

    @IdResource(R.id.activity_club_act_manager_member_layout3)
    private ViewGroup memberLayout3;
    private ImageView memberAvater3;

    @IdResource(R.id.activity_club_act_manager_member_layout4)
    private ViewGroup memberLayout4;
    private ImageView memberAvater4;

    @IdResource(R.id.activity_club_act_manager_member_layout5)
    private ViewGroup memberLayout5;
    private ImageView memberAvater5;

    @IdResource(R.id.activity_club_act_manager_member_layout6)
    private ViewGroup memberLayout6;
    private ImageView memberAvater6;

    private List<ImageView> avaters;

    @IdResource(R.id.activity_club_act_manager_signin)
    private ViewGroup signInVG;
    private TextView signInTV;
    private ImageView signInIV;
    private TextView signigTV;

    @IdResource(R.id.activity_club_act_manager_batch)
    private ViewGroup batchVG;
    private TextView batchTV;
    private ImageView batchIV;

    @IdResource(R.id.activity_club_act_manager_check)
    private ViewGroup checkVG;
    private TextView checkTV;
    private ImageView checkIV;

    @IdResource(R.id.activity_club_act_manager_edit)
    private ViewGroup editVG;
    private TextView editTV;
    private ImageView editIV;

    @IdResource(R.id.activity_club_act_manager_repost)
    private ViewGroup repostVG;
    private TextView repostTV;
    private ImageView repostIV;

    @IdResource(R.id.activity_club_act_manager_cancel)
    private ViewGroup canvelVG;
    private TextView canvelTV;
    private ImageView canvelIV;

    @IdResource(R.id.activity_club_act_manager_member_count)
    private TextView memberCountTV;

    @IdResource(R.id.activity_club_act_manager)
    private View view;

    @IdResource(R.id.activity_club_act_manager_invate_rl)
    private RelativeLayout invateRL;

    @IdResource(R.id.activity_club_act_manager_member_layout)
    private LinearLayout memberLayout;

    private CommonSharePopupWindow commonSharePopupWindow;

    private TaskManager taskManager;

    private LoadingDialog loadingDialog;

    private String title;
    private String targetUrl;
    private String iconUrl;
    private String desc;

    private boolean isEdit;

    private List<ClubActivityUser> clubActivityUsers;

    private CommonShareLinkDTO commonShareLinkDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        if (intent == null)
            return;
        memberAvater1 = (ImageView) memberLayout1.findViewById(R.id.activity_club_act_manager_member_avater);
        memberAvater2 = (ImageView) memberLayout2.findViewById(R.id.activity_club_act_manager_member_avater);
        memberAvater3 = (ImageView) memberLayout3.findViewById(R.id.activity_club_act_manager_member_avater);
        memberAvater4 = (ImageView) memberLayout4.findViewById(R.id.activity_club_act_manager_member_avater);
        memberAvater5 = (ImageView) memberLayout5.findViewById(R.id.activity_club_act_manager_member_avater);
        memberAvater6 = (ImageView) memberLayout6.findViewById(R.id.activity_club_act_manager_member_avater);
        avaters = new ArrayList<>();
        avaters.add(memberAvater1);
        avaters.add(memberAvater2);
        avaters.add(memberAvater3);
        avaters.add(memberAvater4);
        avaters.add(memberAvater5);
        avaters.add(memberAvater6);

        signInTV = (TextView) signInVG.findViewById(R.id.activity_club_act_manager_item_title);
        signInIV = (ImageView) signInVG.findViewById(R.id.activity_club_act_manager_item_icon);
        batchTV = (TextView) batchVG.findViewById(R.id.activity_club_act_manager_item_title);
        batchIV = (ImageView) batchVG.findViewById(R.id.activity_club_act_manager_item_icon);
        checkTV = (TextView) checkVG.findViewById(R.id.activity_club_act_manager_item_title);
        checkIV = (ImageView) checkVG.findViewById(R.id.activity_club_act_manager_item_icon);
        editTV = (TextView) editVG.findViewById(R.id.activity_club_act_manager_item_title);
        editIV = (ImageView) editVG.findViewById(R.id.activity_club_act_manager_item_icon);
        repostTV = (TextView) repostVG.findViewById(R.id.activity_club_act_manager_item_title);
        repostIV = (ImageView) repostVG.findViewById(R.id.activity_club_act_manager_item_icon);
        canvelTV = (TextView) canvelVG.findViewById(R.id.activity_club_act_manager_item_title);
        canvelIV = (ImageView) canvelVG.findViewById(R.id.activity_club_act_manager_item_icon);
        signigTV = (TextView) signInVG.findViewById(R.id.activity_club_act_manager_item_count);
        signigTV.setVisibility(View.VISIBLE);

        signInTV.setText(getResources().getString(R.string.activity_sign_in));
        batchTV.setText(getResources().getString(R.string.activity_batch_message));
        checkTV.setText(getResources().getString(R.string.check_club_activity));
        editTV.setText(getResources().getString(R.string.edit_club_activity));
        repostTV.setText(getResources().getString(R.string.repost_club_activity));
        canvelTV.setText(getResources().getString(R.string.cancel_club_activity));

        signInIV.setImageResource(R.drawable.ic_club_activity_signin);
        batchIV.setImageResource(R.drawable.ic_club_activity_batch_message);
        checkIV.setImageResource(R.drawable.ic_club_activity_check);
        editIV.setImageResource(R.drawable.ic_club_activity_edit);
        repostIV.setImageResource(R.drawable.ic_club_activity_repost);
        canvelIV.setImageResource(R.drawable.ic_club_activity_cancel);

        signInVG.setOnClickListener(this);
        batchVG.setOnClickListener(this);
        checkVG.setOnClickListener(this);
        editVG.setOnClickListener(this);
        repostVG.setOnClickListener(this);
        canvelVG.setOnClickListener(this);
        invateRL.setOnClickListener(this);
        memberLayout.setOnClickListener(this);
        memberIV.setOnClickListener(this);
        memberCountTV.setOnClickListener(this);

        clubActivityManager = new ClubActivityManager(this);
        taskManager = new TaskManager(this);
        clubActivityInfo = (ClubActivityListDTO) intent.getSerializableExtra(EXTRA_DATA);
        getClubActivityStatisticsByActId();
        clubActivityMemberList();
        refreshActivityStatus();

        signigTV.setText("(" + clubActivityInfo.getSignInCount() +
                "/" + clubActivityInfo.getMembers() + ")");

        if (!LocaleManager.isChineseTimeZone()) {
            signInVG.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCAN_CODE:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("scan_result");
                    if (!TextUtils.isEmpty(result)) {
                        clubActSignIn(result);
                    }
                } else if (resultCode == RESULT_CANCELED) {
//                    Toasts.show(this, getResources().getString(R.string.scan_code_failed));
                }
                break;
            case REQUEST_EDIT_ACTIVITY_CODE:// 编辑资料
                this.isEdit = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void finish() {
        if (this.isEdit) {
            setResult(RESULT_OK, getIntent());
        }
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_club_act_manager_signin:
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, SCAN_CODE);
                break;
            case R.id.activity_club_act_manager_batch:
                showDialog();
                break;
            case R.id.activity_club_act_manager_check:

                if (clubActivityInfo == null)
                    return;
                Uri uri = Uri.parse(ClubActivityInfoBrowserActivity.getActivityUrl(clubActivityInfo.getActId(), this));

                final Intent browserIntent = new Intent(this,
                        ClubActivityInfoBrowserActivity.class);
                browserIntent.setData(uri);
                browserIntent.putExtra(ClubActivityInfoBrowserActivity.EXTRA_ACTIVITY_TYPE, 1);
                browserIntent.putExtra(ClubActivityInfoBrowserActivity.EXTRA_CLUB_ACTIVITY_ID, clubActivityInfo.getActId());
                browserIntent.addCategory(Intent.CATEGORY_DEFAULT);
                browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                browserIntent.setPackage(getPackageName());
                this.startActivity(browserIntent);
                break;
            case R.id.activity_club_act_manager_edit:// 编辑活动
                Intent editIntent = new Intent(this, ClubActivityReleaseActivity.class);
                editIntent.putExtra(ClubActivityReleaseActivity.CLUB_ACTIVITY_MANAGE_TAG,
                        ClubActivityReleaseActivity.EDIT_CLUB_ACTIVITY);
                getActInfo(editIntent);
                break;
            case R.id.activity_club_act_manager_repost:
                Intent repostIntent = new Intent(this, ClubActivityReleaseActivity.class);
                repostIntent.putExtra(ClubActivityReleaseActivity.CLUB_ACTIVITY_MANAGE_TAG,
                        ClubActivityReleaseActivity.REPOST_CLUB_ACTIVITY);
                getActInfo(repostIntent);
                break;
            case R.id.activity_club_act_manager_cancel:
                final MaterialDialog msgDislog = new MaterialDialog(this);
                msgDislog.setMessage(R.string.sure_to_cancel_club_activity);
                msgDislog.setPositiveButton(R.string.activity_alert_dialog_text_ok,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelClubActivity();
                                msgDislog.dismiss();
                            }
                        });
                msgDislog.setNegativeButton(R.string.activity_alert_dialog_text_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDislog.dismiss();
                    }
                });
                msgDislog.show();
                break;
            case R.id.activity_club_act_manager_invate_rl:
                getShareContent();
                break;
            case R.id.activity_club_act_manager_member_layout:
            case R.id.activity_club_act_manager_member:
            case R.id.activity_club_act_manager_member_count:
                Intent intent_userlist = new Intent(this, ClubActUserListActivity.class);
                intent_userlist.putExtra(ClubActUserListActivity.EXTRA_DATA, new ClubActUserList(clubActivityUsers));
                intent_userlist.putExtra(ClubActUserListActivity.CLUB_ACT_ID, clubActivityInfo.getActId());
                intent_userlist.putExtra(ClubActUserListActivity.CLUB_ACT_MEMBERS, clubActivityInfo.getMembers());
                if (clubActivityInfo.getMembers() > count) {
                    intent_userlist.putExtra(ClubActUserListActivity.IS_CLUB_ACT_LIST_CAN_LOAD_MORE, true);
                }
                startActivity(intent_userlist);
                break;
        }
    }

    public void clubActSignIn(final String objectId) {
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                return clubActivityManager.clubActSignIn(objectId, clubActivityInfo.getActId());
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (integer == -1)
                    return;
                signigTV.setText("(" + clubActivityInfo.getSignInCount() +
                        "/" + clubActivityInfo.getMembers() + ")");
            }
        });
    }

    public void cancelClubActivity() {
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                return clubActivityManager.cancelClubActivity(clubActivityInfo.getActId());
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                finish();
            }
        });
    }

    /**
     * 显示分享
     */
    private void showShareWindow(CommonShareLinkDTO commonShareLinkDTO) {
        if (null == this.commonSharePopupWindow)
            this.commonSharePopupWindow = new CommonSharePopupWindow(ClubActivityManagerActivity.this, commonShareLinkDTO, "俱乐部活动");

        this.commonSharePopupWindow.showAtLocation(view, Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public void showDialog() {
        final Dialog dialog = new android.app.AlertDialog.Builder(this).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_club_act_batch_message, null);
        dialog.setContentView(view);
        TextView actTitleTV = (TextView) view.findViewById(R.id.dialog_club_act_batch_message_title);
        TextView actTimeTV = (TextView) view.findViewById(R.id.dialog_club_act_batch_message_time);
        TextView actLocationTV = (TextView) view.findViewById(R.id.dialog_club_act_batch_message_location);
        TextView actCancelTV = (TextView) view.findViewById(R.id.dialog_club_act_batch_message_cancel);
        TextView actSendTV = (TextView) view.findViewById(R.id.dialog_club_act_batch_message_send);
        actTitleTV.setText("[" + clubActivityInfo.getTitle() + "]" + getResources().getString(R.string.batch_message_title));
        actTimeTV.setText(getResources().getString(R.string.time) + DateFormatUtil.formatMDHM(DateFormatUtil.timeFormat2Date(clubActivityInfo.getStartDate())) + "-" + DateFormatUtil.formatMDHM(DateFormatUtil.timeFormat2Date(clubActivityInfo.getEndDate())));
        actLocationTV.setText(getResources().getString(R.string.point_of_convergence) + clubActivityInfo.getMobPlace());
        actCancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });
        actSendTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendClubActSms();
                dialog.dismiss();
            }
        });
    }

    private void sendClubActSms() {
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                if (TextUtils.isEmpty(clubActivityInfo.getActId()))
                    return null;
                return clubActivityManager.sendClubActSms(clubActivityInfo.getActId());
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {

            }
        });
    }

    //获取阅读点赞数量
    private void getClubActivityStatisticsByActId() {
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, ClubActivityLikeRead>() {
            @Override
            protected ClubActivityLikeRead doInBackground(Void... voids) {
                if (TextUtils.isEmpty(clubActivityInfo.getActId()))
                    return null;
                return clubActivityManager.getClubActivityStatisticsByActId(clubActivityInfo.getActId());
            }

            @Override
            protected void onPostExecute(ClubActivityLikeRead clubActivityLikeRead) {
                if (clubActivityLikeRead == null)
                    return;
                actLikeTitle.setText(clubActivityLikeRead.getLike() + "");
                actReadTitle.setText(clubActivityLikeRead.getRead() + "");
            }
        });
    }

    //获取参加者列表
    private void clubActivityMemberList() {
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, ClubActivityMember>() {

            @Override
            protected ClubActivityMember doInBackground(Void... voids) {
                if (TextUtils.isEmpty(clubActivityInfo.getActId()))
                    return null;
                return clubActivityManager.clubActivityMemberList(clubActivityInfo.getActId(), page, count);
            }

            @Override
            protected void onPostExecute(ClubActivityMember clubActivityMember) {
                if (clubActivityMember == null)
                    return;
                clubActivityUsers = clubActivityMember.getClubActivityUsers();
                if (clubActivityUsers != null && clubActivityUsers.size() > 0) {
                    memberCountTV.setText(clubActivityUsers.size() + getResources().getString(R.string.person));
                    for (int i = 0; i < clubActivityUsers.size(); i++) {
                        if (i == 6)
                            break;
                        if (!TextUtils.isEmpty(clubActivityUsers.get(i).getAvatar())) {
                            Picasso.with(ClubActivityManagerActivity.this).load(clubActivityUsers.get(i).getAvatar()).fit().centerCrop().error(R.drawable.ic_avatar).
                                    placeholder(R.drawable.ic_avatar).into(avaters.get(i));
                        } else {
                            avaters.get(i).setImageResource(R.drawable.ic_avatar);
                        }
                    }
                }
            }
        });
    }

    //刷新活动状态
    private void refreshActivityStatus() {
        if (clubActivityInfo == null || clubActivityInfo.getApplyStatus() == ClubActivityListDTO.CLUB_ACTIVITY_STATUS_ONGOING)
            return;
        signInVG.setEnabled(false);
        signInTV.setTextColor(getResources().getColor(R.color.activity_fragment_tab_bar_item_label_selected));
        signigTV.setTextColor(getResources().getColor(R.color.activity_fragment_tab_bar_item_label_selected));
        batchVG.setEnabled(false);
        batchTV.setTextColor(getResources().getColor(R.color.activity_fragment_tab_bar_item_label_selected));
        canvelVG.setEnabled(false);
        canvelTV.setTextColor(getResources().getColor(R.color.activity_fragment_tab_bar_item_label_selected));

        signInIV.setImageResource(R.drawable.ic_club_activity_signin_cancel);
        batchIV.setImageResource(R.drawable.ic_club_activity_batch_message_cancel);
        canvelIV.setImageResource(R.drawable.ic_club_activity_cancel_cancel);
    }

    private void getShareContent() {
        if (TextUtils.isEmpty(clubActivityInfo.getActId()))
            return;
        if (!TextUtils.isEmpty(title)) {
            showShareWindow(commonShareLinkDTO);
            return;
        }

        this.loadingDialog = new LoadingDialog(this, "", false);
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                if (null != loadingDialog) {
                    loadingDialog.show();
                }
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                try {
                    return taskManager.getTaskShareMessage(clubActivityInfo.getActId(), 3,null);
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject json) {
                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                }
                if (null == json) {
                    Toasts.show(ClubActivityManagerActivity.this, R.string.task_get_share_content_error);
                    return;
                }

                int code = json.optInt("code");
                if (code == 0) {
                    JSONObject result = json.optJSONObject("result");
                    title = result.optString("title");
                    desc = result.optString("desc");
                    iconUrl = result.optString("url");
                    targetUrl = result.optString("shareLink");
                    CommonShareLinkDTO commonShareLinkDTO = new CommonShareLinkDTO();
                    commonShareLinkDTO.setTitle(title);
                    commonShareLinkDTO.setDesc(desc);
                    commonShareLinkDTO.setIconUrl(iconUrl);
                    commonShareLinkDTO.setTargetUrl(targetUrl);
                    commonShareLinkDTO.setWechatText(desc);
//                    commonShareLinkDTO.setWeiboText(getResources().getString(R.string.hurry_to_join_club_activity) + title + getResources().getString(R.string.hurry_to_join_club_activity_address) + targetUrl + " " + getResources().getString(R.string.weibo_topic) + " " + getResources().getString(R.string.weibo_offical));
                    commonShareLinkDTO.setWeiboText(desc);
                    showShareWindow(commonShareLinkDTO);
                }

            }

        });
    }


    private void getActInfo(final Intent intent) {
        if (TextUtils.isEmpty(clubActivityInfo.getActId()))
            return;
        this.loadingDialog = new LoadingDialog(this, "", true);
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, ClubActivityInfo>() {

            @Override
            protected void onPreExecute() {
                if (null != loadingDialog) {
                    loadingDialog.show();
                }
            }

            @Override
            protected ClubActivityInfo doInBackground(String... params) {
                try {
                    return clubActivityManager.clubActivityInfo(clubActivityInfo.getActId());
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ClubActivityInfo info) {
                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                }
                if (info != null) {
                    Bundle repostBundle = new Bundle();
                    repostBundle.putSerializable(ClubActivityReleaseActivity.EXTRA_ACT_INFO, info);
                    intent.putExtras(repostBundle);
                    startActivityForResult(intent, REQUEST_EDIT_ACTIVITY_CODE);
                }

            }

        });
    }
}