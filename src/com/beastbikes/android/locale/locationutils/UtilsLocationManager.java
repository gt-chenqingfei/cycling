package com.beastbikes.android.locale.locationutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.beastbikes.android.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by caoxiao on 15/11/5.
 */
public class UtilsLocationManager implements Constants {

    private LocationManager locationManager;
    private MyLocationListener myLocationListener;
    private WeakReference<UtilsLocationCallBack> utilsLocationCallBack;
    private static final Logger logger = LoggerFactory
            .getLogger(UtilsLocationManager.class);
    private static UtilsLocationManager utilsLocationManager;
    private Context context;
    private Timer timer;

    private UtilsLocationManager() {

    }

    public synchronized static UtilsLocationManager getInstance() {
        if (utilsLocationManager == null) {
            utilsLocationManager = new UtilsLocationManager();
        }
        return utilsLocationManager;
    }

    public void getLocation(Context context, UtilsLocationCallBack utilsLocationCallBack) {
        getLocation(context, utilsLocationCallBack, 0);
    }

    public void getLocation(Context context, UtilsLocationCallBack locationCallBack, final long time) {
        try {
            this.context = new WeakReference<>(context).get();
            WeakReference<Context> weakContext = new WeakReference<>(context);
            this.utilsLocationCallBack = new WeakReference<>(locationCallBack);
            myLocationListener = new MyLocationListener();
            locationManager = (LocationManager) weakContext.get().getSystemService(context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 200, 0, myLocationListener);
            }

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0, myLocationListener);
            }
            if (time > 0) {
                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (myLocationListener != null) {
                            if (utilsLocationCallBack.get() != null) {
                                utilsLocationCallBack.get().onLocationFail();
                            }
                            locationManager.removeUpdates(myLocationListener);
                            logger.warn("locationManager timeout:" + time);
                        }
                    }
                };
                timer.schedule(timerTask, time);
            }
        } catch (Exception e) {
            logger.error("getLocation exception " + e.toString());
        }
    }

    public void checkChineseVersion(RequestQueue mQueue, Location location, UtilsQQMapCallBack utilsQQMapCallBack) {
        UtilsQQMap.checkChineseVersion(mQueue, location, utilsQQMapCallBack);
    }

    // 获取地址信息
    public void getAddressbyGeoPoint(Context context, Location location) {
        List<Address> result = null;
        // 先将Location转换为GeoPoint
        // GeoPoint gp=getGeoByLocation(location);
        ReverseGeocodingTask reverseGeocodingTask = new ReverseGeocodingTask(context);
        reverseGeocodingTask.execute(location);
    }

    private class ReverseGeocodingTask extends AsyncTask<Location, Void, List<Address>> {
        Context mContext;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected List<Address> doInBackground(Location... params) {
            List<Address> result = null;
            // 先将Location转换为GeoPoint
            // GeoPoint gp=getGeoByLocation(location);

            try {
                if (params[0] != null) {
                    // 获取Geocoder，通过Geocoder就可以拿到地址信息
                    Geocoder gc = new Geocoder(mContext, Locale.getDefault());
                    result = gc.getFromLocation(params[0].getLatitude(), params[0].getLongitude(), 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result != null && result.size() > 0) {
                return result;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
        }
    }


    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location == null) {
//                logger.info("Locationmanager location is null");
                return;
            }
            if (timer != null)
                timer.cancel();
            if (myLocationListener != null) {
                locationManager.removeUpdates(myLocationListener);
                locationManager = null;
                myLocationListener = null;
            }
            if (utilsLocationCallBack.get() != null) {
                utilsLocationCallBack.get().onLocationChanged(location);
//                logger.info("Locationmanager location is " + location.getLatitude() + ";" + location.getLongitude());
                //保存经纬度坐标
                if (context != null) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(UtilsLocationManager.getInstance().getClass().getName(), 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(BLE.PREF_LOCATION_LAT, location.getLatitude() + "");
                    editor.putString(BLE.PREF_LOCATION_LON, location.getLongitude() + "");
                    editor.apply();
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
//                    logger.info("Locationmanager provider " + provider + " is available");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
//                    logger.info("Locationmanager provider " + provider
//                            + " is out of service");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                    logger.info("Locationmanager provider " + provider
//                            + " is temporatily unavailable");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            logger.info("Locationmanager provider " + provider + " is enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            logger.info("Locationmanager provider " + provider + " is disabled");
        }
    }
}
