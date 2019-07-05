package com.beastbikes.android.ble.protocol.v1;

public class PhotoSensitiveSensorCharacteristic extends SensorCharacteristic {
	
	/**
	 * photo_sensitive
	 */
	private int photoSensitive;
	
	public PhotoSensitiveSensorCharacteristic() {
	}

	public int getPhotoSensitive() {
		return photoSensitive;
	}

	public void setPhotoSensitive(int photoSensitive) {
		this.photoSensitive = photoSensitive;
	}
	
}
