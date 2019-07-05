package com.beastbikes.android.modules.cycling.club.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by zhangyao on 2016/1/14.
 */
public class UserHorizontalAdapter extends RecyclerView.Adapter<UserHorizontalAdapter.MyViewHolder>{
    private List<ClubActivityUser> users;
    private Context context;
    private LayoutInflater mInflater;
    public UserHorizontalAdapter(List<ClubActivityUser> users , Context context){
        this.users = users;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(mInflater.inflate(R.layout.activity_club_activty_horizontal_itme,
                parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ClubActivityUser user = users.get(position);
        if (null == user) {
            return;
        }

        if (!TextUtils.isEmpty(user.getAvatar())) {
            Picasso.with(context).load(user.getAvatar()).fit().error(R.drawable.ic_avatar).
                    placeholder(R.drawable.ic_avatar).centerCrop().into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_avatar);
        }
    }

    @Override
    public int getItemCount() {
        if (users.size()>6)
        {
            return 6;
        }
        return users.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.
                    findViewById(R.id.activity_club_activty_horizontal_itme_iv);
        }
    }
}
