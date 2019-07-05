package com.beastbikes.android.modules.cycling.ranking.biz;


import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

/**
 * Created by icedan on 15/11/4.
 */
public interface RankServiceStub extends ServiceStub {

    /**
     * 获取总排行
     *
     * @param page  （页码）
     * @param count （每页个数）
     * @return JsonObject  （总排行列表）
     */
    @HttpPost("/getTotalRank")
    JSONObject getTotalRank(@BodyParameter("page") final int page,
                            @BodyParameter("count") final int count);

    /**
     * 获取月排行
     *
     * @param page  （页码）
     * @param count （每页个数）
     * @return JsonObject （月排行列表）
     */
    @HttpPost("/getMonthlyRank")
    JSONObject getMonthlyRank(@BodyParameter("page") final int page,
                              @BodyParameter("count") final int count);

    /**
     * 获取周排行
     *
     * @param page  （页码）
     * @param count （每页个数）
     * @return JsonObject  （周排行列表）
     */
    @HttpPost("/getWeeklyRank")
    JSONObject getWeeklyRank(@BodyParameter("page") final int page,
                             @BodyParameter("count") final int count);


    /**
     * 获取个人排行
     *
     * @param rankType (总：0，月：1，周，2)
     * @return JsonObject   (个人排行)
     */
    @HttpPost("/getMyRank")
    JSONObject getMyRank(@BodyParameter("rankType") final int rankType,
                         @BodyParameter("geoCode") final String geoCode);

    /**
     * 新 获取排行榜列表
     * rankType	int	0总榜, 1 周榜, 2 月榜, 3 年榜
     * geoCode	string	格式 CN.20.2035607 地区榜则传完整的geoCode,国家榜传第一部分 如中国榜则传CN geoCode 用户登录时会随用户信息返回 不传则为世界榜
     */
    @HttpPost("/getRankList")
    JSONObject getRankList(@BodyParameter("rankType") final int rankType,
                           @BodyParameter("geoCode") final String geoCode,
                           @BodyParameter("page") final int page,
                           @BodyParameter("count") final int count);
    /*

        area	string	当前城市名称
     */

    @HttpPost("/getGeoCode")
    JSONObject getGeoCode(@BodyParameter("area") final String area);
}
