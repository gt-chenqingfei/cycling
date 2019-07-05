package com.beastbikes.android.modules.cycling.club.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.club.biz.ClubActivityManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityInfoList;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityListDTO;
import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.android.utils.NickNameRemarksUtil;
import com.beastbikes.android.widget.SwipeRefreshLoadRecyclerView;
import com.beastbikes.android.widget.helper.ItemClickListener;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by zhangyao on 2016/1/13.
 */
@LayoutResource(R.layout.fragment_club_activity_list)
public class ClubActivityListFragment extends SessionFragment implements SwipeRefreshLoadRecyclerView.RecyclerCallBack, ItemClickListener {

    // 是否需要刷新list
    public static final int REQUEST_EDIT_CODE = 10;

    private ClubActivityManager clubActivityManager;
    private String clubid;
    private List<ClubActivityListDTO> clubActivityInfos = new ArrayList<>();
    public static final int COUNT = 20;

    @IdResource(R.id.content)
    private LinearLayout contentLL;

    private SwipeRefreshLoadRecyclerView swipeRefreshLoadRecyclerView;

    private ClubActivityAdapter clubActivityAdapter;

    private DisplayMetrics dm;
    private double RESOLUTION = 1.88;

    private int page = 1;
    private boolean isRefresh = false;
    private boolean isFirstLoad = true;

    private final String COMPRESS_URL = "?imageView2/1/w/120/h/120";

