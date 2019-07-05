package com.beastbikes.android.ble.protocol.v1;

public class CrcCharacteristic {

	private int crc8;
	
	public CrcCharacteristic() {
	}

	public int getCrc8() {
		return crc8;
	}

	public void setCrc8(int crc8) {
		this.crc8 = crc8;
	}

}
