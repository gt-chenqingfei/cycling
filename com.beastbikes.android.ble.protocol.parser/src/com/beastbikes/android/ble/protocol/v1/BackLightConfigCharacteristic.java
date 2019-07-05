package com.beastbikes.android.ble.protocol.v1;

public class BackLightConfigCharacteristic extends ConfigCharacteristic {

	private int duration;

	public BackLightConfigCharacteristic() {
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

}
