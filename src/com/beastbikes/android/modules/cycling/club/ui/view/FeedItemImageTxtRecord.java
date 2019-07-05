package com.beastbikes.android.modules.cycling.club.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedImageTxtRecord;

import java.util.ArrayList;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class FeedItemImageTxtRecord extends FeedItemBase<ClubFeedImageTxtRecord> {

    private FeedItemImage itemImage;
    private FeedItemCycling cycling;
    View defaultView;

    public FeedItemImageTxtRecord(Context context, View converView, AVUser user) {
        super(context, converView, user);
    }

    @Override
    public void initView() {
        this.setOrientation(VERTICAL);
        itemImage = new FeedItemImage(this.getContext());
        itemImage.setVisibility(View.GONE);

        cycling = new FeedItemCycling(this.getContext());
        cycling.setVisibility(View.GONE);

        defaultView = new View(this.getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 10);
        defaultView.setLayoutParams(params);

    }

    @Override
    public void bind(ClubFeedImageTxtRecord o) {

        if (o == null)
            return;

        if (this.getChildCount() <= 0) {
            if (o.getImageList() != null && o.getImageList().size()>0) {
                this.addView(itemImage);
                this.addView(defaultView);
                this.addView(cycling);
            } else {
                this.addView(cycling);
                this.addView(defaultView);
                this.addView(itemImage);
            }
        }

        bindBase(o);

        if (o.getImageList() != null) {
            itemImage.setVisibility(View.VISIBLE);
            itemImage.bind((ArrayList) o.getImageList());

        }
        if (o.getRecordInfo() != null) {
            cycling.setVisibility(View.VISIBLE);
            cycling.bind(o.getRecordInfo(), o.getUser());
        }
        if (o.getImageList() != null && o.getRecordInfo() != null) {
            LayoutParams params = (LayoutParams) cycling.getLayoutParams();
            params.topMargin = 10;
            cycling.setLayoutParams(params);
        }

        invalidate();
    }

    @Override
    public View getView() {

        return this;
    }

    @Override
    protected void onClick(View v, ClubFeedImageTxtRecord o) {

    }


}
