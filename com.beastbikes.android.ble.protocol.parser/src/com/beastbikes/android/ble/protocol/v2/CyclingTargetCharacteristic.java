package com.beastbikes.android.ble.protocol.v2;

import com.beastbikes.android.ble.protocol.v1.CommandCharacteristic;

/**
 * Created by icedan on 16/9/21.
 */
public class CyclingTargetCharacteristic extends CommandCharacteristic {

    // Default value: 0x00, Monthly cycling target.
    private int targetType;
    // User cycling target value, unit is meter.
    private int targetValue;
    // User current cycling distance, according to the target type, unit is meter.
    private int currentValue;

    public CyclingTargetCharacteristic() {

    }

    public int getTargetType() {
        return targetType;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(int targetValue) {
        this.targetValue = targetValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }
}
