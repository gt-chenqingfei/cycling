package com.beastbikes.android.ble.protocol.v1;

public abstract class SyncDataCharacteristic extends AbstractCharacteristicValue {

	// The Ride Data Type is one of:
	// 0x0001 : Ride Preview
	// 0x0002 : Ride Synchronization
	private int cyclingDataType;

	public SyncDataCharacteristic() {
	}

	public int getCyclingDataType() {
		return cyclingDataType;
	}

	public void setCyclingDataType(int cyclingDataType) {
		this.cyclingDataType = cyclingDataType;
	}

}
