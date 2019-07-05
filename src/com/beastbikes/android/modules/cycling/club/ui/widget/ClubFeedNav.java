package com.beastbikes.android.modules.cycling.club.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.cycling.club.ui.ClubActivitiesListActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubFeedDetailsActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubFeedInfoFrag;
import com.beastbikes.android.modules.cycling.club.ui.ClubMoreActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubMemberRankActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubMsgActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubMemberManagerActivity;
import com.beastbikes.android.modules.cycling.club.ui.MemberRankingActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubRankActivity;
import com.beastbikes.android.modules.social.im.ui.conversation.GroupSettingActivity;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.framework.ui.android.utils.Toasts;

import io.rong.imkit.RongIM;

/**
 * Created by chenqingfei on 15/12/2.
 */
public class ClubFeedNav extends LinearLayout implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener, Constants {

    private ClubInfoCompact clubInfoCompact;

    private View warning;


    private boolean isMyClub = false;

    private Context context;
    private SharedPreferences userSp;

    private TextView activityDotTv;
    private TextView moreDotTv;
    private TextView feedMsgDotTv;
    private TextView gropChatDotTv;

    public ClubFeedNav(Context context, boolean isMyClub) {
        super(context);
        this.context = context;
        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            return;
        }

