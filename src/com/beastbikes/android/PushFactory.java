package com.beastbikes.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.ble.dto.FirstMaintenanceDTO;
import com.beastbikes.android.ble.ui.dialog.FirstMaintenanceDialog;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.embapi.SchemaInterceptor;
import com.beastbikes.android.home.HomeActivity;
import com.beastbikes.android.main.MainActivity;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.ui.ApplyManagerActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubFeedDetailsActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubHistoryNoticeActivity;
import com.beastbikes.android.modules.cycling.route.ui.RouteActivity;
import com.beastbikes.android.modules.message.ui.MessageActivity;
import com.beastbikes.android.modules.social.im.ui.FriendsApplyActivity;
import com.beastbikes.android.modules.user.ui.MedalsActivity;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chenqingfei on 16/6/30.
 */
public class PushFactory implements Constants {
    private static final Logger logger = LoggerFactory.getLogger("PushFactory");
    private static int REQUEST_ID = 0;
    public static final String EXTRA_ACTIVITY_NULL = "extra_activity_null";

    private PushFactory() {
    }

    private static PushFactory instance = null;

    public static PushFactory getInstance() {
        if (instance == null) {
            instance = new PushFactory();
        }
        return instance;
    }

    public void handlePushMessage(JSONObject jsonObject, Context context) {
        try {
            if (jsonObject == null) return;

            final JSONObject data = jsonObject.optJSONObject("data");
            JSONObject notification = null;
            if (data != null) {
                notification = data.optJSONObject("notification");
            }
            Log.d("TAGGGG", "data: " + data.toString());
            Intent intent = buildPushIntent(context, data);

            if (intent.getBooleanExtra(EXTRA_ACTIVITY_NULL, false)) {
                intent.setClass(context, MainActivity.class);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                if (null != data) {
                    intent.putExtra(PUSH_START_ACTIVITY_DATA, data.toString());
                }
            }

            if (null == notification) {
                return;
            }

            final PendingIntent pendingIntent = PendingIntent.getActivity(context, ++REQUEST_ID, intent, 0);
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setContentTitle(notification.optString("title"));
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.ic_launcher_small);
            builder.setLargeIcon(BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.ic_launcher));

            builder.setTicker(notification.optString("ticker"));
            builder.setContentText(notification.optString("text"));
            builder.setContentIntent(pendingIntent);

