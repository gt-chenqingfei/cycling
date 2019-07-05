package com.beastbikes.android.modules.cycling.ranking.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.cycling.ranking.dto.RankDTO;
import com.beastbikes.android.utils.NickNameRemarksUtil;
import com.beastbikes.android.widget.SwipeRefreshLoadRecyclerView;
import com.beastbikes.android.widget.helper.ItemClickListener;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by caoxiao on 16/1/6.
 */
public class RankRecyclerViewAdapter extends SwipeRefreshLoadRecyclerView.BaseRecyclerViewViewAdapter {
    private Context context;
    private ItemClickListener itemClickListener;

    public RankRecyclerViewAdapter(Context context, ItemClickListener listener) {
        this.context = context;
        this.itemClickListener = listener;
    }

    public class HeadViewHolder extends RecyclerView.ViewHolder {
        //        public TextView tv;
        public CircleImageView avater;
        public TextView nickname;
        public TextView cityname;
        public TextView clubname;
        public TextView distance;
        public TextView distanceunit;
        public TextView rank;
        public TextView rankUnit;
        public TextView rankDesc;

        public HeadViewHolder(View view) {
            super(view);
//            tv = (TextView) view.findViewById(R.id.headtv);
            avater = (CircleImageView) view.findViewById(R.id.avater);
            nickname = (TextView) view.findViewById(R.id.nickname);
            cityname = (TextView) view.findViewById(R.id.cityname);
            clubname = (TextView) view.findViewById(R.id.clubname);
            distance = (TextView) view.findViewById(R.id.distance);
            distanceunit = (TextView) view.findViewById(R.id.distanceunit);
            rank = (TextView) view.findViewById(R.id.rank);
            rankUnit = (TextView) view.findViewById(R.id.rankunit);
            rankDesc = (TextView) view.findViewById(R.id.rank_desc);
        }
    }

    class RankingViewHolder extends RecyclerView.ViewHolder {

        public TextView nickname;
        public TextView cityName;
        private TextView clubname;
        private CircleImageView avatar;
        private TextView distanceValue;
        private TextView distanceUnit;
        private TextView ranktv;
        private View shortSplitLine;
        private View longSplitLine;
        private RelativeLayout rankingNum;
        private View view;

        public RankingViewHolder(View view) {
            super(view);
            this.view = view;
            this.nickname = (TextView) view.findViewById(R.id.nickname);
            this.cityName = (TextView) view.findViewById(R.id.cityName);
            this.clubname = (TextView) view.findViewById(R.id.clubname);
            this.avatar = (CircleImageView) view.findViewById(R.id.ranking_fragment_list_item_avatar);
            this.distanceValue = (TextView) view.findViewById(R.id.distanceValue);
            this.distanceUnit = (TextView) view.findViewById(R.id.distanceUnit);
            this.ranktv = (TextView) view.findViewById(R.id.ranktv);
            this.shortSplitLine = view.findViewById(R.id.shortSplitLine);
            this.longSplitLine = view.findViewById(R.id.longSplitLine);
            this.rankingNum = (RelativeLayout) view.findViewById(R.id.rankingNum);
        }
    }

