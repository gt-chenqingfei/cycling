package com.beastbikes.android.modules.message.biz;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

public interface SpeedyServiceStub extends ServiceStub {

    /**
     * 获取未读系统消息数
     *
     * @param lastDate
     */
    @HttpPost("/getBroadcastCount")
    JSONObject getBroadcastCount(@BodyParameter("lastDate") float lastDate);

    /**
     * 获取系统消息列表
     *
     * @return
     */
    @HttpPost("/getBroadcasts")
    JSONObject getBroadcasts();

}
