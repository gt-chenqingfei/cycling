package com.beastbikes.android.modules.shop.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.shop.dto.BikeShopListDTO;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

public final class BikeShopListViewHolder extends ViewHolder<BikeShopListDTO> {

    @IdResource(R.id.bike_shop_name)
    private TextView tvName;

    @IdResource(R.id.bike_shop_distance)
    private TextView tvDistance;

    @IdResource(R.id.bike_shop_address)
    private TextView tvAdress;

    @IdResource(R.id.bike_shop_item_avatar)
    private CircleImageView ivAvatar;

    @IdResource(R.id.bike_shop_auth)
    private TextView tvAuth;

    @IdResource(R.id.bike_shop_tag_activity)
    private TextView tvTagActivity;

    @IdResource(R.id.bike_shop_tag_after_sell)
    public TextView tvTagAfterSell;

    @IdResource(R.id.bike_shop_tag_care)
    public TextView tvTagCare;

    @IdResource(R.id.bike_shop_tag_fix)
    public TextView tvTagFix;

    @IdResource(R.id.bike_shop_tag_rent)
    public TextView tvTagRent;

    @IdResource(R.id.bike_shop_tag_sell)
    public TextView tvTagSell;

    @IdResource(R.id.bike_shop_status)
    public TextView tvStatus;

    private String type;

    public BikeShopListViewHolder(View v, String type) {
        super(v);
        this.type = type;
    }

    private String userId;

    @Override
    public void bind(BikeShopListDTO info) {
        if (info == null)
            return;
        if (AVUser.getCurrentUser() != null) {
            userId = AVUser.getCurrentUser().getObjectId();
        }

        tvName.setText(info.getName());

        double dis = info.getRange();
        if (dis < 0) {
            dis = 0;
        }

        String distance = Math.round(dis / 1000) + " km";
        if (!LocaleManager.isDisplayKM(getContext())) {
            dis = LocaleManager.kilometreToMile(dis / 1000);
            distance = Math.round(dis) + " mi";
        }
        tvDistance.setText("<" + distance);

        tvAdress.setText(info.getAddress());
        if (!TextUtils.isEmpty(info.getAvatar())) {
            Picasso.with(getContext()).load(info.getAvatar()).fit().centerCrop().error(R.drawable.ic_avatar_club)
                    .placeholder(R.drawable.ic_avatar_club).into(this.ivAvatar);
        } else {
            this.ivAvatar.setImageResource(R.drawable.ic_avatar_club);
        }


        if (info.getTagInfo() != null) {
            tvTagActivity.setVisibility(info.getTagInfo().isActivity() ? View.VISIBLE : View.GONE);
//            tvTagAfterSell.setVisibility(info.getLevel() == 1  ? View.VISIBLE : View.GONE);
            tvTagCare.setVisibility(info.getTagInfo().isCare() ? View.VISIBLE : View.GONE);
            tvTagFix.setVisibility(info.getTagInfo().isFix() ? View.VISIBLE : View.GONE);
            tvTagRent.setVisibility(info.getTagInfo().isRent() ? View.VISIBLE : View.GONE);
//            tvTagSell.setVisibility(info.getLevel() == 1  ? View.VISIBLE : View.GONE);
            tvAuth.setVisibility(info.getLevel() == 1 ? View.VISIBLE : View.GONE);
        }

        if (this.type.equals("mine")) {
            tvStatus.setVisibility(View.VISIBLE);
            tvDistance.setVisibility(View.GONE);
            tvStatus.setSelected(info.getStatus() == BikeShopListDTO.STATUS_UNTREATED);
            tvStatus.setText(info.getStatus() == BikeShopListDTO.STATUS_UNTREATED ? R.string.bike_shop_applying : R.string.bike_shop_unpass);
        } else {
            tvStatus.setVisibility(View.GONE);
            tvDistance.setVisibility(View.VISIBLE);
        }

    }
}
