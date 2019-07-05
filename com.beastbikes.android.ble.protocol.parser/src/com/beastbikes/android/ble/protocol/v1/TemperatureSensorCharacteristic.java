package com.beastbikes.android.ble.protocol.v1;

public class TemperatureSensorCharacteristic extends SensorCharacteristic {

	private float degree;

	public TemperatureSensorCharacteristic() {
	}

	public float getDegree() {
		return degree;
	}

	public void setDegree(float degree) {
		this.degree = degree;
	}

}
