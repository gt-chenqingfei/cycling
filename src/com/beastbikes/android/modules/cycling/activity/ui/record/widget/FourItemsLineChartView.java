package com.beastbikes.android.modules.cycling.activity.ui.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewStub;
import android.widget.TextView;

import com.beastbikes.android.R;

/**
 * 折线图下方四个选项
 * Created by secret on 16/10/9.
 */

public class FourItemsLineChartView extends BaseLineChartView {

    /**
     * 左上角
     */
    private TextView mTVLeftTopValue;
    private TextView mTVLeftTopLabel;
    /**
     * 右上角
     */
    private TextView mTVRightTopValue;
    private TextView mTVRightTopLabel;

    /**
     * 左下角
     */
    private TextView mTVLeftBottomValue;
    private TextView mTVLeftBottomLabel;

    /**
     * 右下角
     */
    private TextView mTVRightBottomValue;
    private TextView mTVRightBottomLabel;

    public FourItemsLineChartView(Context context) {
        super(context);
    }

    public FourItemsLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FourItemsLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        super.initView(context, attrs);
        ViewStub viewStub = (ViewStub) findViewById(R.id.layout_cycling_data);
        viewStub.inflate();
        mTVLeftTopValue = (TextView) findViewById(R.id.textView_left_top_value);
        mTVLeftTopLabel = (TextView) findViewById(R.id.textView_left_top_label);

        mTVRightTopValue = (TextView) findViewById(R.id.textView_right_top_value);
        mTVRightTopLabel = (TextView) findViewById(R.id.textView_right_top_label);

        mTVLeftBottomValue = (TextView) findViewById(R.id.textView_left_bottom_value);
        mTVLeftBottomLabel = (TextView) findViewById(R.id.textView_left_bottom_label);

        mTVRightBottomValue = (TextView) findViewById(R.id.textView_right_bottom_value);
        mTVRightBottomLabel = (TextView) findViewById(R.id.textView_right_bottom_label);
    }

    /**
     * 左上角value
     * @param charSequence value
     */
    public void setLeftTopValue(CharSequence charSequence) {
        mTVLeftTopValue.setText(charSequence);
//        this.invalidate();
    }

    /**
     * 左上角value
     * @param resId value
     */
    public void setLeftTopValue(int resId) {
        mTVLeftTopValue.setText(resId);
//        this.invalidate();
    }

    /**
     * 左上角label
     * @param charSequence label
     */
    public void setLeftTopLabel(CharSequence charSequence) {
        mTVLeftTopLabel.setText(charSequence);
//        this.invalidate();
    }

    /**
     * 左上角label
     * @param resId label
     */
    public void setLeftTopLabel(int resId) {
        mTVLeftTopLabel.setText(resId);
//        this.invalidate();
    }

    /**
     * 右上角value
     * @param charSequence value
     */
    public void setRightTopValue(CharSequence charSequence) {
        mTVRightTopValue.setText(charSequence);
//        this.invalidate();
    }

    /**
     * 右上角value
     * @param resId value
     */
    public void setRightTopValue(int resId) {
        mTVRightTopValue.setText(resId);
//        this.invalidate();
    }

    /**
     * 右上角label
     * @param charSequence label
     */
    public void setRightTopLabel(CharSequence charSequence) {
        mTVRightTopLabel.setText(charSequence);
//        this.invalidate();
    }

    /**
     * 右上角label
     * @param resId label
     */
    public void setRightTopLabel(int resId) {
        mTVRightTopLabel.setText(resId);
//        this.invalidate();
    }

    /**
     * 左下角value
     * @param charSequence value
     */
    public void setLeftBottomValue(CharSequence charSequence) {
        mTVLeftBottomValue.setText(charSequence);
//        this.invalidate();
    }

    /**
     * 左下角value
     * @param resId value
     */
    public void setLeftBottomValue(int resId) {
        mTVLeftBottomValue.setText(resId);
//        this.invalidate();
    }

    /**
     * 左下角label
     * @param charSequence label
     */
    public void setLeftBottomLabel(CharSequence charSequence) {
        mTVLeftBottomLabel.setText(charSequence);
//        this.invalidate();
    }

    /**
     * 左下角label
     * @param resId label
     */
    public void setLeftBottomLabel(int resId) {
        mTVLeftBottomLabel.setText(resId);
//        this.invalidate();
    }

    /**
     * 右下角value
     * @param charSequence value
     */
    public void setRightBottomValue(CharSequence charSequence) {
        mTVRightBottomValue.setText(charSequence);
//        this.invalidate();
    }

    /**
     * 右下角value
     * @param resId value
     */
    public void setRightBottomValue(int resId) {
        mTVRightBottomValue.setText(resId);
//        this.invalidate();
    }

    /**
     * 右下角label
     * @param charSequence label
     */
    public void setRightBottomLabel(CharSequence charSequence) {
        mTVRightBottomLabel.setText(charSequence);
//        this.invalidate();
    }

    /**
     * 右下角label
     * @param resId label
     */
    public void setRightBottomLabel(int resId) {
        mTVRightBottomLabel.setText(resId);
//        this.invalidate();
    }

}
