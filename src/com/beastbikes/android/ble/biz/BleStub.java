package com.beastbikes.android.ble.biz;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpGet;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

/**
 * create by qingfei.chen
 */
public interface BleStub extends ServiceStub {

    /**
     * @return JsonObject
     */
    @HttpPost("/checkCControlActive")
    JSONObject checkCControlActive(@BodyParameter("ccontrolNo") final String ccontrolNo);

    /**
     * @return JsonObject
     */
    @HttpPost("/activeCControl")
    JSONObject activeCControl(@BodyParameter("ccontrolNo") final String ccontrolNo);

    /**
     * @return JsonObject
     */
    @HttpPost("/syncDeviceTotalDistance")
    JSONObject syncDeviceTotalDistance(@BodyParameter("ccontrolNo") final String ccontrolNo,
                                       @BodyParameter("totalDistance") final float totalDistance);

    /**
     * save device information to server
     * @param centralId      device central id
     * @param centralName    device central name
     * @param hardwareType   device hardware type: 0-Central Control, 1-Whole Bike
     * @param brandType      device name
     * @return {@link JSONObject}
     */
    @HttpPost("/saveDeviceByCentral")
    JSONObject saveDeviceToServer(@BodyParameter("central_id") final String centralId, @BodyParameter("central_name") final String centralName, @BodyParameter("hardware") final int hardwareType, @BodyParameter("bike_type") final int brandType);

    /**
     * get {@link com.beastbikes.android.ble.dao.entity.BleDevice} from server
     * @return
     */
    @HttpPost("/getDeviceByUserId")
    JSONObject getBleDevices();
}
