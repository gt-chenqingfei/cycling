package com.beastbikes.android.modules.cycling.activity.ui.record.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;


/**
 * Created by secret on 16/10/10.
 */

public class PercentFormatter implements AxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf(value) + "%";
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
