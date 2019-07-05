package com.beastbikes.android.modules.cycling.activity.ui.record.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.text.DecimalFormat;

public class MyAxisValueFormatter implements AxisValueFormatter
{

    private DecimalFormat mFormat;

    public MyAxisValueFormatter() {
        mFormat = new DecimalFormat("#");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (value == 0) {
            return "bpm";
        }
        return mFormat.format(value) + " %";
    }

    @Override
    public int getDecimalDigits() {
        return 1;
    }
}
