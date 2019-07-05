package com.beastbikes.android.modules.cycling.club.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.R;
import com.beastbikes.android.dialog.AlertDialog;
import com.beastbikes.android.dialog.AlertDialog.DialogListener;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.cycling.ranking.dto.RankDTO;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.android.utils.NickNameRemarksUtil;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@LayoutResource(R.layout.activity_member_manager)
@MenuResource(R.menu.membermanagermenu)
public class ClubMemberManagerActivity extends SessionFragmentActivity implements OnItemClickListener {

    public static final String EXTRA_CLUB_ID = "club_id";
    public static final String EXTRA_CLUB_INFO = "club_info";
    public static final String EXTRA_CLUB_MEMBER_SELECT_MODE = "club_select_mode";
    public static final String EXTRA_CLUB_MEMBER = "club_member";
    public static final String EXTRA_CLUB_MEMBER_NAME = "club_member_name";
    public static final String EXTRA_IS_QUIT = "is_quit";

    @IdResource(R.id.captain_layout)
    private ViewGroup captionVG;
    private CircleImageView captionAvatar;
    private TextView captionNickName;
    private TextView captionTime;
    private TextView captionDistance;
    private TextView captionDelete;

    @IdResource(R.id.activity_club_manager_member_list)
    private ListView memberLv;

    private MemberAdapter adapter;

    private List<RankDTO> memberList = new ArrayList<>();

    private ClubManager manager;

    private RankDTO captainDTO;

    private String clubId;

    private ClubInfoCompact clubInfoCompact;


    private boolean editEnable = false;//是否处于编辑模式
    private boolean isRoot = false;//是不是管理员
    private TextView clubFootViewTV;

    private int max;
    private int memberCount;
    private boolean isSelectMode = false;
    private int isQuit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {

            this.clubId = intent
                    .getStringExtra(ClubFeedInfoFrag.EXTRA_CLUB_ID);
            this.clubInfoCompact = (ClubInfoCompact) intent
                    .getSerializableExtra(ClubMemberManagerActivity.EXTRA_CLUB_INFO);
            this.isSelectMode = intent.getBooleanExtra(EXTRA_CLUB_MEMBER_SELECT_MODE, false);
            this.isQuit = intent.getIntExtra(EXTRA_IS_QUIT, 0);

            max = clubInfoCompact.getMaxMembers();
            memberCount = clubInfoCompact.getMembers();
            if (clubInfoCompact != null) {
                isRoot = clubInfoCompact.getLevel() == 128;
            }
            try {
                ClubInfoCompact myClubInfo = new ClubManager(this).getMyClub(getUserId());
                if (myClubInfo != null) {
                    isRoot = myClubInfo.getLevel() == 128;
                }
            } catch (BusinessException e) {
                e.printStackTrace();
            }
        }

        captionAvatar = (CircleImageView) captionVG.findViewById(R.id.club_member_list_item_avatar);
        captionNickName = (TextView) captionVG.findViewById(R.id.club_member_list_item_nickname);
        captionTime = (TextView) captionVG.findViewById(R.id.member_time);
        captionDistance = (TextView) captionVG.findViewById(R.id.member_distance);
        captionDelete = (TextView) captionVG.findViewById(R.id.club_member_list_item_btn_delete);
        captionDelete.setVisibility(View.GONE);

        View footView = LayoutInflater.from(this).inflate(R.layout.layout_membermanager_footview, null);
        clubFootViewTV = (TextView) footView.findViewById(R.id.clubfootviewtv);
        memberLv.addFooterView(footView, null, true);

        clubFootViewTV.setText(memberCount + "/" + max);

        this.adapter = new MemberAdapter(this.memberList, this.manager);

