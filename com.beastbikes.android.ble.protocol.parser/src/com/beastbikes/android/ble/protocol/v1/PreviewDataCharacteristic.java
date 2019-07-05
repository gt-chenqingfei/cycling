package com.beastbikes.android.ble.protocol.v1;

import java.util.Arrays;

/**
 * 预览数据
 * 
 * @author icedan
 * 
 */
public class PreviewDataCharacteristic extends SyncDataCharacteristic {

	// total_packet_count
	private int totalPacketCount;

	// current_packet_index
	private int currentPacketIndex;

	private CyclingActivityCharacteristic activity;

	private CyclingSampleCharacteristic[] samples;

	public PreviewDataCharacteristic() {
	}

	public int getTotalPacketCount() {
		return totalPacketCount;
	}

	public void setTotalPacketCount(int totalPacketCount) {
		this.totalPacketCount = totalPacketCount;
	}

	public int getCurrentPacketIndex() {
		return currentPacketIndex;
	}

	public void setCurrentPacketIndex(int currentPacketIndex) {
		this.currentPacketIndex = currentPacketIndex;
	}

	public CyclingSampleCharacteristic[] getSamples() {
		return samples;
	}

	public void setSamples(CyclingSampleCharacteristic[] samples) {
		this.samples = samples;
	}

	public CyclingActivityCharacteristic getActivity() {
		return activity;
	}

	public void setActivity(CyclingActivityCharacteristic activity) {
		this.activity = activity;
	}

	@Override
	public String toString() {
		return "PreviewDataCharacteristic{" +
				"totalPacketCount=" + totalPacketCount +
				", currentPacketIndex=" + currentPacketIndex +
				", activity=" + activity +
				", samples=" + Arrays.toString(samples) +
				'}';
	}
}
