package com.beastbikes.android.modules.cycling.route.dto;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.search.core.PoiInfo;

import java.io.Serializable;

public class PoiInfoDTO implements Serializable {

    private static final long serialVersionUID = -5914132297437168129L;

    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String city;
    private String area;
    private String province;
    private boolean isEdit;
    private int index;

    public PoiInfoDTO() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public PoiInfoDTO(PoiInfo info) {
        this.name = info.name;
        this.address = info.address;
        this.latitude = info.location.latitude;
        this.longitude = info.location.longitude;
        this.city = info.city;
        this.isEdit = false;
    }

    public PoiInfoDTO(GooglePlaceAddressDTO googlePlaceAddressDTO){
        this.name = googlePlaceAddressDTO.getName();
        this.address = googlePlaceAddressDTO.getAddress();
        this.latitude = googlePlaceAddressDTO.getLatitude();
        this.longitude = googlePlaceAddressDTO.getLongitude();
        this.city = googlePlaceAddressDTO.getCityName();
        this.isEdit = false;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public PoiInfoDTO(BDLocation location) {
        this.name = location.getDistrict();
        this.address = location.getAddrStr();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.city = location.getCity();
    }

    public PoiInfoDTO(RouteNodeDTO rnd) {
        this.name = rnd.getName();
        this.latitude = rnd.getLatitude();
        this.longitude = rnd.getLongitude();
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Override
    public String toString() {
        return "PoiInfoDTO [name=" + name + ", address=" + address
                + ", latitude=" + latitude + ", longitude=" + longitude
                + ", city=" + city + "]";
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