    public static final String CLUB_MEMBER_HAS_FOOTER = "club_member_has_footer";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        clubid = bundle.getString(ClubMemberRankActivity.EXTRA_CLUB_ID);
        ClubActivityInfoList clubActivityInfoList = (ClubActivityInfoList)
                bundle.getSerializable(ClubActivitiesListActivity.CLUBACTIVITY_INFOS);
        clubActivityInfos.addAll(clubActivityInfoList.getList());
        clubActivityManager = new ClubActivityManager(getActivity());

        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        clubActivityAdapter = new ClubActivityAdapter(this.getActivity(), this);
        swipeRefreshLoadRecyclerView = new SwipeRefreshLoadRecyclerView(this.getActivity(), contentLL, clubActivityInfos, SwipeRefreshLoadRecyclerView.HASFOOTER);
        swipeRefreshLoadRecyclerView.setAdapter(clubActivityAdapter);
        swipeRefreshLoadRecyclerView.setRecyclerCallBack(this);
        if (!bundle.getBoolean(CLUB_MEMBER_HAS_FOOTER, true)) {
            swipeRefreshLoadRecyclerView.setHasFooter(false);
        }
    }

    @Override
    public void refreshCallBack() {
        isFirstLoad = true;
        page = 1;
        swipeRefreshLoadRecyclerView.setCanLoadMore(true);
        isRefresh = true;
        swipeRefreshLoadRecyclerView.setHasFooter(true);
        getClubActivityList();
    }

    @Override
    public void loadMoreCallBack() {
        page++;
        swipeRefreshLoadRecyclerView.setHasFooter(true);
        getClubActivityList();
    }

    private void getClubActivityList() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<ClubActivityListDTO>>() {

            @Override
            protected List<ClubActivityListDTO> doInBackground(Void... voids) {
                return clubActivityManager.clubActivityList(clubid, page, COUNT);
            }

            @Override
            protected void onPostExecute(List<ClubActivityListDTO> activityInfos) {
                if (activityInfos == null) {
                    swipeRefreshLoadRecyclerView.setHasFooter(false);
                    swipeRefreshLoadRecyclerView.finishLoad();
                    return;
                }
                if (activityInfos.size() == 0) {
                    swipeRefreshLoadRecyclerView.setCanLoadMore(false);
                    swipeRefreshLoadRecyclerView.setHasFooter(false);
                    swipeRefreshLoadRecyclerView.finishLoad();
                    return;
                }
                if (isRefresh) {
                    isRefresh = false;
                    clubActivityInfos.clear();
                }
                clubActivityInfos.addAll(activityInfos);
                swipeRefreshLoadRecyclerView.notifyDataSetChanged();
                swipeRefreshLoadRecyclerView.finishLoad();
                if (isFirstLoad) {
                    isFirstLoad = false;
                    if (clubActivityInfos.size() < COUNT)
                        swipeRefreshLoadRecyclerView.setHasFooter(false);
                }
            }
        });
    }

    @Override
    public void OnItemClick(RecyclerView.ViewHolder viewHolder, int position) {
        ClubActivityListDTO clubActivityListDTO = clubActivityInfos.get(position);
        if (clubActivityListDTO == null) return;

        if (!clubActivityListDTO.isManager()) {

            Uri uri = Uri.parse(ClubActivityInfoBrowserActivity.
                    getActivityUrl(clubActivityListDTO.getActId(), getContext()));

            final Intent browserIntent = new Intent(getActivity(),
                    ClubActivityInfoBrowserActivity.class);
            browserIntent.setData(uri);
            browserIntent.putExtra(ClubActivityInfoBrowserActivity.EXTRA_ACTIVITY_TYPE, 1);
            browserIntent.putExtra(ClubActivityInfoBrowserActivity.EXTRA_CLUB_ACTIVITY_ID,
                    clubActivityInfos.get(position).getActId());
            browserIntent.addCategory(Intent.CATEGORY_DEFAULT);
            browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
            browserIntent.setPackage(getActivity().getPackageName());

            this.startActivity(browserIntent);
        } else {
            Intent intent = new Intent(this.getActivity(), ClubActivityManagerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ClubActivityManagerActivity.EXTRA_DATA, clubActivityListDTO);
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_EDIT_CODE);
        }
    }

    @Override
    public void OnItemLongClick(RecyclerView.ViewHolder viewHolder, int position) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_EDIT_CODE:
                    this.refreshCallBack();
                    break;
            }
        }
    }

    class ClubActivityAdapter extends SwipeRefreshLoadRecyclerView.BaseRecyclerViewViewAdapter {
        private Context context;
        private ItemClickListener itemClickListener;

        public ClubActivityAdapter(Context context, ItemClickListener itemClickListener) {
            this.context = context;
            this.itemClickListener = itemClickListener;
        }


        class ClubActivityViewHolder extends RecyclerView.ViewHolder {

            private TextView manageTV;
            private TextView titleTV;
            private CircleImageView avaterIV;
            private TextView nickNameTV;
            private TextView locationTV;
            private TextView timeTV;
            private ImageView coverIV;
            private TextView statusTV;
            private View view;

            public ClubActivityViewHolder(View view) {
                super(view);
                this.view = view;
                manageTV = (TextView) view.findViewById(R.id.layout_club_activity_list_item_manage);
                titleTV = (TextView) view.findViewById(R.id.layout_club_activity_list_item_title);
                avaterIV = (CircleImageView) view.findViewById(R.id.layout_club_activity_list_item_avater);
                nickNameTV = (TextView) view.findViewById(R.id.layout_club_activity_list_item_nickname);
                locationTV = (TextView) view.findViewById(R.id.layout_club_activity_list_item_location);
                timeTV = (TextView) view.findViewById(R.id.layout_club_activity_list_item_time);
                coverIV = (ImageView) view.findViewById(R.id.layout_club_activity_list_item_cover);
                statusTV = (TextView) view.findViewById(R.id.layout_club_activity_list_item_status);
            }

        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder() {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_club_activity_list_item, null);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(dm.widthPixels,
                    (int) (dm.widthPixels / RESOLUTION));
            view.setLayoutParams(lp);
            ClubActivityViewHolder clubActivityViewHolder = new ClubActivityViewHolder(view);
            return clubActivityViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, Object value,
                                     final int position, boolean isLastItem) {
            if (holder instanceof ClubActivityViewHolder) {
                final ClubActivityViewHolder clubActivityViewHolder = (ClubActivityViewHolder) holder;
                final ClubActivityListDTO clubActivityInfo = (ClubActivityListDTO) value;
                clubActivityViewHolder.titleTV.setText(clubActivityInfo.getTitle());
                if (clubActivityInfo.isManager()) {
                    clubActivityViewHolder.manageTV.setVisibility(View.VISIBLE);
                } else {
                    clubActivityViewHolder.manageTV.setVisibility(View.GONE);
                }
                clubActivityViewHolder.nickNameTV.setText(NickNameRemarksUtil.disPlayName(
                        clubActivityInfo.getNickname(), clubActivityInfo.getRemarks()));

                clubActivityViewHolder.timeTV.setText(getTime(clubActivityInfo.getStartDate())
                        + " － " + getTime(clubActivityInfo.getEndDate()));
                clubActivityViewHolder.locationTV.setText(getResources().getString(R.string.activity_club_release_activities_activity_place) + "：" + clubActivityInfo.getMobPlace());
                switch (clubActivityInfo.getApplyStatus()) {
                    case ClubActivityListDTO.CLUB_ACTIVITY_STATUS_ONGOING:
                        if (clubActivityInfo.isJoined()) {
                            clubActivityViewHolder.statusTV.setBackgroundResource(
                                    R.drawable.bg_layout_club_activity_list_item_status_joined);
                            clubActivityViewHolder.statusTV.setText(getResources().getString(R.string.club_activity_has_joined));
                        } else {
                            clubActivityViewHolder.statusTV.setBackgroundResource(
                                    R.drawable.bg_layout_club_activity_list_item_status_joining);
                            clubActivityViewHolder.statusTV.setText(getResources().getString(R.string.club_activity_joining));
                        }
                        break;
                    case ClubActivityListDTO.CLUB_ACTIVITY_STATUS_ENDED:
                        clubActivityViewHolder.statusTV.setText(getResources().getString(R.string.club_activity_has_ended));
                        clubActivityViewHolder.statusTV.setBackgroundResource(
                                R.drawable.bg_layout_club_activity_list_item_status_cancel);
                        break;
                    case ClubActivityListDTO.CLUB_ACTIVITY_STATUS_CANCEL:
                        clubActivityViewHolder.statusTV.setText(getResources().getString(R.string.club_activity_has_cancel));
                        clubActivityViewHolder.statusTV.setBackgroundResource(
                                R.drawable.bg_layout_club_activity_list_item_status_cancel);
                        break;
                }
                clubActivityViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.OnItemClick(clubActivityViewHolder, position);
                    }
                });
                if (!TextUtils.isEmpty(clubActivityInfo.getAvatarImage())) {
                    Picasso.with(context)
                            .load(clubActivityInfo.getAvatarImage() + COMPRESS_URL)
                            .fit()
                            .centerCrop().placeholder(R.drawable.ic_avatar)
                            .error(R.drawable.ic_avatar)
                            .into(clubActivityViewHolder.avaterIV);
                } else {
                    Picasso.with(context)
                            .load(R.drawable.ic_avatar)
                            .fit()
                            .centerCrop().placeholder(R.drawable.ic_avatar)
                            .error(R.drawable.ic_avatar)
                            .into(clubActivityViewHolder.avaterIV);
                }

                if (!TextUtils.isEmpty(clubActivityInfo.getCover())) {
                    Picasso.with(context)
                            .load(clubActivityInfo.getCover())
                            .fit()
                            .centerCrop().placeholder(R.drawable.bg_layout_club_activity_list_item)
                            .error(R.drawable.bg_layout_club_activity_list_item)
                            .into(clubActivityViewHolder.coverIV);
                } else {
                    Picasso.with(context)
                            .load(R.drawable.bg_layout_club_activity_list_item)
                            .fit()
                            .centerCrop().placeholder(R.drawable.bg_layout_club_activity_list_item)
                            .error(R.drawable.bg_layout_club_activity_list_item)
                            .into(clubActivityViewHolder.coverIV);
                }
            }
        }

        private String getTime(String date) {
            long time = DateFormatUtil.timeFormat2Date(date);
            SimpleDateFormat sdf = new SimpleDateFormat("MM.dd HH:mm");
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(time);
        }
    }

}
