package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.home.HomeActivity;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.util.List;

/**
 * Created by chenqingfei on 16/7/17.
 */
@LayoutResource(R.layout.fragment_club)
public class ClubFragment extends SessionFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG_FEED_INFO_FRAG = "tag_feed_info_frag";
    public static final String TAG_CLUB_DISCOVER_FRAG = "tag_club_discover_frag";

    /**
     * 创建请求码
     */
    private static final int REQ_CREATE = 101;

    private ClubManager mClubManager;
    private ClubInfoCompact mClubInfo;
    public int status;
    public String clubId;

    private FragmentManager mFragmentManager;

    private SharedPreferences mUserSp;
    private LoadingDialog mLoadingDialog;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mClubManager = new ClubManager(getActivity());
        mFragmentManager = getChildFragmentManager();

        AVUser user = AVUser.getCurrentUser();
        if (null != user)
            this.mUserSp = getContext().getSharedPreferences(user.getObjectId(), 0);

        if (null != mUserSp) {
            this.mUserSp.registerOnSharedPreferenceChangeListener(this);
        }
        this.refresh(false);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        AVUser user = AVUser.getCurrentUser();
        if (null == user) {
            return;
        }
        if (TextUtils.isEmpty(user.getClubId())) {
            inflater.inflate(R.menu.club_activity_menu, menu);
        } else {
            inflater.inflate(R.menu.clubfeed_info_menu, menu);

            if (mClubManager.getClubLevel() != 128) {
                menu.findItem(R.id.menu_post_notice).setVisible(false);
            } else {
                menu.findItem(R.id.menu_post_notice).setVisible(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_club_create:
                createClub();
                break;
            case R.id.menu_post_dynamic:
                Intent it = new Intent(this.getActivity(), ClubFeedPostActivity.class);
                if (null != mClubInfo) {
                    it.putExtra(ClubFeedPostActivity.EXTRA_CLUB_ID, mClubInfo.getObjectId());
                }
                startActivityForResult(it, ClubFeedInfoFrag.REQ_BACK_TO_REFRESH);
                break;
            case R.id.menu_post_activity:
                final Intent intent = new Intent(this.getActivity(),
                        ClubActivityReleaseActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_post_notice:
                Intent noticeIntent = new Intent(getActivity(), ClubPostNoticeActivity.class);
                if (null != mClubInfo) {
                    noticeIntent.putExtra(ClubFeedInfoFrag.EXTRA_CLUB_INFO, mClubInfo);
                    noticeIntent.putExtra(ClubMoreActivity.EXTRA_CLUB_NOTICE, mClubInfo.getNotice());
                    noticeIntent.putExtra(ClubFeedInfoFrag.EXTRA_CLUB_ID, mClubInfo.getObjectId());
                }
                startActivityForResult(noticeIntent, ClubFeedInfoFrag.REQ_BACK_TO_REFRESH);
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = mFragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != this.mUserSp) {
            this.mUserSp.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.contains(Constants.PREF_CLUB_STATUS)) {
            int status = sharedPreferences.getInt(s, 0);
            boolean isChange = false;

            //放在ClubDiscoverFrag里处理
            if (status == ClubInfoCompact.CLUB_STATUS_APPLY_REFUSED || status == ClubInfoCompact.CLUB_STATUS_APPLY) {
                return;
            }

            if (status == ClubInfoCompact.CLUB_STATUS_QUIT) {
                isChange = true;
                status = ClubInfoCompact.CLUB_STATUS_NONE;
            }
            onClubStatusChanged(status, isChange);
        }
    }

    private void onClubStatusChanged(int status, boolean isChange) {
        boolean isReset = false;

        if (status == ClubInfoCompact.CLUB_STATUS_NONE) {
            if (isChange) {
                isReset = true;
                this.mUserSp.edit().putInt(Constants.PREF_CLUB_STATUS, status).apply();
            }
        } else {
            isReset = true;
        }

        if (isReset) {
            this.refresh(true);
            getActivity().supportInvalidateOptionsMenu();
        }

    }

    /**
     * 刷新俱乐部状态
     *
     * @param statusChanged 是否为应用启动后加入俱乐部成功 true 是, false 不是
     */
    public void refresh(boolean statusChanged) {
        AVUser avUser = AVUser.getCurrentUser();
        if (avUser == null) {
            return;
        }

        if (!TextUtils.isEmpty(avUser.getClubId())) {
            Bundle bundle = new Bundle();
            try {
                mClubInfo = mClubManager.getMyClub(avUser.getObjectId());
                if (mClubInfo != null && mClubInfo.getStatus() != ClubInfoCompact.CLUB_STATUS_NONE) {
                    bundle.putSerializable(ClubFeedInfoFrag.EXTRA_CLUB_INFO, mClubInfo);
                    bundle.putString(ClubFeedInfoFrag.EXTRA_CLUB_ID, mClubInfo.getObjectId());
                    bundle.putBoolean(ClubFeedInfoFrag.EXTRA_CLUB_IS_STATUS_CHANGED, statusChanged);
//                    getActivity().setTitle(mClubInfo.getName());
                } else {
                    if (HomeActivity.currentPage == 1) {
                        getActivity().setTitle(R.string.club_info_title);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.showFeedInfoFragment(bundle);

        } else {
            this.showClubDiscoverFragment();
        }
    }

    /**
     * show ClubFeedInfoFrag
     */
    private void showFeedInfoFragment(Bundle bundle) {
        if (null == mFragmentManager) {
            mFragmentManager = getChildFragmentManager();
        }
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        ClubFeedInfoFrag feedInfoFrag = (ClubFeedInfoFrag) mFragmentManager.findFragmentByTag(TAG_FEED_INFO_FRAG);

        if (null == feedInfoFrag) {
            feedInfoFrag = (ClubFeedInfoFrag) Fragment.instantiate(getContext(), ClubFeedInfoFrag.class.getName(), bundle);
        }

        fragmentTransaction.replace(R.id.fragment_club_linear_container, feedInfoFrag);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commitAllowingStateLoss();

    }

    /**
     * show ClubDiscoverFrag
     */
    private void showClubDiscoverFragment() {
        if (HomeActivity.currentPage == 1) {
            getActivity().setTitle(R.string.club_info_title);
        }
        if (null == mFragmentManager) {
            mFragmentManager = getChildFragmentManager();
        }
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        ClubDiscoverFrag clubDiscoverFrag = (ClubDiscoverFrag) mFragmentManager.findFragmentByTag(TAG_CLUB_DISCOVER_FRAG);
        if (null == clubDiscoverFrag) {
            clubDiscoverFrag = (ClubDiscoverFrag) Fragment.instantiate(getContext(), ClubDiscoverFrag.class.getName(), null);
        }

        fragmentTransaction.replace(R.id.fragment_club_linear_container, clubDiscoverFrag);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void createClub() {
        if (status == ClubInfoCompact.CLUB_STATUS_APPLY) {
            final MaterialDialog dialog = new MaterialDialog(getActivity());
            dialog.setMessage(R.string.club_dialog_joined_msg1);
            dialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    postCmdClub();
                }
            });
            dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            startActivityForResult(new Intent(getActivity(), ClubCreateActivity.class), REQ_CREATE);
        }

    }

    /**
     * 申请、退出、撤销申请俱乐部 （0 申请加入，1 退出，2 撤销申请）
     */
    public void postCmdClub() {

        this.mLoadingDialog = new LoadingDialog(getActivity(),
                getString(R.string.club_info_waiting), false);

        this.mLoadingDialog.show();
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    if (!TextUtils.isEmpty(clubId))
                        return mClubManager.postCmdClub(2, clubId, null, null);
                    return false;
                } catch (BusinessException e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (null != mLoadingDialog)
                    mLoadingDialog.dismiss();

                if (result) {
                    startActivity(new Intent(getActivity(), ClubCreateActivity.class));

                } else {
                    Toasts.show(getActivity(), getString(R.string.club_info_cancel_toast_fail));
                }

            }

        });
    }

}
