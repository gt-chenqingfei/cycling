package com.beastbikes.android.modules.cycling.activity.ui.record.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.text.DecimalFormat;

/**
 *
 * Created by secret on 16/10/10.
 */

public class UnitFormatter implements AxisValueFormatter {

    private DecimalFormat decimalFormat;

    public UnitFormatter() {
        this.decimalFormat = new DecimalFormat("#");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return decimalFormat.format(value * 10) + " - " + decimalFormat.format((value + 1) * 10);
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
