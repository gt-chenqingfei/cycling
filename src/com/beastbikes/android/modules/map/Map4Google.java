package com.beastbikes.android.modules.map;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.googlemaputils.GoogleMapCnAPI;
import com.beastbikes.android.locale.googlemaputils.GoogleMapManager;
import com.beastbikes.android.modules.cycling.simplify.SimplifyUtil;
import com.beastbikes.android.utils.BitmapUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chenqingfei on 16/5/11.
 */
public class Map4Google extends MapBase<com.google.android.gms.maps.model.LatLng> implements
        GoogleMap.OnMarkerDragListener,LocationListener,OnMapReadyCallback,GoogleMap.OnCameraChangeListener{

    private com.google.android.gms.maps.MapFragment mMapFragment;
    private TextView noGooglePlayTV;
    private GoogleMap googleMap;
    private FrameLayout googleMapViewFL;
    private ImageView emptyImage;
    private boolean isGoogleMapReady;
    private boolean isGoogleMapNeedGetSamples;
    private LocationManager lm;
    private Location currentLocation;
    private boolean isSetMyLocationConfigeration = false;
    public Map4Google(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.speedx_map_with_google, this);
    }

    @Override
    protected void onInitView() {
        mMapFragment = (com.google.android.gms.maps.MapFragment) context.getFragmentManager()
                .findFragmentById(R.id.mapview);
        noGooglePlayTV = (TextView) findViewById(R.id.noGooglePlayServiceTV);
        googleMapViewFL = (FrameLayout) findViewById(R.id.googleMap_View_FL);
        emptyImage = (ImageView) findViewById(R.id.transparent_image);

    }

    @Override
    protected List<Point> getScreenPoints(List<com.google.android.gms.maps.model.LatLng> points) {
        if (points == null || googleMap.getProjection() == null)
            return null;

        List<Point> screenPoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            screenPoints.add(googleMap.getProjection().toScreenLocation(points.get(i)));
        }
        return screenPoints;
    }

    @Override
    protected String getElevations(List<com.google.android.gms.maps.model.LatLng> points) {
        if (points == null)
            return null;

        final StringBuilder latLngSb = new StringBuilder();
        Iterator<com.google.android.gms.maps.model.LatLng> it = points.iterator();
        while (it.hasNext()) {
            com.google.android.gms.maps.model.LatLng latlng = it.next();
            latLngSb.append(latlng.latitude).append(",")
                    .append(latlng.longitude);
            if (it.hasNext()) {
                latLngSb.append('|');
            }
        }

        return latLngSb.toString();
    }

    @Override
    public void requestLocation() {
        if(lm == null){
            this.lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            this.lm.sendExtraCommand("gps", "force_xtra_injection", null);
            this.lm.sendExtraCommand("gps", "force_time_injection", null);

            this.lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            this.lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        }
    }

    @Override
    public void setMyLocationConfigeration() {
        // 在地图上添加起点Marker，并显示
        if(!isSetMyLocationConfigeration) {
            if (this.currentLocation != null) {
                LatLng origin = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                googleMap.addMarker(new MarkerOptions()
                        .position(origin)
                        .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.route_map_make_location)));
            }
            isSetMyLocationConfigeration = true;
        }
    }

    @Override
    public void zoomTo(float level) {
        if(this.googleMap != null) {
            final float min = this.googleMap.getMinZoomLevel();
            level = Math.max(this.zoomLevel - 1, min);
            final CameraUpdate u = CameraUpdateFactory.zoomTo(level);
            this.googleMap.animateCamera(u);
            this.zoomLevel = level;
        }
    }


    @Override
    public void init(final Activity context, final MapListener mapListener,
                     final boolean isPrivate, ScrollView s) {
        doInit(context, mapListener, isPrivate, s);
        FragmentManager fManager = context.getFragmentManager();
        DisplayMetrics dm = getDm();
        if (!GoogleMapManager.isDeviceSupportGooglePlayService(context)) {
            this.noGooglePlayTV.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams noGooglePlayTVLP = this.noGooglePlayTV.getLayoutParams();
            noGooglePlayTVLP.height = dm.widthPixels;
            noGooglePlayTVLP.width = dm.widthPixels;
            this.noGooglePlayTV.setLayoutParams(noGooglePlayTVLP);
            if (this.mMapFragment != null) {
                fManager.beginTransaction().remove(this.mMapFragment);
            }
            return;
        }

        this.mMapFragment = (com.google.android.gms.maps.MapFragment) fManager
                .findFragmentById(R.id.mapview);
        this.mMapFragment.getMapAsync(this);

    }

    @Override
    public void drawStartingPoint(com.google.android.gms.maps.model.LatLng start,
                                  com.google.android.gms.maps.model.LatLng end) {
        if (null == start || end == null || this.googleMap == null)
            return;

        this.googleMap.clear();

        this.googleMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions()
                .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_activity_detail_start)).position(start));
        this.googleMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions()
                .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_activity_finish_end)).position(end));

    }

    @Override
    public void drawLine(List<LatLng> points) {
        if (points == null || points.size() < 1 || null == googleMap)
            return;

        int color = Color.parseColor("#ff102d");
        final int size = points.size();
        if (size <= 5) {
            final com.google.android.gms.maps.model.PolylineOptions polylineOptions
                    = new com.google.android.gms.maps.model.PolylineOptions().width(8)
                    .color(color).addAll(points).visible(true).zIndex(50);
            googleMap.addPolyline(polylineOptions);
        } else {
            List<Integer> colors = BitmapUtil.getColors(points.size());
            for (int i = 5; i < points.size(); i++) {
                if (i < colors.size()) {
                    color = colors.get(i);
                }

                List<LatLng> lls = new ArrayList<>();
                for (int j = 0; j <= 5; j++) {
                    lls.add(points.get(i - j));
                }
                final com.google.android.gms.maps.model.PolylineOptions polylineOptions
                        = new com.google.android.gms.maps.model.PolylineOptions().width(8)
                        .color(color).addAll(lls).visible(true).zIndex(50);
                googleMap.addPolyline(polylineOptions);
            }
        }
    }

    @Override
    public void zoomToSpan(List<com.google.android.gms.maps.model.LatLng> points) {
        if (null == points || points.isEmpty() || googleMap == null)
            return;

        final com.google.android.gms.maps.model.LatLngBounds.Builder builder
                = new com.google.android.gms.maps.model.LatLngBounds.Builder();

        for (final com.google.android.gms.maps.model.LatLng point : points) {
            builder.include(point);
        }
        try {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        } catch (Exception e) {

        }
    }

    @Override
    public void snapshot(final SnapshotReadyListener listener) {
        if (listener == null || null == this.googleMap)
            return;
        this.googleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {

            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                listener.onSnapshotReady(bitmap);
            }

        });
    }

    @Override
    public void setMapFullScreen() {
        super.setMapFullScreen();
        DisplayMetrics dm = getDm();
        ViewGroup.LayoutParams mapViewLP = googleMapViewFL.getLayoutParams();
        mapViewLP.height = dm.heightPixels;
        mapViewLP.width = dm.widthPixels;
        this.googleMapViewFL.setLayoutParams(mapViewLP);

    }

    @Override
    public void setMapWarpScreen() {
        super.setMapWarpScreen();
        DisplayMetrics dm = getDm();
        ViewGroup.LayoutParams mapViewLP = googleMapViewFL.getLayoutParams();
        mapViewLP.height = dm.widthPixels;
        mapViewLP.width = dm.widthPixels;
        this.googleMapViewFL.setLayoutParams(mapViewLP);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
        setMyLocationConfigeration();
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();
        final LatLng latlng = new LatLng(latitude, longitude);

        lm.removeUpdates(this);
        List<LatLng> latLngs = new ArrayList<>();
        latLngs.add(latlng);
        zoomToSpan(latLngs);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        if (onMapStatusChangeListener != null) {
            onMapStatusChangeListener.onMapStatusChangeStart(
                    marker.getPosition().latitude,  marker.getPosition().longitude);
        }
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if (mapListener != null) {
            mapListener.onMapDrawFrame();
        }
        if (onMapStatusChangeListener != null) {
            onMapStatusChangeListener.onMapStatusChangeFinish(
                    marker.getPosition().latitude,  marker.getPosition().longitude);
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        isGoogleMapReady = true;

        if (mapListener != null) {
            mapListener.onMapLoaded();
        }
        //googleMapGetSamples();
        googleMap.getUiSettings().setZoomControlsEnabled(false);

        emptyImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(scrollView != null) {
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            scrollView.requestDisallowInterceptTouchEvent(true);
                            // Disable touch on transparent view
                            return false;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            scrollView.requestDisallowInterceptTouchEvent(false);
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            scrollView.requestDisallowInterceptTouchEvent(true);
                            return false;

                        default:
                            return true;
                    }
                }
                return false;
            }

        });

        // 添加全局阴影
        List<com.google.android.gms.maps.model.LatLng> lls = new ArrayList<com.google.android.gms.maps.model.LatLng>();
        lls.add(new com.google.android.gms.maps.model.LatLng(-9000, -18000));
        lls.add(new com.google.android.gms.maps.model.LatLng(-9000, -18000));
        lls.add(new com.google.android.gms.maps.model.LatLng(9000, 18000));
        lls.add(new com.google.android.gms.maps.model.LatLng(9000, 18000));

        com.google.android.gms.maps.model.PolygonOptions polygonOptions =
                new com.google.android.gms.maps.model.PolygonOptions()
                        .addAll(lls)
                        .strokeColor(0x8f000000).fillColor(0x6f000000);

        googleMap.addPolygon(polygonOptions);
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnCameraChangeListener(this);

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if(cameraPosition != null){
            if (mapListener != null) {
                mapListener.onMapDrawFrame();
            }
            if (onMapStatusChangeListener != null) {
                onMapStatusChangeListener.onMapStatusChangeFinish(
                        cameraPosition.target.latitude,  cameraPosition.target.longitude);
            }
        }
    }
}
