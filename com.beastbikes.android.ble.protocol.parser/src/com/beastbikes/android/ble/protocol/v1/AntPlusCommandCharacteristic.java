package com.beastbikes.android.ble.protocol.v1;

public class AntPlusCommandCharacteristic extends CommandCharacteristic {

	private int manufacturerId;
	
	public AntPlusCommandCharacteristic() {
	}

	public int getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(int manufacturerId) {
		this.manufacturerId = manufacturerId;
	}
	
}
