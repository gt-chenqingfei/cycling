package com.beastbikes.android.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;
import com.beastbikes.android.main.MainActivity;

public class NotificationUtil {

    public static Notification getNotification(int contentRes) {
        final Context context = BeastBikes.getInstance()
                .getApplicationContext();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(context, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, 0);
        final NotificationCompat.Builder builder = new Builder(context);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setSmallIcon(R.drawable.ic_launcher_small);
        builder.setLargeIcon(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ic_launcher));
        builder.setTicker(context.getResources().getString(R.string.activity_fragment_event_click_start_riding));
        builder.setContentText(context.getResources().getString(contentRes));
        builder.setContentIntent(pendingIntent);

        final Notification notification = builder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
        return notification;
    }

}
