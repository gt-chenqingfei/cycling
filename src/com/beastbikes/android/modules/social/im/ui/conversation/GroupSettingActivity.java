package com.beastbikes.android.modules.social.im.ui.conversation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.cycling.club.ui.ClubFeedInfoFrag;
import com.beastbikes.android.modules.cycling.club.ui.ClubMemberManagerActivity;
import com.beastbikes.android.modules.cycling.ranking.dto.RankDTO;
import com.beastbikes.android.modules.social.im.biz.FriendManager;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.widget.LinearListView;
import com.beastbikes.android.widget.materialdesign.mdswitch.Switch;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ResultCallback;

import static io.rong.imlib.model.Conversation.ConversationNotificationStatus;
import static io.rong.imlib.model.Conversation.ConversationType;

/**
 * Created by chenqingfei on 16/3/11.
 */
@LayoutResource(R.layout.activity_group_setting)
public class GroupSettingActivity extends SessionFragmentActivity implements View.OnClickListener,
        Switch.OnCheckedChangeListener, LinearListView.OnItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String EXTRA_CONVERSATION_TYPE = "CONVERSATION_TYPE";
    public static final String EXTRA_TARGET_ID = "TARGET_ID";
    public static final String EXTRA_CLUB_INFO_COMPACT = "CLUB_INFO_COMPACT";
    public static final String PREF_GROUP_DND_SET = "DND_SET";

    public static String CLUB_NAME = "";

    @IdResource(R.id.activity_group_setting_see_all_lay)
    View seeAll;

    @IdResource(R.id.activity_group_setting_friend_name_show_switch)
    Switch switchShowFriendName;

    @IdResource(R.id.activity_group_setting_gropu_notices_value)
    TextView notices;

    @IdResource(R.id.activity_group_setting_my_group_name_value)
    EditText groupName;

    @IdResource(R.id.activity_group_setting_msg_dnd_switch)
    Switch switchMsgDND;

    @IdResource(R.id.activity_group_setting_group_member)
    LinearListView lvGroup;

    private ConversationType mConversationType;

    private String mTargetId;

    private ClubInfoCompact clubInfoCompact;

    private List<RankDTO> members = new ArrayList<>();

    private MemberAdapter adapter;

    private SharedPreferences defaultSp;

    private SharedPreferences sp;

    private int dndStatus = ConversationNotificationStatus.NOTIFY.getValue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null)
            return;


        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(CLUB_NAME);
        }
        mTargetId = intent.getStringExtra(EXTRA_TARGET_ID);
        int type = intent.getIntExtra(EXTRA_CONVERSATION_TYPE, ConversationType.PRIVATE.ordinal());

        if (type == ConversationType.PRIVATE.ordinal()) {
            mConversationType = ConversationType.PRIVATE;
        } else if (type == ConversationType.GROUP.ordinal()) {
            mConversationType = ConversationType.GROUP;
        }

        try {
            clubInfoCompact = new ClubManager(this).getMyClub(getUserId());
        } catch (BusinessException e) {
            e.printStackTrace();
        }

        seeAll.setOnClickListener(this);
        switchMsgDND.setOnCheckedChangeListener(this);
        switchShowFriendName.setOnCheckedChangeListener(this);
        lvGroup.setOnItemClickListener(this);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_group_setting_see_all_lay:
                final Intent intent = new Intent(this, ClubMemberManagerActivity.class);
                intent.putExtra(ClubFeedInfoFrag.EXTRA_CLUB_ID, clubInfoCompact.getObjectId());
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable(ClubMemberManagerActivity.EXTRA_CLUB_INFO, clubInfoCompact);
                intent.putExtras(bundle2);
                this.startActivity(intent);
                break;
        }

    }

    @Override
    public void finish() {
        super.finish();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(groupName.getWindowToken(), 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        final String groupChatName = groupName.getText().toString();
        if (!TextUtils.isEmpty(groupChatName)) {

            new AsyncTask<String, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(String... params) {
                    try {
                        return new FriendManager(GroupSettingActivity.this).
                                setClubChatNick(groupChatName, clubInfoCompact.getObjectId());

                    } catch (BusinessException e) {
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean result) {

                }

            }.execute();
        }
    }


    @Override
    public void onCheckedChanged(Switch view, boolean checked) {

        if (switchMsgDND.getTag() == null) {
            return;
        }

        if (view == switchMsgDND) {

            final ConversationNotificationStatus status;
            if (dndStatus == 1) {
                status = ConversationNotificationStatus.DO_NOT_DISTURB;
            } else {
                status = ConversationNotificationStatus.NOTIFY;
            }

            if (RongIM.getInstance() == null) {
                return;
            }

            RongIM.getInstance().setConversationNotificationStatus
                    (mConversationType, mTargetId, status,
                            new ResultCallback<ConversationNotificationStatus>() {
                                @Override
                                public void onSuccess(ConversationNotificationStatus conversationNotificationStatus) {
//                                    Toasts.show(GroupSettingActivity.this, R.string.group_setting_success);
                                    sp.edit().putBoolean(Constants.NOTIFY_GROUP_MSG_DND,
                                            status == ConversationNotificationStatus.DO_NOT_DISTURB).apply();
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
//                                    Toasts.show(GroupSettingActivity.this, R.string.group_setting_fail);
                                }
                            });
        }
    }

    @Override
    public void onItemClick(LinearListView parent, View view, int position, long id) {
        if (parent.getId() == R.id.activity_group_setting_group_member) {
            RankDTO dto = (RankDTO) adapter.getItem(position);
            if (dto == null)
                return;

            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setClass(this, ProfileActivity.class);
            intent.putExtra(ProfileActivity.EXTRA_USER_ID, dto.getUserId());
            intent.putExtra(ProfileActivity.EXTRA_AVATAR, dto.getAvatarUrl());
            intent.putExtra(ProfileActivity.EXTRA_NICK_NAME, dto.getNickname());
            startActivity(intent);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    private void init() {
        if (clubInfoCompact == null)
            return;

        if (AVUser.getCurrentUser() != null) {
            this.sp = getSharedPreferences(AVUser.getCurrentUser().getObjectId(), 0);
            String groupChatName = sp.getString(clubInfoCompact.getObjectId(), "");
            groupName.setHint(AVUser.getCurrentUser().getDisplayName());
            if (!TextUtils.isEmpty(groupChatName)) {
                groupName.setText(groupChatName);
            }
            Editable etext = groupName.getText();
            if (!TextUtils.isEmpty(etext)) {
                Selection.setSelection(etext, etext.length());
            }
        }

        switchMsgDND.setEnabled(false);
        switchMsgDND.setClickable(false);
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().getConversationNotificationStatus(mConversationType, mTargetId, new ResultCallback<ConversationNotificationStatus>() {
                @Override
                public void onSuccess(ConversationNotificationStatus conversationNotificationStatus) {
                    dndStatus = conversationNotificationStatus.getValue();
                    switchMsgDND.setChecked(dndStatus == 0);
                    switchMsgDND.setEnabled(true);
                    switchMsgDND.setClickable(true);
                    switchMsgDND.setTag(true);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }

        this.defaultSp = PreferenceManager.getDefaultSharedPreferences(this);
        this.defaultSp.registerOnSharedPreferenceChangeListener(this);

        notices.setText(clubInfoCompact.getNotice());
        adapter = new MemberAdapter();
        lvGroup.setAdapter(adapter);

        getClubMembersList(mTargetId);
    }

    class MemberAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            int count = 0;
            if (members != null) {
                count = members.size() > 6 ? 6 : members.size();
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            return members.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CircleImageView image = new CircleImageView(parent.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.avatar_m),
                    (int) getResources().getDimension(R.dimen.avatar_m));
            params.setMargins(0, 0, (int) getResources().getDimension(R.dimen.avatar_margin), 0);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setLayoutParams(params);

            RankDTO user = (RankDTO) getItem(position);
            if (user != null) {
                if (!TextUtils.isEmpty(user.getAvatarUrl())) {
                    Picasso.with(parent.getContext()).load(user.getAvatarUrl()).fit().error(R.drawable.ic_avatar)
                            .placeholder(R.drawable.ic_avatar).centerCrop().into(image);
                } else {
                    image.setImageResource(R.drawable.ic_avatar);
                }
            }

            return image;
        }
    }

    /**
     * 获取成员排行
     *
     * @param clubId
     */
    private void getClubMembersList(final String clubId) {
        if (TextUtils.isEmpty(clubId)) {
            return;
        }

        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<RankDTO>>() {

                    @Override
                    protected List<RankDTO> doInBackground(String... params) {
                        try {
                            return new ClubManager(GroupSettingActivity.this).getClubMemberList(params[0],
                                    ClubManager.CLUB_MEMBER_ORDERBY_JOINDE, 1, 1000);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<RankDTO> result) {

                        if (null == result || result.isEmpty()) {
                            return;
                        }

                        for (int i = 1; i < result.size(); i++) {
                            RankDTO rankDTO = result.get(i);

                            members.add(rankDTO);
                        }
                        adapter.notifyDataSetChanged();
                    }

                }, clubId);
    }
}
