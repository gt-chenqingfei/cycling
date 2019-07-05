package com.beastbikes.android.modules.cycling.club.ui.view;

import android.content.Context;
import android.view.View;

import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedNotice;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class FeedItemNotice extends FeedItemBase<ClubFeedNotice> {


    public FeedItemNotice(Context context, View converView, AVUser user) {
        super(context, converView, user);

    }

    @Override
    public void initView() {

    }

    @Override
    public void bind(ClubFeedNotice o) {
        bindBase(o);
        extra.setVisibility(View.VISIBLE);
        if (o.getUser().getUserId().equals(currentUser.getObjectId())) {
            delete.setVisibility(View.VISIBLE);

        } else {
            delete.setVisibility(View.GONE);
        }
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    protected void onClick(View v, ClubFeedNotice o) {

    }


}
