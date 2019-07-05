package com.beastbikes.android.modules.social.im.biz;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.main.MainActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.social.im.ui.conversation.ConversationStaticActivity;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.BitmapUtil;
import com.beastbikes.android.utils.NickNameRemarksUtil;
import com.beastbikes.framework.android.utils.ProcessUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.OnReceiveUnreadCountChangedListener;
import io.rong.imkit.RongIM.UserInfoProvider;
import io.rong.imkit.RongIMClientWrapper;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imkit.widget.provider.LocationInputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectCallback;
import io.rong.imlib.RongIMClient.ConnectionStatusListener;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.push.notification.PushNotificationMessage;

public class RongCloudManager extends ConnectCallback implements
        OnReceiveUnreadCountChangedListener, UserInfoProvider, RongIM.GroupInfoProvider,
        ConnectionStatusListener,
        RongIMClient.OnReceiveMessageListener, RongIM.ConversationBehaviorListener {

    private static final Logger logger = LoggerFactory
            .getLogger(RongCloudManager.class);
    private static final String CURRENT_USERID = "current_userid";
    private static final String CURRENT_LAN = "current_lan";
    public static final String RONG_CLOUD_PUSH_KEY = "RONGCLOUDPUSHRONGCLOUDPUSHKEY";
    public static final String RONG_CLOUD_PUSH_VALUE = "RONGCLOUDPUSHVALUE";
    public static final String CUSTOMERSERVICE = "KEFU144774296732097";
    public static final String PUBLIC_SERVICE_FEEDBACK = "speedx";
    public static final String PUBLIC_SERVICE_FEEDBACK_EN = "speedx_Feedback";
    public static final String PUBLIC_SERVICE_SPEEDX = "speedx_services";
    public static final String PUBLIC_SERVICE_SPEEDX_EN = "speedx_service";

    private boolean isRunning = false;
    private ProfileDTO currentProfileDto;
    private Context context;
    private SharedPreferences sp;
    private UnreadCountCallBack unReadCountCallBack;
    private ConversationType conversationType;

    public void setCurrentConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
    }

    private static RongCloudManager instance;

    private RongIM.LocationProvider.LocationCallback mLastLocationCallback;

    public static void init(Context context, String key) {

        if (instance == null) {

            synchronized (RongCloudManager.class) {

                if (instance == null) {
                    instance = new RongCloudManager(context, key);
                }
            }
        }
    }

    public static void reset(Context context, String key) {
        instance = null;
        init(context, key);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void rongCloudUnInit() {
        isRunning = false;
        RongIM.getInstance().logout();
    }

    public void disconnectRongCloud() {
        isRunning = false;
        RongIM.getInstance().disconnect();

    }


    public static RongCloudManager getInstance() {
        return instance;
    }

    private RongCloudManager(Context context, String key) {
        try {
            this.context = context;
            unReadCountCallBack = new UnreadCountCallBack();

            if (context.getPackageName().equals(
                    ProcessUtils.getCurrentProcessName(context))
                    || "io.rong.push".equals(ProcessUtils
                    .getCurrentProcessName(context))) {
                if (TextUtils.isEmpty(key)) {
                    RongIM.init(context);
                } else {
                    RongIM.init(context, key);
                }
                RongIM.setUserInfoProvider(this, true);
                RongIM.setGroupInfoProvider(this, true);
                RongIM.setOnReceiveMessageListener(this);
                RongIM.setConversationBehaviorListener(this);

                // 扩展功能自定义
                InputProvider.ExtendProvider[] provider = {
                        new ImageInputProvider(RongContext.getInstance()),
                        new CameraInputProvider(RongContext.getInstance()),
                        new LocationInputProvider(RongContext.getInstance())};

                RongIM.resetInputExtensionProvider(ConversationType.PRIVATE, provider);

                AVUser user = AVUser.getCurrentUser();
                if (null != user) {
                    connectRongCloud(user.getObjectId());
                }
            }
        } catch (Exception e) {
            logger.error("RongCloud Init Exception " + e.getMessage());
        }
    }

    /**
     * 连接融云服务
     *
     * @param userId
     */
    public void connectRongCloud(String userId) {
        if (isRunning)
            return;

        logger.trace("RongCloud connect userId = [" + userId + "]");
        this.sp = context.getSharedPreferences(userId, 0);
        String token = sp.getString(Constants.PREF_RONGCLOUD_USER_TOKEN, "");

        if (TextUtils.isEmpty(token) || token.equals("null")) {
            logger.trace("RongCloud token is null, to fetch RongCloud token...");
            fetchRongCloudToken(userId);
            return;
        }

        connectRongCloud(token, this);
    }

    private void connectRongCloud(String token, ConnectCallback callback) {
        logger.trace("RongCloud connectting token = [" + token
                + "],isRunning =[" + isRunning + "]");
        try {
            if (!isRunning) {
                RongIM.connect(token, callback);
            }
        } catch (RuntimeException e) {
            logger.error("connectRongCloud Exception e =" + e.getMessage());
        }
    }

    /**
     * 设置融云用户信息
     */
    public void setRongCloudUserInfo(final ProfileDTO dto) {
        if (null == dto)
            return;

        currentProfileDto = dto;

        Uri uri = null;
        if (!TextUtils.isEmpty(dto.getAvatar())) {
            uri = Uri.parse(dto.getAvatar());
        }
        RongIM.getInstance().setCurrentUserInfo(
                new UserInfo(dto.getUserId(), dto.getNickname(), uri));
    }

    private void fetchRongCloudToken(String userId) {

        if (null == context || TextUtils.isEmpty(userId)) {
            logger.error("RongCloud fetchRongCloudToken Error,because "
                    + "context or userId is null");
            return;
        }

        new RongCloudTokenTask().execute(userId);
    }

    final class UnreadCountCallBack extends ResultCallback<Integer> {
        @Override
        public void onError(ErrorCode arg0) {
            logger.error("RongCloud getTotalUnreadCount Error ="
                    + arg0.getMessage());
        }

        @Override
        public void onSuccess(Integer arg0) {
            logger.info("RongCloud getTotalUnreadCount success unreadCount"
                    + "=[" + arg0 + "]");
            if (null != sp) {
                sp.edit().putInt(Constants.PREF_RONGCLOUD_NEW_MESSAGE_COUNT, arg0).apply();
            }
        }
    }

    public RongIM.LocationProvider.LocationCallback getLastLocationCallback() {
        return mLastLocationCallback;
    }

    public void setLastLocationCallback(
            RongIM.LocationProvider.LocationCallback lastLocationCallback) {
        this.mLastLocationCallback = lastLocationCallback;
    }

    final class RongCloudTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return new FriendManager(context).getChatToken();
        }

        @Override
        protected void onPostExecute(String tokenCurrent) {

            logger.error("rongcloud token", tokenCurrent);
            if (TextUtils.isEmpty(tokenCurrent)) {
                return;
            }

            String tokenPre = sp.getString(
                    Constants.PREF_RONGCLOUD_USER_TOKEN, "");


            if (!tokenPre.equals(tokenCurrent)) {

                Editor editor = sp.edit();
                editor.putString(Constants.PREF_RONGCLOUD_USER_TOKEN,
                        tokenCurrent);
                editor.apply();

                isRunning = false;
                connectRongCloud(tokenCurrent, RongCloudManager.this);
            }
        }

    }

    @Override
    public void onTokenIncorrect() {
        // TODO Auto-generated method stub
        logger.info("RongCloud connect tokenIncorrect");
        if (null != currentProfileDto)
            fetchRongCloudToken(currentProfileDto.getUserId());
    }

    @Override
    public void onError(ErrorCode arg0) {
        logger.error("RongCloud connect error efforInfo=" + arg0.getMessage());
        isRunning = false;
    }

    @Override
    public void onSuccess(final String userId) {
        logger.trace("RongCloud connect success userId=[" + userId + "]");
        isRunning = true;
        if (RongIM.getInstance() == null)
            return;
        RongIM.getInstance().setOnReceiveUnreadCountChangedListener(this, ConversationType.PRIVATE);
        RongIM.getInstance().setOnReceiveUnreadCountChangedListener(new OnReceiveUnreadCountChangedListener() {
            @Override
            public void onMessageIncreased(int i) {
                int count = sp.getInt(Constants.PUSH.PREF_KEY.DOT_GROUP_CHAT, 0);
                count++;
                sp.edit().putInt(Constants.PUSH.PREF_KEY.DOT_GROUP_CHAT, count).apply();

                RongIM.getInstance().getTotalUnreadCount(unReadCountCallBack);
            }
        }, ConversationType.GROUP);
        RongIM.getInstance().setOnReceiveUnreadCountChangedListener(this, ConversationType.PUBLIC_SERVICE);

        RongIM.getInstance().getTotalUnreadCount(unReadCountCallBack);
        RongIM.setConnectionStatusListener(this);
        RongIM.getInstance().setMessageAttachedUserInfo(true);
        subscribePublicService();

        AVUser user = AVUser.getCurrentUser();
        if (null != user) {
            RongIM.getInstance().getConversationNotificationStatus(ConversationType.GROUP,
                    user.getClubId(), new ResultCallback<Conversation.ConversationNotificationStatus>() {
                        @Override
                        public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                            int value = conversationNotificationStatus.getValue();
                            if (null == sp) {
                                sp = context.getSharedPreferences(userId, 0);
                            }
                            sp.edit().putBoolean(Constants.NOTIFY_GROUP_MSG_DND, value == 0).apply();
                        }

                        @Override
                        public void onError(ErrorCode errorCode) {
                        }
                    });
        }
    }


    @Override
    public void onMessageIncreased(int arg0) {
        logger.info("RongCloud onMessageIncreased  =" + arg0);
        RongIM.getInstance().getTotalUnreadCount(unReadCountCallBack);
    }

    @Override
    public UserInfo getUserInfo(String userId) {
        UserInfo userInfo = null;
        if (null == context || null == currentProfileDto)
            return null;

        if (userId.equals(currentProfileDto.getUserId())) {
            userInfo = new UserInfo(userId,
                    context.getString(R.string.friends_stranger),
                    BitmapUtil.getResourceUri(R.drawable.ic_avatar, context.getPackageName()));

            String groupNick = sp.getString(currentProfileDto.getClubId(), "");
            userInfo.setName(NickNameRemarksUtil.disPlayName(currentProfileDto.getNickname(),
                    currentProfileDto.getRemarks()));

            if (!TextUtils.isEmpty(groupNick) && conversationType == ConversationType.GROUP) {
                userInfo.setName(groupNick);
            }
            userInfo.setPortraitUri(Uri.parse(currentProfileDto.getAvatar()));

            return userInfo;
        } else {

            ProfileDTO userInfoDTO = null;
            try {
                userInfoDTO = new FriendManager(context).getUserInfoById(userId);
                if (userInfoDTO != null) {
                    userInfo = new UserInfo(userInfoDTO.getUserId(), userInfoDTO.getNickname(),
                            Uri.parse(userInfoDTO.getAvatar()));
                }
            } catch (BusinessException e) {
                e.printStackTrace();
            }
            //new GetUserInfoTask().execute(userId);
        }

        return userInfo;
    }

    final class GetUserInfoTask extends AsyncTask<String, Void, ProfileDTO> {

        @Override
        protected ProfileDTO doInBackground(String... params) {
            try {
                return new FriendManager(context).getUserInfoById(params[0]);
            } catch (BusinessException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ProfileDTO profileDTO) {
            if (profileDTO != null) {
                UserInfo userInfo = new UserInfo(profileDTO.getUserId(), profileDTO.getNickname(),
                        Uri.parse(profileDTO.getAvatar()));
                RongIM.getInstance().setCurrentUserInfo(userInfo);
                RongIM.getInstance().refreshUserInfoCache(userInfo);
            }
        }
    }

    @Override
    public Group getGroupInfo(String s) {
        if (TextUtils.isEmpty(s) || context == null)
            return null;
        ClubManager clubManager = new ClubManager(context);
        try {
            ClubInfoCompact clubInfo = clubManager.getClubInfo(s);
            if (clubInfo == null)
                return null;
            Group group = new Group(clubInfo.getObjectId(), clubInfo.getName(),
                    Uri.parse(clubInfo.getLogo()));
            return group;
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onChanged(ConnectionStatus connectionStatus) {
        // TODO Auto-generated method stub

        logger.info("RongCloud onChanged status=" + connectionStatus);
        switch (connectionStatus) {

            case CONNECTED:// 连接成功。
                isRunning = true;
                break;
            case DISCONNECTED:// 断开连接。
                isRunning = false;
                break;
            case CONNECTING:// 连接中。
                isRunning = false;
                break;
            case NETWORK_UNAVAILABLE:// 网络不可用。
                isRunning = false;
                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                isRunning = false;
                break;
            default:
                isRunning = false;
                break;
        }
    }

    @Override
    public boolean onReceived(Message message, int i) {

        if (ConversationStaticActivity.isInChat) {
            return true;
        }

        if (message.getConversationType() == ConversationType.GROUP &&
                this.sp.getBoolean(Constants.NOTIFY_GROUP_MSG_DND, false)) {
            return true;
        }

        Handler handler = new Handler(context.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(android.os.Message msg) {
                if (msg.obj != null) {

                    MessageContent messageContent = (MessageContent) msg.obj;
                    String title = "";
                    if (messageContent.getUserInfo() != null) {
                        title = messageContent.getUserInfo().getName();
                    }
                    sendNotification(title, messageContent);
                }
                return false;
            }
        });
        android.os.Message msg = handler.obtainMessage(0, message.getContent());
        handler.sendMessage(msg);
        return true;
    }

    /**
     * 当点击用户头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @param userInfo         被点击的用户的信息。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onUserPortraitClick(Context context, ConversationType conversationType, UserInfo userInfo) {
        Log.e("onUserPortraitClick", "onUserPortraitClick");
        if (userInfo == null)
            return false;
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_ID, userInfo.getUserId());
        intent.putExtra(ProfileActivity.EXTRA_NICK_NAME, userInfo.getName());
        intent.putExtra(ProfileActivity.EXTRA_AVATAR, userInfo.getPortraitUri());
        context.startActivity(intent);
        return false;
    }

    /**
     * 当长按用户头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @param userInfo         被点击的用户的信息。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onUserPortraitLongClick(Context context, ConversationType conversationType, UserInfo userInfo) {
        Log.e("onUserPortraitLongClick", "onUserPortraitLongClick");
        return false;
    }

    /**
     * 当点击消息时执行。
     *
     * @param context 上下文。
     * @param view    触发点击的 View。
     * @param message 被点击的消息的实体信息。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true， 否则返回 false, false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        Log.e("onMessageClick", "onMessageClick");
        return false;
    }

    /**
     * 当点击链接消息时执行。
     *
     * @param context 上下文。
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageLinkClick(Context context, String s) {
        Log.e("onMessageLinkClick", "onMessageLinkClick");
        return false;
    }

    /**
     * 当长按消息时执行。
     *
     * @param context 上下文。
     * @param view    触发点击的 View。
     * @param message 被长按的消息的实体信息。
     * @return 如果用户自己处理了长按后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }

    private void sendNotification(String title, MessageContent messageContent) {
        Intent openApp = null;
        if (BeastBikes.hasBeenLaunched) {
            if (context != null && RongContext.getInstance() != null) {
                Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon().appendPath("conversationlist").build();
                openApp = new Intent("android.intent.action.VIEW", uri);
            }
        }

        if (openApp == null) {
            openApp = new Intent(context, MainActivity.class);
            openApp.putExtra(RONG_CLOUD_PUSH_KEY, RONG_CLOUD_PUSH_VALUE);
            openApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openApp,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_launcher_small);
        builder.setLargeIcon(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ic_launcher));

        builder.setContentTitle(title);
        builder.setTicker(title);

        builder.setContentText(handleMessageContent(messageContent));
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1000, notification);
    }

    private String handleMessageContent(MessageContent messageContent) {
        String content;
        if (messageContent instanceof TextMessage) {//文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            content = textMessage.getContent();
            Log.d("TextMessage", "onReceived-TextMessage:" + textMessage.getContent());
        } else if (messageContent instanceof ImageMessage) {//图片消息
            ImageMessage imageMessage = (ImageMessage) messageContent;
            content = context.getResources().getString(R.string.rong_image_message);
            Log.d("ImageMessage", "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (messageContent instanceof VoiceMessage) {//语音消息
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            content = context.getResources().getString(R.string.rong_audio_message);
            Log.d("VoiceMessage", "onReceived-voiceMessage:" + voiceMessage.getUri().toString());
        } else if (messageContent instanceof LocationMessage) {
            Log.d("LocationMessage", "onReceived-LocationMessage:");
            content = context.getResources().getString(R.string.rong_image_location);
        } else {
            Log.d("other", "onReceived-其他消息，自己来判断处理");
            content = context.getResources().getString(R.string.rong_image_other);
        }

        return content;
    }


    /**
     * 受融云限制,需要先取消关注,然后再次关注
     * 判断中英文关注对应公众号以及小助手
     */
    public void subscribePublicService() {

        String lang = Locale.getDefault().getLanguage();
        AVUser avUser = AVUser.getCurrentUser();
        if (null == avUser) {
            Toasts.show(context.getApplicationContext(), "用户可能在其他地方登录,请重新登录");
            return;
        }

        final SharedPreferences sp = context.getApplicationContext().getSharedPreferences(context.getApplicationContext().getPackageName(), 0);

        String currentLang = sp.getString(RongCloudManager.CURRENT_LAN, "zh");
        String userId = sp.getString(RongCloudManager.CURRENT_USERID, "");
        //如果切换了用户,则进行取消再关注操作
        if (!TextUtils.equals(userId, avUser.getObjectId())) {

            this.unsubAndSubPublishService();

            sp.edit().putString(RongCloudManager.CURRENT_USERID, avUser.getObjectId()).apply();

        } else if (!TextUtils.equals(lang, currentLang)) {
            //切换了语言后登入
            this.unsubAndSubPublishService();

            sp.edit().putString(RongCloudManager.CURRENT_LAN, lang);
        } else {
            //用户登出再次登录进入切没有切换语言之后不做操作
        }

    }

    /**
     * 取消并重新关注
     */
    public void unsubAndSubPublishService() {
        final String lang = Locale.getDefault().getLanguage();
        //可能是切换用户
        if (TextUtils.equals(lang, "zh")) {

            RongIMClient.getInstance().unsubscribePublicService(Conversation.PublicServiceType.PUBLIC_SERVICE, RongCloudManager.PUBLIC_SERVICE_FEEDBACK, new RongIMClient.OperationCallback() {
                @Override
                public void onSuccess() {
                    subscribeFeedbackPublishService(true);
                }

                @Override
                public void onError(ErrorCode errorCode) {
                    subscribeFeedbackPublishService(true);
                }

            });


            RongIMClient.getInstance().unsubscribePublicService(Conversation.PublicServiceType.PUBLIC_SERVICE, RongCloudManager.PUBLIC_SERVICE_SPEEDX, new RongIMClient.OperationCallback() {
                @Override
                public void onSuccess() {
                    subscribeServicePublish(true);
                }

                @Override
                public void onError(ErrorCode errorCode) {
                    subscribeServicePublish(true);
                }
            });

        } else {

            RongIMClient.getInstance().unsubscribePublicService(Conversation.PublicServiceType.PUBLIC_SERVICE, RongCloudManager.PUBLIC_SERVICE_FEEDBACK_EN, new RongIMClient.OperationCallback() {
                @Override
                public void onSuccess() {
                    subscribeFeedbackPublishService(false);
                }

                @Override
                public void onError(ErrorCode errorCode) {
                    subscribeFeedbackPublishService(false);
                }
            });

            RongIMClient.getInstance().unsubscribePublicService(Conversation.PublicServiceType.PUBLIC_SERVICE, RongCloudManager.PUBLIC_SERVICE_SPEEDX_EN, new RongIMClient.OperationCallback() {
                @Override
                public void onSuccess() {
                    subscribeServicePublish(false);
                }

                @Override
                public void onError(ErrorCode errorCode) {
                    subscribeServicePublish(false);
                }
            });
        }
    }

    /**
     * 关注野兽小助手公众号
     *
     * @param isChinese 是否为中文来判断公众号
     */
    private void subscribeServicePublish(boolean isChinese) {
        String publishId = isChinese ? PUBLIC_SERVICE_SPEEDX : PUBLIC_SERVICE_SPEEDX_EN;
        RongIMClient.getInstance().subscribePublicService(Conversation.PublicServiceType.PUBLIC_SERVICE, publishId, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(ErrorCode errorCode) {
            }
        });

        //此处取消与语言相反的公众号
        String cancelId = isChinese ? PUBLIC_SERVICE_SPEEDX_EN : PUBLIC_SERVICE_SPEEDX;
        RongIMClient.getInstance().unsubscribePublicService(Conversation.PublicServiceType.PUBLIC_SERVICE, cancelId, null);
    }

    /**
     * 关注反馈助手公众号
     *
     * @param isChinese 是否为中文来判断公众号
     */
    private void subscribeFeedbackPublishService(boolean isChinese) {
        String publishId = isChinese ? PUBLIC_SERVICE_FEEDBACK : PUBLIC_SERVICE_FEEDBACK_EN;
        RongIMClient.getInstance().subscribePublicService(Conversation.PublicServiceType.PUBLIC_SERVICE, publishId, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(ErrorCode errorCode) {
            }
        });

        //此处取消与语言相反的公众号
        String cancelId = isChinese ? PUBLIC_SERVICE_FEEDBACK_EN : PUBLIC_SERVICE_FEEDBACK;
        RongIMClient.getInstance().unsubscribePublicService(Conversation.PublicServiceType.PUBLIC_SERVICE, cancelId, null);
    }


}
