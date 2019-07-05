package com.beastbikes.android.ble.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;

/**
 * 我的设备界面设置选项
 * Created by secret on 16/8/25.
 */
public class SpeedForceSettingView extends LinearLayout {

    //是否可用,true 则左侧label为黑色,ViewGroup可点击; false,左侧label为灰色,不可点击
    private boolean mEnable;
    //左侧label
    private TextView mTVLabel;
    private String mLable;
    //右侧value
    private TextView mTVValue;
    private String mValue;
    //右侧dot
    private View mVDot;
    private boolean mDotVisible;
    //底部线
    private View mVBottomLine;
    private boolean mLineVisible;

    public SpeedForceSettingView(Context context) {
        super(context);
        this.initView(context, null, 0);
    }

    public SpeedForceSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs, 0);
    }

    public SpeedForceSettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {

        inflate(context, R.layout.speed_force_setting_item, this);

        mTVLabel = (TextView) findViewById(R.id.speed_force_setting_item_label);
        mTVValue = (TextView) findViewById(R.id.speed_force_setting_item_value);
        mVDot = findViewById(R.id.speed_force_setting_item_dot);
        mVBottomLine = findViewById(R.id.speed_force_setting_item_bottom_line);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpeedForceSettingView, defStyleAttr, 0);

        mEnable = typedArray.getBoolean(R.styleable.SpeedForceSettingView_isItemEnable, true);
        mLable = typedArray.getString(R.styleable.SpeedForceSettingView_label);
        mValue = typedArray.getString(R.styleable.SpeedForceSettingView_value);
        mDotVisible = typedArray.getBoolean(R.styleable.SpeedForceSettingView_dotVisible, false);
        mLineVisible = typedArray.getBoolean(R.styleable.SpeedForceSettingView_lineVisible, false);

        this.setUI();
    }

    private void setUI() {

        if (mEnable) {
            mTVLabel.setTextColor(Color.parseColor("#212121"));
        } else {
            mTVLabel.setTextColor(Color.parseColor("#4c212121"));
        }

        mTVLabel.setText(mLable);
        mTVValue.setText(mValue);
        mVDot.setVisibility(mDotVisible ? View.VISIBLE : View.GONE);
        mVBottomLine.setVisibility(mLineVisible ? View.VISIBLE : View.GONE);

    }


    /**
     * 设置是否可点击
     *
     * @param enable
     */
    public void setmEnable(boolean enable) {
        this.mEnable = enable;
        if (mEnable) {
            mTVLabel.setTextColor(Color.parseColor("#212121"));
        } else {
            mTVLabel.setTextColor(Color.parseColor("#4c212121"));
        }
    }

    public boolean ismEnable() {
        return mEnable;
    }

    public void setLabel(String label) {
        this.mLable = label;
        mTVLabel.setText(mLable);
    }

    public void setLabel(int resID) {
        this.mLable = getResources().getString(resID);
        mTVLabel.setText(mLable);
    }

    public void setValue(String value) {
        this.mValue = value;
        mTVValue.setText(mValue);
    }

    public void setValue(int resID) {
        this.mValue = getResources().getString(resID);
        mTVValue.setText(mValue);
    }

    public void setDotVisible(boolean visible) {
        this.mDotVisible = visible;
        mVDot.setVisibility(mDotVisible ? View.VISIBLE : View.GONE);
    }

    public void setLineVisible(boolean visible) {
        this.mLineVisible = visible;
        mVBottomLine.setVisibility(mLineVisible ? View.VISIBLE : View.GONE);
    }

}
