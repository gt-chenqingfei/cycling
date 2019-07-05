package com.beastbikes.android.ble.protocol.v1;

public class ReveiveResponseCommandCharacteristic extends CommandCharacteristic {

	/**
	 * Flags Value. 0x01 : Success 0x02 : Fail 0x03 : Timeout 0x04 : CRC Check
	 * Fail
	 */
	private int flags;

	public ReveiveResponseCommandCharacteristic() {
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

}
