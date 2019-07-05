package com.beastbikes.android.modules.cycling.club.biz;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

/**
 * Created by icedan on 15/11/4.
 */
public interface ClubServiceStub extends ServiceStub {

    /**
     * @param page  （页码）
     * @param count （每页个数）
     * @return JsonObject  （俱乐部请求列表）
     */
    @HttpPost("/getClubApplyList")
    JSONObject getClubApplyList(@BodyParameter("page") final int page,
                                @BodyParameter("count") final int count);

    /**
     * @param clubId （俱乐部Id）
     * @return JsonObject  （俱乐部详情）
     */
    @HttpPost("/getClubInfo")
    JSONObject getClubInfo(@BodyParameter("clubId") final String clubId);

    /**
     * @param orderBy （排序）
     * @param city    （城市）
     * @param keyName （关键字）
     * @param page    （页码）
     * @param count   （每页个数）
     * @return JsonObject  （俱乐部列表）
     */
    @HttpPost("/getClubList")
    JSONObject getClubList(@BodyParameter("orderBy") final String orderBy,
                           @BodyParameter("city") final String city,
                           @BodyParameter("keyName") final String keyName,
                           @BodyParameter("page") final int page,
                           @BodyParameter("count") final int count);

    /**
     * @param clubId （俱乐部Id）
     * @param page   （页码）
     * @param count  （每页个数）
     * @param order  （排序）
     * @return JsonObject （俱乐部成员列表）
     */
    @HttpPost("/getClubMemberList")
    JSONObject getClubMemberList(@BodyParameter("clubId") final String clubId,
                                 @BodyParameter("page") final int page,
                                 @BodyParameter("count") final int count,
                                 @BodyParameter("order") final String order);

    /**
     * my club rank
     *
     * @param rankType 0 总榜，1 月榜
     * @return JsonObject （我的俱乐部排行）
     */
    @HttpPost("/getMyClubRank")
    JSONObject getMyClubRank(@BodyParameter("rankType") final int rankType);

    /**
     * @return JsonObject (获取我的俱乐部关系)
     */
    @HttpPost("/getMyClubRelation")
    JSONObject getMyClubRelation();

    /**
     * @param clubId  俱乐部Id
     * @param command 操作类型
     * @param extra   理由
     * @return JsonObject 操作结果
     */
    @HttpPost("/postCmdClub")
    JSONObject postCmdClub(@BodyParameter("clubId") final String clubId,
                           @BodyParameter("command") final int command,
                           @BodyParameter("extra") final String extra);

    /**
     * Update Club Info
     *
     * @param name     俱乐部名字
     * @param logoId   俱乐部logo
     * @param province 省
     * @param city     市
     * @param desc     描述
     * @param notice   公告
     *                 isPrivate  0公开，1私密
     * @return JSONObject
     */
    @HttpPost("/postUpdateClubInfo")
    JSONObject postUpdateClubInfo(@BodyParameter("name") final String name,
                                  @BodyParameter("logoId") final String logoId,
                                  @BodyParameter("province") final String province,
                                  @BodyParameter("city") final String city,
                                  @BodyParameter("desc") final String desc,
                                  @BodyParameter("notice") final String notice,
                                  @BodyParameter("isPrivate") final int isPrivate);


    /**
     * Update Club Notice
     *
     * @param notice 发布新公告
     * @return JSONObject
     */
    @HttpPost("/postUpdateClubInfo")
    JSONObject postUpdateClubNotice(@BodyParameter("notice") final String notice);

    /**
     * @param applyId 申请id
     * @param command 0 通过申请, 1 拒绝申请, 2 删除申请
     * @return JsonObject 处理俱乐部加入申请
     */
    @HttpPost("/postClubApply")
    JSONObject postClubApply(@BodyParameter("applyId") final String applyId,
                             @BodyParameter("command") final int command);

    /**
     * @param memberId 成员Id
     * @param command  操作类型
     * @return JsonObject 剔除成员
     */
    @HttpPost("/postCmdClubMember")
    JSONObject postCmdClubMember(@BodyParameter("memberId") final String memberId,
                                 @BodyParameter("command") final int command);

    /**
     * @return 获取未读消息数
     */
    @HttpPost("/getUnReadCount")
    JSONObject getUnReadCount();

    @HttpPost("/postRegisterClub")
    JSONObject postRegisterClub(@BodyParameter("name") final String name,
                                @BodyParameter("logoId") final String logoId,//选填 七牛
                                @BodyParameter("province") final String province,
                                @BodyParameter("city") final String city,
                                @BodyParameter("desc") final String desc,
                                @BodyParameter("realName") final String realName,
                                @BodyParameter("mobilephone") final String mobilephone,
                                @BodyParameter("qq") final String qq,//选填
                                @BodyParameter("vcode") final String vcode,
                                @BodyParameter("isPrivate") final int isPrivate,
                                @BodyParameter("latitude") final double latitude,
                                @BodyParameter("longitude") final double longitude);

    /**
     * @param clubId 俱乐部Id
     * @return 取消俱乐部创建申请
     */
    @HttpPost("/postCancelClubReg")
    JSONObject postCancelClubReg(@BodyParameter("clubId") final String clubId);

    /**
     * api v2.1
     * 获取俱乐部排行榜列表
     * 参数:
     * rankType (int) – 0 总榜, 1 月榜
     * page (int) – 分页 默认1起
     * count (int) – 单页数量
     */
    @HttpPost("/getClubRankList")
    JSONObject getClubRankList(@BodyParameter("rankType") final int rankType,
                               @BodyParameter("page") final int page,
                               @BodyParameter("count") final int count);

    /**
     * 获取俱乐部成员排行
     * rankType (int) – 0 总榜, 1 月榜
     * page (int) – 分页 默认1起
     * count (int) – 单页数量
     */
    @HttpPost("/getClubMemberRankList")
    JSONObject getClubMemberRankList(@BodyParameter("rankType") final int rankType,
                                     @BodyParameter("page") final int page,
                                     @BodyParameter("count") final int count,
                                     @BodyParameter("clubId") final String clubId);

    /**
     * 获取俱乐部历史公告列表
     */
    @HttpPost("/getClubNoticeList")
    JSONObject getClubNoticeList(@BodyParameter("clubId") final String clubId,
                                 @BodyParameter("page") final int page,
                                 @BodyParameter("count") final int count);

    /**
     * 发送验证码
     */
    @HttpPost("/sendSmscode")
    JSONObject sendSmscode(@BodyParameter("mobilephone") final String mobilephone,
                           @BodyParameter("msgType") final String msgType);

    /**
     * 获取俱乐部等级详情
     */
    @HttpPost("/getClubLevelInfo")
    JSONObject getClubLevelInfo();

    /**
     * 获取俱乐部特权详情
     */
    @HttpPost("/getClubPrivilegInfo")
    JSONObject getClubPrivilegInfo();


    /**
     * 转让俱乐部
     *
     * @param memberId
     * @param isQuit
     * @return
     */
    @HttpPost("/transferClub")
    JSONObject transferClub(@BodyParameter("memberId") final String memberId,
                            @BodyParameter("isQuit") final int isQuit);

    /**
     * 获取俱乐部转让的状态
     */
    @HttpPost("/getClubTransStatus")
    JSONObject getClubTransStatus();

    /**
     * 取消转让俱乐部
     */
    @HttpPost("/cancelClubTrans")
    JSONObject cancelClubTrans();

    /**
     * 取消转让俱乐部
     */
    @HttpPost("/sendClubTransNotify")
    JSONObject sendClubTransNotify();


}
