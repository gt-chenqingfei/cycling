package com.beastbikes.android.utils;

import android.content.Context;
import android.content.Intent;

import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.cycling.club.ui.ClubFeedInfoActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubFeedInfoFrag;

/**
 * Created by chenqingfei on 16/4/26.
 */
public class IntentUtils {


    public static void goClubFeedInfoActivity(Context context, ClubInfoCompact myClubInfo) {
        if (context == null) return;
        Intent it = new Intent(context, ClubFeedInfoActivity.class);
        it.putExtra(ClubFeedInfoFrag.EXTRA_CLUB_ID,
                myClubInfo.getObjectId());
        it.putExtra(ClubFeedInfoFrag.EXTRA_CLUB_INFO,
                myClubInfo);
        context.startActivity(it);
    }

    public static void goClubFeedInfoActivity(Context context, String clubId) {
        if (context == null) return;
        Intent it = new Intent(context, ClubFeedInfoActivity.class);
        it.putExtra(ClubFeedInfoFrag.EXTRA_CLUB_ID, clubId);
        context.startActivity(it);
    }
}
