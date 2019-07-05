package com.beastbikes.android.locale.googlemaputils;

/**
 * Created by caoxiao on 15/11/14.
 */
public class GoogleMapCnBean {
    private String address;
    private String formattedAddress;
    private String cityName;
    private String province;

    public GoogleMapCnBean(String address, String formattedAddress) {
        this.address = address;
        this.formattedAddress = formattedAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
