package com.beastbikes.android.modules.shop.biz;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

/**
 * Created by caoxiao on 16/4/12.
 */
public interface BikeShopStub extends ServiceStub {

    /**
     * 获取车店列表
     * <p>
     * longitude	float	用户当前经度
     * latitude	float	用户当前纬度
     * range	float	数据半径 enumerate value of {1, 60, 120, 180, 240, 300},(min 1000, max 300, unit km)
     * keyWord	string	(非必传)检索key
     * <p>
     * {"message":"","result":[{"range":12458.581614907,"address":null,"location":[116.417999,39.89288],"shopId":15,"name":"123"},{"range":12000.941915626,"address":"车店1","location":[116.343842,39.95607],"shopId":21,"name":"车店1"},{"range":10043.705704259,"address":null,"location":[116.39328,39.930801],"shopId":17,"name":"朽木车店2"},{"range":7199.987975758,"address":"望京soho 塔2 B座 2508","location":[116.447439,39.934865],"shopId":18,"name":"车店名称"},{"range":4904.742631005,"address":null,"location":[116.419373,40.008683],"shopId":10,"name":"123"},{"range":4904.742631005,"address":null,"location":[116.419373,40.008683],"shopId":7,"name":"123"},{"range":4904.742631005,"address":null,"location":[116.419373,40.008683],"shopId":14,"name":"123"},{"range":572.768377759,"address":null,"location":[116.4701818803,39.9922931608],"shopId":3,"name":"ooxx已认证车店"}],"code":0}
     */
    @HttpPost("/getBikeShopList")
    JSONObject getBikeShopList(@BodyParameter("longitude") final double longitude,
                               @BodyParameter("latitude") final double latitude,
                               @BodyParameter("range") final float range,
                               @BodyParameter("keyWord") final String keyWord,
                               @BodyParameter("uLatitude") final double uLatitude,
                               @BodyParameter("uLongitude") final double uLongitude,
                               @BodyParameter("type") final String type);


    /**
     * shopId	int	车店ID
     * longitude	float	用户当前经度
     * latitude	float	用户当前纬度
     */
    @HttpPost("/getBikeShopInfo")
    JSONObject getBikeShopInfo(@BodyParameter("shopId") final long shopId,
                               @BodyParameter("longitude") final float longitude,
                               @BodyParameter("latitude") final float latitude);

    /**
     * shopId	int	车店ID
     */
    @HttpPost("/deleteBikeShop")
    JSONObject deleteBikeShop(@BodyParameter("shopId") final long shopId);


}
