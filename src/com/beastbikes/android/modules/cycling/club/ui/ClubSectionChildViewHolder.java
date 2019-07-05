package com.beastbikes.android.modules.cycling.club.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

public final class ClubSectionChildViewHolder extends ViewHolder<ClubInfoCompact> {

    @IdResource(R.id.tv_name)
    private TextView tvName;

    @IdResource(R.id.tv_city)
    private TextView tvCity;

    @IdResource(R.id.tv_members)
    private TextView tvMembers;

    @IdResource(R.id.club_list_item_avatar)
    private CircleImageView ivAvatar;

    @IdResource(R.id.tv_totaldistance)
    private TextView tvDistance;

    @IdResource(R.id.divider_last_long)
    public View dividerLong;

    @IdResource(R.id.divider_last_short)
    public View dividerShort;

    @IdResource(R.id.iv_type)
    public ImageView ivType;

    public ClubSectionChildViewHolder(View v) {
        super(v);
    }

    @Override
    public void bind(ClubInfoCompact info) {
        if (info == null)
            return;

        tvName.setText(info.getName());
        tvCity.setText(info.getCity());

        double dis = info.getMilestone();
        if (dis < 0) {
            dis = 0;
        }

        String distance = getContext().getString(R.string.club_info_total_distance) +
                ":" + Math.round(dis / 1000) + " km";
        if (!LocaleManager.isDisplayKM(getContext())) {
            dis = LocaleManager.kilometreToMile(dis / 1000);
            distance = getContext().getString(R.string.club_info_total_distance) +
                    ":" + Math.round(dis) + " mi";
        }
        tvDistance.setText(distance);
        tvMembers.setText(getContext().getString(R.string.club_info_item_member_rank_desc) +
                ":" + info.getMembers());
        if (!TextUtils.isEmpty(info.getLogo())) {
            Picasso.with(getContext()).load(info.getLogo()).fit().centerCrop().error(R.drawable.ic_avatar_club)
                    .placeholder(R.drawable.ic_avatar_club).into(this.ivAvatar);
        } else {
            this.ivAvatar.setImageResource(R.drawable.ic_avatar_club);
        }

        if(LocaleManager.isChineseTimeZone()) {
            if (info.getType() == 0) {
                ivType.setImageResource(0);
            } else if (info.getType() == 1) {
                ivType.setImageResource(R.drawable.ic_club_discover_shop);
            } else if (info.getType() == 2) {
                ivType.setImageResource(R.drawable.ic_club_discover_school);
            }
        }
        else{
            ivType.setVisibility(View.INVISIBLE);
        }
    }
}
