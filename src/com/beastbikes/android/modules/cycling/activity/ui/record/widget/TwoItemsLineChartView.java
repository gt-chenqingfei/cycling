package com.beastbikes.android.modules.cycling.activity.ui.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;

/**
 * line chart bottom two items
 * Created by secret on 16/10/9.
 */

public class TwoItemsLineChartView extends BaseLineChartView {

    /**
     * 左侧
     */
    private TextView mTVLeftValue;
    private TextView mTVLeftLabel;

    /**
     * 右侧
     */
    private TextView mTVRightValue;
    private TextView mTVRightLabel;
    private TextView mTVDataSource;

    public TwoItemsLineChartView(Context context) {
        super(context);
    }

    public TwoItemsLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TwoItemsLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        super.initView(context, attrs);

        ViewStub viewStub = (ViewStub) findViewById(R.id.layout_cycling_data1);
        viewStub.inflate();

        ViewStub viewStub1 = (ViewStub) findViewById(R.id.viewStub_chart_no_data);
        viewStub1.inflate();

        ViewStub viewStub2 = (ViewStub) findViewById(R.id.viewStub_chart_data_source);
        viewStub2.inflate();

        mTVDataSource = (TextView) findViewById(R.id.textView_chart_data_source);

        mTVLeftValue = (TextView) findViewById(R.id.textView_left_item_value);
        mTVLeftLabel = (TextView) findViewById(R.id.textView_left_item_label);

        mTVRightValue = (TextView) findViewById(R.id.textView_right_item_value);
        mTVRightLabel = (TextView) findViewById(R.id.textView_right_item_label);
    }

    /**
     * 右上角数据来自于
     * @param charSequence
     */
    public void setDataSource(CharSequence charSequence) {
        this.mTVDataSource.setVisibility(View.VISIBLE);
        this.mTVDataSource.setText(getContext().getString(R.string.str_label_come_from) + charSequence);
    }

    /**
     * 左侧value
     * @param charSequence value
     */
    public void setLeftValue(CharSequence charSequence) {
        mTVLeftValue.setText(charSequence);
    }

    /**
     * 左侧value
     * @param resId value
     */
    public void setLeftValue(int resId) {
        mTVLeftValue.setText(resId);
    }

    /**
     * 左侧label
     * @param charSequence label
     */
    public void setLeftLabel(CharSequence charSequence) {
        mTVLeftLabel.setText(charSequence);
    }

    /**
     * 左侧label
     * @param resId label
     */
    public void setLeftLabel(int resId) {
        mTVLeftLabel.setText(resId);
    }

    /**
     * 右侧value
     * @param charSequence value
     */
    public void setRightValue(CharSequence charSequence) {
        mTVRightValue.setText(charSequence);
    }

    /**
     * 右侧value
     * @param resId value
     */
    public void setRightValue(int resId) {
        mTVRightValue.setText(resId);
    }

    /**
     * 右侧label
     * @param charSequence label
     */
    public void setRightLabel(CharSequence charSequence) {
        mTVRightLabel.setText(charSequence);
    }

    /**
     * 右侧label
     * @param resId label
     */
    public void setRightLabel(int resId) {
        mTVRightLabel.setText(resId);
    }

    public void showNodataView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relative_chart_no_data);
        relativeLayout.setVisibility(View.VISIBLE);

        ImageView mImgNoDataIcon = (ImageView) findViewById(R.id.img_chart_no_data_icon);
        TextView mTVNoConnectedDevices = (TextView) findViewById(R.id.textView_chart_no_data_function);
        TextView mTVFunctionDesc = (TextView) findViewById(R.id.textView_chart_no_data_function_desc);
        TextView mTVNoDataDesc = (TextView) findViewById(R.id.textView_chart_no_data_bottom_desc);

        mImgNoDataIcon.setImageResource(R.drawable.ic_chart_no_heart_rage);
        mTVNoConnectedDevices.setText(R.string.str_have_not_connect_heart_rate_device);
        mTVNoDataDesc.setText(R.string.str_heart_rate_only_support_desc);
        mTVFunctionDesc.setText(R.string.str_heart_rate_no_data_desc);
    }
}
