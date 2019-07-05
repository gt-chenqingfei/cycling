package com.beastbikes.android.ble.protocol.v1;

public class BeepConfigCharacteristic extends ConfigCharacteristic {

	private int muted;
	
	public BeepConfigCharacteristic() {
	}

	public int getMuted() {
		return muted;
	}

	public void setMuted(int muted) {
		this.muted = muted;
	}

}
