package com.beastbikes.android.modules.cycling.club.ui.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubActivityInfoBrowserActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class FeedItemActivity extends FeedItemBase<ClubFeedActivity> {


    private TextView tvTitle;
    private TextView tvDate;
    private TextView tvPlace;
    private ImageView ivImage;
    private Context context;

    public FeedItemActivity(Context context, View converView, AVUser user) {
        super(context, converView, user);
        this.context = context;
    }

    @Override
    public void initView() {
        tvTitle = (TextView) findViewById(R.id.title);
        tvDate = (TextView) findViewById(R.id.date);
        tvPlace = (TextView) findViewById(R.id.place);
        ivImage = (ImageView) findViewById(R.id.image);
        this.setOnClickListener(this);
    }

    @Override
    public void bind(ClubFeedActivity o) {
        bindBase(o);

        if (o != null) {
            this.tvTitle.setText(o.getTitle());
            this.tvDate.setText(o.getStartDate());
            this.tvPlace.setText(o.getRouteName());
            if (!TextUtils.isEmpty(o.getRouteImage())) {
                Picasso.with(context).load(o.getRouteImage()).fit().centerCrop().into(ivImage);
            }
        }
    }

    @Override
    public View getView() {
        return LayoutInflater.from(this.getContext()).inflate(R.layout.clubfeed_item_activity, this);
    }

    @Override
    protected void onClick(View v, ClubFeedActivity o) {
        if (v == this) {
            if(o == null)
                return;

            String cluId = o.getActId();
            Intent intent = new Intent(getContext(),  ClubActivityInfoBrowserActivity.class);

            Uri uri = Uri.parse(ClubActivityInfoBrowserActivity.
                    getActivityUrl(o.getActId(),getContext()));

            intent.setData(uri);
            intent.putExtra(ClubActivityInfoBrowserActivity.EXTRA_ACTIVITY_TYPE, 1);
            intent.putExtra(ClubActivityInfoBrowserActivity.EXTRA_CLUB_ACTIVITY_ID, cluId);

            getContext().startActivity(intent);
        }
    }


}
