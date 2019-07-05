package com.beastbikes.android.modules.cycling.ranking.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.cycling.ranking.dto.RankDTO2;
import com.beastbikes.android.utils.NickNameRemarksUtil;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by caoxiao on 15/12/3.
 */
public final class RankViewHolder2 extends ViewHolder<RankDTO2> {

    private RequestQueueManager rqm;

    private View view;

    @IdResource(R.id.rank_list_item_layout)
    public RelativeLayout rankListItemLayout;

    @IdResource(R.id.ranking_fragment_list_item_ordinal)
    private TextView ordinal;

    @IdResource(R.id.ranking_fragment_list_item_avatar)
    private CircleImageView avatar;

    @IdResource(R.id.ranking_fragment_list_item_nickname)
    private TextView nickname;

    @IdResource(R.id.ranking_fragment_list_item_level)
    private TextView levelTv;

    @IdResource(R.id.ranking_fragment_list_item_location)
    private TextView locationTv;

    @IdResource(R.id.ranking_fragment_list_item_city)
    private TextView city;

    @IdResource(R.id.ranking_fragment_list_item_district)
    private TextView district;

    @IdResource(R.id.ranking_fragment_list_item_distance)
    private TextView distance;

    @IdResource(R.id.ranking_framgent_list_item_km)
    private TextView distanceUnit;

    private boolean isChineseVersion = true;

    public RankViewHolder2(RequestQueueManager rqm, View v, boolean isChineseVersion) {
        super(v);
        this.rqm = rqm;
        this.view = v;
        this.isChineseVersion = isChineseVersion;
    }

    @Override
    public void bind(RankDTO2 dto) {
        if (dto == null)
            return;
        switch (dto.getOrdinal()) {
            case 1:
                this.ordinal.setBackgroundResource(R.drawable.ordinal_bg_1);
                break;
            case 2:
                this.ordinal.setBackgroundResource(R.drawable.ordinal_bg_2);
                break;
            case 3:
                this.ordinal.setBackgroundResource(R.drawable.ordinal_bg_3);
                break;
            default:
                this.ordinal.setBackgroundResource(R.drawable.transparent);
                break;
        }
        final String avatarUrl = dto.getAvatarImage();
        if (!TextUtils.isEmpty(avatarUrl)) {
            Picasso.with(getContext()).load(avatarUrl).fit().error(R.drawable.ic_avatar).placeholder(R.drawable.ic_avatar)
                    .centerCrop().into(avatar);
        } else {
            this.avatar.setImageResource(R.drawable.ic_avatar);
        }

        this.ordinal.setText(String.valueOf(dto.getOrdinal()));

        String nickName = dto.getNickname();
        if (TextUtils.isEmpty(nickName))
            nickName = dto.getNickname();

        if (nickName == null)
            nickName = "";

        this.nickname.setText(NickNameRemarksUtil.disPlayName(nickName, dto.getRemarks()));

        String provice = dto.getProvince();
        if (!TextUtils.isEmpty(provice) && !provice.equals("null")
                && !provice.equals("unknown")) {
            this.locationTv.setText(provice);
        } else {
            this.locationTv.setText("");
        }

        String city = dto.getCity();
        if (!TextUtils.isEmpty(city) && !city.equals("null")
                && !city.equals("unknown")) {
            this.city.setText(city);
        } else {
            this.city.setText("");
        }

        String district = dto.getCity();
        if (!TextUtils.isEmpty(district) && !district.equals("null")
                && !district.equals("unknown")) {
            this.district.setText(dto.getCity());
        } else {
            this.district.setText("");
        }
        double distance = dto.getMilestone();
        if (distance < 0) {
            distance = 0;
        }

        if (isChineseVersion) {
            this.distance.setText(String.format("%.0f", distance / 1000f));
        } else {
            this.distance.setText(String.format("%.0f", LocaleManager.kilometreToMile(distance / 1000f)));
            distanceUnit.setText(LocaleManager.STRINGMILE);
        }

        if (dto.getLevel() == 128) {
            this.levelTv.setVisibility(View.VISIBLE);
        } else {
            this.levelTv.setVisibility(View.GONE);
        }

    }

}

