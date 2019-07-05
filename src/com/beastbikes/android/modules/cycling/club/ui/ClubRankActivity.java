package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.cycling.club.dto.ClubRankBean;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by caoxiao on 15/12/2.
 */
@Alias("新俱乐部排行")
@LayoutResource(R.layout.activity_clubranking)
public class ClubRankActivity extends SessionFragmentActivity implements View.OnClickListener {

    @IdResource(R.id.bg_clubranktitle)
    private ImageView tileBG;

    @IdResource(R.id.monthrank)
    private TextView monthrank;

    @IdResource(R.id.totalrank)
    private TextView totalrank;

    @IdResource(R.id.btn_back)
    private RelativeLayout btnback;

    @IdResource(R.id.head)
    private RelativeLayout head;

    @IdResource(R.id.club_logo)
    private CircleImageView logo;

    @IdResource(R.id.clubname)
    private TextView clubname;

    @IdResource(R.id.scoretv)
    private TextView scoretv;

    @IdResource(R.id.desctv)
    private TextView desctv;

    @IdResource(R.id.activity_club_rank_club_rank_view_all_tv)
    private TextView rankAllViewTV;

    private FragmentManager fm;
    private MonthClubRankFrag monthMemberRankFrag;
    private TotalClubRankFrag totalMemberRankFrag;

    private double windowWidth;
    private double imageHeight;
    private static final float RESOLUTION = 640f / 375f;
    private ClubInfoCompact clubInfoCompact;
    private ClubManager clubManager;

    private boolean isMyClub = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        Intent intent = getIntent();
        if (intent != null) {
            clubInfoCompact = (ClubInfoCompact) getIntent().getSerializableExtra(ClubMemberManagerActivity.EXTRA_CLUB_INFO);
            if (clubInfoCompact != null) {

                AVUser user = AVUser.getCurrentUser();
                if (user != null && !TextUtils.isEmpty(user.getClubId())) {
                    isMyClub = user.getClubId().equals(clubInfoCompact.getObjectId());
                    refreshViewMyClub();
                } else {
                    refreshViewOthersClub();
                }
            }
        }
        tileBG.setImageResource(R.drawable.bg_clubrankingtitle);

        this.clubManager = new ClubManager(this);
        this.getMyClubRank(1);
        fm = getSupportFragmentManager();
        monthrank.setSelected(true);
        showFragment(1);
        monthrank.setOnClickListener(this);
        totalrank.setOnClickListener(this);
        btnback.setOnClickListener(this);
        rankAllViewTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.monthrank:
                showFragment(1);
                this.getMyClubRank(1);
                break;
            case R.id.totalrank:
                showFragment(2);
                this.getMyClubRank(0);
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.activity_club_rank_club_rank_view_all_tv:
                Intent intent = new Intent(ClubRankActivity.this, ClubDiscoverActivity.class);
                if (clubInfoCompact != null) {
                    if (clubInfoCompact.getStatus() == ClubInfoCompact.CLUB_STATUS_ESTABLISHED || clubInfoCompact.getStatus() == ClubInfoCompact.CLUB_STATUS_JOINED) {
                        intent.putExtra(ClubDiscoverActivity.ClUB_DIS_FRAG_MENU_SHOW, false);
                    }
                }
                startActivity(intent);
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    /**
     * 获取个人俱乐部排行
     *
     * @param rankType
     */
    private void getMyClubRank(final int rankType) {
        this.getAsyncTaskQueue().add(new AsyncTask<Integer, Void, ClubInfoCompact>() {
            @Override
            protected ClubInfoCompact doInBackground(Integer... params) {
                if (!isMyClub)
                    return null;
                return clubManager.getMyClubRank(rankType);
            }

            @Override
            protected void onPostExecute(ClubInfoCompact clubInfoCompact) {
                if (null != clubInfoCompact) {
                    if (LocaleManager.isDisplayKM(ClubRankActivity.this)) {
                        scoretv.setText(Math.round(clubInfoCompact.getMilestone() / 1000) + "" + getResources().getText(R.string.kilometre));
                    } else {
                        scoretv.setText(Math.round(LocaleManager.kilometreToMile(clubInfoCompact.getMilestone() / 1000)) + "" + getResources().getText(R.string.mi));
                    }

                    desctv.setText(clubInfoCompact.getRank() + "");
                }
            }
        });
    }

    private void refreshViewMyClub() {
        if (!TextUtils.isEmpty(clubInfoCompact.getLogo())) {
            Picasso.with(this).load(clubInfoCompact.getLogo()).fit().error(R.drawable.ic_avatar_club)
                    .placeholder(R.drawable.ic_avatar_club).centerCrop().into(logo);
        } else {
            this.logo.setImageResource(R.drawable.ic_avatar_club);
        }
        if (!TextUtils.isEmpty(clubInfoCompact.getName()))
            clubname.setText(clubInfoCompact.getName());

        if (LocaleManager.isDisplayKM(ClubRankActivity.this)) {
            scoretv.setText(Math.round(clubInfoCompact.getMilestone() / 1000) + "" + getResources().getText(R.string.kilometre));
        } else {
            scoretv.setText(Math.round(LocaleManager.kilometreToMile(clubInfoCompact.getMilestone() / 1000)) + "" + getResources().getText(R.string.mi));
        }

        desctv.setText(clubInfoCompact.getRank() + "");
    }

