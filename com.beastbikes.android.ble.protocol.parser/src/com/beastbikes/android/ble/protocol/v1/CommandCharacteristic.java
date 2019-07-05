package com.beastbikes.android.ble.protocol.v1;

public abstract class CommandCharacteristic extends AbstractCharacteristicValue {

	private int type;

	public CommandCharacteristic() {
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
