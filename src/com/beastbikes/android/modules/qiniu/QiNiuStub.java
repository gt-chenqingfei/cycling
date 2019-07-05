package com.beastbikes.android.modules.qiniu;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

/**
 * Created by caoxiao on 16/5/10.
 */
public interface QiNiuStub extends ServiceStub {

    /**
     * @param  bucket
     * @return JsonObject
     */
    @HttpPost("/getQiniuToken")
    JSONObject getQiniuToken(@BodyParameter("bucket") final String bucket);
}
