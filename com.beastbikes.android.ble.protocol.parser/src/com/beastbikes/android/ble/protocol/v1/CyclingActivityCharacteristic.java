package com.beastbikes.android.ble.protocol.v1;

/**
 * cycling_preview_activity 预览骑行记录
 * 
 * @author icedan
 * 
 */
public class CyclingActivityCharacteristic {

	// sync_data_type
	private int syncDataType;

	private int stopTime;

	private int startTime;

	private int sampleRate;

	private int totalDistance;

	private int totalTime;

	private int sampleCount;

	private int climbHeight;

	public CyclingActivityCharacteristic() {
	}

	public int getSyncDataType() {
		return syncDataType;
	}

	public void setSyncDataType(int syncDataType) {
		this.syncDataType = syncDataType;
	}

	public int getStopTime() {
		return stopTime;
	}

	public void setStopTime(int stopTime) {
		this.stopTime = stopTime;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public int getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(int totalDistance) {
		this.totalDistance = totalDistance;
	}

	public int getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}

	public int getSampleCount() {
		return sampleCount;
	}

	public void setSampleCount(int sampleCount) {
		this.sampleCount = sampleCount;
	}

	public int getClimbHeight() {
		return climbHeight;
	}

	public void setClimbHeight(int climbHeight) {
		this.climbHeight = climbHeight;
	}

	@Override
	public String toString() {
		return "CyclingActivityCharacteristic{" +
				"syncDataType=" + syncDataType +
				", stopTime=" + stopTime +
				", startTime=" + startTime +
				", sampleRate=" + sampleRate +
				", totalDistance=" + totalDistance +
				", totalTime=" + totalTime +
				", sampleCount=" + sampleCount +
				", climbHeight=" + climbHeight +
				'}';
	}
}
