package com.beastbikes.android.modules.user.biz;


import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;
import com.beastbikes.android.sphere.restful.annotation.MatrixParameter;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by icedan on 15/11/9.
 */
public interface UserServiceStub extends ServiceStub {

    /**
     * 获取个人详情
     *
     * @return JSONObject
     */
    @HttpPost("/getUserInfoByUserId")
    JSONObject getUserInfoByUserId(@BodyParameter("userId") final String userId);

    /**
     * 更新用户信息
     *
     * @return JSONObject
     */
    @HttpPost("/updateUserInfo")
    JSONObject updateUserInfo(@BodyParameter("nickname") final String nickname,
                              @BodyParameter("sex") final String sex,
                              @BodyParameter("weight") final float weight,
                              @BodyParameter("height") final float height,
                              @BodyParameter("area") final String area,
                              @BodyParameter("city") final String city,
                              @BodyParameter("province") final String province,
                              @BodyParameter("birthday") final String birthday,
                              @BodyParameter("avatarImageId") final String avatarImageId);

    /**
     * 获取勋章列表
     *
     * @return JSONObject
     */
    @HttpPost("/getUserMedalList")
    JSONObject getUserMedalList();

    /**
     * 获取个人排行柱状图
     *
     * @param userId
     * @param days
     * @return
     */
    @HttpPost("/getUserDiagram")
    JSONObject getUserDiagram(@BodyParameter("userId") final String userId,
                              @BodyParameter("days") final int days);

    /**
     * 获取俱乐部柱状图
     *
     * @param clubId
     * @param days
     * @return
     */
    @HttpPost("/getClubDiagram")
    JSONObject getClubDiagram(@BodyParameter("clubId") final String clubId,
                              @BodyParameter("days") final int days);

    /**
     * 获取粉丝列表
     *
     * @param userId
     * @param page
     * @param count
     * @return
     */
    @HttpPost("/getFansList")
    JSONObject getFansList(@BodyParameter("userId") final String userId,
                           @BodyParameter("page") final int page,
                           @BodyParameter("count") final int count);

    /**
     * 获取关注列表
     *
     * @param userId
     * @param page
     * @param count
     * @return
     */
    @HttpPost("/getFollowList")
    JSONObject getFollowList(@BodyParameter("userId") final String userId,
                             @BodyParameter("page") final int page,
                             @BodyParameter("count") final int count);

    /**
     *
     */
    @HttpPost("/seekFriends")
    JSONObject seekFriendsWithFile(@BodyParameter("seekType") final int seekType,
                                   @BodyParameter("thirdKey") final String thirdKey,
                                   @BodyParameter("thirdToken") final String thirdToken,
                                   @MatrixParameter("contact") final File contact);

    /**
     *
     */
    @HttpPost("/seekFriends")
    JSONObject seekFriends(@BodyParameter("seekType") final int seekType,
                           @BodyParameter("thirdKey") final String thirdKey,
                           @BodyParameter("thirdToken") final String thirdToken);

    /**
     * 关注
     *
     * @param targetId
     * @return
     */
    @HttpPost("/follow")
    JSONObject follow(@BodyParameter("targetId") final String targetId);

    /**
     * 取消关注
     *
     * @param userId
     * @return
     */
    @HttpPost("/unfollow")
    JSONObject unfollow(@BodyParameter("userId") final String userId);

    /**
     * 用户反馈
     *
     * @param content
     * @param contact
     * @param type
     * @param detail
     * @param logId
     * @return
     */
    @HttpPost("/feedback")
    JSONObject feedback(@BodyParameter("content") final String content,
                        @BodyParameter("contact") final String contact,
                        @BodyParameter("type") final int type,
                        @BodyParameter("detail") final String detail,
                        @BodyParameter("logId") final String logId);

    /**
     * 获取勋章列表 isHistory 0: 获取点亮勋章,1 获取过期勋章, 2 获取未查看且已点亮的勋章列表
     *
     * @param isHistory
     * @param page
     * @param count
     * @return
     */
    @HttpPost("/getBadgeList")
    JSONObject getBadgeList(@BodyParameter("isHistory") final int isHistory,
                            @BodyParameter("page") final int page,
                            @BodyParameter("count") final int count,
                            @BodyParameter("userId") final String userId);

    /**
     * 获取勋章详情
     *
     * @param id
     * @return
     */
    @HttpPost("/getBadgeInfo")
    JSONObject getBadgeInfo(@BodyParameter("badgeId") final int id,
                            @BodyParameter("userId") final String userId);

    /**
     * 更新设备信息
     *
     * @param latitude
     * @param longitude
     * @return
     */
    @HttpPost("/updateDeviceInfo")
    JSONObject updateDeviceInfo(@BodyParameter("latitude") final double latitude,
                                @BodyParameter("longitude") final double longitude,
                                @BodyParameter("deviceToken") final String deviceToken);

    /**
     * 获取用户柱状图数据    source 0:所有 1:硬件
     *
     * @param userId
     * @param source
     * @param days
     * @return
     */
    @HttpPost("/getUserDiagramBySource")
    JSONObject getUserDiagramBySource(@BodyParameter("userId") final String userId,
                                      @BodyParameter("source") final int source,
                                      @BodyParameter("days") final int days);

    /**
     * 获取用户柱状图数据    source 0:所有 1:硬件
     *
     * @param userId
     * @param centralId
     * @param days
     * @return
     */
    @HttpPost("/getUserDiagramByCentral")
    JSONObject getUserDiagramByCentral(@BodyParameter("useId") final String userId,
                                       @BodyParameter("centralId") final String centralId,
                                       @BodyParameter("days") final int days);

    /**
     * 获取用户硬件设备总数据  source 0:所有 1:硬件
     *
     * @param source
     * @return
     */
    @HttpPost("/getUserGoalInfoBySource")
    JSONObject getUserGoalInfoBySource(@BodyParameter("source") final int source);

    /**
     * 获取用户硬件设备总数据
     *
     * @param centralId
     * @return
     */
    @HttpPost("/getUserGoalInfoByCentral")
    JSONObject getUserGoalInfoByCentral(@BodyParameter("centralId") final String centralId, @BodyParameter("useId") final String useId);

    /**
     * 设置用户最大心率
     *
     * @param cardiacRate 最大心率
     * @return
     */
    @HttpPost("/updateUserInfo")
    JSONObject updateUserInfo(@BodyParameter("cardiacRate") final int cardiacRate);


}
