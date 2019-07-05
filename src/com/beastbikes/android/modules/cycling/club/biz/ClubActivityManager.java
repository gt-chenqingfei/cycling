package com.beastbikes.android.modules.cycling.club.biz;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.modules.cycling.club.dto.ClubActRegisterInfo;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityInfo;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityListDTO;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityMember;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityOriginator;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityUser;
import com.beastbikes.android.modules.cycling.club.dto.ClubActivityLikeRead;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyao on 2016/1/11.
 */
public class ClubActivityManager extends AbstractBusinessObject implements Constants {
    private ClubActivityStub clubReleaseStub;
    private Activity activity;

    public ClubActivityManager(Context context) {
        super((BusinessContext) context.getApplicationContext());
        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        this.clubReleaseStub = factory.create(ClubActivityStub.class,
                RestfulAPI.BASE_URL, RestfulAPI.getParams(context));
    }

    public ClubActivityManager(Activity context) {
        super((BusinessContext) context.getApplicationContext());
        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        this.activity = context;
        this.clubReleaseStub = factory.create(ClubActivityStub.class,
                RestfulAPI.BASE_URL, RestfulAPI.getParams(context));
    }

    /**
     * 发布俱乐部活动
     */
    public ClubActivityInfo createClubActivity(String title, String desc, String mobPlace, String mobPoint,
                                               String startDate, String endDate, String routeId, String routeName,
                                               String mobilephone, String applyEndDate,
                                               int maxMembers, int isClubPrivate, String decstiption, String cover) {
        try {
            final JSONObject obj = this.clubReleaseStub.createClubActivity(title, desc, mobPlace, mobPoint,
                    startDate, endDate, routeId, routeName, mobilephone, applyEndDate, maxMembers, isClubPrivate, decstiption, cover);
            if (obj == null) {
                Toasts.showOnUiThread(activity, activity.getString(R.string.club_act_release_failure));
                return null;
            }

            if (obj.optInt("code") == 0) {
                JSONObject result = obj.optJSONObject("result");
                return new ClubActivityInfo(result);
            }

            String message = obj.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 获取俱乐部活动列表
     */
    public List<ClubActivityListDTO> clubActivityList(String clubId, int page, int count) {
        final JSONObject obj = this.clubReleaseStub.clubActivityList(clubId, page, count);
        if (obj == null) {
            Toasts.showOnUiThread(activity, activity.getString(R.string.club_act_list_failure));
            return null;
        }
        if (obj.optInt("code") == 0) {
            JSONArray jsonArray = obj.optJSONArray("result");
            List<ClubActivityListDTO> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject result = jsonArray.optJSONObject(i);
                list.add(new ClubActivityListDTO(result));
            }
            return list;
        }
        String message = obj.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }
        return null;
    }


