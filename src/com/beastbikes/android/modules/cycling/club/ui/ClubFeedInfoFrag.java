package com.beastbikes.android.modules.cycling.club.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.dialog.InputDialog;
import com.beastbikes.android.home.HomeActivity;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedManager;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedService;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeed;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.cycling.club.ui.adapter.ClubFeedAdapter;
import com.beastbikes.android.modules.cycling.club.ui.view.FeedItemHeader;
import com.beastbikes.android.modules.cycling.club.ui.widget.CommentEditView;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.HistogramDTO;
import com.beastbikes.android.modules.user.widget.HistogramView;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.SwipeRefreshAndLoadLayout;
import com.beastbikes.android.widget.stickylistlibrary.stickylistheaders.StickyListHeadersListView;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqingfei on 16/1/8.
 */
@LayoutResource(R.layout.clubfeed_frag)
public class ClubFeedInfoFrag extends SessionFragment implements
        StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
        StickyListHeadersListView.OnStickyHeaderChangedListener, Constants,
        ClubFeedManager.CacheFeedDataListener, View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, SwipeRefreshAndLoadLayout.OnLoadListener,
        ClubFeedService.ClubFeedPostNotifyListener, View.OnTouchListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String EXTRA_CLUB_ID = "club_id";
    public static final String EXTRA_CLUB_INFO = "club_info";
    public static final String EXTRA_CLUB_IS_STATUS_CHANGED = "is_statusChanged";
    public static final int REQ_FINISH_CLUB_INFO = 2;
    public static final int REQ_BACK_TO_REFRESH = 4;

    @IdResource(R.id.refresh_layout)
    private SwipeRefreshAndLoadLayout refreshLayout;

    @IdResource(R.id.list)
    private StickyListHeadersListView stickyList;

    @IdResource(R.id.clubfeed_post_container)
    private FrameLayout clubfeePostContainer;

    @IdResource(R.id.addclubrl)
    private RelativeLayout addclubRL;

    @IdResource(R.id.joinclub)
    private TextView joinclubTV;

    @IdResource(R.id.clubfeed_isprivate)
    private LinearLayout clubfeedIsPrivate;

    @IdResource(R.id.clubfeed_join_club)
    private TextView clubfeedPrivateMsg;

    private HistogramView clubHistogramView;

    private ClubFeedAdapter adapterClubFeed;
    private boolean fadeHeader = true;
    private FeedItemHeader feedHeader;
    private ClubInfoCompact clubInfoCompact;
    private ClubInfoCompact myClubInfo;
    private ClubManager clubManager;
    private ClubFeedManager clubFeedManager;
    private String clubId;
    private CommentEditView commentEditView;
    private static final int PAGE_COUNT = 10;
    private long startStamp = 0;
    private InputDialog clubApplyDialog;
    private boolean isPrivate;
    private boolean isManager = false;
    public boolean isMyClub = false;
    private boolean isStatusChanged;

    private SharedPreferences userSp;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SpeedxAnalytics.onEvent(getActivity(),
                "查看某个未加入的俱乐部主页", "click_club");

        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }

        this.clubHistogramView = new HistogramView(getActivity());
        this.clubfeedIsPrivate.addView(this.clubHistogramView);
        this.clubHistogramView.setTitle(R.string.label_every_month_distance);

        clubManager = new ClubManager(getActivity());
        clubFeedManager = new ClubFeedManager(getActivity());

        if (bundle.getSerializable(EXTRA_CLUB_INFO) != null) {
            this.clubInfoCompact = (ClubInfoCompact) bundle.getSerializable(EXTRA_CLUB_INFO);
            this.isPrivate = this.clubInfoCompact.getIsPrivate();
        }
        this.clubId = bundle.getString(EXTRA_CLUB_ID);

        this.isStatusChanged = bundle.getBoolean(EXTRA_CLUB_IS_STATUS_CHANGED, false);
        if (TextUtils.isEmpty(clubId))
            return;
        myClubInfo = getMyClub();

        if (myClubInfo != null) {
            isManager = myClubInfo.getLevel() == 128;
            isMyClub = clubId.equals(myClubInfo.getObjectId());
            if (isMyClub) {
                clubInfoCompact = myClubInfo;
            }
        }
        clubfeePostContainer.addView(commentEditView = new CommentEditView(getActivity()));
        feedHeader = new FeedItemHeader(getActivity());
        feedHeader.setViewPagerNestedpParent(refreshLayout, stickyList);
        refreshLayout.setChildListView(stickyList.getWrappedList());
        adapterClubFeed = new ClubFeedAdapter(getActivity(), clubInfoCompact, commentEditView, isMyClub);

        stickyList.setOnStickyHeaderChangedListener(this);
        stickyList.setOnStickyHeaderOffsetChangedListener(this);
        stickyList.addHeaderView(feedHeader);

        stickyList.setDrawingListUnderStickyHeader(true);
        stickyList.setAreHeadersSticky(true);
        stickyList.setAdapter(adapterClubFeed);

        stickyList.setStickyHeaderTopOffset(-20);
        stickyList.setOnTouchListener(this);

        refreshLayout.setOnLoadListener(this);
        refreshLayout.setOnRefreshListener(this);
        addclubRL.setOnClickListener(this);

        if (adapterClubFeed != null) {
            List<ClubFeed> feeds = new ArrayList<>();
            feeds.add(new ClubFeed(-1));
            adapterClubFeed.notifyDataSetChanged(feeds, false);
        }

        ClubFeedService.getInstance().checkSchedule(getActivity());

        refreshUI();
        getClubInfo(clubId);

        if (AVUser.getCurrentUser() != null) {
            this.userSp = this.getActivity().getSharedPreferences(AVUser.getCurrentUser()
                    .getObjectId(), 0);
            this.userSp.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onResume() {
        super.onResume();
        ClubFeedService.getInstance().setClubFeedPostNotifyListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (clubInfoCompact != null) {
//                getActivity().setTitle(clubInfoCompact.getName());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_BACK_TO_REFRESH:
                if (resultCode == Activity.RESULT_OK) {
                    fetchClubFeedList(clubId, false, this);
                }
                break;
            case REQ_FINISH_CLUB_INFO:
                if (resultCode == Activity.RESULT_OK) {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ClubFeedService.getInstance().setClubFeedPostNotifyListener(null);
    }

    @Override
    public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition,
                                      long headerId) {
        header.setAlpha(1);
    }

    @Override
    public void onStickyHeaderOffsetChanged(StickyListHeadersListView l, View header, int offset) {
        if (fadeHeader && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            header.setAlpha(1 - (offset / (float) header.getMeasuredHeight()));
        }
    }

    /**
     * 获取我的俱乐部信息
     */
    private ClubInfoCompact getMyClub() {

        try {
            return clubManager.getMyClub(getUserId());
        } catch (BusinessException e) {
            return null;
        }
    }

    /**
     * 获取俱乐部详情
     *
     * @param clubId
     */
    private void getClubInfo(final String clubId) {
        if (TextUtils.isEmpty(clubId)) {
            return;
        }

        fetchClubFeedList(clubId, false, null);
        getAsyncTaskQueue().add(
                new AsyncTask<String, Void, ClubInfoCompact>() {

                    @Override
                    protected ClubInfoCompact doInBackground(String... params) {
                        try {
                            return clubManager.getClubInfo(params[0]);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(ClubInfoCompact result) {
                        if (null == result)
                            return;

                        clubInfoCompact = result;

                        if (clubInfoCompact.getIsPrivate()) {
                            isPrivate = true;
                        } else {
                            isPrivate = false;
                        }

                        isManager = clubInfoCompact.getLevel() == 128;

                        refreshUI();
                    }

                }, clubId);
    }

    /**
     * 获取俱乐Feed
     *
     * @param clubId
     * @param isPullRefresh 是否为手动刷新
     */
    private void fetchClubFeedList(final String clubId, final boolean isPullRefresh, final
    ClubFeedManager.CacheFeedDataListener listener) {

        String myClubId = "";
        if (myClubInfo != null) {
            myClubId = myClubInfo.getObjectId();
        }
        if (TextUtils.isEmpty(clubId) || (isPrivate && !clubId.equals(myClubId))) {
            refreshLayout.setRefreshing(false);
            return;
        }

        getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<ClubFeed>>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        refreshLayout.setRefreshing(isPullRefresh);
                    }

                    @Override
                    protected List<ClubFeed> doInBackground(String... params) {
                        try {
                            return clubFeedManager.getClubTimeLine
                                    (params[0], PAGE_COUNT, listener);

                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<ClubFeed> result) {
                        refreshLayout.setRefreshing(false);
                        refreshLayout.setCanLoad(!(result == null || result.size() < PAGE_COUNT));
                        if (null == result)
                            return;
                        if (result.size() > 0) {
                            startStamp = result.get(result.size() - 1).getStamp();

                            if (adapterClubFeed != null) {
                                adapterClubFeed.notifyDataSetChanged(result, false);
                            }
                        }
                    }

                }, clubId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addclubrl:
//            case R.id.clubfeed_join_club:
                if (clubInfoCompact == null)
                    return;
                switch (clubInfoCompact.getStatus()) {
                    case ClubInfoCompact.CLUB_STATUS_NONE:
                        if (null != myClubInfo && myClubInfo.getStatus() == ClubInfoCompact.CLUB_STATUS_APPLY &&
                                myClubInfo.getObjectId() != clubInfoCompact.getObjectId()) {//是否已经加入其他俱乐部
                            final MaterialDialog dialog = new MaterialDialog(getActivity());
                            dialog.setMessage(R.string.club_dialog_apply_again_msg)
                                    .setPositiveButton(R.string.activity_club_manager_dialog_ok, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            applyJoinClub("");
                                        }
                                    }).setNegativeButton(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            }).show();
                        } else {
                            if (clubInfoCompact.getMembers() == clubInfoCompact.getMaxMembers()) {
                                final MaterialDialog dialog = new MaterialDialog(getActivity());
                                dialog.setMessage(R.string.club_full)
                                        .setPositiveButton(R.string.clubapplyanyway, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                applyJoinClub("");
                                            }
                                        }).setNegativeButton(R.string.cancel, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            } else {

                                clubApplyDialog = new InputDialog(getActivity(),
                                        getString(R.string.club_dialog_hint), null,
                                        new InputDialog.OnInputDialogClickListener() {

                                            @Override
                                            public void onInputDialogClickOk(String text) {
                                                applyJoinClub(text);
                                                clubApplyDialog.dismiss();
                                            }
                                        });
                                clubApplyDialog.show();


                            }
                        }
                        break;
                    case ClubInfoCompact.CLUB_STATUS_APPLY://撤销加入申请
                        final MaterialDialog dialog = new MaterialDialog(getActivity());
                        dialog.setMessage(R.string.club_dialog_quit_warning).
                                setPositiveButton(R.string.activity_club_manager_dialog_ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        cancelClubApply();
                                    }
                                }).setNegativeButton(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }).show();
                        break;
                }
                break;
        }
    }

    @Override
    public void onGetFeedCacheData(final List<ClubFeed> data) {
        Activity activity = getActivity();
        if (activity == null) return;

        if (data != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapterClubFeed != null) {
                        adapterClubFeed.dataSetChanged(data);
                        adapterClubFeed.notifyDataSetClean();
                    }
                }
            });

        }
    }

    @Override
    public void onRefresh() {

        fetchClubFeedList(clubId, true, null);
    }

    @Override
    public void onLoad() {
        if (clubInfoCompact == null || clubInfoCompact.getStatus() == ClubInfoCompact.CLUB_STATUS_APPLY ||
                clubInfoCompact.getStatus() == ClubInfoCompact.CLUB_STATUS_NONE) {
            refreshLayout.setCanLoad(false);
            Toasts.show(getActivity(), getString(R.string.clubfeed_loadmore_tip));
            return;
        }
        clubFeedLoadMore(clubId);
    }

    @Override
    public void onClubFeedNotify(final String clubId) {
        if (clubId.equals(this.clubId)) {
            Activity activity = getActivity();
            if (activity == null) return;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fetchClubFeedList(clubId, false, null);
                }
            });
        }
    }

    /**
     * 申请加入俱乐部
     *
     * @param content
     */
    private void applyJoinClub(final String content) {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return new ClubManager(getActivity()).
                            postCmdClub(0, clubInfoCompact.getObjectId(), content, null);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    clubInfoCompact.setStatus(ClubInfoCompact.CLUB_STATUS_APPLY);
                    getMyClub();
                    refreshUI();
                }
            }
        });
    }

    private void cancelClubApply() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return new ClubManager(getActivity()).
                            postCmdClub(2, clubInfoCompact.getObjectId(), "", null);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });
    }

    private void refreshUI() {
        if (clubInfoCompact != null) {

            switch (clubInfoCompact.getStatus()) {
                case ClubInfoCompact.CLUB_STATUS_NONE: {
                    if (isAdded()) {
                        joinclubTV.setText(getResources().getString(R.string.club_info_item_club_apply));
                    }
                    if (!clubInfoCompact.getIsPrivate()) {
                        isPrivate = false;
                        addclubRL.setVisibility(View.VISIBLE);
                        if (clubfeedIsPrivate.getVisibility() == View.VISIBLE) {
                            clubfeedIsPrivate.setVisibility(View.GONE);
                            this.clubfeedPrivateMsg.setVisibility(View.GONE);
                        }

                    } else {
                        isPrivate = true;
                        clubfeedIsPrivate.setVisibility(View.VISIBLE);
                        this.clubfeedPrivateMsg.setVisibility(View.VISIBLE);
                        this.fetchClubDiagram(clubInfoCompact.getObjectId());
                        addclubRL.setVisibility(View.VISIBLE);
                    }
                    break;
                }
                case ClubInfoCompact.CLUB_STATUS_APPLY: {
                    if (clubInfoCompact.getIsPrivate()) {
                        clubfeedIsPrivate.setVisibility(View.VISIBLE);
                        this.clubfeedPrivateMsg.setVisibility(View.VISIBLE);
                        this.fetchClubDiagram(clubInfoCompact.getObjectId());
                    }
                    addclubRL.setVisibility(View.VISIBLE);
                    if (isAdded()) {
                        joinclubTV.setText(getResources().getString(R.string.club_info_item_club_undo_apply));
                    }
                    break;
                }
                case ClubInfoCompact.CLUB_STATUS_ESTABLISHED:
                case ClubInfoCompact.CLUB_STATUS_JOINED: {
                    addclubRL.setVisibility(View.INVISIBLE);
                    clubfeedIsPrivate.setVisibility(View.INVISIBLE);
                    this.clubfeedPrivateMsg.setVisibility(View.INVISIBLE);
                    stickyList.setPadding(0, 0, 0, 0);
                    break;
                }

            }

            if (feedHeader != null) {
                feedHeader.notify(clubInfoCompact);
            }
            if (adapterClubFeed != null) {
                adapterClubFeed.notifyNav(clubInfoCompact);
            }

            //TODO
            if (isStatusChanged && HomeActivity.currentPage == 1) {
                if (getActivity() != null)
                    getActivity().setTitle(clubInfoCompact.getName());
            }
        }

    }

    /**
     * 获取俱乐Feed
     *
     * @param clubId
     */
    private void clubFeedLoadMore(final String clubId) {
        if (TextUtils.isEmpty(clubId)) {
            return;
        }
        getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<ClubFeed>>() {
                    @Override
                    protected List<ClubFeed> doInBackground(String... params) {
                        try {
                            return clubFeedManager.clubTimeLineLoadMore
                                    (params[0], startStamp, 0, PAGE_COUNT, ClubFeedInfoFrag.this);

                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<ClubFeed> result) {
                        refreshLayout.setLoading(false);

                        refreshLayout.setCanLoad(!(result == null || result.size() < PAGE_COUNT));

                        if (null == result) {
                            return;
                        }

                        if (result.size() > 0) {
                            startStamp = result.get(result.size() - 1).getStamp();
                            if (adapterClubFeed != null) {
                                adapterClubFeed.notifyDataSetChanged(result, true);
                            }
                        }
                    }

                }, clubId);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PREF_CLUB_STATUS.equals(key)) {
            int status = sharedPreferences.getInt(PREF_CLUB_STATUS, 0);
            if (status == ClubInfoCompact.CLUB_STATUS_JOINED) {
                getClubInfo(clubId);
            }
        } else if (PUSH.PREF_KEY.NOTIFY_CLUB_NOTICE.equals(key)
                ||  PUSH.PREF_KEY.NOTIFY_CLUB_FEED.equals(key)
                || PREF_CLUB_REFRESH.equals(key)) {
            onRefresh();
        } else if (PUSH.PREF_KEY.NOTIFY_CLUB_MEMBER_QUIT.equals(key)) {
            clubInfoCompact.setMembers(clubInfoCompact.getMembers() - 1);
            feedHeader.notify(clubInfoCompact);
        }
        else if(CLUB_NAME.equals(key)){
            String clubName = userSp.getString(CLUB_NAME,"");
            if(!TextUtils.isEmpty(clubName)) {
                clubInfoCompact.setName(clubName);
            }
//            getActivity().setTitle(clubInfoCompact.getName());
        }
    }

    /**
     * 获取俱乐部月排行柱状图
     *
     * @param clubId
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void fetchClubDiagram(final String clubId) {
        if (null == getActivity() || getActivity().isDestroyed() || getActivity().isFinishing()) {
            return;
        }
        final UserManager userManager = new UserManager(getActivity());
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, HistogramDTO>() {
            @Override
            protected HistogramDTO doInBackground(String... params) {
                return userManager.getDiagram(1, clubId, 30);
            }

            @Override
            protected void onPostExecute(HistogramDTO histogramDTO) {
                clubHistogramView.setHistogramDTO(histogramDTO);
            }
        });
    }

}
