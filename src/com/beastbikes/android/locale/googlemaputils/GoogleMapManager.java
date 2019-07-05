package com.beastbikes.android.locale.googlemaputils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;

/**
 * Created by caoxiao on 15/11/4.
 */
public class GoogleMapManager {
    private GoogleApiClient mGoogleApiClient;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
            .getLogger(GoogleMapManager.class);
    private ManagerConnectionCallbacks connectionCallbacks = new ManagerConnectionCallbacks();
    private ManagerConnectionFailedListener connectionFailedListener = new ManagerConnectionFailedListener();
    //    private GoogleMapConnectionCallBack googleMapConnectionCallBack;
    private WeakReference<GoogleMapConnectionCallBack> googleMapConnectionCallBack;
    //    private GoogleMapGeoCallBack googleMapGeoCallBack;
    private WeakReference<GoogleMapGeoCallBack> googleMapGeoCallBack;

    /**
     * 初始化
     *
     * @param context
     */
    public synchronized void buildGoogleApiClient(Context context) {
        this.buildGoogleApiClient(context, null);
    }

    public synchronized void buildGoogleApiClient(Context context, GoogleMapConnectionCallBack googleMapConnectionCallBack) {
//        logger.info("GoogleApiClient build");
        this.googleMapConnectionCallBack = new WeakReference<>(googleMapConnectionCallBack);
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .addApi(LocationServices.API)
                .build();
    }

    public static boolean isDeviceSupportGooglePlayService(Context context) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
//        logger.error("isDeviceSupportGooglePlayService:" + status);
        if (status == ConnectionResult.SERVICE_MISSING || status == ConnectionResult.SERVICE_INVALID || status == ConnectionResult.SERVICE_DISABLED) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 连接服务器以获取定位等信息
     */
    public synchronized void googleApiClientConnect() {
        if (mGoogleApiClient == null || mGoogleApiClient.isConnected())
            return;
        mGoogleApiClient.connect();
    }

    public synchronized void googleApiClientDisconnect() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())
            return;
        mGoogleApiClient.disconnect();
    }

    /**
     * 把坐标点转化为地址
     *
     * @param mLastLocation
     */
    public void locationToAddress(Context context, Location mLastLocation, GoogleMapGeoCallBack googleMapGeoCallBack, Handler handler) {
        this.googleMapGeoCallBack = new WeakReference<GoogleMapGeoCallBack>(googleMapGeoCallBack);
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(com.beastbikes.android.locale.googlemaputils.Constants.RECEIVER, new AddressResultReceiver(handler));
        intent.putExtra(com.beastbikes.android.locale.googlemaputils.Constants.LOCATION_DATA_EXTRA, mLastLocation);
        context.startService(intent);
    }

    /**
     * 地址查询返回处理
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (googleMapGeoCallBack.get() == null)
                return;
            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(com.beastbikes.android.locale.googlemaputils.Constants.RESULT_DATA_KEY);
            if (resultCode == com.beastbikes.android.locale.googlemaputils.Constants.SUCCESS_RESULT) {
                String[] result = mAddressOutput.split(";");
                String address = result[0];
                String countryCode = result[1];
                googleMapGeoCallBack.get().onReceiveResult(address, countryCode);
            }

        }
    }

    private class ManagerConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(Bundle bundle) {
//            logger.info("ManagerConnectionCallbacks onConnected");
//            Log.e("Google", "onConnected");
            if (googleMapConnectionCallBack.get() == null)
                return;
            googleMapConnectionCallBack.get().onSuccessed(LocationServices.FusedLocationApi.getLastLocation(//最后一次定位的坐标
                    mGoogleApiClient), bundle);
        }

        @Override
        public void onConnectionSuspended(int i) {
//            logger.info("ManagerConnectionCallbacks onConnectionSuspended");
//            Log.e("Google", "onConnectionSuspended");
            if (googleMapConnectionCallBack.get() == null)
                return;
            googleMapConnectionCallBack.get().onSuspended(i);
        }
    }

    private class ManagerConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
//            logger.info("ManagerConnectionFailedListener onConnectionFailed");
//            Log.e("Google", "onConnectionFailed");
            if (googleMapConnectionCallBack.get() == null)
                return;
            googleMapConnectionCallBack.get().onFail(connectionResult);
        }
    }

    public void setGoogleMapConnectionCallBack(GoogleMapConnectionCallBack googleMapConnectionCallBack) {
        this.googleMapConnectionCallBack = new WeakReference<GoogleMapConnectionCallBack>(googleMapConnectionCallBack);
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }
}
