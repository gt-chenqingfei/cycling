package com.beastbikes.android.modules.cycling.sections.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.cycling.sections.dto.SectionListDTO;
import com.beastbikes.android.utils.SpeedXFormatUtil;
import com.beastbikes.android.widget.SwipeRefreshLoadRecyclerView;
import com.beastbikes.android.widget.helper.ItemClickListener;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

/**
 * Created by caoxiao on 16/4/5.
 */
public class SectionRecyclerViewAdapter extends SwipeRefreshLoadRecyclerView.BaseRecyclerViewViewAdapter {

    private Context context;
    private ItemClickListener itemClickListener;
    private boolean isShowKilometerOrMiles;

    public SectionRecyclerViewAdapter(Context context, ItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        isShowKilometerOrMiles = LocaleManager.isDisplayKM(context);
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder() {
        SectionViewHolder sectionViewHolder = new SectionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_competition_section, null,
                false));
        return sectionViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Object value, final int position, boolean isLastItem) {
        if (holder instanceof SectionViewHolder) {
            final SectionViewHolder viewHolder = (SectionViewHolder) holder;
            SectionListDTO sectionListDTO = (SectionListDTO) value;
            if (!TextUtils.isEmpty(sectionListDTO.getLordAvatar())) {
                Picasso.with(context)
                        .load(sectionListDTO.getLordAvatar())
                        .fit().placeholder(R.drawable.ic_launch_logo)
                        .error(R.drawable.ic_launch_logo)
                        .centerCrop()
                        .into(viewHolder.sectionAvater);
            } else {
                Picasso.with(context)
                        .load(R.drawable.ic_launch_logo)
                        .fit().placeholder(R.drawable.ic_launch_logo)
                        .error(R.drawable.ic_launch_logo)
                        .centerCrop()
                        .into(viewHolder.sectionAvater);
            }
            viewHolder.sectionTitle.setText(sectionListDTO.getName());
            if (TextUtils.isEmpty(sectionListDTO.getLordNick())) {
                viewHolder.sectionOwner.setText(context.getResources().getString(R.string.section_no_lord));
            } else {
                viewHolder.sectionOwner.setText(sectionListDTO.getLordNick() + context.getResources().getString(R.string.occupy));
            }
            viewHolder.sectionRatingbar.setRating(sectionListDTO.getDifficult());

            if (isShowKilometerOrMiles) {
                viewHolder.sectionDiatance.setText(context.getResources().getString(R.string.distance_less_than) + SpeedXFormatUtil.BigDecimalOne(sectionListDTO.getRange() / 1000) + context.getResources().getString(R.string.task_info_activity_joined_unit));
                viewHolder.sectionElevation.setText(context.getResources().getString(R.string.altitude_difference) + " " + (int) sectionListDTO.getAltDiff() + LocaleManager.LocaleString.meter);
                double legLength = sectionListDTO.getLegLength() / 1000;
                if (legLength < 10) {
                    BigDecimal bd = new BigDecimal(legLength);
                    bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                    viewHolder.sectionTotalDiatance.setText(bd + "");
                } else {
                    viewHolder.sectionTotalDiatance.setText((int) legLength + "");
                }
                viewHolder.sectionTotalDiatanceUnit.setText(context.getResources().getString(R.string.kilometre));
            } else {
                double mi = LocaleManager.kilometreToMile(sectionListDTO.getRange() / 1000);
                viewHolder.sectionDiatance.setText(context.getResources().getString(R.string.distance_less_than) + SpeedXFormatUtil.BigDecimalOne(mi) + context.getResources().getString(R.string.mi));
                viewHolder.sectionElevation.setText(context.getResources().getString(R.string.altitude_difference) + " " + (int) LocaleManager.metreToFeet(sectionListDTO.getAltDiff()) + LocaleManager.LocaleString.feet);
                double legLength = LocaleManager.kilometreToMile(sectionListDTO.getLegLength() / 1000);
                if (legLength < 10) {
                    BigDecimal bd = new BigDecimal(legLength);
                    bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                    viewHolder.sectionTotalDiatance.setText(bd + "");
                } else {
                    viewHolder.sectionTotalDiatance.setText((int) legLength + "");
                }
                viewHolder.sectionTotalDiatanceUnit.setText(context.getResources().getString(R.string.miles));
            }
            if (isLastItem) {
                viewHolder.itemSectionDiver.setVisibility(View.GONE);
            } else {
                viewHolder.itemSectionDiver.setVisibility(View.VISIBLE);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.OnItemClick(viewHolder, position);
                }
            });

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemClickListener.OnItemLongClick(viewHolder, position);
                    return true;
                }
            });
        }
    }

    class SectionViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView sectionAvater;
        private TextView sectionTitle;
        private TextView sectionDiatance;
        private TextView sectionOwner;
        private RatingBar sectionRatingbar;
        private TextView sectionElevation;
        private TextView sectionTotalDiatance;
        private TextView sectionTotalDiatanceUnit;
        private View itemSectionDiver;
        public final View itemView;

        public SectionViewHolder(View v) {
            super(v);
            this.itemView = v;
            sectionAvater = (CircleImageView) v.findViewById(R.id.item_competition_section_avater);
            sectionTitle = (TextView) v.findViewById(R.id.item_competition_section_title);
            sectionDiatance = (TextView) v.findViewById(R.id.item_competition_section_diatance);
            sectionOwner = (TextView) v.findViewById(R.id.item_competition_section_owner);
            sectionRatingbar = (RatingBar) v.findViewById(R.id.section_ratingbar);
            sectionElevation = (TextView) v.findViewById(R.id.section_elevation);
            sectionTotalDiatance = (TextView) v.findViewById(R.id.item_competition_section_total_distance);
            sectionTotalDiatanceUnit = (TextView) v.findViewById(R.id.item_competition_section_total_distance_unit);
            itemSectionDiver = v.findViewById(R.id.item_competition_section_diver);
        }
    }
}
