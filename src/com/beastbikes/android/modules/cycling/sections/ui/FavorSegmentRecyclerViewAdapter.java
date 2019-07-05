package com.beastbikes.android.modules.cycling.sections.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.cycling.sections.dto.UserSegmentDTO;
import com.beastbikes.android.widget.SwipeRefreshLoadRecyclerView;
import com.beastbikes.android.widget.helper.ItemClickListener;

import java.math.BigDecimal;

/**
 * Created by caoxiao on 16/4/18.
 */
public class FavorSegmentRecyclerViewAdapter extends SwipeRefreshLoadRecyclerView.BaseRecyclerViewViewAdapter {

    private Context context;
    private ItemClickListener itemClickListener;
    private boolean isShowKilometerOrMiles;

    public FavorSegmentRecyclerViewAdapter(Context context, ItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        isShowKilometerOrMiles = LocaleManager.isDisplayKM(context);
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder() {
        FavorSegmentViewHolder sectionViewHolder = new FavorSegmentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_favor_segment, null,
                false));
        return sectionViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Object value, final int position, boolean isLastItem) {
        if (holder instanceof FavorSegmentViewHolder) {
            final FavorSegmentViewHolder viewHolder = (FavorSegmentViewHolder) holder;
            UserSegmentDTO sectionListDTO = (UserSegmentDTO) value;
            viewHolder.nameTV.setText(sectionListDTO.getName());
            int rank = sectionListDTO.getRank();
            if (rank > 0 && rank <= 3) {
                viewHolder.rankTV.setText(rank + "");
                viewHolder.rankTV.setBackgroundResource(R.drawable.ic_favor_segment_rank1);
            } else if (rank > 3 && rank < 100) {
                viewHolder.rankTV.setText(rank + "");
                viewHolder.rankTV.setBackgroundResource(R.drawable.ic_favor_segment_rank2);
            } else if (rank >= 100) {
                viewHolder.rankTV.setText("");
                viewHolder.rankTV.setBackgroundResource(R.drawable.ic_favor_segment_rank3);
            } else {
                viewHolder.rankTV.setText("");
                viewHolder.rankTV.setBackgroundResource(0);
            }

            long time = sectionListDTO.getDuration();
            int hour = (int) time / 3600;
            String hourStr = "";
            if (hour == 0) {
                hourStr = "00";
            } else if (hour < 10) {
                hourStr = "0" + hour;
            } else {
                hourStr = "" + hour;
            }
            int min = (int) (time - hour * 3600) / 60;
            String minStr = "";
            if (min == 0) {
                minStr = "00";
            } else if (min < 10) {
                minStr = "0" + min;
            } else {
                minStr = "" + min;
            }
            int second = (int) time - hour * 3600 - min * 60;
            String secondStr = "";
            if (second == 0) {
                secondStr = "00";
            } else if (second < 10) {
                secondStr = "0" + second;
            } else {
                secondStr = "" + second;
            }
            String timeStr = hourStr + ":" + minStr + ":" + secondStr;

            if (isShowKilometerOrMiles) {
                double legLength = sectionListDTO.getLegLength() / 1000;
                String legLengthStr = "";
                if (legLength < 10) {
                    BigDecimal bd = new BigDecimal(legLength);
                    bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                    legLengthStr = bd + "";
                } else {
                    legLengthStr = (int) legLength + "";
                }

                double avgSpeed = sectionListDTO.getAvgSpeed();
                if (avgSpeed < 10) {
                    BigDecimal bdAvg = new BigDecimal(avgSpeed);
                    bdAvg = bdAvg.setScale(1, BigDecimal.ROUND_HALF_UP);
                    viewHolder.detailTV.setText(legLengthStr + context.getResources().getString(R.string.task_info_activity_joined_unit) + "-" + timeStr + "-" + bdAvg + context.getResources().getString(R.string.label_speed_per_hour));
                } else {
                    viewHolder.detailTV.setText(legLengthStr + context.getResources().getString(R.string.task_info_activity_joined_unit) + "-" + timeStr + "-" + (int) avgSpeed + context.getResources().getString(R.string.label_speed_per_hour));
                }

            } else {
                double legLength = LocaleManager.kilometreToMile(sectionListDTO.getLegLength()) / 1000;

                String legLengthStr = "";
                if (legLength < 10) {
                    BigDecimal bd = new BigDecimal(legLength);
                    bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                    legLengthStr = bd + "";
                } else {
                    legLengthStr = (int) legLength + "";
                }

                BigDecimal bd = new BigDecimal(legLength);
                bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                double avgSpeed = LocaleManager.kphToMph(sectionListDTO.getAvgSpeed());
                if (avgSpeed < 10) {
                    BigDecimal bdAvg = new BigDecimal(avgSpeed);
                    bdAvg = bdAvg.setScale(1, BigDecimal.ROUND_HALF_UP);
                    viewHolder.detailTV.setText(legLengthStr + context.getResources().getString(R.string.mi) + "-" + timeStr + "-" + bdAvg + LocaleManager.LocaleString.activity_max_speed_unit);
                } else {
                    viewHolder.detailTV.setText(legLengthStr + context.getResources().getString(R.string.mi) + "-" + timeStr + "-" + (int) avgSpeed + LocaleManager.LocaleString.activity_max_speed_unit);
                }
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.OnItemClick(viewHolder, position);
                }
            });

        }
    }

    class FavorSegmentViewHolder extends RecyclerView.ViewHolder {
        private TextView rankTV;
        private TextView nameTV;
        private TextView detailTV;
        public final View itemView;

        public FavorSegmentViewHolder(View v) {
            super(v);
            this.itemView = v;
            rankTV = (TextView) v.findViewById(R.id.item_favor_segment_rank_tv);
            nameTV = (TextView) v.findViewById(R.id.item_favor_segment_name_tv);
            detailTV = (TextView) v.findViewById(R.id.item_favor_segment_detail_tv);
        }
    }
}
