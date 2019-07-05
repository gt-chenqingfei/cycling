package com.beastbikes.android.modules.cycling.activity.biz;


import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;
import com.beastbikes.framework.business.BusinessException;

import org.json.JSONObject;

public interface CyclingServiceStub extends ServiceStub {

    /**
     * 获取用户骑行记录
     *
     * @param userId （用户ID）
     * @param count  （每页个数）
     * @param page   （页码）
     * @return JsonObject  （骑行记录）
     */
    @HttpPost("/getActivitiesByUserId")
    JSONObject getActivitiesByUserId(@BodyParameter("userId") final String userId,
                                     @BodyParameter("count") final int count,
                                     @BodyParameter("page") final int page);

    /**
     * New Api 获取骑行记录列表
     *
     * @param userId
     * @param count
     * @param page
     * @param centralId
     * @return
     */
    @HttpPost("/getCyclingRecordList")
    JSONObject getCyclingRecordList(@BodyParameter("userId") final String userId,
                                    @BodyParameter("count") final int count,
                                    @BodyParameter("page") final int page,
                                    @BodyParameter("central_id") final String centralId);

    /**
     * 获取用户骑行数据
     *
     * @param userId （用户Id）
     * @return JsonObject  （骑行数据）
     */
    @HttpPost("/getActivityDataByUserId")
    JSONObject getActivityDataByUserId(@BodyParameter("userId") final String userId);

    /**
     * 获取骑行记录详情
     *
     * @param userId     （用户id）
     * @param activityId （骑行记录id）
     * @return JsonObject  （骑行记录详情）
     */
    @HttpPost("/getActivityInfoByActivityId")
    JSONObject getActivityInfoByActivityId(@BodyParameter("userId") final String userId,
                                           @BodyParameter("activityId") final String activityId);

    /**
     * 获取骑行记录详情
     *
     * @param activityId （骑行记录Id）
     * @return JsonObject  （骑行记录详情）
     */
    @HttpPost("/getActivityInfoByActivityId")
    JSONObject getActivityInfoByActivityId(
            @BodyParameter("activityId") final String activityId);

    /**
     * 获取骑行记录打点数据
     *
     * @param userId     （用户id）
     * @param activityId （骑行记录Id）
     * @return JsonObject   （骑行打点数据）
     */
    @HttpPost("/getActivitySamplesByActivityId")
    JSONObject getActivitySamplesByActivityId(@BodyParameter("userId") final String userId,
                                              @BodyParameter("activityId") final String activityId);

    /**
     * 获取骑行详细统计数据
     *
     * @param userId （用户id）
     * @return JsonObject   （骑行详细统计数据）
     */
    @HttpPost("/getActivityDetailDataByUserId")
    JSONObject getActivityDetailDataByUserId(@BodyParameter("userId") final String userId);

    /**
     * @return JsonObject   （最后一次骑行时间）
     */
    @HttpPost("/getLatestCyclingTime")
    JSONObject getLatestCyclingTime();

    /**
     * @param activityId 骑行记录id
     * @return JSONObject
     */
    @HttpPost("/deleteActivityByActivityId")
    JSONObject deleteActivityByActivityId(@BodyParameter("activityId") final String activityId);

    /**
     * @param phone       手机号
     * @param description 描述
     * @param activityId  骑行记录id
     * @param phoneMode   手机型号
     * @param phoneSystem 手机系统版本
     * @return
     */
    @HttpPost("/postAppeal")
    JSONObject postAppeal(@BodyParameter("phone") final String phone,
                          @BodyParameter("description") final String description,
                          @BodyParameter("activityId") final String activityId,
                          @BodyParameter("phoneMode") final String phoneMode,
                          @BodyParameter("phoneSystem") final String phoneSystem);

    /**
     * @param activityId 骑行记录id
     * @param reason     理由
     * @return JSONObject
     */
    @HttpPost("/postReportSportRoute")
    JSONObject postReportSportRoute(@BodyParameter("activityId") final String activityId,
                                    @BodyParameter("reason") final String reason);

