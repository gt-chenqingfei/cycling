package com.beastbikes.android.modules.cycling.sections.biz;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

/**
 * Created by caoxiao on 16/4/7.
 */
public interface SectionServiceStub extends ServiceStub {

    /**
     * 获取赛段列表
     */
    @HttpPost("/getSegmentList")
    JSONObject getSegmentList(@BodyParameter("longitude") final double longitude,
                              @BodyParameter("latitude") final double latitude,
                              @BodyParameter("range") final float range,
                              @BodyParameter("difficult") final String difficult,
                              @BodyParameter("legRange") final String legRange,
                              @BodyParameter("altRange") final String altRange,
                              @BodyParameter("slopeRange") final String slopeRange,
                              @BodyParameter("orderby") final String orderby);

    /**
     * 获取赛段详情
     */
    @HttpPost("/getSegmentInfo")
    JSONObject getSegmentInfo(@BodyParameter("segmentId") final long segmentId,
                              @BodyParameter("longitude") final float longitude,
                              @BodyParameter("latitude") final float latitude);

    /**
     * 获取赛段排行
     */
    @HttpPost("/getSegmentRankList")
    JSONObject getSegmentRank(@BodyParameter("segmentId") final long segmentId,
                              @BodyParameter("page") final int page,
                              @BodyParameter("count") final int count);

    /**
     * 获取赛段排行
     */
    @HttpPost("/favorSegment")
    JSONObject favorSegment(@BodyParameter("segmentId") final long segmentId);

    /**
     * 获取已收藏路段列表
     */
    @HttpPost("/getUserSegmentList")
    JSONObject getUserSegmentList(@BodyParameter("userId") final String userId,
                                  @BodyParameter("page") final int page,
                                  @BodyParameter("count") final int count);

    /**
     * 获取骑行记录途经的赛段列表
     */
    @HttpPost("/getRecordSegmentList")
    JSONObject getRecordSegmentList(@BodyParameter("sportIdentify") final String sportIdentify);
}