        this.manager = new ClubManager(this);
        this.memberLv.setOnItemClickListener(this);
        this.memberLv.setAdapter(this.adapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null && menu.size() > 0) {
            menu.getItem(0).setTitle(getResources().getString(R.string.manage));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (!isRoot || isSelectMode)
            return true;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.member_manager_menu_item:
                if (!editEnable) {
                    editEnable = true;
                    item.setTitle(getResources().getString(R.string.activity_state_label_finish));
                    adapter.notifyDataSetChanged();
                } else {
                    editEnable = false;
                    item.setTitle(getResources().getString(R.string.manage));
                    adapter.notifyDataSetChanged();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.fetchMemberDTO();
    }

    @Override
    public void finish() {
        editEnable = false;
        adapter.notifyDataSetChanged();
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final RankDTO dto = (RankDTO) parent.getItemAtPosition(position);
        if (dto == null)
            return;
        final Intent intent = new Intent();
        if (isSelectMode) {
            final MaterialDialog dialog = new MaterialDialog(this);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setMessage(String.format(getString(R.string.club_member_transfer_msg), dto.getNickname()));
            dialog.setPositiveButton(R.string.club_member_transfer_ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    intent.putExtra(EXTRA_CLUB_MEMBER, dto.getUserId());
                    intent.putExtra(EXTRA_IS_QUIT, isQuit);
                    intent.putExtra(EXTRA_CLUB_MEMBER_NAME, dto.getNickname());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }).setNegativeButton(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            }).show();

        } else {
            intent.setClass(ClubMemberManagerActivity.this, ProfileActivity.class);
            intent.putExtra(ProfileActivity.EXTRA_USER_ID, dto.getUserId());
            intent.putExtra(ProfileActivity.EXTRA_AVATAR, dto.getAvatarUrl());
            intent.putExtra(ProfileActivity.EXTRA_CITY, dto.getCity());
            intent.putExtra(ProfileActivity.EXTRA_REMARKS, dto.getRemarks());
            startActivity(intent);
        }
    }


    /**
     * 查看成员列表
     */
    private void fetchMemberDTO() {
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<RankDTO>>() {

            @Override
            protected List<RankDTO> doInBackground(Void... params) {
                if (null == manager || TextUtils.isEmpty(clubId))
                    return null;

                try {
                    final List<RankDTO> list = manager.getClubMemberList(clubId,
                            ClubManager.CLUB_MEMBER_ORDERBY_MILESTONE, 1, 1000);
                    return list;
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<RankDTO> result) {
                if (null != result && !result.isEmpty()) {
                    List<RankDTO> list = new ArrayList<>();
                    for (RankDTO dto : result) {
                        if (dto.isManager()) {
                            captainDTO = dto;
                        } else {
                            list.add(dto);
                        }
                    }
                    memberList.clear();
                    memberList.addAll(list);
                    adapter.notifyDataSetChanged();
                    captionNickName.setText(captainDTO.getNickname());
                    Date date = DateFormatUtil.stringFormat2Date(captainDTO.getJoined());
                    captionTime.setText(ClubMemberManagerActivity.this.getResources().getString(R.string.jointime) + DateFormatUtil.dateFormat2StringYearMonthDay(date));

                    double milestone = 0;
                    if (captainDTO.getMilestone() > 0) {
                        milestone =  captainDTO.getMilestone() / 1000;
                    }
                    if (LocaleManager.isDisplayKM(ClubMemberManagerActivity.this)) {
                        captionDistance.setText(String.format("%.1f", milestone) + " " + getResources().getString(R.string.activity_param_label_distance_unit));
                    } else {
                        captionDistance.setText(String.format("%.1f", LocaleManager.
                                kilometreToMile(milestone)) + " " + getResources().getString(R.string.mi));
                    }
                    if (!TextUtils.isEmpty(captainDTO.getAvatarUrl())) {
                        Picasso.with(ClubMemberManagerActivity.this).load(captainDTO.getAvatarUrl()).fit()
                                .centerCrop().error(R.drawable.ic_avatar).placeholder(R.drawable.ic_avatar)
                                .into(captionAvatar);
                    } else {
                        captionAvatar.setImageResource(R.drawable.ic_avatar);
                    }
                    clubFootViewTV.setText(memberList.size() + 1 + "/" + max);
                }
            }

        });
    }


    private final class MemberViewHolder extends ViewHolder<RankDTO> {


        private ClubManager manager;

        @IdResource(R.id.club_member_list_item_avatar)
        private CircleImageView avatar;

        @IdResource(R.id.club_member_list_item_nickname)
        private TextView nickname;

        @IdResource(R.id.club_member_list_item_btn_delete)
        private TextView btnDelete;

        @IdResource(R.id.member_time)
        private TextView memberTime;

        @IdResource(R.id.member_distance)
        private TextView memberDistance;

        @IdResource(R.id.bottomView)
        public View bottomView;

        @IdResource(R.id.bottomView_long)
        public View bottomViewLong;

        protected MemberViewHolder(View v,
                                   ClubManager manager) {
            super(v);
            this.manager = manager;
        }

        @Override
        public void bind(final RankDTO dto) {
            if (null == dto) {
                return;
            }

            if (!TextUtils.isEmpty(dto.getAvatarUrl())) {
                Picasso.with(getContext()).load(dto.getAvatarUrl()).fit().centerCrop().error(R.drawable.ic_avatar)
                        .placeholder(R.drawable.ic_avatar).into(this.avatar);
            } else {
                this.avatar.setImageResource(R.drawable.ic_avatar);
            }

//            this.nickname.setText();
            this.nickname.setText(NickNameRemarksUtil.disPlayName(dto.getNickname(), dto.getRemarks()));

            Date date = DateFormatUtil.stringFormat2Date(dto.getJoined());
            this.memberTime.setText(ClubMemberManagerActivity.this.getResources().getString(R.string.jointime) + DateFormatUtil.dateFormat2StringYearMonthDay(date));
            double tottalDistance = 0;
            if (dto.getMilestone() > 0)
                tottalDistance =  dto.getMilestone() / 1000;
            if (LocaleManager.isDisplayKM(ClubMemberManagerActivity.this)) {
                this.memberDistance.setText(String.format("%.1f", tottalDistance) + " " + getResources().getString(R.string.activity_param_label_distance_unit));
            } else {
                this.memberDistance.setText(String.format("%.1f", LocaleManager.
                        kilometreToMile(tottalDistance)) + " " + getResources().getString(R.string.mi));
            }
            if (editEnable) {
                this.btnDelete.setVisibility(View.VISIBLE);
                this.btnDelete.setTag(dto.getUserId());
                this.btnDelete.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        deleteMember(dto);
                        SpeedxAnalytics.onEvent(getContext(), " 删除成员",null);
                    }
                });
            } else {
                this.btnDelete.setVisibility(View.GONE);
            }
        }

        private void deleteMember(final RankDTO dto) {
            SpeedxAnalytics.onEvent(ClubMemberManagerActivity.this, "", "click_propose");
            final Context ctx = getContext();
            new AlertDialog(ctx,
                    ctx.getString(R.string.activity_member_manager_dialog_msg), null,
                    new DialogListener() {

                        @Override
                        public void onClickOk(int id) {
                            final ClubMemberManagerActivity ctx = (ClubMemberManagerActivity) getContext();
                            ctx.getAsyncTaskQueue().add(
                                    new AsyncTask<Void, Void, Boolean>() {

                                        @Override
                                        protected Boolean doInBackground(
                                                Void... params) {
                                            try {
                                                // 0代表删除成员
                                                if (null == manager) {
                                                    manager = new ClubManager(ctx);
                                                }
                                                return manager.postCmdClubMember(dto.getUserId(), 0);
                                            } catch (BusinessException e) {
                                                return false;
                                            }
                                        }

                                        @Override
                                        protected void onPostExecute(
                                                Boolean result) {
                                            if (result) {
                                                Toasts.show(ctx, R.string.activity_member_manager_delete_success);
                                                ctx.memberList.remove(dto);
                                                ctx.adapter.notifyDataSetChanged();
//                                                memberCount = ctx.memberList.size();
                                                memberCount--;
                                                clubFootViewTV.setText((memberList.size() + 1) + "/" + max);
//                                                fetchMemberDTO();
//                                                ctx.countTv.setText(ctx.adapter.getCount() +
//                                                        ctx.getString(R.string.club_member_count));
                                            } else {
                                                Toasts.show(ctx, R.string.activity_member_manager_delete_failed);
                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onClickCancel(int id) {
                        }
                    }, R.id.dialog_club_cancel_create_apply_warning).show();
        }
    }

    private final class MemberAdapter extends BaseAdapter {
        private ClubManager manager;
        private List<RankDTO> list;

        public MemberAdapter(List<RankDTO> list, ClubManager manager) {
            this.list = list;
            this.manager = manager;
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
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MemberViewHolder holder;
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_member_list_item, null);
                holder = new MemberViewHolder(convertView, this.manager);
            } else {
                holder = (MemberViewHolder) convertView.getTag();
            }
            holder.bind(this.list.get(position));
            if (position == list.size() - 1) {
                holder.bottomView.setVisibility(View.GONE);
                holder.bottomViewLong.setVisibility(View.VISIBLE);
            } else {
                holder.bottomView.setVisibility(View.VISIBLE);
                holder.bottomViewLong.setVisibility(View.GONE);
            }
            return convertView;
        }

    }

}
