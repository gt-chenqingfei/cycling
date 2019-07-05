package com.beastbikes.android.ble.protocol.v1;

public class BatterySensorCharacteristic extends SensorCharacteristic {

	private int percentage;

	/**
	 * 0: 未充电
	 * 1: 充电中
	 */
	private int chargeState;
	
	public BatterySensorCharacteristic() {
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public int getChargeState() {
		return chargeState;
	}

	public void setChargeState(int chargeState) {
		this.chargeState = chargeState;
	}

	@Override
	public String toString() {
		return "BatterySensorCharacteristic{" +
				"percentage=" + percentage +
				", chargeState=" + chargeState +
				'}';
	}
}
