package com.beastbikes.android.locale.googlemaputils;

import com.beastbikes.android.modules.cycling.route.dto.GooglePlaceAddressDTO;

import java.util.List;

/**
 * Created by caoxiao on 16/4/27.
 */
public interface GooglePlaceAPICallBack {
    void placeAPIonConnectionFailed();

    void getAutocompletePredictionFail(com.google.android.gms.common.api.Status status);

    void getGooglePlaceAddressDTOList(List<GooglePlaceAddressDTO> googlePlaceAddressList);

}
