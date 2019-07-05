package com.beastbikes.android.ble.protocol.v1;

public abstract class SensorCharacteristic extends AbstractCharacteristicValue {

	private int type;

	public SensorCharacteristic() {
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
