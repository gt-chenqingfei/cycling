package com.beastbikes.android.ble.protocol.v1;

public class BarometerSensorCharacteristic extends SensorCharacteristic {

	private int diff;

	private float altitude;

	public BarometerSensorCharacteristic() {
	}

	public int getDifference() {
		return diff;
	}

	public void setDifference(int diff) {
		this.diff = diff;
	}

	public float getAltitude() {
		return altitude;
	}

	public void setAltitude(float altitude) {
		this.altitude = altitude;
	}

}
