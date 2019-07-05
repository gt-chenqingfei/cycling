package com.beastbikes.android.modules.cycling.activity.ui.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.activity.ui.record.model.HeartRateModel;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 心率饼图view
 * Created by secret on 16/10/10.
 */

public class HeartPieChartView extends BasePieChartView implements View.OnClickListener {

    private TextView mTVDesc;
    private TextView mTVTime;
    private TextView mTVPercent;

    private TextView mTVDataSource;

    //恢复区
    private HeartRateBottomItemView mHeartRateBottomItemView1;
    //燃脂区
    private HeartRateBottomItemView mHeartRateBottomItemView2;
    //训练区
    private HeartRateBottomItemView mHeartRateBottomItemView3;
    //无氧区
    private HeartRateBottomItemView mHeartRateBottomItemView4;
    //极限区
    private HeartRateBottomItemView mHeartRateBottomItemView5;

    private ArrayList<HeartRateBottomItemView> heartRateBottomItemViews;
    private int currentPos = -1;
    private ArrayList<PieEntry> mEntries;

    private Animation mAnimationUp;
    private Animation mAnimationDown;

    public HeartPieChartView(Context context) {
        super(context);
    }

    public HeartPieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeartPieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        super.initView(context, attrs);

        ViewStub viewStub = (ViewStub) findViewById(R.id.viewStub_heart_rate_bottom);
        viewStub.inflate();

        ViewStub viewStub1 = (ViewStub) findViewById(R.id.viewStub_chart_data_source);
        viewStub1.inflate();

        mTVDataSource = (TextView) findViewById(R.id.textView_chart_data_source);

        mTVDesc = (TextView) findViewById(R.id.textView_heart_rate_pie_desc);
        mTVTime = (TextView) findViewById(R.id.textView_heart_rate_pie_time);
        mTVPercent= (TextView) findViewById(R.id.textView_heart_rate_pie_percent);

        mAnimationUp = AnimationUtils.loadAnimation(context, R.anim.animation_move_up);
        mAnimationUp.setFillAfter(true);
        mAnimationDown = AnimationUtils.loadAnimation(context, R.anim.animation_move_down);
        mAnimationDown.setFillAfter(true);

        mHeartRateBottomItemView1 = (HeartRateBottomItemView) findViewById(R.id.heart_rate_bottom_item_view1);
        mHeartRateBottomItemView2 = (HeartRateBottomItemView) findViewById(R.id.heart_rate_bottom_item_view2);
        mHeartRateBottomItemView3 = (HeartRateBottomItemView) findViewById(R.id.heart_rate_bottom_item_view3);
        mHeartRateBottomItemView4 = (HeartRateBottomItemView) findViewById(R.id.heart_rate_bottom_item_view4);
        mHeartRateBottomItemView5 = (HeartRateBottomItemView) findViewById(R.id.heart_rate_bottom_item_view5);

        heartRateBottomItemViews = new ArrayList<>();
        heartRateBottomItemViews.add(mHeartRateBottomItemView1);
        heartRateBottomItemViews.add(mHeartRateBottomItemView2);
        heartRateBottomItemViews.add(mHeartRateBottomItemView3);
        heartRateBottomItemViews.add(mHeartRateBottomItemView4);
        heartRateBottomItemViews.add(mHeartRateBottomItemView5);

