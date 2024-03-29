package com.beastbikes.android.locale.googlemaputils;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.beastbikes.android.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by caoxiao on 15/11/5.
 */
public class FetchAddressIntentService extends IntentService {
    private String TAG = "FetchAddressIntentService";
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory
            .getLogger(FetchAddressIntentService.class);
    protected ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super("GoogleFetchAddressIntentService");
        logger.info("FetchAddressIntentService is constructed");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);
        mReceiver = intent.getParcelableExtra(com.beastbikes.android.locale.googlemaputils.Constants.RECEIVER);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
            logger.error(errorMessage);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
            logger.error(errorMessage);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            StringBuilder stringBuilder = new StringBuilder();
//            Log.e("getAdminArea",address.getAdminArea());
            Log.e("getCountryCode", address.getCountryCode());
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                stringBuilder.append(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));

            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    stringBuilder.toString() + ";" + address.getCountryCode());
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}
