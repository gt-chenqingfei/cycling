package com.beastbikes.android.modules.cycling.activity.ui.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewStub;

import com.beastbikes.android.R;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

/**
 * 爬坡饼图
 * Created by secret on 16/10/10.
 */

public class SlopePieChartView extends BasePieChartView {

    private SlopePieBottomItemView slopePieBottomItemView1;
    private SlopePieBottomItemView slopePieBottomItemView2;
    private SlopePieBottomItemView slopePieBottomItemView3;

    public SlopePieChartView(Context context) {
        super(context);
    }

    public SlopePieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlopePieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        super.initView(context, attrs);
        ViewStub viewStub = (ViewStub) findViewById(R.id.viewStub_slope_bottom);
        viewStub.inflate();

        setTouchable(false);

        slopePieBottomItemView1 = (SlopePieBottomItemView) findViewById(R.id.slope_pie_bottom_item_view1);
        slopePieBottomItemView2 = (SlopePieBottomItemView) findViewById(R.id.slope_pie_bottom_item_view2);
        slopePieBottomItemView3 = (SlopePieBottomItemView) findViewById(R.id.slope_pie_bottom_item_view3);
    }

    /**
     * 设置数据
     * @param entries
     */
    public void setData(ArrayList<PieEntry> entries) {
        setData(entries, getColors());
    }

    private int[] getColors() {
//        int[] colors = new int[] {Color.parseColor("#1f8b4d"), Color.parseColor("#27ae60"), Color.parseColor("#2ecc71")};
        int[] colors = new int[] {0xff1f8b4d, 0xff27ae60, 0xff2ecc71};
        return colors;
    }

    /**
     * 上坡百分比
     * @param charSequence percent
     */
    public void setUpSlopePercent(CharSequence charSequence) {
        slopePieBottomItemView1.setSlopePercent(charSequence);
    }

    /**
     * 上坡平均速度
     * @param charSequence speed
     */
    public void setUpSlopeAverageSpeed(CharSequence charSequence) {
        slopePieBottomItemView1.setSlopAverageSpeed(charSequence);
    }

    /**
     * 平路百分比
     * @param charSequence percent
     */
    public void setFlatRoadPercent(CharSequence charSequence) {
        slopePieBottomItemView2.setSlopePercent(charSequence);
    }

    /**
     * 平路平均速度
     * @param charSequence speed
     */
    public void setFlatRoadAverageSpeed(CharSequence charSequence) {
        slopePieBottomItemView2.setSlopAverageSpeed(charSequence);
    }

    /**
     * 下坡百分比
     * @param charSequence percent
     */
    public void setDownSlopePercent(CharSequence charSequence) {
        slopePieBottomItemView3.setSlopePercent(charSequence);
    }

    /**
     * 下坡平均速度
     * @param charSequence speed
     */
    public void setDownSlopeAverageSpeed(CharSequence charSequence) {
        slopePieBottomItemView3.setSlopAverageSpeed(charSequence);
    }
}
