package com.beastbikes.android.modules.cycling.ranking.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.cycling.ranking.biz.RankingManager;
import com.beastbikes.android.modules.cycling.ranking.dto.RankDTO;
import com.beastbikes.android.utils.NickNameRemarksUtil;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

public final class RankViewHolder extends ViewHolder<RankDTO> {


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

    public RankViewHolder(View v, boolean isChineseVersion) {
        super(v);
        this.view = v;
        this.isChineseVersion = isChineseVersion;
    }

    @Override
    public void bind(RankDTO dto) {
        if (dto == null)
            return;

        switch (dto.getOrdinal()) {
            case 1:
                this.ordinal.setTextColor(getContext().getResources().getColor(
                        R.color.ranking_fragment_ordinal_default));
                this.ordinal.setBackgroundResource(R.drawable.ordinal_bg_1);
                break;
            case 2:
                this.ordinal.setTextColor(getContext().getResources().getColor(
                        R.color.ranking_fragment_ordinal_default));
                this.ordinal.setBackgroundResource(R.drawable.ordinal_bg_2);
                break;
            case 3:
                this.ordinal.setTextColor(getContext().getResources().getColor(
                        R.color.ranking_fragment_ordinal_default));
                this.ordinal.setBackgroundResource(R.drawable.ordinal_bg_3);
                break;
            default:
                this.ordinal.setTextColor(getContext().getResources().getColor(
                        R.color.ranking_fragment_ordinal_other));
                this.ordinal
                        .setBackgroundResource(android.R.color.transparent);
                break;
        }

        final String avatarUrl = dto.getAvatarUrl();
        if (!TextUtils.isEmpty(avatarUrl)) {
            Picasso.with(getContext()).load(avatarUrl).fit().error(R.drawable.ic_avatar).
                    placeholder(R.drawable.ic_avatar).centerCrop().into(avatar);
        } else {
            this.avatar.setImageResource(R.drawable.ic_avatar);
        }

        this.ordinal.setText(String.valueOf(dto.getOrdinal()));

        AVUser user = AVUser.getCurrentUser();
        if (null != user && dto.getUserId().equals(user.getObjectId())) {
            view.setBackgroundResource(R.color.universal_rank_item_color_2);
        } else {
            view.setBackgroundResource(R.color.universal_rank_item_color_1);
        }

        String nickName = dto.getNickname();
        if (TextUtils.isEmpty(nickName))
            nickName = dto.getUsername();

        if (nickName == null)
            nickName = "";

//        this.nickname.setText(nickName);

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

        String district = dto.getDistrict();
        if (!TextUtils.isEmpty(district) && !district.equals("null")
                && !district.equals("unknown")) {
            this.district.setText(dto.getDistrict());
        } else {
            this.district.setText("");
        }

        double distance = dto.getTotalDistance();
        switch (dto.getRankType()) {
            case RankingManager.WEEKLY_RANK:
                distance = dto.getWeeklyDistance();
                break;
            case RankingManager.MONTHLY_RANK:
                distance = dto.getMonthlyDistance();
                break;
            case RankingManager.TOTAL_RANK:
                distance = dto.getTotalDistance();
                break;
        }

        if (distance < 0 ) {
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
