package com.beastbikes.android.modules.cycling.activity.ui.record;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.widget.slidingup_pannel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class RecordActionBar extends RecordBase<ActivityDTO> implements View.OnClickListener {

    public interface OnActionBarItemClickListener {
        void onActionBarItemClick(int id);
    }

    private View mVGTools;
    private View mVGCheat;
    private ImageView mBtnBack;
    private ImageView mBtnCamera;
    private ImageView mBtnUpload;
    private ImageView mBtnShare;
    private TextView mTvCheat;
    private TextView mTvReport;
    private List<Animator> mAnimatorSetIn;
    private List<Animator> mAnimatorSetOut;
    SlidingUpPanelLayout.PanelState mCurrentState = SlidingUpPanelLayout.PanelState.COLLAPSED;
    private OnActionBarItemClickListener mItemClickListener;

    public RecordActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setItemClickListener(OnActionBarItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public int getLayRes() {
        return R.layout.record_action_bar;
    }

    @Override
    public void onCreateView() {
        super.onCreateView();
        mBtnBack = (ImageView) findViewById(R.id.action_bar_back);
        mBtnCamera = (ImageView) findViewById(R.id.action_bar_tools_camera);
        mBtnUpload = (ImageView) findViewById(R.id.action_bar_tools_upload);
        mBtnShare = (ImageView) findViewById(R.id.action_bar_tools_share);
        mTvCheat = (TextView) findViewById(R.id.action_bar_tip_cheat);
        mTvReport = (TextView) findViewById(R.id.action_bar_tools_report);
        mVGTools = findViewById(R.id.action_bar_tools);
        mVGCheat = findViewById(R.id.action_bar_cheat_report);
        mBtnBack.setOnClickListener(this);
        mBtnCamera.setOnClickListener(this);
        mBtnShare.setOnClickListener(this);
        mTvCheat.setOnClickListener(this);
        mBtnUpload.setOnClickListener(this);
        mTvReport.setOnClickListener(this);
        mVGCheat.setOnClickListener(this);

        createAnimatorIn(getContext());
        createAnimatorOut(getContext());
    }

    @Override
    public void onClick(View view) {
        if (mItemClickListener != null) {
            mItemClickListener.onActionBarItemClick(view.getId());
        }
    }


    public void animation(SlidingUpPanelLayout.PanelState state,
                          SlidingUpPanelLayout.PanelState preState) {

        if (mAnimatorSetIn == null || mAnimatorSetOut == null)
            return;

        if (mCurrentState == state && state != SlidingUpPanelLayout.PanelState.DRAGGING) {
            return;
        }

        if (state == SlidingUpPanelLayout.PanelState.DRAGGING
                && mCurrentState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            for (int i = 0; i < mAnimatorSetIn.size(); i++) {
                Animator mAnimatorIn = mAnimatorSetIn.get(i);
                if (mAnimatorIn.isRunning()) {
                    mAnimatorIn.end();
                    mAnimatorIn.cancel();
                }
                mAnimatorIn.start();
            }
            mCurrentState = state;
        } else if (state == SlidingUpPanelLayout.PanelState.EXPANDED) {

            mCurrentState = state;
        } else if (state == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            for (int i = 0; i < mAnimatorSetOut.size(); i++) {
                Animator mAnimatorOut = mAnimatorSetOut.get(i);
                if (mAnimatorOut.isRunning()) {
                    mAnimatorOut.end();
                    mAnimatorOut.cancel();
                }
                mAnimatorOut.start();
            }
            mCurrentState = state;
        }
    }

    @Override
    public void onDataChanged(ActivityDTO dto) {
        super.onDataChanged(dto);
        if (dto == null)
            return;
        if (TextUtils.isEmpty(dto.getUserId()))
            return;
        if (dto.getUserId().equals(AVUser.getCurrentUser().getObjectId())) {
            if (dto.isFake()) { //如果是自己的数据显示为作弊数据
                mVGCheat.setVisibility(View.VISIBLE);
                mTvCheat.setVisibility(View.VISIBLE);
                return;
            }
            mVGTools.setVisibility(View.VISIBLE);
            if (!dto.isSynced()) {
                mBtnUpload.setVisibility(View.VISIBLE);
            }
            mBtnShare.setVisibility(View.VISIBLE);
            mBtnCamera.setVisibility(View.VISIBLE);
        } else {
            mVGCheat.setVisibility(View.VISIBLE);
            if (dto.isFake()) {
                mTvCheat.setVisibility(View.VISIBLE);
                mTvReport.setVisibility(View.GONE);
                return;
            }
            mTvCheat.setVisibility(View.GONE);
            mTvReport.setVisibility(View.VISIBLE);

            if (dto.isHasReport()) {
                mTvReport
                        .setText(R.string.activity_complete_activity_already_report);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    mTvReport.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
                            R.drawable.ic_activity_complete_report_icon_selected, 0, 0);
                }
                mTvReport.setTextColor(getResources().getColor(
                        R.color.activity_complete_activity_report_desc_color));
                mTvReport.setClickable(false);
            } else {
                mTvReport
                        .setText(R.string.activity_complete_activity_report);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    mTvReport.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
                            R.drawable.ic_activity_complete_report_icon, 0, 0);
                }
                mTvReport.setTextColor(0xffffffff);
                mTvReport.setClickable(true);
            }
        }

    }

    private void createAnimatorIn(Context context) {
        mAnimatorSetIn = new ArrayList<>();
        Animator animator = AnimatorInflater.loadAnimator(context,
                R.animator.actionbar_animator_indicator);
        animator.setTarget(mBtnBack);
        mAnimatorSetIn.add(animator);

        animator = animator.clone();
        animator.setTarget(mBtnShare);
        mAnimatorSetIn.add(animator);

        animator = animator.clone();
        animator.setTarget(mBtnUpload);
        mAnimatorSetIn.add(animator);

        animator = animator.clone();
        animator.setTarget(mTvCheat);
        mAnimatorSetIn.add(animator);

        animator = animator.clone();
        animator.setTarget(mTvReport);
        mAnimatorSetIn.add(animator);

        animator = animator.clone();
        animator.setTarget(mBtnCamera);
        mAnimatorSetIn.add(animator);

    }

    private void createAnimatorOut(Context context) {
        mAnimatorSetOut = new ArrayList<>();

        Animator animator = AnimatorInflater.loadAnimator(context, R.animator.actionbar_animator);
        animator.setTarget(mBtnBack);
        mAnimatorSetOut.add(animator);

        animator = animator.clone();
        animator.setTarget(mBtnShare);
        mAnimatorSetOut.add(animator);

        animator = animator.clone();
        animator.setTarget(mBtnUpload);
        mAnimatorSetOut.add(animator);

        animator = animator.clone();
        animator.setTarget(mTvCheat);
        mAnimatorSetOut.add(animator);

        animator = animator.clone();
        animator.setTarget(mTvReport);
        mAnimatorSetOut.add(animator);

        animator = animator.clone();
        animator.setTarget(mBtnCamera);
        mAnimatorSetOut.add(animator);
    }


}