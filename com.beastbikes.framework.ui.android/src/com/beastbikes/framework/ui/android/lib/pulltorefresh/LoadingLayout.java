package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.beastbikes.framework.ui.android.R;


public class LoadingLayout extends FrameLayout implements PullableView {

    static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

    //	private final ImageView headerImage;
//	private final ProgressBar headerProgress;
    private final TextView headerText;
//	private final TextView timeText;

    private String pullLabel;
    private String refreshingLabel;
    private String releaseLabel;

    private long time;

    private int emptyTxtColor = 0xff666666;

    // private final Animation rotateAnimation, resetRotateAnimation;

    public LoadingLayout(Context context) {
        super(context);
        ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(
                R.layout.pull_to_refresh_header, this);
        headerText = (TextView) header.findViewById(R.id.pull_to_refresh_text);
//		timeText = (TextView) header
//				.findViewById(R.id.pull_to_refresh_updatetime);
//		headerImage = (ImageView) header
//				.findViewById(R.id.pull_to_refresh_image);
//		headerProgress = (ProgressBar) header
//				.findViewById(R.id.pull_to_refresh_progress);

        // final Interpolator interpolator = new LinearInterpolator();
        // rotateAnimation = new RotateAnimation(0, 180,
        // Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
        // 0.5f);
        // rotateAnimation.setInterpolator(interpolator);
        // rotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
        // rotateAnimation.setFillAfter(true);
        //
        // resetRotateAnimation = new RotateAnimation(180, 0,
        // Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
        // 0.5f);
        // resetRotateAnimation.setInterpolator(interpolator);
        // resetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
        // resetRotateAnimation.setFillAfter(true);

        this.releaseLabel = context.getString(R.string.pull_down_to_refresh_pull_label);
        this.pullLabel = context.getString(R.string.pull_to_refresh_release_label);
        this.refreshingLabel = context.getString(R.string.pull_to_refresh_refreshing_label);
        headerText.setTextColor(emptyTxtColor);

//		headerImage.setImageResource(R.drawable.loading);
    }

    public LoadingLayout(Context context, final int mode, String releaseLabel,
                         String pullLabel, String refreshingLabel) {
        super(context);
        ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(
                R.layout.pull_to_refresh_header, this);
        headerText = (TextView) header.findViewById(R.id.pull_to_refresh_text);
        headerText.setTextColor(emptyTxtColor);
//		timeText = (TextView) header
//				.findViewById(R.id.pull_to_refresh_updatetime);
//		headerImage = (ImageView) header
//				.findViewById(R.id.pull_to_refresh_image);
//		headerProgress = (ProgressBar) header
//				.findViewById(R.id.pull_to_refresh_progress);

        // final Interpolator interpolator = new LinearInterpolator();
        // rotateAnimation = new RotateAnimation(0, 180,
        // Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
        // 0.5f);
        // rotateAnimation.setInterpolator(interpolator);
        // rotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
        // rotateAnimation.setFillAfter(true);
        //
        // resetRotateAnimation = new RotateAnimation(180, 0,
        // Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
        // 0.5f);
        // resetRotateAnimation.setInterpolator(interpolator);
        // resetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
        // resetRotateAnimation.setFillAfter(true);

        this.releaseLabel = releaseLabel;
        this.pullLabel = pullLabel;
        this.refreshingLabel = refreshingLabel;

        switch (mode) {
            case PullToRefreshBase.MODE_PULL_UP_TO_REFRESH:
//			headerImage.setImageResource(R.drawable.loading);
                break;
            case PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH:
            default:
//			headerImage.setImageResource(R.drawable.loading);
                break;
        }
    }

    @Override
    public void reset() {
        headerText.setText(pullLabel);
        headerText.setTextColor(emptyTxtColor);
//		headerImage.setVisibility(View.VISIBLE);
//		headerProgress.setVisibility(View.GONE);
    }

    @Override
    public void releaseToRefresh() {
        headerText.setText(releaseLabel);
    }

    @Override
    public void setPullLabel(String pullLabel) {
        this.pullLabel = pullLabel;
        this.headerText.setText(pullLabel);
        headerText.setTextColor(emptyTxtColor);
    }

    @Override
    public void refreshing() {
        headerText.setText(refreshingLabel);
        headerText.setTextColor(emptyTxtColor);
//		headerImage.setVisibility(View.INVISIBLE);
//		headerProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void setRefreshingLabel(String refreshingLabel) {
        this.refreshingLabel = refreshingLabel;
    }

    @Override
    public void setReleaseLabel(String releaseLabel) {
        this.releaseLabel = releaseLabel;
    }

    @Override
    public void pullToRefresh() {
//		timeText.setText(this.getUpdateTimeText());
        headerText.setText(pullLabel);
        headerText.setTextColor(emptyTxtColor);
//		headerImage.clearAnimation();
        // headerImage.startAnimation(resetRotateAnimation);
    }

    @Override
    public void setTextColor(int color) {
        headerText.setTextColor(emptyTxtColor);
//		timeText.setTextColor(color);
    }

    @Override
    public void setUpdateTime(long t) {
        time = t;
    }

    @Override
    public long getUpdateTime() {
        return time;
    }

    @Override
    public void updateTimeLabel() {
//		timeText.setText(this.getUpdateTimeText());
    }

//	private String getUpdateTimeText() {
//		if (time > 0) {
//			Date lastDate = new Date(time);
//			return lastDate.toString();
//		}
//		return null;
//	}

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void updateRefresh(int curValue, int maxValue) {
        //	float degree = ((float) Math.abs(curValue) / (float) maxValue) * 360 * 3;
//		ViewHelper.setRotation(headerImage, degree);
    }


}
