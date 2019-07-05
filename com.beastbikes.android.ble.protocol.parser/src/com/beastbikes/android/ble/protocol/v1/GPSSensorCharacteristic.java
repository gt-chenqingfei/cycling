package com.beastbikes.android.ble.protocol.v1;

public class GPSSensorCharacteristic extends SensorCharacteristic {

	private double latitude;

	private double longitude;

	private float altitude;

	private int accuracy;

	public GPSSensorCharacteristic() {
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public float getAltitude() {
		return altitude;
	}

	public void setAltitude(float altitude) {
		this.altitude = altitude;
	}

	public int getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}

	@Override
	public String toString() {
		return "GPSSensorCharacteristic{" +
				"latitude=" + latitude +
				", longitude=" + longitude +
				", altitude=" + altitude +
				", accuracy=" + accuracy +
				'}';
	}
}
