package com.beastbikes.android.modules.cycling.club.biz;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

/**
 * Created by zhangyao on 2016/1/11.
 */
public interface ClubActivityStub extends ServiceStub {
    /**
     * title	string	活动标题
     * desc	string	描述(旧版)
     * mobPlace	string	集合地点
     * mobPoint	string	集合坐标
     * startDate	date	开始日期 格式 2016-03-15T01:53:00+00:00
     * endDate	date	结束日期
     * routeId	string	路线id (非必填)
     * routeName	string	路线名称 (非必填)
     * mobilephone	string	电话
     * applyEndDate	date	参加截止日期 (非必填)
     * maxMembers	int	最大允许参加人数
     * isClubPrivate	int	是否允许俱乐部以外的人参加 1 允许 0 不允许
     * decstiption	string	描述 富文本(新版)
     * cover	string	封面图 请传七牛连接
     */
    @HttpPost("/createClubActivity")
    JSONObject createClubActivity(@BodyParameter("title") final String title,
                                  @BodyParameter("desc") final String desc,
                                  @BodyParameter("mobPlace") final String mobPlace,
                                  @BodyParameter("mobPoint") final String mobPoint,
                                  @BodyParameter("startDate") final String startDate,
                                  @BodyParameter("endDate") final String endDate,
                                  @BodyParameter("routeId") final String routeId,
                                  @BodyParameter("routeName") final String routeName,
                                  @BodyParameter("mobilephone") final String mobilephone,
                                  @BodyParameter("applyEndDate") final String applyEndDate,
                                  @BodyParameter("maxMembers") final int maxMembers,
                                  @BodyParameter("isClubPrivate") final int isClubPrivate,
                                  @BodyParameter("decstiption") final String decstiption,
                                  @BodyParameter("cover") final String cover);

    /**
     * @param clubId （要查看的俱乐部id）
     * @param page   （分页 默认 1 起）
     * @param count  （单页数量 默认20）  # 0 未开始, 1 进行中, 2 已结束, 3 已取消
     * @return JsonObject  （俱乐部活动列表）
     */
    @HttpPost("/clubActivityList")
    JSONObject clubActivityList(
            @BodyParameter("clubId") final String clubId,
            @BodyParameter("page") final int page,
            @BodyParameter("count") final int count
    );

    @HttpPost("/clubActivityMemberList")
    /**
     *
     * @param activityId  （活动id）
     */
    JSONObject clubActivityMemberList(
            @BodyParameter("activityId") final String activityId,
            @BodyParameter("page") final int page,
            @BodyParameter("count") final int count
    );

    @HttpPost("/clubActivityInfo")
    /**
     *
     * @param activityId  （活动id）
     */
    JSONObject clubActivityInfo(
            @BodyParameter("activityId") final String activityId
    );

    @HttpPost("/clubActRegister")
    /**
     *
     * @param activityId  （活动id）
     * @param name （名字）
     * @param gender  （性别）
     * @param mobilephone （联系电话）
     * @param extra （附加信息）
     *  @param contact （联系人）
     */
    JSONObject clubActRegister(
            @BodyParameter("activityId") final String activityId,
            @BodyParameter("name") final String name,
            @BodyParameter("gender") final int gender,
            @BodyParameter("mobilephone") final String mobilephone,
            @BodyParameter("extra") final String extra,
            @BodyParameter("contact") final String contact
    );


    /**
     * @param activityId （活动id）
     */
    @HttpPost("/cancelClubActivity")
    JSONObject cancelClubActivity(
            @BodyParameter("activityId") final String activityId
    );


    @HttpPost("/getClubActivityStatisticsByActId")
    /**
     *
     * @param activityId  （活动id）
     */
    JSONObject getClubActivityStatisticsByActId(
            @BodyParameter("activityId") final String activityId
    );

    @HttpPost("/updateClubActivity")
    /**
     * 更新
     *
     * string	活动id(必填)
     * title	string	活动标题
     * desc	string	描述(旧版)
     * mobPlace	string	集合地点
     * mobPoint	string	集合坐标
     * startDate date	开始日期 格式 2016-03-15T01:53:00+00:00
     * endDate	date	结束日期
     * routeId	string	路线id
     * routeName	string	路线名称
     * mobilephone	string	电话
     * applyEndDate	string	参加截止日期
     * maxMembers	int	最大允许参加人数
     * isClubPrivate	int	是否允许俱乐部以外的人参加 1 允许 0 不允许
     * decstiption	string	描述 富文本(新版)
     * cover	string	封面图 请传七牛连接
     */
    JSONObject updateClubActivity(
            @BodyParameter("activityId") final String activityId,
            @BodyParameter("title") final String title,
            @BodyParameter("desc") final String desc,
            @BodyParameter("mobPlace") final String mobPlace,
            @BodyParameter("mobPoint") final String mobPoint,
            @BodyParameter("startDate") final String startDate,
            @BodyParameter("endDate") final String endDate,
            @BodyParameter("routeId") final String routeId,
            @BodyParameter("routeName") final String routeName,
            @BodyParameter("mobilephone") final String mobilephone,
            @BodyParameter("applyEndDate") final String applyEndDate,
            @BodyParameter("maxMembers") final int maxMembers,
            @BodyParameter("isClubPrivate") final int isClubPrivate,//1允许,0不允许
            @BodyParameter("decstiption") final String decstiption,
            @BodyParameter("cover") final String cover
    );

    /**
     * sendClubActSms
     * <p/>
     * activityId	string	活动id
     */
    @HttpPost("/sendClubActSms")
    JSONObject sendClubActSms(
            @BodyParameter("activityId") final String activityId);

    /**
     * ClubActSignIn
     * <p/>
     * objectId	string	扫码返回的字符串
     */
    @HttpPost("/ClubActSignIn")
    JSONObject clubActSignIn(
            @BodyParameter("objectId") final String objectId,
            @BodyParameter("activityId") final String activityId);
}
