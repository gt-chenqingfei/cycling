package com.beastbikes.android.modules.cycling.grid.biz;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

/**
 * Created by icedan on 15/12/21.
 */
public interface GridServiceStub extends ServiceStub {

    @HttpPost("/getUserGridList")
    JSONObject getUserGridList(@BodyParameter("userId") final String userId);
}
