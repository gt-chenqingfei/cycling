package com.beastbikes.android.modules.cycling.activity.ui.record.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.text.DecimalFormat;

/**
 *
 * Created by secret on 16/10/8.
 */

public class MAxisValueFormatter implements AxisValueFormatter {

    private String unit;
    private float totalDistance;
    private DecimalFormat decimalFormat;

    public MAxisValueFormatter(String unit) {
        this.unit = unit;
        decimalFormat = new DecimalFormat("0.0");
    }

    public MAxisValueFormatter(String unit, float totalDistance) {
        this.unit = unit;
        this.totalDistance = totalDistance;
        decimalFormat = new DecimalFormat("0.0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (value == 0) {
            return unit;
        }
        if (totalDistance == 0) {
            return decimalFormat.format(value);
        }
        return decimalFormat.format(value / axis.getAxisMaximum() * totalDistance);
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
