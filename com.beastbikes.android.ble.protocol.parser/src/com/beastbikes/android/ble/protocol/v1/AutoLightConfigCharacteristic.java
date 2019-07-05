package com.beastbikes.android.ble.protocol.v1;

public class AutoLightConfigCharacteristic extends ConfigCharacteristic {

	private int enabled;
	
	public AutoLightConfigCharacteristic() {
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

}
