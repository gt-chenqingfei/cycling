package com.beastbikes.android.modules.cycling.route.dto;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class RouteCommentAdapter extends BaseAdapter {


    private List<RouteCommentDTO> rcdList;

    public RouteCommentAdapter(
                               List<RouteCommentDTO> rcdList) {
        this.rcdList = rcdList;
    }

    @Override
    public int getCount() {
        return this.rcdList.size();
    }

    @Override
    public Object getItem(int position) {
        return rcdList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RouteCommentViewHolder holder;

        if (null == convertView) {
            convertView = View.inflate(parent.getContext(),
                    R.layout.route_comment_list_item, null);
            holder = new RouteCommentViewHolder(convertView);
        } else {
            holder = (RouteCommentViewHolder) convertView.getTag();
        }

        holder.bind(rcdList.get(position));
        return convertView;
    }

    private final class RouteCommentViewHolder extends
            ViewHolder<RouteCommentDTO> {


        @IdResource(R.id.route_activity_comment_user)
        private CircleImageView avatar;

        @IdResource(R.id.route_comment_user_name)
        private TextView userName;

        @IdResource(R.id.route_comment_date)
        private TextView createTime;

        @IdResource(R.id.route_comment_content)
        private TextView content;

        protected RouteCommentViewHolder( View v) {
            super(v);
        }

        @Override
        public void bind(RouteCommentDTO rcd) {
            final String avatarUrl = rcd.getAvatarUrl();

            if (!TextUtils.isEmpty(avatarUrl)) {
                Picasso.with(getContext()).load(avatarUrl).fit().error(R.drawable.ic_avatar)
                        .placeholder(R.drawable.ic_avatar).centerCrop().into(this.avatar);
            } else {
                this.avatar.setImageResource(R.drawable.ic_avatar);
            }

            this.userName.setText(rcd.getNickName());

            final CharSequence span = DateUtils.getRelativeTimeSpanString(rcd
                    .getCreateTime().getTime());
            if (System.currentTimeMillis()-rcd.getCreateTime().getTime()<60000) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                sdf.setTimeZone(TimeZone.getDefault());
                this.createTime.setText(sdf.format(rcd.getCreateTime().getTime()));
            }
            else {
                this.createTime.setText(span);
            }
            this.content.setText(rcd.getContent());
        }

    }

}
