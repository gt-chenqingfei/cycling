package com.beastbikes.android.ble.protocol.v1;

/**
 * Sample 打点数据
 * 
 * @author icedan
 * 
 */
public class CyclingSampleCharacteristic {

	private int syncDataType;

	private int timestamp;

	private double latitude;

	private double longitude;

	private int altitude;

	private int speed;

	private int maxSpeed;

	private int distance;

	private int cadence;

	private int maxCadence;

	private int heartRate;

	private int maxHeartRate;

	public CyclingSampleCharacteristic() {
	}

	public int getSyncDataType() {
		return syncDataType;
	}

	public void setSyncDataType(int syncDataType) {
		this.syncDataType = syncDataType;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
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

	public int getAltitude() {
		return altitude;
	}

	public void setAltitude(int altitude) {
		this.altitude = altitude;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getCadence() {
		return cadence;
	}

	public void setCadence(int cadence) {
		this.cadence = cadence;
	}

	public int getMaxCadence() {
		return maxCadence;
	}

	public void setMaxCadence(int maxCadence) {
		this.maxCadence = maxCadence;
	}

	public int getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(int heartRate) {
		this.heartRate = heartRate;
	}

	public int getMaxHeartRate() {
		return maxHeartRate;
	}

	public void setMaxHeartRate(int maxHeartRate) {
		this.maxHeartRate = maxHeartRate;
	}

	@Override
	public String toString() {
		return "CyclingSampleCharacteristic{" +
				"syncDataType=" + syncDataType +
				", timestamp=" + timestamp +
				", latitude=" + latitude +
				", longitude=" + longitude +
				", altitude=" + altitude +
				", speed=" + speed +
				", maxSpeed=" + maxSpeed +
				", distance=" + distance +
				", cadence=" + cadence +
				", maxCadence=" + maxCadence +
				", heartRate=" + heartRate +
				", maxHeartRate=" + maxHeartRate +
				'}';
	}
}
