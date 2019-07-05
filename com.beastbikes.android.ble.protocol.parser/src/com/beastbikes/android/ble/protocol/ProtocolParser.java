package com.beastbikes.android.ble.protocol;

import android.bluetooth.BluetoothGattCharacteristic;


public interface ProtocolParser {

	public CharacteristicValue parse(byte[] data);
	
	public CharacteristicValue parse(BluetoothGattCharacteristic characteristic);

}
