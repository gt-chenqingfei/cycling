package com.beastbikes.android.ble.protocol.v1;

public abstract class ConfigCharacteristic extends AbstractCharacteristicValue {

	private int type;
	
	public ConfigCharacteristic() {
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
