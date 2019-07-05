package com.beastbikes.android.modules.social.im;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.main.MainActivity;
import com.beastbikes.android.modules.social.im.biz.RongCloudManager;
import io.rong.imkit.RongContext;
import io.rong.push.RongPushClient;
import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * Created by chenqingfei on 16/8/10.
 */
public class RongPushReceiver extends PushMessageReceiver {
    private SharedPreferences sp;

    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage pushNotificationMessage) {
        AVUser user = AVUser.getCurrentUser();
        if (user == null)
            return true;
        this.sp = context.getSharedPreferences(user.getObjectId(), 0);
        if (pushNotificationMessage.getConversationType() == RongPushClient.ConversationType.GROUP) {
            int groupChatCount = sp.getInt(Constants.PUSH.PREF_KEY.DOT_GROUP_CHAT, 0);
            groupChatCount++;
            sp.edit().putInt(Constants.PUSH.PREF_KEY.DOT_GROUP_CHAT, groupChatCount).apply();
            return true;
        }


        Intent openApp = null;
        if (BeastBikes.hasBeenLaunched) {
            if (context != null && RongContext.getInstance() != null) {
                Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon().appendPath("conversationlist").build();
                openApp = new Intent("android.intent.action.VIEW", uri);
            }
        }

        if (openApp == null) {
            openApp = new Intent(context, MainActivity.class);
            openApp.putExtra(RongCloudManager.RONG_CLOUD_PUSH_KEY, RongCloudManager.RONG_CLOUD_PUSH_VALUE);
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

        builder.setContentTitle(pushNotificationMessage.getSenderName());
        builder.setTicker(pushNotificationMessage.getSenderName());

        builder.setContentText(pushNotificationMessage.getPushContent());
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1000, notification);
        return true;
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage pushNotificationMessage) {
        return false;
    }
}
