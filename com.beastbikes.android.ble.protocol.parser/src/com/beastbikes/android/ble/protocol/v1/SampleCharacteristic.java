package com.beastbikes.android.ble.protocol.v1;

public class SampleCharacteristic extends AbstractCharacteristicValue {

    private int totalDistance;

    private long timestamp;

    private int gpsSpeed;

    private int antPlusSpeed;

    private int cadence;

    private int heartBeatRate;

    private int totalTime;

    public SampleCharacteristic() {
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getGpsSpeed() {
        return gpsSpeed;
    }

    public void setGpsSpeed(int gpsSpeed) {
        this.gpsSpeed = gpsSpeed;
    }

    public int getAntPlusSpeed() {
        return antPlusSpeed;
    }

    public void setAntPlusSpeed(int antPlusSpeed) {
        this.antPlusSpeed = antPlusSpeed;
    }

    public int getCadence() {
        return cadence;
    }

    public void setCadence(int cadence) {
        this.cadence = cadence;
    }

    public int getHeartBeatRate() {
        return heartBeatRate;
    }

    public void setHeartBeatRate(int heartBeatRate) {
        this.heartBeatRate = heartBeatRate;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        return "SampleCharacteristic{" +
                "totalDistance=" + totalDistance +
                ", timestamp=" + timestamp +
                ", gpsSpeed=" + gpsSpeed +
                ", antPlusSpeed=" + antPlusSpeed +
                ", cadence=" + cadence +
                ", heartBeatRate=" + heartBeatRate +
                ", totalTime=" + totalTime +
                '}';
    }
}
