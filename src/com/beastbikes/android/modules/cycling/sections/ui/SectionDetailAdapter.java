package com.beastbikes.android.modules.cycling.sections.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.sections.dto.SegmentRankDTO;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by caoxiao on 16/4/10.
 */
public class SectionDetailAdapter extends BaseAdapter {

    private Context context;
    private List<SegmentRankDTO> rankLists;

    public SectionDetailAdapter(Context context, List<SegmentRankDTO> rankLists) {
        this.context = context;
        this.rankLists = rankLists;
    }

    @Override
    public int getCount() {
        return rankLists.size();
    }

    @Override
    public Object getItem(int i) {
        return rankLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SectionDetailHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_section_detail, null);
            vh = new SectionDetailHolder(convertView);
        } else {
            vh = (SectionDetailHolder) convertView.getTag();
        }
        vh.bind(rankLists.get(position));
        switch (position) {
            case 0:
                vh.rankTV.setText("");
                vh.rankTV.setBackgroundResource(R.drawable.ic_section_detail_rank1);
                break;
            case 1:
                vh.rankTV.setText("");
                vh.rankTV.setBackgroundResource(R.drawable.ic_section_detail_rank2);
                break;
            case 2:
                vh.rankTV.setText("");
                vh.rankTV.setBackgroundResource(R.drawable.ic_section_detail_rank3);
                break;
            default:
                vh.rankTV.setText(position + 1 + "");
                vh.rankTV.setBackgroundResource(0);
        }
        return convertView;
    }

    public final class SectionDetailHolder extends ViewHolder<SegmentRankDTO> {

        @IdResource(R.id.item_section_detail_rank_tv)
        private TextView rankTV;

        @IdResource(R.id.item_section_detail_avater)
        private CircleImageView detailAvater;

        @IdResource(R.id.item_section_detail_nickname)
        private TextView detailNickname;

        @IdResource(R.id.item_section_detail_time)
        private TextView detailTime;

        protected SectionDetailHolder(View v) {
            super(v);
        }

        @Override
        public void bind(SegmentRankDTO dto) {
            if (dto == null)
                return;
//            detailNickname.setText(dto.getNickName());
            if (null != dto && !TextUtils.isEmpty(dto.getAvatar())) {
                Picasso.with(context).load(dto.getAvatar()).fit().centerCrop().error(R.drawable.ic_avatar).
                        placeholder(R.drawable.ic_avatar).into(detailAvater);
            } else {
                detailAvater.setImageResource(R.drawable.ic_avatar);
            }
            detailNickname.setText(dto.getNickname());
            double time = dto.getDuration();
            int hour = (int) time / 3600;
            int min = (int) (time - hour * 3600) / 60;
            int second = (int) (time - hour * 3600 - min * 60);
            String houtStr;
            String minStr;
            String secondStr;
            if (hour < 10) {
                houtStr = "0" + hour;
            } else {
                houtStr = "" + hour;
            }
            if (min < 10) {
                minStr = "0" + min;
            } else {
                minStr = "" + min;
            }
            if (second < 10) {
                secondStr = "0" + second;
            } else {
                secondStr = "" + second;
            }
            detailTime.setText(houtStr + ":" + minStr + ":" + secondStr);
        }
    }
}
