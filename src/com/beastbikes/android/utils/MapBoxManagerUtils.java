package com.beastbikes.android.utils;

import android.content.Context;
import android.util.Log;

import com.beastbikes.android.BeastBikes;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by caoxiao on 16/4/28.
 */
public class MapBoxManagerUtils {
    private MapView mapView;

    public MapView init(Context context) {
        MapboxMapOptions mapboxMapOptions = new MapboxMapOptions();
        mapboxMapOptions.accessToken(BeastBikes.getMapBoxAccessToken());
        mapboxMapOptions.attributionEnabled(false);
        mapboxMapOptions.logoEnabled(false);
        mapboxMapOptions.zoomControlsEnabled(false);
        mapboxMapOptions.rotateGesturesEnabled(false);
        mapboxMapOptions.zoomGesturesEnabled(true);
        mapboxMapOptions.compassEnabled(false);
        mapboxMapOptions.scrollGesturesEnabled(true);
        mapboxMapOptions.styleUrl(Style.DARK);
        mapView = new MapView(context, mapboxMapOptions);
        return mapView;
    }

    public double getZoomLevel(MapView mapView) {
        if (mapView == null)
            return 11;
        try {
            Class clazz = mapView.getClass();
            Method getZoom = clazz.getDeclaredMethod("getZoom");
            getZoom.setAccessible(true);
            double zoom = (double) getZoom.invoke(mapView);
//                    Toast.makeText(MainActivity.this, zoom + "", Toast.LENGTH_SHORT).show();
//            Log.e("zoom", "" + zoom);
            return zoom;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return 11;
    }

    public void zoomByLatLngs(MapboxMap mapboxMap, List<LatLng> latLngs) {
        if (mapboxMap == null)
            return;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < latLngs.size(); i++) {
            builder.include(latLngs.get(i));
        }
        LatLngBounds latLngBounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 20);
        mapboxMap.animateCamera(cameraUpdate, 1000);
    }

}
