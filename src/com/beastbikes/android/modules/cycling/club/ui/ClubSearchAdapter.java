package com.beastbikes.android.modules.cycling.club.ui;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.framework.ui.android.lib.list.BaseListAdapter;

@SuppressLint("InflateParams")
public class ClubSearchAdapter extends BaseListAdapter<ClubInfoCompact> {


    public ClubSearchAdapter(Handler handler, AbsListView listView  ) {
        super(handler, listView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ClubSectionChildViewHolder vh;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.club_list_item, null);
            vh = new ClubSectionChildViewHolder( convertView);
        } else {
            vh = (ClubSectionChildViewHolder) convertView.getTag();
        }

        vh.bind(getItem(position));

        if (position == getCount() - 1) {
            vh.dividerLong.setVisibility(View.VISIBLE);
            vh.dividerShort.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    @Override
    protected void recycleView(View view) {

    }
}
