package com.beastbikes.android.modules.ad.biz;

import android.content.Context;
import android.text.TextUtils;

import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.modules.ad.dto.AdDto;
import com.beastbikes.android.modules.qiniu.*;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by chenqingfei on 16/7/18.
 */
public class AdManager extends AbstractBusinessObject {
    private AdStub adStub = null;

    /**
     * Create an instance with the specified {@link BusinessContext}
     *
     * @param context The business context
     */
    public AdManager(Context context) {
        super((BusinessContext) context.getApplicationContext());
        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        this.adStub = factory.create(AdStub.class, RestfulAPI.BASE_URL, RestfulAPI.getParams(context));
    }

    public AdDto adCycle() throws BusinessException {
        try {
            final JSONObject jsonObject = this.adStub.adCycle();
            if (null == jsonObject) {
                return null;
            }
            int code = jsonObject.optInt("code");
            if (code == 0) {
                JSONArray results = jsonObject.optJSONArray("result");
                if (results != null && results.length()>0) {
                    JSONObject result = results.getJSONObject(0);
                    if(result != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        AdDto d = mapper.readValue(result.toString(), AdDto.class);
                        return d;
                    }
                }
            }
        } catch (Exception e) {

        }
        return null;
    }
}