        this.userSp = context.getSharedPreferences(user.getObjectId(), 0);
        this.isMyClub = isMyClub;
        LayoutInflater.from(context).inflate(R.layout.clubfeed_nav, this);
        initView();
        this.userSp = context.getSharedPreferences(user.getObjectId(), 0);
        this.userSp.registerOnSharedPreferenceChangeListener(this);
    }

    public void setClubInfoCompact(ClubInfoCompact clubInfoCompact) {
        this.clubInfoCompact = clubInfoCompact;
    }

    private void initView() {

        feedMsgDotTv = (TextView) findViewById(R.id.tv_new_msg);
        warning = findViewById(R.id.tv_tip);

        warning.setOnClickListener(this);
        feedMsgDotTv.setOnClickListener(this);

        gropChatDotTv = (TextView) findViewById(R.id.dot_chat);
        findViewById(R.id.nav_group_chat).setOnClickListener(this);
        findViewById(R.id.nav_activity).setOnClickListener(this);
        findViewById(R.id.nav_member).setOnClickListener(this);
        findViewById(R.id.nav_scroe).setOnClickListener(this);
        findViewById(R.id.nav_more).setOnClickListener(this);

        this.activityDotTv = (TextView) findViewById(R.id.dot_activity_item_dot);
        this.moreDotTv = (TextView) findViewById(R.id.dot_more_item_dot);

        refreshView();
    }

    @Override
    public void onClick(View v) {

        if (clubInfoCompact == null)
            return;
        switch (v.getId()) {
            case R.id.nav_group_chat:
                SpeedxAnalytics.onEvent(context, "俱乐群聊", "click_cube_messenger_send");
                if (!isMyClub) {
                    Toasts.show(context, getResources().getString(R.string.cannotlookbeforejoin));
                    break;
                }
                GroupSettingActivity.CLUB_NAME = clubInfoCompact.getName();
//                this.userSp.edit().putInt(PUSH.PREF_KEY.DOT_GROUP_CHAT, 0).apply();
                RongIM.getInstance().startGroupChat(getContext(), clubInfoCompact.getObjectId(), clubInfoCompact.getName());
                break;
            case R.id.nav_activity:
                SpeedxAnalytics.onEvent(context, "俱乐部活动", null);
                Intent clubactivitylist = new Intent(getContext(), ClubActivitiesListActivity.class);
                clubactivitylist.putExtra(ClubMemberRankActivity.EXTRA_CLUB_ID,
                        clubInfoCompact.getObjectId());
                clubactivitylist.putExtra(ClubActivitiesListActivity.IS_MYCLUB, isMyClub);
                this.userSp.edit().putInt(PUSH.PREF_KEY.DOT_CLUB_ACTIVITY, 0).apply();
                getContext().startActivity(clubactivitylist);
                break;
            case R.id.nav_member:
                SpeedxAnalytics.onEvent(context, "俱乐部成员", null);
                Intent intentRank = new Intent(getContext(), MemberRankingActivity.class);
                intentRank.putExtra(ClubMemberRankActivity.EXTRA_CLUB_ID,
                        clubInfoCompact.getObjectId());
                getContext().startActivity(intentRank);
                break;
            case R.id.nav_scroe:
                SpeedxAnalytics.onEvent(context, "俱乐部积分排行", null);
                Intent intentRankClub = new Intent(getContext(), ClubRankActivity.class);
                intentRankClub.putExtra(ClubMemberRankActivity.EXTRA_CLUB_ID,
                        clubInfoCompact.getObjectId());
                intentRankClub.putExtra(ClubMemberManagerActivity.EXTRA_CLUB_INFO, clubInfoCompact);
                getContext().startActivity(intentRankClub);

                break;
            case R.id.nav_more:
                SpeedxAnalytics.onEvent(context, "俱乐更多", null);
                if (!isMyClub) {
                    Toasts.show(context, getResources().getString(R.string.cannotlookbeforejoin));
                    break;
                }
                Intent intent = new Intent(getContext(), ClubMoreActivity.class);
                intent.putExtra(ClubMemberManagerActivity.EXTRA_CLUB_INFO, clubInfoCompact);
                intent.putExtra(ClubMemberRankActivity.EXTRA_CLUB_ID, clubInfoCompact.getObjectId());

//                this.userSp.edit().putInt(PUSH.PREF_KEY.DOT_CLUB_MORE, 0).apply();
                if (getContext() instanceof Activity) {
                    Activity activity = (Activity) getContext();
                    activity.startActivityForResult(intent, ClubFeedInfoFrag.REQ_FINISH_CLUB_INFO);
                }
                break;
            case R.id.tv_tip:
                break;
            case R.id.tv_new_msg:
                SpeedxAnalytics.onEvent(context, "俱乐Feed消息", null);
                Intent itNewMsg = new Intent(getContext(), ClubMsgActivity.class);
                itNewMsg.putExtra(ClubFeedDetailsActivity.EXTRA_IS_MY_CLUB, isMyClub);
                this.userSp.edit().putInt(PUSH.PREF_KEY.DOT_CLUB_MSG_TOTAL_COUNT, 0).apply();
                getContext().startActivity(itNewMsg);
                break;
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.contains(PUSH.PREF_KEY.DOT_CLUB_ACTIVITY)
                || key.contains(PUSH.PREF_KEY.DOT_CLUB_MORE)
                || key.contains(PUSH.PREF_KEY.DOT_CLUB_MSG_TOTAL_COUNT)
                || key.equals(PREF_CLUB_FEED_ERROR_WARNING)) {
            refreshView();
        }
    }


    public void refreshView() {

        if (null != userSp && isMyClub) {

            int clubMsgDotTotalCount = userSp.getInt(PUSH.PREF_KEY.DOT_CLUB_MSG_TOTAL_COUNT, 0);
            int moreDotCount = userSp.getInt(PUSH.PREF_KEY.DOT_CLUB_MORE, 0);
            int clubActivityDotCount = userSp.getInt(PUSH.PREF_KEY.DOT_CLUB_ACTIVITY, 0);
//            int groupChatDotCount = userSp.getInt(PUSH.PREF_KEY.DOT_GROUP_CHAT, 0);
            boolean isPostError = userSp.getBoolean(PREF_CLUB_FEED_ERROR_WARNING, false);

            if (clubMsgDotTotalCount > 0) {
                feedMsgDotTv.setVisibility(VISIBLE);
                feedMsgDotTv.setText(clubMsgDotTotalCount + getContext().getString(R.string.clubfeed_new_msg));
            } else {
                feedMsgDotTv.setVisibility(View.GONE);
            }

            if (isPostError) {
                warning.setVisibility(VISIBLE);
            } else {
                warning.setVisibility(GONE);
            }

            setDotText(moreDotTv,moreDotCount);
            setDotText(activityDotTv,clubActivityDotCount);
//            setDotText(gropChatDotTv,groupChatDotCount);
        }
    }

    private void setDotText(TextView tv, int count) {
        if (count > 0) {
            tv.setVisibility(View.VISIBLE);
            tv.setText(count >= 99 ? "99+" : count + "");
        } else {
            tv.setVisibility(View.GONE);
        }
    }

}