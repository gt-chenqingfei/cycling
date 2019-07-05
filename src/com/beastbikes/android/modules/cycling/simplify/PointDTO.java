package com.beastbikes.android.modules.cycling.simplify;

import com.baidu.mapapi.model.LatLng;

public class PointDTO implements Point {

    private double latitude;
    private double longitude;

    public PointDTO(LatLng latlng) {
        this.latitude = latlng.latitude;
        this.longitude = latlng.longitude;
    }

    @Override
    public double getX() {
        return this.longitude;
    }

    @Override
    public double getY() {
        return this.latitude;
    }

    @Override
    public String toString() {
        return "{" + "x=" + latitude + ", y=" + longitude + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointDTO myPoint = (PointDTO) o;

        if (Double.compare(myPoint.latitude, latitude) != 0) return false;
        if (Double.compare(myPoint.longitude, longitude) != 0) return false;

        return true;
    }

}
