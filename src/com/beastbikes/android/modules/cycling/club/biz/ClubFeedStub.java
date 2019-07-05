package com.beastbikes.android.modules.cycling.club.biz;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;
import com.beastbikes.framework.business.BusinessException;

import org.json.JSONObject;

/**
 * Created by icedan on 15/11/4.
 */
public interface ClubFeedStub extends ServiceStub {

    /**
     * @param clubId     （俱乐部id）
     * @param startStamp （ feed最大标记戳,下拉刷新时传0或不传,上拉时给上次请求的最后一个stamp）
     * @param endStamp   ( feed最小标记戳,下拉刷新时可不传或已有的最大stamp均可)
     * @return JsonObject  （俱乐部feed列表）
     */
    @HttpPost("/getClubTimeLine")
    JSONObject getClubTimeLine(@BodyParameter("clubId") final String clubId,
                               @BodyParameter("startStamp") final long startStamp,
                               @BodyParameter("endStamp") final long endStamp,
                               @BodyParameter("count") final int count);

    /**
     * @param feedId （feedId (int) – feed的id # getClubTimeLine 中的fid）
     * @return JsonObject  （俱乐部feed详情）
     */
    @HttpPost("/getClubFeedInfo")
    JSONObject getClubFeedInfo(@BodyParameter("feedId") final int feedId);

    /**
     * 发布一个俱乐部feeed
     *
     * @param clubId        用户所属俱乐部id
     * @param content       用户输入的文字
     * @param sportIdentify 用户所选的骑行记录
     * @param imageList     exp: “[{‘id’:’‘,’url’:’‘,’mine’:’image/jpeg’,’width’:500, ‘heigth’:500},]”  id迁移到七牛后弃用，但是必须有值,url为完整的地址
     * @param needSync      是否同步到俱乐部相册 0不同步, 1 同步
     * @return
     */
    @HttpPost("/postClubFeed")
    JSONObject postClubFeed(@BodyParameter("clubId") final String clubId,
                            @BodyParameter("content") final String content,
                            @BodyParameter("sportIdentify") final String sportIdentify,
                            @BodyParameter("imageList") final String imageList,
                            @BodyParameter("needSync") final int needSync);

    /**
     * 对俱乐部feed点赞/取消点赞
     *
     * @param cmd    0 like, 1 unlike
     * @param feedId
     * @return
     */
    @HttpPost("/likeClubFeed")
    JSONObject likeClubFeed(@BodyParameter("cmd") final int cmd,
                            @BodyParameter("feedId") final int feedId);

    /**
     * 获取单个俱乐部feed的点赞列表
     *
     * @param feedId
     * @param page
     * @param count
     * @return
     */
    @HttpPost("/getClubFeedLikeList")
    JSONObject getClubFeedLikeList(@BodyParameter("feedId") final int feedId,
                                   @BodyParameter("page") final int page,
                                   @BodyParameter("count") final int count
    );

    /**
     * 获取俱乐部feed评论列表
     *
     * @param feedId
     * @param page
     * @param count
     * @return
     */
    @HttpPost("/getClubFeedCommentList")
    JSONObject getClubFeedCommentList(@BodyParameter("feedId") final int feedId,
                                      @BodyParameter("page") final int page,
                                      @BodyParameter("count") final int count
    );

    /**
     * 评论一条俱乐部feed
     *
     * @param feedId
     * @param content
     * @param replyId
     * @return
     */
    @HttpPost("/postCommentClubFeed")
    JSONObject postCommentClubFeed(@BodyParameter("feedId") final int feedId,
                                   @BodyParameter("content") final String content,
                                   @BodyParameter("replyId") final int replyId
    );

    /**
     * 删除俱乐部feed
     *
     * @param feedId
     * @return
     */
    @HttpPost("/deleteClubFeed")
    JSONObject deleteClubFeed(@BodyParameter("feedId") final int feedId);

