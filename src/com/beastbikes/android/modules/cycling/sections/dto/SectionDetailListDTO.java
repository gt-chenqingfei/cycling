package com.beastbikes.android.modules.cycling.sections.dto;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by caoxiao on 16/4/10.
 */
public class SectionDetailListDTO {
    private boolean hasFavor;
    private double range;
    private long segmentId;
    private String lordNick;
    private JSONArray origin;
    private double originLongitude;
    private double originLatitude;
    private int difficult;
    private String lordAvatar;
    private JSONArray destination;
    private double destinationLongitude;
    private double destinationLatitude;
    private String lordId;
    private String polyline;
    private double legLength;
    private int favorNum;
    private int slope;
    private String name;
    private double altDiff;
    private int challengeNum;

    public SectionDetailListDTO(JSONObject jsonObject) {
        this.hasFavor = jsonObject.optBoolean("hasFavor");
        this.range = jsonObject.optDouble("range");
        this.segmentId = jsonObject.optLong("segmentId");
        this.lordNick = jsonObject.optString("lordNick");
        this.origin = jsonObject.optJSONArray("origin");
        this.originLongitude = origin.optDouble(0);
        this.originLatitude = origin.optDouble(1);
        this.difficult = jsonObject.optInt("difficult");
        this.lordAvatar = jsonObject.optString("lordAvatar");
        this.destination = jsonObject.optJSONArray("origin");
        this.destinationLongitude = destination.optDouble(0);
        this.destinationLatitude = destination.optDouble(1);
        this.lordId = jsonObject.optString("lordId");
        this.polyline = jsonObject.optString("polyline");
        this.legLength = jsonObject.optDouble("legLength");
        this.favorNum = jsonObject.optInt("favorNum");
        this.slope = jsonObject.optInt("slope");
        this.name = jsonObject.optString("name");
        this.altDiff = jsonObject.optDouble("altDiff");
        this.challengeNum = jsonObject.optInt("challengeNum");
    }

    public boolean isHasFavor() {
        return hasFavor;
    }

    public void setHasFavor(boolean hasFavor) {
        this.hasFavor = hasFavor;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public long getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(long segmentId) {
        this.segmentId = segmentId;
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

    public String getLordId() {
        return lordId;
    }

    public void setLordId(String lordId) {
        this.lordId = lordId;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public double getLegLength() {
        return legLength;
    }

    public void setLegLength(double legLength) {
        this.legLength = legLength;
    }

    public int getFavorNum() {
        return favorNum;
    }

    public void setFavorNum(int favorNum) {
        this.favorNum = favorNum;
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

    public int getChallengeNum() {
        return challengeNum;
    }

    public void setChallengeNum(int challengeNum) {
        this.challengeNum = challengeNum;
    }
}
