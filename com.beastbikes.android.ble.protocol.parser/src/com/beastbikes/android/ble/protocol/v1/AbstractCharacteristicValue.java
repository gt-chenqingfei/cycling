package com.beastbikes.android.ble.protocol.v1;

import com.beastbikes.android.ble.protocol.CharacteristicValue;

public abstract class AbstractCharacteristicValue implements
		CharacteristicValue {

	private int protocol;

	private int crc;

	@Override
	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	@Override
	public int getCrc() {
		return crc;
	}

	public void setCrc(int crc) {
		this.crc = crc;
	}

}
