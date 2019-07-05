package com.beastbikes.android.modules.cycling.simplify;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by caoxiao on 15/12/19.
 */
public class PointDTO4MB implements Point {

    private double latitude;
    private double longitude;

    public PointDTO4MB(LatLng latlng) {
        this.latitude = latlng.getLatitude();
        this.longitude = latlng.getLongitude();
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

        PointDTO4MB myPoint = (PointDTO4MB) o;

        if (Double.compare(myPoint.latitude, latitude) != 0) return false;
        if (Double.compare(myPoint.longitude, longitude) != 0) return false;

        return true;
    }
}
