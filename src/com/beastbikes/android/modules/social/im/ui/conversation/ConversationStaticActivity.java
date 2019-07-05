package com.beastbikes.android.modules.social.im.ui.conversation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.baidu.mapapi.model.LatLng;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.user.ui.FeedBackActivity;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.framework.ui.android.WebActivity;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.ui.widget.SaveImagePopupWindow;
import com.beastbikes.android.modules.social.im.biz.RongCloudManager;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.Gps2GoogleUtil;
import com.beastbikes.framework.android.utils.FileUtils;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

import io.rong.imkit.IPublicServiceMenuClickListener;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.PublicServiceMenuItem;
import io.rong.imlib.model.UserInfo;
import io.rong.message.LocationMessage;

public class ConversationStaticActivity extends SessionFragmentActivity
        implements RongIM.ConversationBehaviorListener, RongIM.LocationProvider, IPublicServiceMenuClickListener {
    public static final int REQUEST_CODE_LOCALTION_SELECT = 100;
    private static final String SOSO_MAP_KEY = "UJXBZ-EARR3-ZZ63I-3CWGZ-IOGY7-4KFG5";
    public static boolean isInChat = false;
    private static final Logger logger = LoggerFactory
            .getLogger(LocationSelectActivity.class);

    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;
    private String mTargetId;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setDisplayHomeAsUpEnabled(true);

        SpeedxAnalytics.onEvent(this, "进入私信页", null);
        AVUser user = AVUser.getCurrentUser();

        setContentView(R.layout.rc_conversation);

        Intent intent = getIntent();
        if (null == intent || intent.getData() == null || user == null) {
            finish();
        }
        this.sp = getSharedPreferences(user.getObjectId(), 0);
        Uri uri = intent.getData();
        if (uri != null) {
            setTitle(uri.getQueryParameter("title").toString());
        }

        mConversationType = Conversation.ConversationType.
                valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

        mTargetId = intent.getData().getQueryParameter("targetId");
        RongCloudManager.getInstance().setCurrentConversationType(mConversationType);

        UserInfo info = new UserInfo(user.getObjectId(), user.getDisplayName(), Uri.parse(user.getAvatar()));
        if (mConversationType == ConversationType.GROUP) {
            String groupChatName = sp.getString(mTargetId, "");
            if (!TextUtils.isEmpty(groupChatName)) {
                info = new UserInfo(user.getObjectId(), groupChatName, Uri.parse(user.getAvatar()));
            }
            sp.edit().putInt(Constants.PUSH.PREF_KEY.DOT_GROUP_CHAT, 0).apply();
        } else if (mConversationType == ConversationType.PUBLIC_SERVICE) {
            RongIM.getInstance().setPublicServiceMenuClickListener(this);
        }

        RongIM.getInstance().setCurrentUserInfo(info);
        RongIM.getInstance().refreshUserInfoCache(info);
        RongIM.setConversationBehaviorListener(this);
        RongIM.setLocationProvider(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mConversationType == ConversationType.GROUP) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.group_setting_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.group_setting_member_setting) {
            Intent it = new Intent(this, GroupSettingActivity.class);
            it.putExtra(GroupSettingActivity.EXTRA_TARGET_ID, mTargetId);
            it.putExtra(GroupSettingActivity.EXTRA_CONVERSATION_TYPE, mConversationType.ordinal());
            startActivity(it);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInChat = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInChat = false;
    }

    @Override
    public void finish() {

        super.finish();
        super.overridePendingTransition(R.anim.activity_none,
                R.anim.activity_out_to_right);
    }

    @Override
    public boolean onMessageClick(Context arg0, View arg1, Message message) {
        if (message.getContent() instanceof LocationMessage) {
            LocationMessage lm = (LocationMessage) message.getContent();
            Intent intent = new Intent(ConversationStaticActivity.this, ConversationMapView.class);
            intent.putExtra(ConversationMapView.LATLNGLATTAG, lm.getLat());
            intent.putExtra(ConversationMapView.LATLNGLONTAG, lm.getLng());
            intent.putExtra(ConversationMapView.LATLNGADDRESS, lm.getPoi());
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//			intent.onDataChanged(Uri.parse("geo:+" + lm.getLat() + "," + lm.getLng()));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
        return false;
    }

    @Override
    public boolean onMessageLinkClick(Context arg0, String arg1) {
        final Uri uri = Uri.parse(arg1);
        final Intent intent = new Intent(getApplicationContext(),
                BrowserActivity.class);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setPackage(getPackageName());
        intent.putExtra(WebActivity.EXTRA_ENTER_ANIMATION,
                R.anim.activity_in_from_right);
        intent.putExtra(WebActivity.EXTRA_EXIT_ANIMATION,
                R.anim.activity_out_to_right);
        intent.putExtra(WebActivity.EXTRA_NONE_ANIMATION, R.anim.activity_none);
        startActivity(intent);
        return true;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }

    @Override
    public boolean onUserPortraitClick(Context arg0, ConversationType arg1,
                                       UserInfo arg2) {
        if (null == arg2)
            return false;
        if (arg1 == ConversationType.PRIVATE || arg1 == ConversationType.GROUP) {
            final Intent intent = new Intent();
            intent.setClass(ConversationStaticActivity.this,
                    ProfileActivity.class);
            intent.putExtra(ProfileActivity.EXTRA_USER_ID, arg2.getUserId());

            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, ConversationType conversationType, UserInfo userInfo) {
        return false;
    }

    @Override
    public void onStartLocation(Context arg0,
                                LocationCallback lastLocationCallback) {
        RongCloudManager.getInstance().setLastLocationCallback(
                lastLocationCallback);
        Intent intent = new Intent(this, LocationSelectActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOCALTION_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOCALTION_SELECT) {
            if (resultCode == RESULT_OK) {
                if (null != data) {
                    double lat = data.getDoubleExtra(
                            LocationSelectActivity.EXTRA_LAT, 0);
                    double lng = data.getDoubleExtra(
                            LocationSelectActivity.EXTRA_LNG, 0);
                    LatLng latLng = Gps2GoogleUtil.baiduMapLatToGpsLat(new LatLng(lat, lng));
                    lat = latLng.latitude;
                    lng = latLng.longitude;
                    LatLng latLng2 = Gps2GoogleUtil.transform4Baidu(lat, lng);
                    lat = latLng2.latitude;
                    lng = latLng2.longitude;
                    String addr = data
                            .getStringExtra(LocationSelectActivity.EXTRA_ADDR);

                    Uri uri = Uri
                            .parse("http://apis.map.qq.com/ws/staticmap/v2")
                            .buildUpon()
                            .appendQueryParameter("size", "480*240")
                            .appendQueryParameter("key", SOSO_MAP_KEY)
                            .appendQueryParameter("zoom", "16")
                            .appendQueryParameter("center", lat + "," + lng)
                            .build();
                    LocationMessage msg = LocationMessage.obtain(lat, lng,
                            addr, uri);

                    RongCloudManager.getInstance().getLastLocationCallback()
                            .onSuccess(msg);
                    logger.info("LocationMessage info:" + "lat=" + lat
                            + ",lng=" + lng + ",addr=" + addr + "uri" + uri);
                }
            }
        }
    }

    @Override
    public boolean onClick(ConversationType conversationType, String targetId, PublicServiceMenuItem publicServiceMenuItem) {

        if (publicServiceMenuItem.getUrl().equals("http://questions")) {
            final StringBuilder sb = new StringBuilder(Constants.UrlConfig.DEV_SPEEDX_HOST)
                    .append("/app/faq-android.html");
            final Uri browserUri = Uri.parse(sb.toString());
            final Intent browserIntent = new Intent(ConversationStaticActivity.this,
                    BrowserActivity.class);
            browserIntent.setData(browserUri);
            browserIntent.addCategory(Intent.CATEGORY_DEFAULT);
            browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
            browserIntent.setPackage(getPackageName());
            browserIntent.putExtra(WebActivity.EXTRA_TITLE,
                    getString(R.string.setting_fragment_item_faq));
            browserIntent.putExtra(WebActivity.EXTRA_ENTER_ANIMATION,
                    R.anim.activity_in_from_right);
            browserIntent.putExtra(WebActivity.EXTRA_EXIT_ANIMATION,
                    R.anim.activity_out_to_right);
            browserIntent.putExtra(WebActivity.EXTRA_NONE_ANIMATION,
                    R.anim.activity_none);
            ConversationStaticActivity.this.startActivity(browserIntent);
            SpeedxAnalytics.onEvent(ConversationStaticActivity.this, "进入问题帮助", null);
        } else if (publicServiceMenuItem.getUrl().equals("http://feedback")) {
            startActivity(new Intent(ConversationStaticActivity.this, FeedBackActivity.class));
        }
        return true;
    }
}
