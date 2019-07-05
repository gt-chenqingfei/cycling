package com.beastbikes.android.ble.protocol.v1;

public class OTARequestCommandCharacteristic extends CommandCharacteristic {

	/**
     *  Process Type Value:
     *      0x01 : OTA_BLE
     *      0x02 : OTA_MCU
     *      0x03 : OTA_UI
     *      0x04 : OTA_A_GPS
     */
	private int processType;

	/*
     *  The `Flags` is one of:
     *      0x01 : OTA request packet number
     *      0x02 : OTA data transfer end
     *      0x03 : OTA activate end
     */
	private int flags;

	private int requestPacketIndex;

	public OTARequestCommandCharacteristic() {
	}

	public int getProcessType() {
		return processType;
	}

	public void setProcessType(int processType) {
		this.processType = processType;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public int getRequestPacketIndex() {
		return requestPacketIndex;
	}

	public void setRequestPacketIndex(int requestPacketIndex) {
		this.requestPacketIndex = requestPacketIndex;
	}

	@Override
	public String toString() {
		return "OTARequestCommandCharacteristic{" +
				"processType=" + processType +
				", flags=" + flags +
				", requestPacketIndex=" + requestPacketIndex +
				'}';
	}
}
