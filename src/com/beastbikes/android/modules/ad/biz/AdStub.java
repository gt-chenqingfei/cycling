package com.beastbikes.android.modules.ad.biz;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

/**
 * create by qingfei.chen
 */
public interface AdStub extends ServiceStub {

    /**
     * @return JsonObject
     */
    @HttpPost("/adCycle")
    JSONObject adCycle();
}
