package com.beastbikes.android.modules.social.im.ui.conversation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.message.biz.MessageManager;
import com.beastbikes.android.modules.message.dto.MessageDTO;
import com.beastbikes.android.modules.message.ui.MessageActivity;
import com.beastbikes.android.modules.social.im.biz.RongCloudManager;
import com.beastbikes.android.modules.user.ui.ProfileFragment;
import com.beastbikes.android.utils.DateUtil;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.framework.business.BusinessException;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.model.Conversation;

public class ConversationListStaticActivity extends SessionFragmentActivity
        implements OnClickListener, OnSharedPreferenceChangeListener, RongIM.ConversationListBehaviorListener {

    private TextView tvUnRead;
    private TextView tvBody;
    private TextView tvTime;
    private MessageManager messageManager;
    private SharedPreferences userSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setDisplayHomeAsUpEnabled(true);

        SpeedxAnalytics.onEvent(this, "查看私信列表", null);

        setContentView(R.layout.rc_conversation_list);

        this.userSp = getSharedPreferences(getUserId(), 0);
        this.userSp.registerOnSharedPreferenceChangeListener(this);

        findViewById(R.id.conversation_list_header).setOnClickListener(this);

        tvUnRead = (TextView) findViewById(R.id.rc_unread_message1);
        tvBody = (TextView) findViewById(R.id.rc_body);
        tvTime = (TextView) findViewById(R.id.rc_time);


        ConversationListFragment fragment = new ConversationListFragment();
        Uri uri = Uri
                .parse("rong://" + getApplicationInfo().packageName)
                .buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false").
                        appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false").
                        appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false").
                        build();
        fragment.setUri(uri);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        // xxx 为你要加载的 id
        transaction.add(R.id.frag_container, fragment);
        transaction.commit();

        messageManager = new MessageManager(this);

        RongIM.setConversationListBehaviorListener(this);

        fetchMessageList();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        refreshUnRead(ProfileFragment.PREF_FRIEND_NEW_MESSAGE_COUNT);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.conversation_list_header:
                startActivity(new Intent(this, MessageActivity.class));
                break;
        }
    }

    private void refreshUnRead(String key) {
        int messageCount = this.userSp.getInt(key, 0);
        if (messageCount <= 0) {
            this.tvUnRead.setVisibility(View.GONE);
        } else {
            if (messageCount > 99) {
                this.tvUnRead.setText("...");
            } else {
                this.tvUnRead.setText(String.valueOf(messageCount));
            }
            this.tvUnRead.setVisibility(View.VISIBLE);
        }

    }

    private void fetchMessageList() {

        this.getAsyncTaskQueue().add(
                new AsyncTask<Void, Void, List<MessageDTO>>() {

                    @Override
                    protected List<MessageDTO> doInBackground(Void... params) {
                        try {
                            return messageManager.getMessageList();
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<MessageDTO> result) {
                        if (null == result || result.isEmpty())
                            return;

                        MessageDTO dto = result.get(result.size() - 1);
                        if (null != dto) {
                            tvBody.setText(dto.getMessage());
                            tvTime.setText(DateUtil.getTimestampString(dto.getAvailableTime()));
                        }

                    }
                });
    }


    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none,
                R.anim.activity_out_to_right);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        // TODO Auto-generated method stub
        if (key.equals(ProfileFragment.PREF_FRIEND_NEW_MESSAGE_COUNT)) {
            refreshUnRead(key);
        }
    }

    @Override
    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }

    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {

        if (uiConversation.getUnReadMessageCount() > 0) {
            if (uiConversation.getConversationType() == Conversation.ConversationType.GROUP) {
                SpeedxAnalytics.onEvent(this, "消息列表点击俱乐部未读消息", "click_get_into_club_session_page");
            } else if (uiConversation.getConversationType() == Conversation.ConversationType.PRIVATE) {
                SpeedxAnalytics.onEvent(this, "消息列表点击个人未读消息", "click_get_into_session_page");
            }
        }
        return false;
    }
}
