package com.beastbikes.android.modules.cycling.activity.biz;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.beastbikes.android.Constants;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.cycling.activity.dto.GoalConfigDTO;
import com.beastbikes.android.modules.cycling.activity.dto.MyGoalInfoDTO;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessObject;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by icedan on 16/1/7.
 */
public class CyclingManager extends AbstractBusinessObject implements
        BusinessObject {

    private final Activity activity;
    private final CyclingServiceStub stub;
    private SharedPreferences userSp;

    public CyclingManager(Activity activity) {
        super((BusinessContext) activity.getApplicationContext());
        this.activity = activity;

        final RestfulAPIFactory factory = new RestfulAPIFactory(activity);
        this.stub = factory.create(CyclingServiceStub.class, RestfulAPI.BASE_URL,
                RestfulAPI.getParams(activity));
        AVUser user = AVUser.getCurrentUser();
        if (null != user) {
            this.userSp = activity.getSharedPreferences(user.getObjectId(), 0);
        }
    }

    /**
     * 获取目标配置列表
     *
     * @return List
     */
    public List<GoalConfigDTO> getGoalConfigs() {
        try {
            JSONObject result = this.stub.getGoalConfig();
            if (null == result) {
                return null;
            }

            if (result.optInt("code") == 0) {
                JSONArray array = result.optJSONArray("result");
                if (null == array || array.length() <= 0) {
                    return null;
                }

                List<GoalConfigDTO> list = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    GoalConfigDTO dto = new GoalConfigDTO(array.optJSONObject(i));
                    if (null != userSp) {
                        String key = this.userSp.getString(Constants.PREF_CYCLING_TARGET_SETTING_KEY,
                                Constants.SPEEDX_MONTHLY_SVG_DISTANCE);
                        if (key.equals(dto.getKey())) {
                            dto.setChecked(true);
                        }
                    }
                    list.add(dto);
                }

                return list;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message) && null != activity) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取我的目标信息
     *
     * @return MyGoalInfoDTO
     */
    public MyGoalInfoDTO getMyGoalInfo() {
        try {
            JSONObject result = this.stub.getMyGoalInfo();
            if (null == result) {
                return null;
            }

            if (result.optInt("code") == 0) {
                JSONObject obj = result.optJSONObject("result");
                if (null != userSp) {
                    userSp.edit().putString(Constants.PREF_CYCLING_MY_GOAL_KEY,
                            obj.toString()).commit();
                }
                return new MyGoalInfoDTO(obj);
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message) && null != activity) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置目标
     *
     * @param distance 目标里程
     * @return
     */
    public boolean setMyGoal(double distance) {
        try {
            JSONObject result = this.stub.setMyGoal(distance);
            if (null == result) {
                return false;
            }

            if (result.optInt("code") == 0) {
                return result.optBoolean("result");
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message) && null != activity) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
