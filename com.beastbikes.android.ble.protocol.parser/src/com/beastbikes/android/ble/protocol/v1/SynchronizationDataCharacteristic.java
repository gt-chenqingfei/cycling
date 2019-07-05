package com.beastbikes.android.ble.protocol.v1;

import java.util.Arrays;

/**
 * 同步数据
 * @author icedan
 *
 */
public class SynchronizationDataCharacteristic extends
		SyncDataCharacteristic {

	// total_packet_count
	private int totalPacketCount;
	
	// current_packet_index
	private int currentPacketIndex;
	
	private CyclingSampleCharacteristic[] samples;
	
	public SynchronizationDataCharacteristic() {
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

	@Override
	public String toString() {
		return "SynchronizationDataCharacteristic{" +
				"totalPacketCount=" + totalPacketCount +
				", currentPacketIndex=" + currentPacketIndex +
				", samples=" + Arrays.toString(samples) +
				'}';
	}
}
