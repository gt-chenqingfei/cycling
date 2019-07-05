package com.beastbikes.android.modules.pay;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;
import com.beastbikes.framework.business.BusinessException;

import org.json.JSONObject;

/**
 * create by qingfei.chen
 */
public interface PayStub extends ServiceStub {

    /**
     * @return JsonObject
     */
    @HttpPost("/getPayCharge")
    JSONObject getPayCharge(@BodyParameter("channel") final String payChannel,
                            @BodyParameter("amount") final String amount) throws BusinessException;

}