    /**
     * 获取俱乐部相册列表
     *
     * @param clubId
     * @param startDate
     * @param endDate
     * @param count
     * @return
     */
    @HttpPost("/getClubGalleryList")
    JSONObject getClubGalleryList(@BodyParameter("clubId") final String clubId,
                                  @BodyParameter("startDate") final String startDate,
                                  @BodyParameter("endDate") final String endDate,
                                  @BodyParameter("count") final int count);

    /**
     * 上传俱乐部相册图片
     *
     * @param imageList
     * @param content
     * @return
     */
    @HttpPost("/postClubPhotos")
    JSONObject postClubPhotos(@BodyParameter("imageList") final String imageList,
                              @BodyParameter("content") final String content);

    /**
     * 删除俱乐部照片
     *
     * @param photoIds
     * @return
     */
    @HttpPost("/deleteClubPhotos")
    JSONObject deleteClubPhotos(@BodyParameter("photoIds") final String photoIds);

    /**
     * 获取照片详情(应广大客户端大爷的意愿，该接口废弃)
     *
     * @param photoId
     * @return
     */
    @HttpPost("/getClubPhotoInfo")
    JSONObject getClubPhotoInfo(@BodyParameter("photoId") final int photoId);

    /**
     * 对一张俱乐部照片点赞/取消点赞
     *
     * @param photoId
     * @param cmd
     * @return
     */
    @HttpPost("/likeClubPhoto")
    JSONObject likeClubPhoto(@BodyParameter("photoId") final int photoId,
                             @BodyParameter("cmd") final int cmd);


    /**
     * 评论一张俱乐部照片
     *
     * @param photoId
     * @param content 评论内容
     * @param replyId 要回复的评论id, 可不传
     * @throws BusinessException
     */
    @HttpPost("/postClubPhotoComment")
    JSONObject postClubPhotoComment(@BodyParameter("photoId") int photoId,
                                    @BodyParameter("content") String content,
                                    @BodyParameter("replyId") int replyId);

    /**
     * 获取俱乐部照片评论列表
     *
     * @param photoId
     * @param page
     * @param count
     * @return
     */
    @HttpPost("/getClubPhotoCommentList")
    JSONObject getClubPhotoCommentList(@BodyParameter("photoId") int photoId,
                                       @BodyParameter("page") int page,
                                       @BodyParameter("count") int count);

    /**
     * 获取俱乐部照片点赞列表
     *
     * @param photoId
     * @param page
     * @param count
     * @return
     */
    @HttpPost("/getClubPhotoLikeList")
    JSONObject getClubPhotoLikeList(@BodyParameter("photoId") int photoId,
                                    @BodyParameter("page") int page,
                                    @BodyParameter("count") int count);

    /**
     * 获取未读俱乐部消息数量
     *
     * @return
     */
    @HttpPost("/getMyClubMsgCount")
    JSONObject getMyClubMsgCount();

    /**
     * 获取俱乐部消息(未读消息/历史消息)
     *
     * @param history
     * @param stamp
     * @param count
     * @return
     */
    @HttpPost("/getPushRecordList")
    JSONObject getPushRecordList(@BodyParameter("history") int history,
                                @BodyParameter("stamp") Long stamp,
                                @BodyParameter("count") int count);

    /**
     * 清空俱乐部消息
     *
     * @return
     */
    @HttpPost("/cleanMyClubMsgList")
    JSONObject cleanMyClubMsgList();

    /**
     * 获取某一时间段图片个数
     *
     * @param clubId
     * @param startDate
     * @param endDate
     * @return
     */
    @HttpPost("/getClubGalleryCount")
    JSONObject getClubGalleryCount(@BodyParameter("clubId") final String clubId,
                                   @BodyParameter("startDate") final String startDate,
                                   @BodyParameter("endDate") final String endDate);
    /**
     * 删除俱乐部评论
     *
     * @param commentId

     * @return
     */
    @HttpPost("/deleteClubFeedComment")
    JSONObject deleteClubFeedComment(@BodyParameter("commentId") final int commentId);


}
