package com.beastbikes.android.modules.cycling.club.ui.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.activity.ui.record.CyclingCompletedActivity;
import com.beastbikes.android.modules.cycling.club.dto.ClubUser;
import com.beastbikes.android.modules.cycling.club.dto.RecordInfo;
import com.beastbikes.android.modules.user.util.ActivityDataUtil;
import com.beastbikes.android.utils.DateUtil;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class FeedItemCycling extends LinearLayout implements View.OnClickListener {

    private TextView tvTitle;
    private TextView tvDate;
    private TextView tvPlace;
    private ImageView ivImage;
    private ClubUser user;
    private RecordInfo info;
    private Context context;
    public FeedItemCycling(Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(this.getContext()).inflate(R.layout.clubfeed_item_cycling, this);
        initView();

    }

    public void initView() {
        this.tvTitle = (TextView) findViewById(R.id.title);
        this.tvDate = (TextView)  findViewById(R.id.date);
        this.tvPlace = (TextView)  findViewById(R.id.place);
        this.ivImage = (ImageView) findViewById(R.id.image);
        this.setOnClickListener(this);
    }


    public void bind(RecordInfo o, ClubUser user) {
        if (o != null) {
            this.info = o;
            this.user = user;
            String title = o.getTitle();
            if (TextUtils.isEmpty(title) || title.equals("null")) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
                String date = sdf.format(o.getStartDate());
                if (getContext() != null) {
                    title = ActivityDataUtil.formatDateTime(getContext(), o.getStartDate().getTime());
                    this.tvTitle.setText(date + title);
                }
            } else {
                this.tvTitle.setText(title);
            }
            this.tvTitle.setText(o.getTitle());

            this.tvDate.setText(getResources().getString
                    (R.string.activity_finished_activity_elapsed_label) +
                    DateUtil.toTimeBySecond((int) o.getTime() / 1000));
            double distance = 0;
            if (o.getDistance() > 0) {
                distance = o.getDistance() / 1000;
            }
            this.tvPlace.setText(getResources().getString(R.string.activity_param_label_distance) + ": " + String.format("%.2f", distance) + " km");

            if (!TextUtils.isEmpty(o.getCyclingImage())) {
                Picasso.with(getContext()).load(o.getCyclingImage()).fit().error(R.drawable.ic_map_loading)
                        .placeholder(R.drawable.ic_map_loading).centerCrop().into(this.ivImage);
            } else {
                this.ivImage.setImageResource(R.drawable.ic_map_loading);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == this) {
            final Intent intent;

            intent = new Intent(getContext(), CyclingCompletedActivity.class);

            if(user != null) {
                intent.putExtra(CyclingCompletedActivity.EXTRA_USER_ID, user.getUserId());
                intent.putExtra(CyclingCompletedActivity.EXTRA_AVATAR_URL, user.getAvatar());
                intent.putExtra(CyclingCompletedActivity.EXTRA_NICK_NAME, user.getNickName());
            }
            intent.putExtra(CyclingCompletedActivity.EXTRA_SPORT_IDENTIFY, info.getSportIdentify());
            getContext().startActivity(intent);
        }
    }


}
