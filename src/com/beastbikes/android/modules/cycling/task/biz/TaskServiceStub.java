package com.beastbikes.android.modules.cycling.task.biz;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

/**
 * Created by icedan on 15/11/9.
 */
public interface TaskServiceStub extends ServiceStub {

    /**
     * 获取分享内容
     *
     * @param activityId 活动ID
     * @param type       分享类型
     * @return JSONObject
     */
    @HttpPost("/getShareContent")
    JSONObject getShareContent(@BodyParameter("type") final int type,
                               @BodyParameter("activityId") final String activityId,
                               @BodyParameter("bikeshopId") final String bikeshopId);

    /**
     * 活动弹窗
     *
     * @return JSONObject
     */
    @HttpPost("/bazinga")
    JSONObject bazinga();

}
