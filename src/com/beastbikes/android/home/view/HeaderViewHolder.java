package com.beastbikes.android.home.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.cycling.activity.dto.MyGoalInfoDTO;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chenqingfei on 16/1/7.
 */
public class HeaderViewHolder implements SharedPreferences.OnSharedPreferenceChangeListener{

    private View convertView;
    private ImageView ivAvtar;
    private TextView tvUserName;
    private TextView tvClubName;
    private TextView tvDot;
    private TextView progress;
    private ProgressBar pbTarget;
    private Context context;
    private MyGoalInfoDTO goalInfo;
    private AVUser user;

    public HeaderViewHolder(Context context, ViewGroup group) {
        user = AVUser.getCurrentUser();

        this.context = context;
        convertView = LayoutInflater.from(context).
                inflate(R.layout.header_just_username, group, false);

        ivAvtar = (ImageView) convertView.findViewById(R.id.iv_avatar1);
        tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
        tvClubName = (TextView) convertView.findViewById(R.id.tv_club_name);
        progress = (TextView) convertView.findViewById(R.id.tv_progress_value);
        pbTarget = (ProgressBar) convertView.findViewById(R.id.pb_target);
        tvDot = (TextView) convertView.findViewById(R.id.dot_userinfo_item);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        SharedPreferences sp = context.getSharedPreferences(user.getObjectId(), 0);
        sp.registerOnSharedPreferenceChangeListener(this);
        refreshGoal(sp);
    }

    public View getView() {
        return convertView;
    }

    public void bind() {
        user = AVUser.getCurrentUser();
        if (user == null) return;

        if (TextUtils.isEmpty(user.getAvatar())) {
            Picasso.with(context).load(R.drawable.ic_avatar);
        } else {
            Picasso.with(context).load(user.getAvatar())
                    .fit().placeholder(R.drawable.ic_avatar).
                    error(R.drawable.ic_avatar).centerCrop().into(ivAvtar);
        }

        tvUserName.setText(user.getDisplayName());
        tvClubName.setText(user.getClubName());
    }

    public void onResume(){
        bind();
    }

    private void refreshGoal(SharedPreferences sp) {
        String goalInfoStr = sp.getString(Constants.PREF_CYCLING_MY_GOAL_KEY, "");
        try {
            JSONObject obj = new JSONObject(goalInfoStr);
            goalInfo = new MyGoalInfoDTO(obj);

            if (goalInfo.getCurGoal() > 0 && goalInfo.getMyGoal() > 0) {

                String p = String.format("%.0f", (goalInfo.getCurGoal() / goalInfo.getMyGoal() * 100));
                final int prece = Integer.valueOf(p);

                pbTarget.setProgress(prece);
                pbTarget.setMax(100);
                progress.setText(prece + "%");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.PREF_CYCLING_MY_GOAL_KEY)) {
            refreshGoal(sharedPreferences);
        } else if (key.equals(Constants.PREF_UPDATE_USERINFO)) {
            bind();
        } else if (key.equals(Constants.PREF_CLUB_STATUS)) {
            int status = sharedPreferences.getInt(key, 0);

            switch (status) {
                case ClubInfoCompact.CLUB_STATUS_JOINED:
                case ClubInfoCompact.CLUB_STATUS_ESTABLISHED:
                case ClubInfoCompact.CLUB_STATUS_NONE:
                    bind();
                    break;
            }
        }
    }

}
