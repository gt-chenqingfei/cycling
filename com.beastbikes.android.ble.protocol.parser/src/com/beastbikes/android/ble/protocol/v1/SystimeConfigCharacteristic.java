package com.beastbikes.android.ble.protocol.v1;

public class SystimeConfigCharacteristic extends ConfigCharacteristic {

	private long systime;
	
	public SystimeConfigCharacteristic() {
	}

	public long getSystime() {
		return systime;
	}

	public void setSystime(long systime) {
		this.systime = systime;
	}
	
}