    /**
     * @param title          骑行记录title
     * @param sportIdentify  骑行记录sportIdentify
     * @param calories       消耗卡路里
     * @param speed          速度
     * @param speedMax       最大速度
     * @param baiduMap       是否为百度地图, 0 否, 1 是
     * @param stopDate       结束时间
     * @param startDate      开始时间
     * @param time           持续时间
     * @param riseTotal      爬升距离
     * @param uphillDistance 爬坡距离
     * @param distance       里程
     * @param source         数据源
     * @param centralId      中控mac地址
     * @param cadenceMax     最大踏频
     * @param cardiacRateMax 最大心率
     * @param cadence        平均踏频
     * @param cardiacRate    平均 心率
     *
     * @return JSONObject
     */
    @HttpPost("/saveCyclingRecord")
    JSONObject saveCyclingRecord(@BodyParameter("userId") final String userId,
                                 @BodyParameter("title") final String title,
                                 @BodyParameter("sportIdentify") final String sportIdentify,
                                 @BodyParameter("calories") final double calories,
                                 @BodyParameter("speed") final double speed,
                                 @BodyParameter("speedMax") final double speedMax,
                                 @BodyParameter("baiduMap") final int baiduMap,
                                 @BodyParameter("stopDate") final String stopDate,
                                 @BodyParameter("startDate") final String startDate,
                                 @BodyParameter("time") final double time,
                                 @BodyParameter("riseTotal") final double riseTotal,
                                 @BodyParameter("uphillDistance") final double uphillDistance,
                                 @BodyParameter("distance") final double distance,
                                 @BodyParameter("source") final String source,
                                 @BodyParameter("centralId") final String centralId,
                                 @BodyParameter("cadenceMax") final double cadenceMax,
                                 @BodyParameter("cardiacRateMax") final double cardiacRateMax,
                                 @BodyParameter("cadence") final Double cadence,
                                 @BodyParameter("cardiacRate") final Double cardiacRate,
                                 @BodyParameter("centralName") final String centralName);

    /**
     * 更新骑行名称
     *
     * @param activityId 骑行记录id
     * @param title      骑行记录title
     * @return JSONObject
     */
    @HttpPost("/updateCyclingRecord")
    JSONObject updateCyclingRecordTitle(@BodyParameter("activityId") final String activityId,
                                        @BodyParameter("title") final String title) throws BusinessException;

    /**
     * 上传骑行记录
     *
     * @param activityId     骑行记录id
     * @param cyclingImageId 骑行记录图片
     * @return JSONObject
     */
    @HttpPost("/updateCyclingRecord")
    JSONObject updateCyclingRecordImage(@BodyParameter("activityId") final String activityId,
                                        @BodyParameter("cyclingImageId") final String cyclingImageId);

    /**
     * 上传骑行打点数据
     *
     * @param activityId 骑行记录id
     * @param sequence   打点顺序
     * @param data       打点数据
     * @return JSONObject
     */
    @HttpPost("/saveSample")
    JSONObject saveSample(@BodyParameter("activityId") final String activityId,
                          @BodyParameter("sequence") final int sequence,
                          @BodyParameter("data") final String data);

    /**
     * 获取目标配置列表
     *
     * @return JSONObject
     */
    @HttpPost("/getGoalConfig")
    JSONObject getGoalConfig();

    /**
     * 设定目标
     *
     * @param distance double
     * @return JSONObject
     */
    @HttpPost("/setMyGoal")
    JSONObject setMyGoal(@BodyParameter("distance") final double distance);

    /**
     * 获取我设定的目标
     *
     * @return JSONObject
     */
    @HttpPost("/getMyGoalInfo")
    JSONObject getMyGoalInfo();

    /**
     * 隐藏地图
     *
     * @return JSONObject
     */
    @HttpPost("/updateCyclingRecord")
    JSONObject updateCyclingRecord(@BodyParameter("activityId") final String activityId,
                                   @BodyParameter("isPrivate") final int isPrivate);


}
