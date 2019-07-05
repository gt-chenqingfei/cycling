package com.beastbikes.android.modules.cycling.club.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ApplyDTO;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.NickNameRemarksUtil;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

@Alias("入队申请")
@LayoutResource(R.layout.activity_apply_manager)
public class ApplyManagerActivity extends SessionFragmentActivity implements
        Constants {

    @IdResource(R.id.activity_apply_manager_list)
    private ListView applyLv;

    @IdResource(R.id.activity_apply_manager_no_apply)
    private TextView noApplyTv;

    private ClubManager manager;

    private ApplyAdapter adapter;

    private List<ApplyDTO> applyList = new ArrayList<>();

    private int pageIndex = 1;
    private int maxCount;
    private int count;

    private SharedPreferences userSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.manager = new ClubManager(this);

        this.adapter = new ApplyAdapter(this.applyList, this.manager);

        this.applyLv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final ApplyDTO dto = (ApplyDTO) parent
                        .getItemAtPosition(position);

                if (null == dto) {
                    return;
                }

                final Intent intent = new Intent();
                intent.setClass(ApplyManagerActivity.this,
                        ProfileActivity.class);
                intent.putExtra(ProfileActivity.EXTRA_USER_ID, dto.getUserId());
                intent.putExtra(ProfileActivity.EXTRA_AVATAR, dto.getAvatarUrl());
                intent.putExtra(ProfileActivity.EXTRA_NICK_NAME, dto.getNickname());
                intent.putExtra(ProfileActivity.EXTRA_REMARKS, dto.getRemarks());
                startActivity(intent);
            }
        });

        this.applyLv.setAdapter(this.adapter);
        SharedPreferences sp = getSharedPreferences(getUserId(), 0);
        if (sp != null) {
            sp.edit().putInt(PUSH.PREF_KEY.DOT_CLUB_MORE, 0).commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.fetchMyClubMemberCount();
        this.fetchApplyDTO();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    private void fetchApplyDTO() {
        this.getAsyncTaskQueue().add(
                new AsyncTask<Void, Void, List<ApplyDTO>>() {

                    @Override
                    protected List<ApplyDTO> doInBackground(Void... params) {
                        if (null == manager) {
                            return null;
                        }
                        try {
                            final List<ApplyDTO> list = manager
                                    .getClubApplyList(pageIndex,
                                            1000);
                            return list;
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<ApplyDTO> result) {
                        if (null != result && !result.isEmpty()) {
                            if (count != 0 && count == maxCount) {
                                for (ApplyDTO applyDTO : result) {
                                    if (applyDTO.getStatus() == 0) {
                                        applyDTO.setStatus(3);
                                    }
                                }
                            }
                            applyList.clear();
                            applyList.addAll(result);
                            adapter.notifyDataSetChanged();
                        } else {
                            noApplyTv.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void fetchMyClubMemberCount() {
        final String userId = this.getUserId();
        this.getAsyncTaskQueue().add(
                new AsyncTask<Void, Void, ClubInfoCompact>() {

                    @Override
                    protected ClubInfoCompact doInBackground(Void... params) {
                        if (null != manager) {
                            try {
                                return manager.getMyClub(userId);
                            } catch (BusinessException e) {
                                return null;
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(ClubInfoCompact result) {
                        if (null != result) {
                            count = result.getMembers();
                            maxCount = result.getMaxMembers();
                        }
                    }
                });
    }

    private final class ApplyAdapter extends BaseAdapter {

        private List<ApplyDTO> list;
        private ClubManager manager;

        public ApplyAdapter(List<ApplyDTO> list,
                            ClubManager manager) {
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
            final ApplyViewHolder holder;
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.apply_list_item_black, null);
                holder = new ApplyViewHolder(convertView,
                        this.manager);
            } else {
                holder = (ApplyViewHolder) convertView.getTag();
            }

            holder.bind(this.list.get(position));
            if (position == list.size() - 1)
                holder.diverLong.setVisibility(View.VISIBLE);
            return convertView;
        }

    }

    private static final class ApplyViewHolder extends ViewHolder<ApplyDTO> {

        private ClubManager manager;

        @IdResource(R.id.apply_list_item_avatar)
        private CircleImageView avatar;

        @IdResource(R.id.apply_list_item_nickname)
        private TextView nickname;

        @IdResource(R.id.apply_list_item_extra)
        private TextView extraTv;

        @IdResource(R.id.apply_list_item_btn_agree)
        private TextView agree;

        @IdResource(R.id.apply_list_item_btn_refuse)
        private TextView refuse;

        @IdResource(R.id.apply_list_item_status)
        private TextView status;

        @IdResource(R.id.apply_list_item_diver_long)
        public View diverLong;

        protected ApplyViewHolder(View v,
                                  ClubManager manager) {
            super(v);
            this.manager = manager;
        }

        private void showLastMemberDialog() {
//            final Context ctx = getContext();
//            new AlertDialog(ctx, "", ctx.getResources().getString(R.string.club_full_notice), ctx.getResources().getString(R.string.activity_alert_dialog_text_ok), null, new AlertDialog.DialogListener() {
//                @Override
//                public void onClickOk(int id) {
//
//                }
//
//                @Override
//                public void onClickCancel(int id) {
//
//                }
//            }, R.id.dialog_club_cancel_create_apply_warning).show();

            final MaterialDialog dialog = new MaterialDialog(getContext());
            dialog.setMessage(R.string.club_full_notice).
                    setPositiveButton(R.string.activity_alert_dialog_text_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();

        }

        @Override
        public void bind(final ApplyDTO dto) {
            if (null == dto)
                return;

            if (!TextUtils.isEmpty(dto.getAvatarUrl())) {
                Picasso.with(getContext()).load(dto.getAvatarUrl()).fit().error(R.drawable.ic_avatar)
                        .placeholder(R.drawable.ic_avatar).centerCrop().into(this.avatar);
            } else {
                this.avatar.setImageResource(R.drawable.ic_avatar);
            }

//            this.nickname.setText(dto.getNickname());
            this.nickname.setText(NickNameRemarksUtil.disPlayName(dto.getNickname(), dto.getRemarks()));

            String extra = dto.getExtra();
            if (TextUtils.isEmpty(extra)) {
                extra = getContext().getString(
                        R.string.activity_club_apply_default_extra);
            }
            this.extraTv.setText(extra);

            this.agree.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final ApplyManagerActivity ctx = ((ApplyManagerActivity) getContext());
                    SpeedxAnalytics.onEvent(ctx, "同意入队申请", null);
                    if (ctx.count >= ctx.maxCount) {
//                        Toasts.show(ctx, ctx.getString(R.string.club_info_item_club_full));
                        showLastMemberDialog();
                        return;
                    }
                    ctx.getAsyncTaskQueue().add(
                            new AsyncTask<Void, Void, Boolean>() {

                                @Override
                                protected Boolean doInBackground(Void... params) {
                                    try {
                                        return manager.postClubApply(
                                                dto.getObjectId(), 0);
                                    } catch (BusinessException e) {
                                        return false;
                                    }
                                }

                                @Override
                                protected void onPostExecute(Boolean result) {
                                    if (result.booleanValue()) {
                                        status.setVisibility(View.VISIBLE);
                                        Toasts.show(
                                                getContext(),
                                                R.string.activity_club_apply_item_status_agreed);
                                        agree.setVisibility(View.GONE);
                                        refuse.setVisibility(View.GONE);
                                        status.setText(R.string.activity_club_apply_item_status_agreed);
                                        dto.setStatus(1);
                                        ctx.adapter.notifyDataSetChanged();
                                        ctx.count++;
                                    } else {
                                        Toasts.show(
                                                getContext(),
                                                R.string.activity_club_apply_item_error);
                                    }
                                }
                            });
                }
            });

            this.refuse.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final ApplyManagerActivity ctx = ((ApplyManagerActivity) getContext());
                    ctx.getAsyncTaskQueue().add(
                            new AsyncTask<Void, Void, Boolean>() {

                                @Override
                                protected Boolean doInBackground(Void... params) {
                                    try {
                                        return manager.postClubApply(
                                                dto.getObjectId(), 1);
                                    } catch (BusinessException e) {
                                        return false;
                                    }
                                }

                                @Override
                                protected void onPostExecute(Boolean result) {
                                    if (result.booleanValue()) {
                                        status.setVisibility(View.VISIBLE);
                                        Toasts.show(
                                                getContext(),
                                                R.string.activity_club_apply_item_status_refused);
                                        agree.setVisibility(View.GONE);
                                        refuse.setVisibility(View.GONE);
                                        status.setText(R.string.activity_club_apply_item_status_refused);
                                        dto.setStatus(3);
                                        ctx.adapter.notifyDataSetChanged();
                                    } else {
                                        Toasts.show(
                                                getContext(),
                                                R.string.activity_club_apply_item_error);
                                    }
                                }
                            });
                }
            });

            final int status = dto.getStatus();
            switch (status) {
                case 0:
                    this.status.setVisibility(View.GONE);
                    this.agree.setVisibility(View.VISIBLE);
                    this.refuse.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    this.agree.setVisibility(View.GONE);
                    this.refuse.setVisibility(View.GONE);
                    this.status.setVisibility(View.VISIBLE);
                    this.status
                            .setText(R.string.activity_club_apply_item_status_agreed);
                    break;
                case 2:
                    this.agree.setVisibility(View.GONE);
                    this.refuse.setVisibility(View.GONE);
                    this.status.setVisibility(View.VISIBLE);
                    this.status
                            .setText(R.string.activity_club_apply_item_status_canceled);
                    break;
                case 3:
                    this.agree.setVisibility(View.GONE);
                    this.refuse.setVisibility(View.GONE);
                    this.status.setVisibility(View.VISIBLE);
                    this.status
                            .setText(R.string.activity_club_apply_item_status_refused);
                    break;
                default:
                    break;
            }
        }
    }

}