    @Override
    public RecyclerView.ViewHolder getHeadViewHolder() {//不是必须，有header时候才需要重写否则不需要重写
        View view = LayoutInflater.from(context)
                .inflate(R.layout.rankheadview, null, false);
        return new HeadViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder() {
        RankingViewHolder holder = new RankingViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_rank, null,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Object value, final int position, boolean isLastItem) {
        if (holder instanceof RankingViewHolder) {
            final RankingViewHolder viewHolder = (RankingViewHolder) holder;
            RankDTO rankDTO = (RankDTO) value;
//            viewHolder.nickname.setText(rankDTO.getNickname());
            viewHolder.nickname.setText(NickNameRemarksUtil.disPlayName(rankDTO.getNickname(), rankDTO.getRemarks()));
            String city = rankDTO.getCity();
            if (!TextUtils.isEmpty(city) && !city.equals("null")) {
                viewHolder.cityName.setVisibility(View.VISIBLE);
                viewHolder.cityName.setText(city);
            } else {
                viewHolder.cityName.setVisibility(View.GONE);
            }
            String club = rankDTO.getClubName();
            if (!TextUtils.isEmpty(club) && !club.equals("null")) {
                viewHolder.clubname.setText(club);
            } else {
                viewHolder.clubname.setText("");
            }
            double milestone = rankDTO.getRankDistance() / 1000;
            if (!LocaleManager.isDisplayKM(context)) {
                viewHolder.distanceUnit.setText(context.getResources().getString(R.string.mi));
                milestone = LocaleManager.kilometreToMile(milestone);
            }
            viewHolder.distanceValue.setText((int) milestone + "");
            viewHolder.ranktv.setText(position + 1 + "");
            if (isLastItem) {
                viewHolder.longSplitLine.setVisibility(View.VISIBLE);
                viewHolder.shortSplitLine.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.longSplitLine.setVisibility(View.INVISIBLE);
                viewHolder.shortSplitLine.setVisibility(View.VISIBLE);

            }
            switch (position + 1) {
                case 1:
                    viewHolder.rankingNum.setBackgroundResource(R.drawable.rankno1);
                    break;
                case 2:
                    viewHolder.rankingNum.setBackgroundResource(R.drawable.rankno2);
                    break;
                case 3:
                    viewHolder.rankingNum.setBackgroundResource(R.drawable.rankno3);
                    break;
                default:
                    viewHolder.rankingNum.setBackgroundResource(R.drawable.transparent);
                    break;
            }
            String avatarUrl = rankDTO.getAvatarUrl();
            if (!TextUtils.isEmpty(avatarUrl)) {
                Picasso.with(context)
                        .load(rankDTO.getAvatarUrl())
                        .fit()
                        .centerCrop().placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .into(viewHolder.avatar);
            } else {
                Picasso.with(context)
                        .load(R.drawable.ic_avatar)
                        .fit()
                        .centerCrop().placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .into(viewHolder.avatar);
            }

            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.OnItemClick(viewHolder, position);
                }
            });
        } else {
            if (value == null)
                return;
            RankDTO rankDTO = (RankDTO) value;
            HeadViewHolder viewHolder = (HeadViewHolder) holder;
            double milestone = rankDTO.getMilestone() / 1000;
            if (!LocaleManager.isDisplayKM(context)) {
                viewHolder.distanceunit.setText(context.getResources().getString(R.string.mi));
                milestone = LocaleManager.kilometreToMile(milestone);
            }
            viewHolder.distance.setText("" + (int) milestone);
            viewHolder.rank.setText(rankDTO.getOrdinal() + "");
            if (milestone <= 0) {
                viewHolder.rank.setVisibility(View.INVISIBLE);
                viewHolder.rankUnit.setVisibility(View.INVISIBLE);
                viewHolder.rankDesc.setVisibility(View.VISIBLE);
            } else {
                viewHolder.rank.setVisibility(View.VISIBLE);
                viewHolder.rankUnit.setVisibility(View.VISIBLE);
                viewHolder.rankDesc.setVisibility(View.INVISIBLE);
            }
            viewHolder.nickname.setText(rankDTO.getNickname());
            viewHolder.clubname.setText(rankDTO.getClubName());
            String city = rankDTO.getCity();
            if (!TextUtils.isEmpty(city) && !city.equals("null"))
                viewHolder.cityname.setText(city);
            String avatarUrl = rankDTO.getAvatarUrl();
            if (!TextUtils.isEmpty(avatarUrl)) {
                Picasso.with(context)
                        .load(rankDTO.getAvatarUrl())
                        .fit().placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .centerCrop()
                        .into(viewHolder.avater);
            } else {
                Picasso.with(context)
                        .load(R.drawable.ic_avatar)
                        .fit().placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .centerCrop()
                        .into(viewHolder.avater);
            }
        }
    }
}

