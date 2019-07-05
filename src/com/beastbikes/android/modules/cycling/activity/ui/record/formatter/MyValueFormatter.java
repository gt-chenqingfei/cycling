package com.beastbikes.android.modules.cycling.activity.ui.record.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.text.DecimalFormat;

public class MyValueFormatter implements AxisValueFormatter {

    private DecimalFormat mFormat;
    
    public MyValueFormatter() {
        mFormat = new DecimalFormat("0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (value == 0) {
            return "";
        }
        return mFormat.format(value);
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
