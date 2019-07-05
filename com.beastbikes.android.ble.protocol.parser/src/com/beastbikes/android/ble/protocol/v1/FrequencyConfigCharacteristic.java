package com.beastbikes.android.ble.protocol.v1;

public class FrequencyConfigCharacteristic extends ConfigCharacteristic {

	private int frequency;
	
	public FrequencyConfigCharacteristic(){
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

}
