package com.beastbikes.android.ble.protocol.v1;

public class WheelConfigCharacteristic extends ConfigCharacteristic {

	private int size;
	
	public WheelConfigCharacteristic() {
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