    private void refreshViewOthersClub() {
        if (!TextUtils.isEmpty(clubInfoCompact.getName())) {
            clubname.setText(getResources().getText(R.string.did_not_join_club));
        }
        scoretv.setText("-");
        desctv.setText("-");
    }

    public void showFragment(int index) {
        FragmentTransaction ft = fm.beginTransaction();
        hideFragments(ft);

        switch (index) {
            case 1:
                monthrank.setSelected(true);
                totalrank.setSelected(false);
                if (monthMemberRankFrag != null)
                    ft.show(monthMemberRankFrag);
                else {
                    monthMemberRankFrag = new MonthClubRankFrag();
                    ft.add(R.id.frag_container, monthMemberRankFrag);
                }
                break;
            case 2:
                totalrank.setSelected(true);
                monthrank.setSelected(false);
                if (totalMemberRankFrag != null)
                    ft.show(totalMemberRankFrag);
                else {
                    totalMemberRankFrag = new TotalClubRankFrag();
                    ft.add(R.id.frag_container, totalMemberRankFrag);
                }
                break;
        }
        ft.commit();
    }

    // 当fragment已被实例化，就隐藏起来
    public void hideFragments(FragmentTransaction ft) {
        if (monthMemberRankFrag != null)
            ft.hide(monthMemberRankFrag);
        if (totalMemberRankFrag != null)
            ft.hide(totalMemberRankFrag);
    }

    public static class ClubRankAdapter extends BaseAdapter {

        private List<ClubRankBean> list;
        private Context ctx;

        public ClubRankAdapter(Context context, List<ClubRankBean> list) {
            this.ctx = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public Object getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ClubRankViewHolder vh;
            if (null == convertView) {
                convertView = LayoutInflater.from(this.ctx).inflate(
                        R.layout.universal_rank_item, null);
                vh = new ClubRankViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ClubRankViewHolder) convertView.getTag();
            }

            ClubRankBean info = this.list.get(position);
            info.setOrdinal(position + 1);
            vh.bind(info);
            return convertView;
        }

    }

    private static class ClubRankViewHolder extends ViewHolder<ClubRankBean> {

        @IdResource(R.id.universal_rank_item_ordinal)
        private TextView ordinal;

        @IdResource(R.id.universal_rank_item_avatar)
        private CircleImageView avatarIv;

        @IdResource(R.id.universal_rank_item_name)
        private TextView nameTv;

        @IdResource(R.id.universal_rank_item_desc)
        private TextView addressTv;

        @IdResource(R.id.universal_rank_item_value)
        private TextView valueTv;

        @IdResource(R.id.universal_rank_item_unit)
        private TextView unitTv;

        protected ClubRankViewHolder(View v) {
            super(v);
        }

        @Override
        public void bind(ClubRankBean t) {
            if (null == t) {
                return;
            }
            switch (t.getOrdinal()) {
                case 1:
                    this.ordinal.setBackgroundResource(R.drawable.ordinal_bg_1);
                    break;
                case 2:
                    this.ordinal.setBackgroundResource(R.drawable.ordinal_bg_2);
                    break;
                case 3:
                    this.ordinal.setBackgroundResource(R.drawable.ordinal_bg_3);
                    break;
                default:
                    this.ordinal.setBackgroundResource(R.drawable.transparent);
                    break;
            }
            this.ordinal.setText(String.valueOf(t.getOrdinal()));
            this.nameTv.setText(t.getName());

            if (!TextUtils.isEmpty(t.getLogo())) {
                Picasso.with(getContext()).load(t.getLogo()).fit().error(R.drawable.ic_avatar_club)
                        .placeholder(R.drawable.ic_avatar_club).centerCrop().into(this.avatarIv);
            } else {
                this.avatarIv.setImageResource(R.drawable.ic_avatar_club);
            }

            StringBuilder sb = new StringBuilder("");

            if (!TextUtils.isEmpty(t.getCity())) {
                sb.append(t.getCity());
            }
            String address = sb.toString();
            if (!TextUtils.isEmpty(address) && !address.equals("null")) {
                this.addressTv.setText(address);
            } else {
                this.addressTv.setVisibility(View.INVISIBLE);
            }

            this.valueTv.setVisibility(View.VISIBLE);

            if (LocaleManager.isDisplayKM(getContext())) {
                this.unitTv.setText(getContext().getResources().getText(R.string.kilometre));
                this.valueTv.setText(Math.round(t.getMilestone() / 1000) + "");
            } else {
                this.unitTv.setText(getContext().getResources().getText(R.string.mi));
                this.valueTv.setText(Math.round(LocaleManager.kilometreToMile(t.getMilestone() / 1000)) + "");
            }
            this.unitTv.setVisibility(View.VISIBLE);
        }

    }
}