        this.setListener();
    }

    private void setListener() {
        mHeartRateBottomItemView1.setOnClickListener(this);
        mHeartRateBottomItemView2.setOnClickListener(this);
        mHeartRateBottomItemView3.setOnClickListener(this);
        mHeartRateBottomItemView4.setOnClickListener(this);
        mHeartRateBottomItemView5.setOnClickListener(this);
    }

    /**
     * 设置数据
     * @param entries
     */
    public void setData(ArrayList<PieEntry> entries) {
        this.mEntries = entries;
        setData(entries, getColors());
        if (null == entries || entries.isEmpty()) {
            showNoDataView();
        }
    }

    private ArrayList<Integer> getColors() {
        ArrayList<Integer> colors = new ArrayList<>();
//        int[] colors = new int[] {0xfff6a09a, 0xfff06c63, 0xffe8372d, 0xffc91c03, 0xff931506};
        colors.add(0xfff6a09a);
        colors.add(0xfff06c63);
        colors.add(0xffe8372d);
        colors.add(0xffc91c03);
        colors.add(0xff931506);
        return colors;
    }

    public void setHeartRateMAxValue(int heartRate) {
        this.setItem1Range("<" + (int) Math.ceil(heartRate * 0.6));
        this.setItem2Range((int) Math.ceil(heartRate * 0.6) + " - " + (int) Math.ceil(heartRate * 0.7));
        this.setItem3Range((int) Math.ceil(heartRate * 0.7) + " - " + (int) Math.ceil(heartRate * 0.8));
        this.setItem4Range((int) Math.ceil(heartRate * 0.8) + " - " + (int) Math.ceil(heartRate * 0.9));
        this.setItem5Range(">" + (int) Math.ceil(heartRate * 0.9));
    }

    @Override
    protected void onValueSelected(Entry e) {
        super.onValueSelected(e);
        for (int i = 0; i < this.mEntries.size(); i++) {
            if (this.mEntries.get(i).getData() == e.getData()) {
                selectBottom(i);
                setMaxPercentHeartRateData((HeartRateModel) this.mEntries.get(i).getData());
                break;
            }
        }

    }

    private void selectBottom(int position) {
        heartRateBottomItemViews.get(position).startAnimation(mAnimationUp);
        if(currentPos == -1) {
            currentPos = position;
            return;
        }
        heartRateBottomItemViews.get(currentPos).startAnimation(mAnimationDown);
        heartRateBottomItemViews.get(currentPos).clearAnimation();
        currentPos = position;

    }

    @Override
    protected void onNoValueSelected() {
        super.onNoValueSelected();
        if (currentPos == -1) {
            return;
        }
        heartRateBottomItemViews.get(currentPos).startAnimation(mAnimationDown);
        heartRateBottomItemViews.get(currentPos).clearAnimation();
        currentPos = -1;
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
     * 设置下方数据
     * @param resId desc
     * @param time time
     * @param percent percent
     */
    public void setMaxPercentHeartRateData(int resId, String time, String percent) {
        this.setPieDesc(resId);
        this.setPieTime(time);
        this.setPiePercent(percent);
    }

    /**
     * 设置下方数据
     * @param model model
     */
    public void setMaxPercentHeartRateData(HeartRateModel model) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。
        setMaxPercentHeartRateData(model.getLabel(), formatter.format(model.getTime()), model.getPercent() + "%");
    }

    /**
     * 设置饼图下方区域描述
     * @param charSequence desc
     */
    private void setPieDesc(CharSequence charSequence) {
        mTVDesc.setText(charSequence);
    }

    /**
     * 设置饼图下方区域描述
     * @param resId desc
     */
    private void setPieDesc(int resId) {
        mTVDesc.setText(resId);
    }

    /**
     * 设置饼图下方时间
     * @param charSequence time
     */
    private void setPieTime(CharSequence charSequence) {
        mTVTime.setText(charSequence);
    }

    /**
     * 设置饼图下方百分比
     * @param charSequence percent
     */
    private void setPiePercent(CharSequence charSequence) {
        mTVPercent.setText(charSequence);
    }

    /**
     * 设置恢复区范围
     * @param charSequence range
     */
    private void setItem1Range(CharSequence charSequence) {
        mHeartRateBottomItemView1.setHeartRateRange(charSequence);
    }

    /**
     * 设置燃脂区范围
     * @param charSequence range
     */
    private void setItem2Range(CharSequence charSequence) {
        mHeartRateBottomItemView2.setHeartRateRange(charSequence);
    }

    /**
     * 设置训练区范围
     * @param charSequence range
     */
    private void setItem3Range(CharSequence charSequence) {
        mHeartRateBottomItemView3.setHeartRateRange(charSequence);
    }

    /**
     * 设置无氧区范围
     * @param charSequence range
     */
    private void setItem4Range(CharSequence charSequence) {
        mHeartRateBottomItemView4.setHeartRateRange(charSequence);
    }

    /**
     * 设置极限区区范围
     * @param charSequence range
     */
    private void setItem5Range(CharSequence charSequence) {
        mHeartRateBottomItemView5.setHeartRateRange(charSequence);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.heart_rate_bottom_item_view1:
                if (currentPos == 0) {
                    setNoHighLightValue();
                    return;
                }
                if (mEntries.get(0).getValue() == 0) {
                    return;
                }
                setHighLightValue(0);
                break;

            case R.id.heart_rate_bottom_item_view2:
                if (currentPos == 1) {
                    setNoHighLightValue();
                    return;
                }
                if (mEntries.get(1).getValue() == 0) {
                    return;
                }
                setHighLightValue(1);
                break;

            case R.id.heart_rate_bottom_item_view3:
                if (currentPos == 2) {
                    setNoHighLightValue();
                    return;
                }
                if (mEntries.get(2).getValue() == 0) {
                    return;
                }
                setHighLightValue(2);
                break;

            case R.id.heart_rate_bottom_item_view4:
                if (currentPos == 3) {
                    setNoHighLightValue();
                    return;
                }
                if (mEntries.get(3).getValue() == 0) {
                    return;
                }
                setHighLightValue(3);
                break;

            case R.id.heart_rate_bottom_item_view5:
                if (currentPos == 4) {
                    setNoHighLightValue();
                    return;
                }
                if (mEntries.get(4).getValue() == 0) {
                    return;
                }
                setHighLightValue(4);
                break;
        }
    }
}
