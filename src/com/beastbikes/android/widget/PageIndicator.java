package com.beastbikes.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;

public class PageIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {
    public static final int INDICATOR_TYPE_CIRCLE = 0;
    public static final int INDICATOR_TYPE_FRACTION = 1;
    public static final String ACTION_PAGE_CHANGED ="com.airtalkee.ACTION_PAGE_CHANGED";
    public static final String EXTRA_PAGE_INDEX ="EXTRA_PAGE_INDEX";

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        updateIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public enum IndicatorType {
        CIRCLE(INDICATOR_TYPE_CIRCLE),
        FRACTION(INDICATOR_TYPE_FRACTION),
        UNKNOWN(-1);

        private int type;
        IndicatorType(int type) {
            this.type = type;
        }

        public static IndicatorType of(int value) {
            switch (value) {
                case INDICATOR_TYPE_CIRCLE:
                    return CIRCLE;
                case INDICATOR_TYPE_FRACTION:
                    return FRACTION;
                default:
                    return UNKNOWN;
            }
        }
    }

    public static final int DEFAULT_INDICATOR_SPACING = 5;

    private int mActivePosition = -1;
    private int mIndicatorSpacing;
    private boolean mIndicatorTypeChanged = false;

    private IndicatorType mIndicatorType = IndicatorType.of(INDICATOR_TYPE_CIRCLE);
    private ViewPager mViewPager;

    public PageIndicator(Context context) {
        this(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.PageIndicator, 0, 0);
        try {
            mIndicatorSpacing = a.getDimensionPixelSize(
                    R.styleable.PageIndicator_indicator_spacing,
                    dp2px(context, DEFAULT_INDICATOR_SPACING));
            int indicatorTypeValue = a.getInt(
                    R.styleable.PageIndicator_indicator_type,
                    mIndicatorType.type);
            mIndicatorType = IndicatorType.of(indicatorTypeValue);
        } finally {
            a.recycle();
        }

        init();
    }

    @SuppressLint("InlinedApi") 
    private void init() {
        setOrientation(HORIZONTAL);
        if (!(getLayoutParams() instanceof FrameLayout.LayoutParams)) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.BOTTOM | Gravity.START;
            setLayoutParams(params);
        }
    }

    public void setViewPager(ViewPager pager) {
        mViewPager = pager;
        setIndicatorType(mIndicatorType);
        mViewPager.setOnPageChangeListener(this);
    }

    public void setIndicatorType(IndicatorType indicatorType) {
        mIndicatorType = indicatorType;
        mIndicatorTypeChanged = true;
        if (mViewPager != null) {
            addIndicator(mViewPager.getAdapter().getCount());
        }
    }

    private void removeIndicator() {
        removeAllViews();
    }

    private void addIndicator(int count) {
        removeIndicator();
        if (count <= 0) return;
        if (mIndicatorType == IndicatorType.CIRCLE) {
            for (int i = 0; i < count; i++) {
                ImageView img = new ImageView(getContext());
                LayoutParams params = new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.leftMargin = mIndicatorSpacing;
                params.rightMargin = mIndicatorSpacing;
                img.setImageResource(R.drawable.circle_indicator_stroke);
                addView(img, params);
            }
        } else if (mIndicatorType == IndicatorType.FRACTION) {
            TextView textView = new TextView(getContext());
            textView.setTextColor(Color.WHITE);
            int padding = dp2px(getContext(), 10);
            textView.setPadding(padding, padding >> 1, padding, padding >> 1);
            textView.setBackgroundResource(R.drawable.transparent);
            textView.setTag(count);
            LayoutParams params = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            addView(textView, params);
        }
        updateIndicator(mViewPager.getCurrentItem());
    }

    private void updateIndicator(int position) {
        if (mIndicatorTypeChanged || mActivePosition != position) {
            mIndicatorTypeChanged = false;
            if (mIndicatorType == IndicatorType.CIRCLE) {
                if (mActivePosition == -1){
                    ((ImageView) getChildAt(position)).setImageResource(R.drawable.circle_indicator_solid);
                    mActivePosition = position;
                    return;
                }
                ((ImageView) getChildAt(mActivePosition))
                        .setImageResource(R.drawable.circle_indicator_stroke);
                ((ImageView) getChildAt(position))
                        .setImageResource(R.drawable.circle_indicator_solid);
            } else if (mIndicatorType == IndicatorType.FRACTION) {
                TextView textView = (TextView) getChildAt(0);
                //noinspection RedundantCast
                textView.setText(String.format("%d/%d", position + 1, Integer.parseInt( textView.getTag().toString())));
            }
            mActivePosition = position;
        }
    }



    private int dp2px(Context context, int dpValue) {
        return (int) context.getResources().getDisplayMetrics().density * dpValue;
    }


}
