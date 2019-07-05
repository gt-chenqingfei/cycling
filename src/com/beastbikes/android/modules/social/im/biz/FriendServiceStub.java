package com.beastbikes.android.modules.social.im.biz;


import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpGet;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;
import com.beastbikes.android.sphere.restful.annotation.HttpPut;
import com.beastbikes.android.sphere.restful.annotation.PathParameter;
import com.beastbikes.android.sphere.restful.annotation.QueryParameter;

import org.json.JSONObject;

public interface FriendServiceStub extends ServiceStub {

    /**
     * 添加好友
     *
     * @param userId User Id
     * @param extra  Extra
     * @return JSONObject
     */
    @HttpPost("/addFriend")
    JSONObject addFriend(@BodyParameter("userId") final String userId,
                         @BodyParameter("extra") final String extra);

    /**
     * 获取新的好友请求数
     *
     * @return JSONObject
     */
    @HttpPost("/checkFriendRequestsCount")
    JSONObject checkFriendRequestsCount(
            @BodyParameter("timestamp") final long lastTime);

    /**
     * 查询用户关系
     *
     * @param userId User Id
     * @return JSONObject
     */
    @HttpGet("/checkFriendStatus")
    JSONObject checkFriendStatus(
            @QueryParameter("userId") final String userId);

    /**
     * 清空好友请求
     *
     * @return JSONObject
     */
    @HttpPost("/cleanFriendRequests")
    JSONObject cleanFriendRequests();

    /**
     * 通过or拒绝好友申请 （0 通过， 1 拒绝）
     *
     * @param requestId Request Id
     * @param command   Command
     * @return JSONObject
     */
    @HttpPost("/friendRequestCmd")
    JSONObject friendRequestCmd(
            @BodyParameter("requestId") final int requestId,
            @BodyParameter("command") final int command);

    /**
     * 获取用户好友申请列表
     *
     * @param page  Page
     * @param count Count
     * @return JSONObject
     */
    @HttpGet("/friendRequestsList")
    JSONObject friendRequestsList(
            @QueryParameter("page") final int page,
            @QueryParameter("count") final int count);

    /**
     * 获取用户好友列表
     *
     * @param userId User Id
     * @param page   Page
     * @param count  Count
     * @return JSONObject
     */
    @HttpGet("/friendsList")
    JSONObject friendsList(
            @QueryParameter("userId") final String userId,
            @QueryParameter("page") final int page,
            @QueryParameter("count") final int count);

    /**
     * 通过昵称查询用户
     *
     * @param keyName nick name
     * @param page    page
     * @param count   count
     * @return JSONObject
     */
    @HttpPost("/searchUserByNickname")
    JSONObject searchUserByNickname(
            @BodyParameter("keyName") final String keyName,
            @BodyParameter("page") final int page,
            @BodyParameter("count") final int count);

    /**
     * 解除好友关系
     *
     * @param userId User Id
     * @return JSONObject
     */
    @HttpPost("/unfollow")
    JSONObject unfollow(@BodyParameter("userId") final String userId);

    // ---------------------------------- RESTFUL ------------------------------

    /**
     * 获取好友请求列表
     *
     * @param page  Page
     * @param count Count
     * @return JSONObject
     */
    @HttpGet("/friendRequests")
    JSONObject friendRequests(@QueryParameter("page") final int page,
                              @QueryParameter("count") final int count);

    /**
     * 创建好友申请
     *
     * @param userId User Id
     * @param extra  Extra
     * @return JSONObject
     */
    @HttpPost("/friendRequests")
    JSONObject friendRequests(
            @BodyParameter("userId") final String userId,
            @BodyParameter("extra") final String extra);

    /**
     * 通过好友申请
     *
     * @param command Command
     * @return JSONObject
     */
    @HttpPut("/friendRequestCmd/{requestId}")
    JSONObject friendRequestCmds(
            @PathParameter("requestId") final int requestId,
            @BodyParameter("command") final int command);


    /*备注信息
     * target_user_id	string	好友id 必填
     *  remarks	string	备注信息
     */
    @HttpPost("/updateSocialInfo")
    JSONObject updateSocialInfo(
            @BodyParameter("target_user_id") final String target_user_id,
            @BodyParameter("remarks") final String remarks );

    /**
     * 获取融云token
     *
     * @return JSONObject
     */
    @HttpPost("/getChatToken")
    JSONObject getChatToken();

    /**
     * 设置群聊昵称
     *
     * @param clubId
     * @param nickname
     * @return JSONObject
     */
    @HttpPost("/setClubChatNick")
    JSONObject setClubChatNick(
            @BodyParameter("clubId") final String clubId,
            @BodyParameter("nickname") final String nickname);

    /**
     * 获取群聊昵称
     *
     * @param clubId
     * @param userId
     * @return JSONObject
     */
    @HttpPost("/getClubChatNick")
    JSONObject getClubChatNick(
            @BodyParameter("clubId") final String clubId,
            @BodyParameter("userId") final String userId);

    /**
     * 获取群聊昵称
     *
     * @param userIds
     * @return JSONObject
     */
    @HttpPost("/getChatInfoByIds")
    JSONObject getChatInfoByIds(
            @BodyParameter("userIds") final String userIds);
}
