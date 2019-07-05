package com.beastbikes.android.modules.cycling.task.biz;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.beastbikes.android.Constants;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.modules.cycling.task.dto.Bazinga;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;

import org.json.JSONObject;

public class TaskManager extends AbstractBusinessObject {

    private final TaskServiceStub stub;
    Activity context;

    public TaskManager(Activity context) {

        super((BusinessContext) context.getApplicationContext());
        this.context = context;
        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        this.stub = factory.create(TaskServiceStub.class, RestfulAPI.BASE_URL,
                RestfulAPI.getParams(context));

    }


    /**
     * 获取分享信息
     *
     * @param activityId 活动ID
     * @param shareType  1: 活动详情分享; 2: 我的分享; 3:俱乐部活动分享; 4是车店分享
     * @return
     * @throws BusinessException
     */
    public JSONObject getTaskShareMessage(final String activityId,
                                          final int shareType,
                                          final String bikeshopId  ) throws BusinessException {
        try {
            return this.stub.getShareContent(shareType,activityId,bikeshopId);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 获取弹窗信息
     *
     * @throws BusinessException
     */
    public Bazinga bazinga() throws BusinessException {
        try {
            SharedPreferences defaultSp = context.getSharedPreferences(Constants.PREF_BAZINGA, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = defaultSp.edit();

            int count = defaultSp.getInt(Constants.PREF_BAZINGA_COUNT, 0);
            int counter = defaultSp.getInt(Constants.PREF_BAZINGA_COUNTER, 0);
            int bid = defaultSp.getInt(Constants.PREF_BAZINGA_BID, 0);
            String linkTo = defaultSp.getString(Constants.PREF_BAZINGA_LINK_TO, "");
            String imageUrl = defaultSp.getString(Constants.PREF_BAZINGA_IMAGE_URL, "");
            JSONObject object = this.stub.bazinga();
            if (object.optInt("code") == 0) {

                JSONObject result = object.optJSONObject("result");
                int currentBid = result.optInt("bid");

                if (currentBid > 0 && currentBid != bid) {
                    count = result.optInt("mCount", 0);
                    linkTo = result.optString("linkTo");
                    imageUrl = result.optString("imageUrl");
                    editor.putInt(Constants.PREF_BAZINGA_COUNT, count);
                    editor.putString(Constants.PREF_BAZINGA_IMAGE_URL, result.optString("imageUrl"));
                    editor.putString(Constants.PREF_BAZINGA_LINK_TO, result.optString("linkTo"));
                    editor.putInt(Constants.PREF_BAZINGA_COUNTER, 0);
                    editor.putInt(Constants.PREF_BAZINGA_BID, bid);

                    editor.commit();
                }
            }

            return new Bazinga(linkTo, imageUrl, count > counter, counter);
        } catch (Exception e) {
            throw new BusinessException(e);
        }

    }

}
