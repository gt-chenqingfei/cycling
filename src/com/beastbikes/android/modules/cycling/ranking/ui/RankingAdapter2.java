package com.beastbikes.android.modules.cycling.ranking.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.cycling.ranking.dto.RankDTO2;
import com.beastbikes.framework.android.schedule.RequestQueueManager;

import java.util.List;

/**
 * Created by caoxiao on 15/12/3.
 */
public final class RankingAdapter2 extends BaseAdapter {
    private final RequestQueueManager rqm;
    private final List<RankDTO2> items;
    private boolean isChineseVersion = true;
    //    private boolean isBlackBackground = false;
//    private int type;
    private String userID;

    public RankingAdapter2(Context context, final RequestQueueManager rqm, List<RankDTO2> items, String userID) {
        this.rqm = rqm;
        this.items = items;
//        this.type = type;
        if (!LocaleManager.isDisplayKM(context)) {
            isChineseVersion = false;
        }
        this.userID = userID;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RankViewHolder2 vh;

        if (convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.ranking_fragment_list_item2, null);

            vh = new RankViewHolder2(this.rqm, convertView, isChineseVersion);
        } else {
            vh = (RankViewHolder2) convertView.getTag();
        }
        RankDTO2 rankDto = items.get(position);
        rankDto.setOrdinal(position + 1);
        vh.bind(rankDto);
        if (rankDto.getUserId().equals(userID)) {
            vh.rankListItemLayout.setBackgroundResource(R.color.discover_color1);
        } else {
            vh.rankListItemLayout.setBackgroundResource(R.color.discover_color3);
        }
//        if (isBlackBackground)
//            vh.rankListItemLayout.setBackgroundResource(R.color.club_create_locationvalues);
        return convertView;
    }
}

