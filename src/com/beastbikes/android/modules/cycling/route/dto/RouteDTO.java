package com.beastbikes.android.modules.cycling.route.dto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RouteDTO implements Serializable {

    private static final long serialVersionUID = -2149866648468981051L;

    private String id;

    private String name;

    private String englishName;

    private String desc;

    private String areaCode;

    private double difficultyCoefficient;

    private double trafficCoefficient;

    private double viewCoefficient;

    private double totalDistance;

    private double distanceToMe;

    private String coverUrl;

    private String mapUrl;

    private int numberOfFollowers;

    private double originAltitude;

    private double originLatitude;

    private double originLongitude;

    private double destinationAltitude;

    private double destinationLatitude;

    private double destinationLongitude;

    private String userId;

    /**
     * 是否已关注
     */
    private boolean isFollowed;

    private boolean isUse;

    private List<RouteNodeDTO> nodes;

    public RouteDTO() {
    }

    public RouteDTO(JSONObject json) {
        this.name = json.optString("name");
        this.desc = json.optString("desc");
        this.areaCode = json.optString("cityId");
        this.totalDistance = json.optDouble("distance");
        this.numberOfFollowers = json.optInt("followers");
        this.difficultyCoefficient = json.optDouble("difficultyCoefficient");
        this.trafficCoefficient = json.optDouble("trafficCoefficient");
        this.viewCoefficient = json.optDouble("viewCoefficient");
        this.id = json.optString("id");
        this.englishName = json.optString("name_en");

        this.isFollowed = json.optBoolean("hasFollowed");

        this.coverUrl = json.optString("cover");

        this.mapUrl = json.optString("map");

        if (json.has("routeNodes")) {
            this.nodes = new ArrayList<>();
            JSONArray array = json.optJSONArray("routeNodes");
            for (int i = 0; i < array.length(); i++) {
                this.nodes.add(new RouteNodeDTO(true, array.optJSONObject(i)));
            }
        }

        try {
            final String[] org = json.getString("origin").split("\\s*,\\s*");
            if (org.length > 0) {
                this.originLatitude = Double.parseDouble(org[1]);
            }
            if (org.length > 1) {
                this.originLongitude = Double.parseDouble(org[0]);
            }
            if (org.length > 2) {
                this.originAltitude = Double.parseDouble(org[2]);
            }
        } catch (JSONException e) {
        }

        try {
            final String[] dest = json.getString("destination").split(
                    "\\s*,\\s*");
            if (dest.length > 0) {
                this.destinationLatitude = Double.parseDouble(dest[1]);
            }
            if (dest.length > 1) {
                this.destinationLongitude = Double.parseDouble(dest[0]);
            }
            if (dest.length > 2) {
                this.destinationAltitude = Double.parseDouble(dest[2]);
            }
        } catch (JSONException e) {
        }
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean hasFollowed) {
        this.isFollowed = hasFollowed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAreaCode() {
        return this.areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getDescription() {
        return this.desc;
    }

    public void setDescription(String desc) {
        this.desc = desc;
    }

    public double getDifficultyCoefficient() {
        return this.difficultyCoefficient;
    }

    public void setDifficultyCoefficient(double difficultyCoefficient) {
        this.difficultyCoefficient = difficultyCoefficient;
    }

    public double getTrafficCoefficient() {
        return this.trafficCoefficient;
    }

    public void setTrafficCoefficient(double trafficCoefficient) {
        this.trafficCoefficient = trafficCoefficient;
    }

    public double getViewCoefficient() {
        return this.viewCoefficient;
    }

    public void setViewCoefficient(double viewCoefficient) {
        this.viewCoefficient = viewCoefficient;
    }

    public double getTotalDistance() {
        return this.totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getDistanceToMe() {
        return this.distanceToMe;
    }

    public void setDistanceToMe(double distanceToMe) {
        this.distanceToMe = distanceToMe;
    }

    public String getCoverURL() {
        return this.coverUrl;
    }

    public void setCoverURL(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getMapURL() {
        return this.mapUrl;
    }

    public void setMapURL(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public int getNumberOfFollowers() {
        return this.numberOfFollowers;
    }

    public void setNumberOfFollowers(int numberOfFollowers) {
        this.numberOfFollowers = numberOfFollowers;
    }

    public double getOriginAltitude() {
        return this.originAltitude;
    }

    public void setOriginAltitude(double originAltitude) {
        this.originAltitude = originAltitude;
    }

    public double getOriginLatitude() {
        return this.originLatitude;
    }

    public void setOriginLatitude(double originLatitude) {
        this.originLatitude = originLatitude;
    }

    public double getOriginLongitude() {
        return this.originLongitude;
    }

    public void setOriginLongitude(double originLongitude) {
        this.originLongitude = originLongitude;
    }

    public double getDestinationAltitude() {
        return this.destinationAltitude;
    }

    public void setDestinationAltitude(double destinationAltitude) {
        this.destinationAltitude = destinationAltitude;
    }

    public double getDestinationLatitude() {
        return this.destinationLatitude;
    }

    public void setDestinationLatitude(double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public double getDestinationLongitude() {
        return this.destinationLongitude;
    }

    public void setDestinationLongitude(double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public boolean isUse() {
        return isUse;
    }

    public void setUse(boolean isUse) {
        this.isUse = isUse;
    }

    public List<RouteNodeDTO> getNodes() {
        return nodes;
    }

    public void setNodes(List<RouteNodeDTO> nodes) {
        this.nodes = nodes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "RouteDTO [id=" + id + ", name=" + name + ", englishName="
                + englishName + ", desc=" + desc + ", areaCode=" + areaCode
                + ", difficultyCoefficient=" + difficultyCoefficient
                + ", trafficCoefficient=" + trafficCoefficient
                + ", viewCoefficient=" + viewCoefficient + ", totalDistance="
                + totalDistance + ", distanceToMe=" + distanceToMe
                + ", coverUrl=" + coverUrl + ", mapUrl=" + mapUrl
                + ", numberOfFollowers=" + numberOfFollowers
                + ", originAltitude=" + originAltitude + ", originLatitude="
                + originLatitude + ", originLongitude=" + originLongitude
                + ", destinationAltitude=" + destinationAltitude
                + ", destinationLatitude=" + destinationLatitude
                + ", destinationLongitude=" + destinationLongitude
                + ", isFollowed=" + isFollowed + "]";
    }

}
