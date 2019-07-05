package com.beastbikes.android.modules.cycling.sections.dto;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by caoxiao on 16/4/5.
 */
public class SectionListDTO {
    private String title;
    private String polyline;
    private double range;
    private double legLength;
    private long segmentId;
    private int slope;
    private String name;
    private double altDiff;
    private String lordNick;
    private JSONArray origin;
    private double originLongitude;
    private double originLatitude;
    private int difficult;
    private String lordAvatar;
    private JSONArray destination;
    private double destinationLongitude;
    private double destinationLatitude;

    public SectionListDTO(JSONObject jsonObject) {
        this.title = jsonObject.optString("title");
        this.polyline = jsonObject.optString("polyline");
        this.range = jsonObject.optDouble("range");
        this.legLength = jsonObject.optDouble("legLength");
        this.segmentId = jsonObject.optLong("segmentId");
        this.slope = jsonObject.optInt("slope");
        this.name = jsonObject.optString("name");
        this.altDiff = jsonObject.optDouble("altDiff");
        this.lordNick = jsonObject.optString("lordNick");
        this.origin = jsonObject.optJSONArray("origin");
        this.originLongitude = origin.optDouble(0);
        this.originLatitude = origin.optDouble(1);
        this.difficult = jsonObject.optInt("difficult");
        this.lordAvatar = jsonObject.optString("lordAvatar");
        this.destination = jsonObject.optJSONArray("origin");
        this.destinationLongitude = destination.optDouble(0);
        this.destinationLatitude = destination.optDouble(1);
    }

    public String getLordId() {
        return title;
    }

    public void setLordId(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getLegLength() {
        return legLength;
    }

    public void setLegLength(double legLength) {
        this.legLength = legLength;
    }

    public long getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(long segmentId) {
        this.segmentId = segmentId;
    }

    public int getSlope() {
        return slope;
    }

    public void setSlope(int slope) {
        this.slope = slope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAltDiff() {
        return altDiff;
    }

    public void setAltDiff(double altDiff) {
        this.altDiff = altDiff;
    }

    public String getLordNick() {
        return lordNick;
    }

    public void setLordNick(String lordNick) {
        this.lordNick = lordNick;
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

    public int getDifficult() {
        return difficult;
    }

    public void setDifficult(int difficult) {
        this.difficult = difficult;
    }

    public String getLordAvatar() {
        return lordAvatar;
    }

    public void setLordAvatar(String lordAvatar) {
        this.lordAvatar = lordAvatar;
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
}
