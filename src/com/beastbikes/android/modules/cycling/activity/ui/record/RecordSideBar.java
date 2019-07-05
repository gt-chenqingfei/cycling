package com.beastbikes.android.modules.cycling.activity.ui.record;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.widget.slidingup_pannel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class RecordSideBar extends RecordBase<ActivityDTO> implements View.OnClickListener {

    public interface OnSideBarItemClickListener {
        public void onSideBarItemClick(int id);
    }

    private OnSideBarItemClickListener mItemClickListener;
    private ImageView mBtnZoom;
    private ImageView mBtnVisible;
    private int mToggleDefaultMargin = 0;

    private List<Animator> mAnimatorSetIn;
    private List<Animator> mAnimatorSetOut;
    private SlidingUpPanelLayout.PanelState mCurrentState;

    private long currentMill;


    public RecordSideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayRes() {
        return R.layout.record_side_bar;
    }

    @Override
    public void onCreateView() {
        super.onCreateView();
        mBtnVisible = (ImageView) findViewById(R.id.record_side_btn_visible);
        mBtnZoom = (ImageView) findViewById(R.id.record_side_btn_zoom);
        mBtnZoom.setOnClickListener(this);
        mBtnVisible.setOnClickListener(this);
        createAnimatorIn(getContext());
        createAnimatorOut(getContext());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.record_side_btn_visible) {
            long diff = System.currentTimeMillis() - currentMill;
            if (diff < 3000)
                return;
            currentMill = System.currentTimeMillis();
        }
        if (this.mItemClickListener != null) {
            this.mItemClickListener.onSideBarItemClick(view.getId());
        }
    }

    public void setOnSideBarItemClickListener(OnSideBarItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setDefaultMargin(int margin) {
        this.mToggleDefaultMargin = margin;
        zoom();
    }

    public void zoom() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();

        if (params.bottomMargin == 0) {
            params.setMargins(0, 0, 0, this.mToggleDefaultMargin);
            mBtnZoom.setImageResource(R.drawable.ic_side_zoom_out);

        } else {
            params.setMargins(0, 0, 0, 0);
            mBtnZoom.setImageResource(R.drawable.ic_side_zoom_in);
        }

        this.setLayoutParams(params);
    }

    public void visibleToggle(boolean isVisible) {
        mBtnVisible.setImageResource(!isVisible
                ? R.drawable.ic_side_public
                : R.drawable.ic_side_private);
    }

    public void animation(SlidingUpPanelLayout.PanelState state,
                          SlidingUpPanelLayout.PanelState preState) {
        if (mAnimatorSetIn == null || mAnimatorSetOut == null)
            return;

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
        if (dto.getUserId().equals(AVUser.getCurrentUser().getObjectId())) {
            mBtnVisible.setVisibility(View.VISIBLE);
            visibleToggle(dto.getIsPrivate() == 1);
        }
    }

    private void createAnimatorIn(Context context) {
        mAnimatorSetIn = new ArrayList<>();
        Animator animator = AnimatorInflater.loadAnimator(context,
                R.animator.actionbar_animator_indicator);
        animator.setTarget(mBtnZoom);
        mAnimatorSetIn.add(animator);

        animator = animator.clone();
        animator.setTarget(mBtnVisible);
        mAnimatorSetIn.add(animator);


    }

    private void createAnimatorOut(Context context) {
        mAnimatorSetOut = new ArrayList<>();

        Animator animator = AnimatorInflater.loadAnimator(context, R.animator.actionbar_animator);
        animator.setTarget(mBtnZoom);
        mAnimatorSetOut.add(animator);

        animator = animator.clone();
        animator.setTarget(mBtnVisible);
        mAnimatorSetOut.add(animator);
    }
}