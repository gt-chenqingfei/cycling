package com.beastbikes.android.modules.shop.biz;

import android.app.Activity;
import android.text.TextUtils;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.modules.shop.dto.BikeShopInfoDTO;
import com.beastbikes.android.modules.shop.dto.BikeShopListDTO;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 16/4/12.
 */
public class BikeShopManager extends AbstractBusinessObject implements Constants {
    private BikeShopStub bikeShopStub;
    private Activity activity;

    public BikeShopManager(Activity activity) {
        super((BusinessContext) activity.getApplicationContext());
        this.activity = activity;
        final RestfulAPIFactory factory = new RestfulAPIFactory(activity);
        this.bikeShopStub = factory.create(BikeShopStub.class,
                RestfulAPI.BASE_URL, RestfulAPI.getParams(activity));
    }

    public List<BikeShopListDTO> getBikeShopList(double longitude, double latitude, float range,
                                                 final String keyWord, double uLatitude,
                                                 double uLongitude, final String type) throws BusinessException {
        try {

            if ("mine".equals(type)) {
                longitude = 0;
                latitude = 0;
                range = 0;
                uLatitude = 0;
                uLongitude = 0;
            }

            final JSONObject obj = this.bikeShopStub.getBikeShopList
                    (longitude, latitude, range, keyWord, uLatitude, uLongitude, type);
            if (null == obj) {
                Toasts.showOnUiThread(activity, activity.getString(R.string.club_act_release_failure));
                return null;
            }
            if (obj.optInt("code") == 0) {
                JSONArray jsonArray = obj.optJSONArray("result");
                if (jsonArray == null || jsonArray.length() == 0)
                    return null;
                List<BikeShopListDTO> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(new BikeShopListDTO(jsonArray.optJSONObject(i)));
                }
                return list;
            }

            String message = obj.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BikeShopInfoDTO getBikeShopInfo(long shopId, float longitude, float latitude) throws BusinessException {
        try {
            final JSONObject obj = this.bikeShopStub.getBikeShopInfo(shopId, longitude, latitude);
            if (null == obj) {
                Toasts.showOnUiThread(activity, activity.getString(R.string.club_act_release_failure));
                return null;
            }
            if (obj.optInt("code") == 0) {
                BikeShopInfoDTO bikeShopInfoDTO = new BikeShopInfoDTO(obj.optJSONObject("result"));
                return bikeShopInfoDTO;
            }
            String message = obj.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteBikeShop(long shopId) throws BusinessException {
        try {
            final JSONObject obj = this.bikeShopStub.deleteBikeShop(shopId);
            if (null == obj) {
                Toasts.showOnUiThread(activity, activity.getString(R.string.club_act_release_failure));
                return false;
            }
            if (obj.optInt("code") == 0) {
                boolean ret = obj.optBoolean("result");
                return ret;
            }
            String message = obj.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
