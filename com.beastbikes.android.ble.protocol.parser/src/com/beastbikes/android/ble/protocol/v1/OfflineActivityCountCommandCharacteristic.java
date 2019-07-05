package com.beastbikes.android.ble.protocol.v1;

public class OfflineActivityCountCommandCharacteristic extends
		CommandCharacteristic {
	
	private int count;
	
	public OfflineActivityCountCommandCharacteristic() {
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
