package com.beastbikes.android.modules.strava.biz;


import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;
import com.beastbikes.android.sphere.restful.annotation.QueryParameter;

import org.json.JSONObject;

public interface StravaServiceStub extends ServiceStub {

    /**
     * 获取token
     *
     * @param clientId
     * @param clientSecret
     * @param type
     * @return
     */
    @HttpPost("/oauth/token")
    public JSONObject token(@QueryParameter("client_id") final int clientId,
                            @QueryParameter("client_secret") final String clientSecret,
                            @QueryParameter("code") final String type
    );

    /**
     * 取消认证
     *
     * @param accessToken
     * @return
     */
    @HttpPost("/oauth/deauthorize")
    public JSONObject deauthorize(@QueryParameter("access_token") final String accessToken

    );


}
