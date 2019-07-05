package com.beastbikes.android.embapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.beastbikes.android.modules.cycling.activity.ui.record.CyclingCompletedActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubFeedInfoActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubFeedInfoFrag;
import com.beastbikes.android.modules.user.ui.ProfileActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;

/**
 * Created by chenqingfei on 16/7/8.
 */
public class SchemaInterceptor {
    private final static Logger logger = LoggerFactory.getLogger(SchemaInterceptor.class);


    public static Intent interceptUrlSchema(Uri data, Activity context) {
        return interceptUrlSchema(data, context, true);
    }

    /**
     * 拦截schema 请求 并跳转
     *
     * @param data
     */
    public static Intent interceptUrlSchema(Uri data, Context context, boolean open) {
        if (data == null)
            return null;
        logger.info("dispatchUrlSchema schema=" + data.toString());
        String host = data.getHost();
        String path = data.getPath();
        if (TextUtils.isEmpty(host) || !host.equals("speedx.com")) {
            logger.info("not support this schema! ");
            return null;
        }

        Intent intent = null;

        if (TextUtils.isEmpty(path)) {
            logger.info("schema path is null");
            return null;
        }
        if ("/club".equals(path)) {
            String clubId = data.getQueryParameter("clubId");
            if (!TextUtils.isEmpty(clubId)) {
                intent = new Intent(context, ClubFeedInfoActivity.class);
                intent.putExtra(ClubFeedInfoFrag.EXTRA_CLUB_ID, clubId);
            }
        } else if ("/user".equals(path)) {
            String userId = data.getQueryParameter("userId");
            if (!TextUtils.isEmpty(userId)) {
                intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(ProfileActivity.EXTRA_USER_ID, userId);
            }
        } else if ("/record".equals(path)) {
            String sportIdentify = data.getQueryParameter("sportIdentify");
            if (!TextUtils.isEmpty(sportIdentify)) {
                intent = new Intent(context, CyclingCompletedActivity.class);
                intent.putExtra(CyclingCompletedActivity.EXTRA_SPORT_IDENTIFY, sportIdentify);
            }
        } else if ("/open".equals(path)) {
            String openUri = data.getQueryParameter("uri");
            openUri = URLDecoder.decode(URLDecoder.decode(openUri));
            String menu = data.getQueryParameter("menu");

            if (!TextUtils.isEmpty(openUri)) {
                final Uri uri = Uri.parse(openUri);
                intent = new Intent(context, BrowserActivity.class);
                intent.setData(uri);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setPackage(context.getPackageName());
                if (!TextUtils.isEmpty(menu)) {
                    intent.putExtra(BrowserActivity.EXTRA_MENU_STATUS, true);
                }
            }

        } else {
            logger.info("schema path is not yet!!");
        }

        if (null != intent && open) {
            context.startActivity(intent);
            Activity activity = (Activity) context;
            activity.getIntent().setData(null);
        }

        return intent;
    }
}
