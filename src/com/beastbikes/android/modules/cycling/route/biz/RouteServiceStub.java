package com.beastbikes.android.modules.cycling.route.biz;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

/**
 * Created by icedan on 15/11/5.
 */
public interface RouteServiceStub extends ServiceStub {

    /**
     * @return 城市列表
     */
    @HttpPost("/getRouteCities")
    JSONObject getRouteCities();

    /**
     * @param cityId 城市id
     * @return JSONObject 路线列表
     */
    @HttpPost("/getRoutesByCityId")
    JSONObject getRoutesByCityId(@BodyParameter("cityId") final String cityId);

    /**
     * @param routeId 路线Id
     * @return JSONObject 路线详情
     */
    @HttpPost("/postRouteFollower")
    JSONObject postRouteFollower(
            @BodyParameter("routeId") final String routeId);

    /**
     * @param routeId 路线id
     * @return JSONObject 路线详情
     */
    @HttpPost("/getRouteInfoByRouteId")
    JSONObject getRouteInfoByRouteId(@BodyParameter("routeId") final String routeId);

    /**
     * @param routeId 路线id
     * @param page    页码
     * @param count   每页个数
     * @return JSONObject 获取评论
     */
    @HttpPost("/getRouteCommentsByRouteId")
    JSONObject getRouteCommentsByRouteId(@BodyParameter("routeId") final String routeId,
                                         @BodyParameter("page") final int page,
                                         @BodyParameter("count") final int count);

    /**
     * @param routeId  路线id
     * @param content  路线评论
     * @param parentId 评论parentUserId
     * @return JSONObject 评论
     */
    @HttpPost("/postRouteComment")
    JSONObject postRouteComment(@BodyParameter("routeId") final String routeId,
                                @BodyParameter("content") final String content,
                                @BodyParameter("parentId") final String parentId);

    /**
     * @param routeId 路线id
     * @return JSONObject   路线风景图
     */
    @HttpPost("/getRoutePhotosByRouteId")
    JSONObject getRoutePhotosByRouteId(@BodyParameter("routeId") final String routeId);

    /**
     * @param page  页码
     * @param count 每页个数
     * @return JSONObject
     */
    @HttpPost("/getMyRoutes")
    JSONObject getMyRoutes(@BodyParameter("page") final int page,
                           @BodyParameter("count") final int count);

    /**
     * @param routeId 路线id
     * @return JSONObject
     */
    @HttpPost("/deleteRouteByRouteId")
    JSONObject deleteRouteByRouteId(@BodyParameter("routeId") final String routeId);

    /**
     * @param routeId 路线id
     * @return JSONObject  使用活动路线
     */
    @HttpPost("/postFavoriteRoute")
    JSONObject postFavoriteRoute(@BodyParameter("routeId") final String routeId);

    /**
     * @param routeId 路线id
     * @param name    路线名称
     * @return JSONObject
     */
    @HttpPost("/updateRoute")
    JSONObject updateRoute(@BodyParameter("routeId") final String routeId,
                           @BodyParameter("name") final String name);

    /**
     * @param routeId     路线id
     * @param name        路线名称
     * @param origin      起点坐标
     * @param destination 终点坐标
     * @param distance    里程
     * @param mapUrl       图片id
     * @param routeNodes  路线点
     * @return JSONObject
     */
    @HttpPost("/updateRoute")
    JSONObject updateRoute(@BodyParameter("routeId") final String routeId,
                           @BodyParameter("name") final String name,
                           @BodyParameter("origin") final String origin,
                           @BodyParameter("destination") final String destination,
                           @BodyParameter("distance") final double distance,
                           @BodyParameter("mapUrl") final String mapUrl,
//                           @BodyParameter("mapId") final String mapId,
                           @BodyParameter("routeNodes") final String routeNodes);

    /**
     * @param name        路线名称
     * @param origin      起点坐标
     * @param destination 终点坐标
     * @param distance    里程
     * @param mapUrl       图片id
     * @param routeNodes  路线id
     * @return JSONObject
     */
    @HttpPost("/uploadRoute")
    JSONObject uploadRoute(@BodyParameter("name") final String name,
                           @BodyParameter("origin") final String origin,
                           @BodyParameter("destination") final String destination,
                           @BodyParameter("distance") final double distance,
//                           @BodyParameter("mapId") final String mapId,
                           @BodyParameter("mapUrl") final String mapUrl,
                           @BodyParameter("routeNodes") final String routeNodes);

}
