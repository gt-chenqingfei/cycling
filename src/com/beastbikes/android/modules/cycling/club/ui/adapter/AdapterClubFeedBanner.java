package com.beastbikes.android.modules.cycling.club.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenqingfei on 15/12/2.
 */
public class AdapterClubFeedBanner extends PagerAdapter implements SharedPreferences.OnSharedPreferenceChangeListener, Constants {

    private Context context;
    private ClubInfoCompact clubInfo;
    private LayoutInflater inflater;
    private Map<Integer, ViewHolder> hoderMap = new HashMap<Integer, ViewHolder>();
    private SharedPreferences userSp;

    public AdapterClubFeedBanner(ClubInfoCompact club, Context context) {
        this.clubInfo = club;
        this.context = context;
        inflater = LayoutInflater.from(context);
        AVUser user = AVUser.getCurrentUser();
        if (null != user) {
            userSp = context.getSharedPreferences(user.getObjectId(), 0);
            this.userSp.registerOnSharedPreferenceChangeListener(this);
        }
    }

    public void notifyDataSetChanged(ClubInfoCompact clubInfo) {
        super.notifyDataSetChanged();
        this.clubInfo = clubInfo;
        for (int i = 0; i < hoderMap.size(); i++) {
            ViewHolder hoder = hoderMap.get(i);
            hoder.bind(i);
        }
    }

    public void notifyClubUpdateChanged(String path) {
        if (hoderMap == null || hoderMap.isEmpty())
            return;
        ViewHolder hoder = hoderMap.get(0);
        hoder.notifyClubUpdateChanged(path);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View converView = inflater.inflate(R.layout.clubfeed_banner, view, false);
        ViewHolder holder = new ViewHolder(converView, position);
        if (position == 0) {
            holder.tvDesc.setVisibility(View.VISIBLE);
            holder.avata.setVisibility(View.VISIBLE);
            holder.clubLevel.setVisibility(View.VISIBLE);
            holder.tvNotice.setVisibility(View.GONE);
        } else {
            holder.tvDesc.setVisibility(View.GONE);
            holder.avata.setVisibility(View.GONE);
            holder.clubLevel.setVisibility(View.GONE);
            holder.tvNotice.setVisibility(View.VISIBLE);
        }
        holder.bind(position);
        view.addView(converView, 0);
        return converView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    class ViewHolder {
        private TextView tvNotice;
        private CircleImageView avata;
        private TextView tvDesc;
        private TextView clubLevel;

        public ViewHolder(View converView, int pos) {
            this.avata = (CircleImageView) converView.findViewById(R.id.club_info_logo);
            this.tvNotice = (TextView) converView.findViewById(R.id.club_info_notice);
            this.tvDesc = (TextView) converView.findViewById(R.id.club_info_desc);
            this.clubLevel = (TextView) converView.findViewById(R.id.club_level);
            hoderMap.put(pos, this);
        }

        public void notifyClubUpdateChanged(String path) {
            if (!TextUtils.isEmpty(path) && avata != null) {
                Log.e("path",path);
                File f = new File(path);
                Picasso.with(context).load(f).fit().placeholder(R.drawable.ic_avatar_club)
                        .error(R.drawable.ic_avatar_club).centerCrop().into(avata);
            }
        }

        public void bind(int position) {
            if (clubInfo == null)
                return;
            if (position == 0) {
                if (!TextUtils.isEmpty(clubInfo.getLogo())) {
                    Picasso.with(context).load(clubInfo.getLogo()).fit().placeholder(R.drawable.ic_avatar_club)
                            .error(R.drawable.ic_avatar_club).centerCrop().into(avata);
                    Picasso.with(context).invalidate(clubInfo.getLogo());
                } else {
                    avata.setImageResource(R.drawable.ic_avatar_club);
                }
                tvDesc.setText(clubInfo.getDesc());
                clubLevel.setText("LV." + clubInfo.getClubLevel());
                clubLevel.setVisibility(View.VISIBLE);
            } else {
                tvNotice.setText(clubInfo.getNotice());
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key != null && key.equals(CLUB_LOGO_CHANGE)) {
            String logo = userSp.getString(CLUB_LOGO_LOCALE, "");
            this.notifyClubUpdateChanged(logo);
        }

        if (null != key && key.equals(CLUB_LOGO) || (key.equals(CLUB_NAME) || key.equals(CLUB_DESC) || key.equals(CLUB_NOTICE))) {
            String logo = userSp.getString(CLUB_LOGO, "");
            if (!TextUtils.isEmpty(logo))
                clubInfo.setLogo(logo);
            String name = userSp.getString(CLUB_NAME, "");
            if (!TextUtils.isEmpty(name))
                clubInfo.setName(name);
            String desc = userSp.getString(CLUB_DESC, "");
            if (!TextUtils.isEmpty(desc))
                clubInfo.setDesc(desc);
            String notice = userSp.getString(CLUB_NOTICE, "");
            if (!TextUtils.isEmpty(notice))
                clubInfo.setNotice(notice);
            this.notifyDataSetChanged(clubInfo);
        }
    }

}
