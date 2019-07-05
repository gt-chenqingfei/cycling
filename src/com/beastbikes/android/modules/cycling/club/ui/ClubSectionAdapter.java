package com.beastbikes.android.modules.cycling.club.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.ui.android.lib.list.BaseSectionListFilterAdapter;

import java.util.ArrayList;

@SuppressLint("InflateParams")
public class ClubSectionAdapter extends
        BaseSectionListFilterAdapter<ClubSectionGroupable, ClubInfoCompact> {
    public interface OnItemClickListener {
        public void onItemClick(ClubInfoCompact info);
    }

    private OnItemClickListener listener;
    private RequestQueueManager rqm;

    public ClubSectionAdapter(Context activity, ExpandableListView listview,
                              ArrayList<ClubSectionGroupable> groups, RequestQueueManager rqm,
                              OnItemClickListener listener) {
        super(activity, listview, groups);
        this.listener = listener;
        this.rqm = rqm;
    }

    @Override
    protected boolean isChildMatched(ClubInfoCompact child, String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return true;
        }
        return child.getName().contains(keyword);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final ClubSectionChildViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.club_list_item, null);
            vh = new ClubSectionChildViewHolder(convertView);
        } else {
            vh = (ClubSectionChildViewHolder) convertView.getTag();
        }
        final ClubInfoCompact userInfo = getChild(groupPosition, childPosition);
        vh.bind(userInfo);
        if (isLastChild) {
            vh.dividerLong.setVisibility(View.VISIBLE);
        } else {
            vh.dividerLong.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (listener != null)
                    listener.onItemClick(userInfo);
            }
        });
        convertView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                // TODO Auto-generated method stub
                return false;
            }
        });
        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupHodler hodler = null;

        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.pull_to_refresh_list_section, null);
            hodler = new GroupHodler(convertView);
            convertView.setTag(hodler);
        } else {
            hodler = (GroupHodler) convertView.getTag();
        }
        ClubSectionGroupable clubSectionGroupable = getGroup(groupPosition);
        if (clubSectionGroupable != null) {
            hodler.fill(clubSectionGroupable, groupPosition);
        }
        return convertView;
    }

    @Override
    public void recycleView(View view) {

    }

    private class GroupHodler {
        public TextView tvName;
        public TextView tvName2;
        public LinearLayout llMore;
        public View divider2;

        public GroupHodler(View baseView) {
            tvName = (TextView) baseView.findViewById(R.id.textView);
            llMore = (LinearLayout) baseView.findViewById(R.id.llmore);
            tvName2 = (TextView) baseView.findViewById(R.id.textViewmore);
            divider2 = baseView.findViewById(R.id.divider_2);
        }

        public void fill(ClubSectionGroupable info, int pos) {
            tvName.setText(info.getTitle());
            divider2.setVisibility(pos == 0 ? View.GONE : View.VISIBLE);
            if (!TextUtils.isEmpty(info.getTitle2())) {
                llMore.setVisibility(View.VISIBLE);
                tvName2.setText(info.getTitle2());
                if (null != info.getListener()) {
                    llMore.setOnClickListener(info.getListener());
                }
            }
        }
    }
}
