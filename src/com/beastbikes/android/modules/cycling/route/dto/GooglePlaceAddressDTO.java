package com.beastbikes.android.modules.cycling.route.dto;

import com.google.android.gms.location.places.Place;

/**
 * Created by caoxiao on 16/4/27.
 */
public class GooglePlaceAddressDTO {
    private String id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String cityName;

    public GooglePlaceAddressDTO(Place place) {
        this.id = place.getId();
        this.name = place.getName().toString();
        this.address = place.getAddress().toString();
        this.latitude = place.getLatLng().latitude;
        this.longitude = place.getLatLng().longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