            final int notifiId = (int) System.currentTimeMillis();
            final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(notifiId, builder.build());

        } catch (Exception e) {
            logger.error("Parse push data error", e);
        }
    }


    public Intent buildPushIntent(Context context, JSONObject data) {

        AVUser user = AVUser.getCurrentUser();

        if (context == null || user == null)
            return null;

        Intent intent = buildPageIntent(data, context);

        Log.d("JPush", "data: " + data);
        if (data.has("redbadge")) {
            JSONObject badge = data.optJSONObject("redbadge");

            final SharedPreferences sp = context.getSharedPreferences(user.getObjectId(), 0);
            SharedPreferences.Editor editor = sp.edit();

            intent.putExtra(SessionFragmentActivity.EXTRA_NOTIFY_COUNT_KEY, PUSH.PREF_KEY.DOT_CLUB_MSG_TOTAL_COUNT);
            int clubTotalCount = sp.getInt(PUSH.PREF_KEY.DOT_CLUB_MSG_TOTAL_COUNT, 0);
            if (badge.has(PUSH.BADGE.CLUB_TRANSFER_SUCCESS_KEY)) {

                clubTotalCount += badge.optInt(PUSH.BADGE.CLUB_TRANSFER_SUCCESS_KEY);
                editor.putLong(PUSH.PREF_KEY.NOTIFY_CLUB_TRANSFER, System.currentTimeMillis());
            } else if (badge.has(PUSH.BADGE.CLUB_TRANSFER_CANCEL_KEY)) {

                clubTotalCount += badge.optInt(PUSH.BADGE.CLUB_TRANSFER_CANCEL_KEY);
            } else if (badge.has(PUSH.BADGE.CLUB_TRANSFER_MASTER_KEY)) {

                clubTotalCount += badge.optInt(PUSH.BADGE.CLUB_TRANSFER_MASTER_KEY);
                editor.putLong(PUSH.PREF_KEY.NOTIFY_CLUB_TRANSFER_MASTER, System.currentTimeMillis());
            } else if (badge.has(PUSH.BADGE.CLUB_TRANSFER_REAPPLY_KEY)) {
                editor.putLong(PUSH.PREF_KEY.NOTIFY_CLUB_APPLY_REFUSE, System.currentTimeMillis());
            } else if (badge.has(PUSH.BADGE.CLUB_TRANSFER_APPLY_KEY)) {

                clubTotalCount += badge.optInt(PUSH.BADGE.CLUB_TRANSFER_APPLY_KEY);
            } else if (badge.has(PUSH.BADGE.FOLLOWED_KEY)) {

                int followCount = sp.getInt(PUSH.PREF_KEY.DOT_FOLLOW, 0);
                followCount += badge.optInt(PUSH.BADGE.FOLLOWED_KEY);
                editor.putInt(PUSH.PREF_KEY.DOT_FOLLOW, followCount);
                editor.putLong(PUSH.PREF_KEY.NOTIFY_FOLLOW, System.currentTimeMillis());
                intent.putExtra(SessionFragmentActivity.EXTRA_NOTIFY_COUNT_KEY, PUSH.PREF_KEY.DOT_FOLLOW);
            } else if (badge.has(PUSH.BADGE.LIKE_FEED_KEY)) {

                clubTotalCount += badge.optInt(PUSH.BADGE.LIKE_FEED_KEY);
                editor.putLong(PUSH.PREF_KEY.NOTIFY_CLUB_FEED, System.currentTimeMillis());
            } else if (badge.has(PUSH.BADGE.COMMENT_FEED_KEY)) {

                clubTotalCount += badge.optInt(PUSH.BADGE.COMMENT_FEED_KEY);
                editor.putLong(PUSH.PREF_KEY.NOTIFY_CLUB_FEED, System.currentTimeMillis());
            } else if (badge.has(PUSH.BADGE.MEDAL_UNRECEIVE_KEY)) {

                int cyclingActivityCount = sp.getInt(PUSH.PREF_KEY.DOT_CYCLING_ACTIVITY, 0);
                cyclingActivityCount += badge.optInt(PUSH.BADGE.MEDAL_UNRECEIVE_KEY);
                editor.putInt(PUSH.PREF_KEY.DOT_CYCLING_ACTIVITY, cyclingActivityCount);
                intent.putExtra(SessionFragmentActivity.EXTRA_NOTIFY_COUNT_KEY, PUSH.PREF_KEY.DOT_CYCLING_ACTIVITY);
            } else if (badge.has(PUSH.BADGE.MEDAL_NEW_ACTIVE)) {
                //v2.5.0 有新的点亮的勋章
                //TODO 新点亮的勋章推送

            } else if (badge.has(PUSH.BADGE.CLUB_APPLY_KEY)) {

                int clubApply = badge.optInt(PUSH.BADGE.CLUB_APPLY_KEY);
                clubApply += sp.getInt(PUSH.PREF_KEY.DOT_CLUB_MORE, 0);
                if (clubApply >= 0) {
                    editor.putInt(PUSH.PREF_KEY.DOT_CLUB_MORE, clubApply);
                }
            } else if (badge.has(PUSH.BADGE.CLUB_ACTIVITY_KEY)) {

                int clubAct = badge.optInt(PUSH.BADGE.CLUB_ACTIVITY_KEY);
                clubAct += sp.getInt(PUSH.PREF_KEY.DOT_CLUB_ACTIVITY, 0);
                if (clubAct >= 0) {
                    editor.putInt(PUSH.PREF_KEY.DOT_CLUB_ACTIVITY, clubAct);
                }
                intent.putExtra(SessionFragmentActivity.EXTRA_NOTIFY_COUNT_KEY, PUSH.PREF_KEY.DOT_CLUB_ACTIVITY);
            } else if (badge.has(PUSH.BADGE.CLUB_MEMBER_SUCCESS_KEY)) {
                clubTotalCount += badge.optInt(PUSH.BADGE.CLUB_MEMBER_SUCCESS_KEY);
                editor.putLong(PUSH.PREF_KEY.NOTIFY_CLUB_APPLY_PASS, System.currentTimeMillis());
            } else if (badge.has(PUSH.BADGE.CLUB_MEMBER_QUIT_KEY)) {
                clubTotalCount += badge.optInt(PUSH.BADGE.CLUB_MEMBER_QUIT_KEY);
                editor.putLong(PUSH.PREF_KEY.NOTIFY_CLUB_MEMBER_QUIT, System.currentTimeMillis());
            } else if (badge.has(PUSH.BADGE.CLUB_NOTICE_KEY)) {
                clubTotalCount += badge.optInt(PUSH.BADGE.CLUB_NOTICE_KEY);
                editor.putLong(PUSH.PREF_KEY.NOTIFY_CLUB_NOTICE, System.currentTimeMillis());
            } else if (badge.has(PUSH.BADGE.SHOW_TEXT)) {
                JSONObject params = data.optJSONObject("params");
                if (params != null) {
                    FirstMaintenanceDTO dto = new FirstMaintenanceDTO(data);
                    FirstMaintenanceDialog dialog = new FirstMaintenanceDialog(context, dto);
                    dialog.show();
                }
            }

            editor.putInt(PUSH.PREF_KEY.DOT_CLUB_MSG_TOTAL_COUNT, clubTotalCount);
            editor.commit();
        }
        return intent;
    }


    public Intent buildPageIntent(JSONObject data, Context context) {

        Intent intent = new Intent();
        if (data.has("page")) {
            final Integer page = data.optInt("page", -1);
            if (page != null && page != -1) {

                switch (page) {
                    case PUSH.OPEN_PAGE.PAGE_MAIN:
                        intent.setClass(context, MainActivity.class);
                        break;
                    case PUSH.OPEN_PAGE.PAGE_PROFILE:
                        intent.setClass(context, ProfileActivity.class);
                        break;
                    case PUSH.OPEN_PAGE.PAGE_ROUTE:
                        intent.setClass(context, RouteActivity.class);
                        break;
                    case PUSH.OPEN_PAGE.PAGE_CLUB_FEED_DETAIL:
                        intent.setClass(context, ClubFeedDetailsActivity.class);

                        if (data.has("params")) {
                            JSONObject params = data.optJSONObject("params");
                            if (!JSONUtil.isNull(params)) {
                                int feedId = params.optInt(ClubFeedDetailsActivity.EXTRA_FID);
                                intent.putExtra(ClubFeedDetailsActivity.EXTRA_FID, feedId);
                                intent.putExtra(ClubFeedDetailsActivity.EXTRA_CLUBSHOWINPUT, false);
                                intent.putExtra(ClubFeedDetailsActivity.EXTRA_IS_MY_CLUB, true);
                            }
                        }
                        break;
                    case PUSH.OPEN_PAGE.PAGE_CLUB_FEED_INFO:
                        intent.setClass(context, HomeActivity.class);
                        break;
                    case PUSH.OPEN_PAGE.PAGE_APPLY_MANAGER:
                        intent.setClass(context, ApplyManagerActivity.class);
                        break;
                    case PUSH.OPEN_PAGE.PAGE_FRIENDS_APPLY:
                        intent.setClass(context, FriendsApplyActivity.class);
                        break;
                    case PUSH.OPEN_PAGE.PAGE_BROADCAST:
                    case PUSH.OPEN_PAGE.PAGE_MESSAGE:
                        intent.setClass(context, MessageActivity.class);
                        break;
                    case PUSH.OPEN_PAGE.PAGE_MEDAL:
                        intent.setClass(context, MedalsActivity.class);
                        intent.putExtra(MedalsActivity.EXTRA_FROM_PUSH, true);
                        break;
                    case PUSH.OPEN_PAGE.PAGE_CLUB_HISTORY_NOTICE:
                        intent.setClass(context, ClubHistoryNoticeActivity.class);
                        break;
                    default:
                        intent.putExtra(EXTRA_ACTIVITY_NULL, true);
                        break;
                }
            }
        }
        if (data.has("uri")) {
            String url = data.optString("uri");

            if (!TextUtils.isEmpty(url) && !"null".equals(url)) {
                final Uri uri = Uri.parse(url);
                intent = SchemaInterceptor.interceptUrlSchema(uri,context,false);
                if(intent == null) {
                    intent = new Intent(context, BrowserActivity.class);
                    intent.setData(uri);
                }
            }
        }

        if (data.has("params")) {
            final JSONObject params = data.optJSONObject("params");
            if (null != params) {
                final JSONArray paramNames = params.names();
                final int n = params.length();

                for (int i = 0; i < n; i++) {
                    final String paramName = paramNames.optString(i);
                    final Object paramValue = params.opt(paramName);

                    if (paramValue instanceof Integer) {
                        intent.putExtra(paramName, (Integer) paramValue);
                    } else if (paramValue instanceof Float) {
                        intent.putExtra(paramName, (Float) paramValue);
                    } else if (paramValue instanceof Double) {
                        intent.putExtra(paramName, (Double) paramValue);
                    } else if (paramValue instanceof Long) {
                        intent.putExtra(paramName, (Long) paramValue);
                    } else if (paramValue instanceof CharSequence) {
                        intent.putExtra(paramName, String.valueOf(paramValue));
                    } else if (paramValue instanceof Boolean) {
                        intent.putExtra(paramName, (Boolean) paramValue);
                    } else if (paramValue instanceof JSONObject
                            || paramValue instanceof JSONArray) {
                        intent.putExtra(paramName, String.valueOf(paramValue));
                    }
                }
            }
        }

        return intent;
    }

}
