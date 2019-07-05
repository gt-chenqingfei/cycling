package com.beastbikes.android.locale.googlemaputils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.beastbikes.android.modules.cycling.route.dto.GooglePlaceAddressDTO;
import com.beastbikes.framework.android.schedule.AsyncTaskQueue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by caoxiao on 16/4/27.
 */
public class GooglePlaceAPIUtils implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<PlaceBuffer> {

    private boolean hasConnect;

    private GoogleApiClient mGoogleApiClient;
    private AsyncTaskQueue asyncTaskQueue;
    private RequestQueue requestQueue;
    private GooglePlaceAPICallBack googlePlaceAPICallBack;
    //    private int count;
    private List<GooglePlaceAddressDTO> googlePlaceAddressDTOList;

    private List<AutocompletePrediction> autocompletePredictionList = new ArrayList<>();

    private Logger logger = LoggerFactory.getLogger(GooglePlaceAPIUtils.class);

    public GooglePlaceAPIUtils(Context context, AsyncTaskQueue asyncTaskQueue, RequestQueue requestQueue, GooglePlaceAPICallBack googlePlaceAPICallBack) {
        this.asyncTaskQueue = asyncTaskQueue;
        this.requestQueue = requestQueue;
        this.googlePlaceAPICallBack = googlePlaceAPICallBack;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        hasConnect = true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (googlePlaceAPICallBack != null)
            googlePlaceAPICallBack.placeAPIonConnectionFailed();
    }

    public void getResultByGoogle(final String keyWord) {
        if (!hasConnect)
            return;
        if (asyncTaskQueue == null || TextUtils.isEmpty(keyWord))
            return;
        asyncTaskQueue.add(new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                googlePlaceAddressDTOList = new ArrayList<>();
//                count = 0;
                PendingResult<AutocompletePredictionBuffer> results =
                        Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, keyWord, null, null);
                // Wait for predictions, set the timeout.
                AutocompletePredictionBuffer autocompletePredictions = results
                        .await(60, TimeUnit.SECONDS);
                final com.google.android.gms.common.api.Status status = autocompletePredictions.getStatus();
                if (!status.isSuccess()) {
                    if (googlePlaceAPICallBack != null)
                        googlePlaceAPICallBack.getAutocompletePredictionFail(status);
                    logger.error("Error getting place predictions: " + status
                            .toString());
                    autocompletePredictions.release();
                    return null;
                }

//                logger.info("Query completed. Received " + autocompletePredictions.getCount()
//                        + " predictions.");
                autocompletePredictionList.clear();
                Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
                while (iterator.hasNext()) {
                    AutocompletePrediction prediction = iterator.next();
//                    logger.trace("prediction " + prediction.get
                    autocompletePredictionList.add(prediction);
                }
                for (int i = 0; i < autocompletePredictionList.size(); i++) {
                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                            .getPlaceById(mGoogleApiClient, autocompletePredictionList.get(i).getPlaceId());
                    placeResult.setResultCallback(GooglePlaceAPIUtils.this);
                }
                if (autocompletePredictionList.size() == 0 && googlePlaceAPICallBack != null) {
                    googlePlaceAPICallBack.getAutocompletePredictionFail(status);
                    return null;
                }
                autocompletePredictions.release();
                return null;
            }
        });
    }

    @Override
    public void onResult(@NonNull final PlaceBuffer places) {
        if (!places.getStatus().isSuccess()) {
            logger.error("Place query did not complete. Error: " + places.getStatus().toString());
            places.release();
            return;
        }
        final Place place = places.get(0);
        GoogleMapCnAPI googleMapCnAPI = new GoogleMapCnAPI();
        googleMapCnAPI.geoCode(requestQueue, place.getLatLng().latitude, place.getLatLng().longitude, new GoogleMapCnCallBack() {
            @Override
            public void onGetGeoCodeInfo(GoogleMapCnBean googleMapCnBean) {
                GooglePlaceAddressDTO googlePlaceAddressDTO = new GooglePlaceAddressDTO(place);
                googlePlaceAddressDTO.setCityName(googleMapCnBean.getCityName());
                getResult(googlePlaceAddressDTO);
                places.release();
            }

            @Override
            public void onGetGeoInfoError(VolleyError volleyError) {
                GooglePlaceAddressDTO googlePlaceAddressDTO = new GooglePlaceAddressDTO(place);
                getResult(googlePlaceAddressDTO);
                places.release();
            }
        });

    }

    private void getResult(GooglePlaceAddressDTO googlePlaceAddressDTO) {
        if (googlePlaceAddressDTO == null)
            return;
        googlePlaceAddressDTOList.add(googlePlaceAddressDTO);
        if (googlePlaceAddressDTOList.size() == autocompletePredictionList.size() && googlePlaceAddressDTOList.size() != 0) {
            if (googlePlaceAPICallBack != null)
                googlePlaceAPICallBack.getGooglePlaceAddressDTOList(googlePlaceAddressDTOList);
        }
    }
}
