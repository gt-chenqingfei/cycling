package com.beastbikes.android.modules.cycling.sections.dto;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by caoxiao on 16/4/18.
 */
public class UserSegmentDTO {
    private JSONArray origin;
    private double originLongitude;
    private double originLatitude;
    private int slope;
    private long segmentId;
    private double legLength;
    private long duration;
    private JSONArray destination;
    private double destinationLongitude;
    private double destinationLatitude;
    private String lordNick;
    private int rank;
    private String lordAvatar;
    private double range;
    private String polyline;
    private int difficult;
    private double avgSpeed;
    private String lordId;
    private double altDiff;
    private String name;

    public UserSegmentDTO(JSONObject jsonObject) {
        this.origin = jsonObject.optJSONArray("origin");
        if(origin!=null) {
            this.originLongitude = origin.optDouble(0);
            this.originLatitude = origin.optDouble(1);
        }
        this.slope = jsonObject.optInt("slope");
        this.segmentId = jsonObject.optLong("segmentId");
        this.legLength = jsonObject.optDouble("legLength");
        this.duration = jsonObject.optLong("duration");
        this.destination = jsonObject.optJSONArray("origin");
        if(destination!=null) {
            this.destinationLongitude = destination.optDouble(0);
            this.destinationLatitude = destination.optDouble(1);
        }
        this.lordNick = jsonObject.optString("lordNick");
        this.rank = jsonObject.optInt("rank");
        this.lordAvatar = jsonObject.optString("lordAvatar");
        this.range = jsonObject.optDouble("range");
        this.polyline = jsonObject.optString("polyline");
        this.difficult = jsonObject.optInt("difficult");
        this.avgSpeed = jsonObject.optDouble("avgSpeed");
        this.lordId = jsonObject.optString("lordId");
        this.altDiff = jsonObject.optDouble("altDiff");
        this.name = jsonObject.optString("name");
    }

    public JSONArray getOrigin() {
        return origin;
    }

    public void setOrigin(JSONArray origin) {
        this.origin = origin;
    }

    public double getOriginLongitude() {
        return originLongitude;
    }

    public void setOriginLongitude(double originLongitude) {
        this.originLongitude = originLongitude;
    }

    public double getOriginLatitude() {
        return originLatitude;
    }

    public void setOriginLatitude(double originLatitude) {
        this.originLatitude = originLatitude;
    }

    public int getSlope() {
        return slope;
    }

    public void setSlope(int slope) {
        this.slope = slope;
    }

    public long getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(long segmentId) {
        this.segmentId = segmentId;
    }

    public double getLegLength() {
        return legLength;
    }

    public void setLegLength(double legLength) {
        this.legLength = legLength;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public JSONArray getDestination() {
        return destination;
    }

    public void setDestination(JSONArray destination) {
        this.destination = destination;
    }

    public double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public String getLordNick() {
        return lordNick;
    }

    public void setLordNick(String lordNick) {
        this.lordNick = lordNick;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getLordAvatar() {
        return lordAvatar;
    }

    public void setLordAvatar(String lordAvatar) {
        this.lordAvatar = lordAvatar;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public int getDifficult() {
        return difficult;
    }

    public void setDifficult(int difficult) {
        this.difficult = difficult;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getLordId() {
        return lordId;
    }

    public void setLordId(String lordId) {
        this.lordId = lordId;
    }

    public double getAltDiff() {
        return altDiff;
    }

    public void setAltDiff(double altDiff) {
        this.altDiff = altDiff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