    /**
     * 获取俱乐部活动成员列表
     */
    public ClubActivityMember clubActivityMemberList(String activityId, int page, int count) {
        final JSONObject obj = this.clubReleaseStub.clubActivityMemberList(activityId, page, count);
        if (obj == null) {
            Toasts.showOnUiThread(activity, activity.
                    getString(R.string.club_act_memberlist_failure));
            return null;
        }
        if (obj.optInt("code") == 0) {
            ClubActivityMember clubActivityMember = new ClubActivityMember();
            JSONObject result = obj.optJSONObject("result");
            JSONObject originator = result.optJSONObject("originator");
            JSONArray members = result.optJSONArray("members");
            List<ClubActivityUser> clubActivityUsers = new ArrayList<ClubActivityUser>();
            for (int i = 0; i < members.length(); i++) {
                JSONObject info = members.optJSONObject(i);
                clubActivityUsers.add(new ClubActivityUser(info));
            }
            clubActivityMember.setActId(result.optString("actId"));
            clubActivityMember.setClubActivityOriginator(new ClubActivityOriginator(originator));
            clubActivityMember.setIsManager(result.optBoolean("isManager"));
            clubActivityMember.setClubActivityUsers(clubActivityUsers);
            clubActivityMember.setCount(result.optInt("count"));
            return clubActivityMember;
        }
        String message = obj.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }
        return null;
    }


    /**
     * 获取俱乐部活动详情
     */
    public ClubActivityInfo clubActivityInfo(String activityId) {
        final JSONObject obj = this.clubReleaseStub.clubActivityInfo(activityId);
        if (obj == null) {
            Toasts.showOnUiThread(activity, activity.
                    getString(R.string.club_act_info_failure));
            return null;
        }
        if (obj.optInt("code") == 0) {
            JSONObject result = obj.optJSONObject("result");
            return new ClubActivityInfo(result);
        }
        String message = obj.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }
        return null;
    }

    /**
     * 加入活动
     */
    public ClubActRegisterInfo clubActRegister(String activityId, String name, int gender, String mobilephone,
                                               String extra, String contact) {
        final JSONObject obj = this.clubReleaseStub.clubActRegister(activityId, name, gender, mobilephone,
                extra, contact);
        if (obj == null) {
            Toasts.showOnUiThread(activity, activity.
                    getString(R.string.club_act_register_failure));
            return null;
        }
        if (obj.optInt("code") == 0) {
            JSONObject result = obj.optJSONObject("result");
            return new ClubActRegisterInfo(result);
        }
        String message = obj.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }
        return null;
    }

    /**
     * 取消活动
     */
    public boolean cancelClubActivity(String activityId) {
        final JSONObject obj = this.clubReleaseStub.cancelClubActivity(activityId);
        if (obj == null) {
            Toasts.showOnUiThread(activity, activity.
                    getString(R.string.club_act_cancel_failure));
            return false;
        }
        String message = obj.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }
        if (obj.optInt("code") == 0) {
            boolean result = obj.optBoolean("result");
            return result;
        }
        return false;
    }

    /**
     * 获取俱乐部活动点赞评论信息
     */
    public ClubActivityLikeRead getClubActivityStatisticsByActId(String activityId) {
        final JSONObject obj = this.clubReleaseStub.getClubActivityStatisticsByActId(activityId);
        if (obj == null) {
            Toasts.showOnUiThread(activity, activity.
                    getString(R.string.club_act_cancel_failure));
            return null;
        }
        if (obj.optInt("code") == 0) {
            JSONObject result = obj.optJSONObject("result");
            ClubActivityLikeRead clubActivityLikeRead = new ClubActivityLikeRead();
            clubActivityLikeRead.setRead(result.optInt("read_count"));
            clubActivityLikeRead.setLike(result.optInt("like_count"));
            return clubActivityLikeRead;
        }
        String message = obj.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }
        return null;
    }

    //更新俱乐部活动信息
    public ClubActivityInfo updateClubActivity(String activityId, String title, String desc, String mobPlace, String mobPoint, String startDate, String endDate,
                                               String routeId, String routeName, String mobilephone, String applyEndDate, int maxMembers,
                                               int isClubPrivate, String decstiption, String cover) {
        final JSONObject result = this.clubReleaseStub.updateClubActivity(activityId, title, desc, mobPlace, mobPoint, startDate, endDate,
                routeId, routeName, mobilephone, applyEndDate, maxMembers, isClubPrivate, decstiption, cover);
        if (result == null) {
            Toasts.showOnUiThread(activity, activity.
                    getString(R.string.club_act_cancel_failure));
            return null;
        }
        if (result.optInt("code") == 0) {

            return new ClubActivityInfo(result.optJSONObject("result"));
        }
        String message = result.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }
        return null;
    }

    public boolean sendClubActSms(String activityId) {
        final JSONObject result = this.clubReleaseStub.sendClubActSms(activityId);
        if (result == null) {
            Toasts.showOnUiThread(activity, activity.
                    getString(R.string.club_act_cancel_failure));
            return false;
        }
        if (result.optInt("code") == 0) {
            Toasts.showOnUiThread(activity, activity.getResources().getString(R.string.send_message_successfully));
            return true;
        }
        String message = result.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
        }
        return false;
    }

    public int clubActSignIn(String objectId, String activityId) {
        final JSONObject result = this.clubReleaseStub.clubActSignIn(objectId, activityId);
        if (result == null) {
            Toasts.showOnUiThread(activity, activity.
                    getString(R.string.club_act_cancel_failure));
            return -1;
        }
        if (result.optInt("code") == 0) {
            int count = result.optInt("result");
            Toasts.showOnUiThread(activity, activity.getResources().getString(R.string.sign_in_success));
            return count;
        }
        String message = result.optString("message");
        if (!TextUtils.isEmpty(message)) {
            Toasts.showOnUiThread(activity, message);
//            Toasts.show(activity, activity.getResources().getString(R.string.scan_code_failed));
        }
        return -1;
    }

}
