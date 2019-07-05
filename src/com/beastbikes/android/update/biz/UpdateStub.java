package com.beastbikes.android.update.biz;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

/**
 * create by qingfei.chen
 */
public interface UpdateStub extends ServiceStub {

    /**
     * @return JsonObject
     */
    @HttpPost("/checkLatestApkUpdate")
    JSONObject checkLatestApkUpdate();
}
