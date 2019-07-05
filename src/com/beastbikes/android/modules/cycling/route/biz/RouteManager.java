package com.beastbikes.android.modules.cycling.route.biz;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import com.beastbikes.android.R;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.modules.cycling.route.dao.RemoteRouteNode;
import com.beastbikes.android.modules.cycling.route.dto.CityDTO;
import com.beastbikes.android.modules.cycling.route.dto.RouteCommentDTO;
import com.beastbikes.android.modules.cycling.route.dto.RouteDTO;
import com.beastbikes.android.modules.cycling.route.dto.RouteNodeDTO;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RouteManager extends AbstractBusinessObject {

    private static final Logger logger = LoggerFactory
            .getLogger(RouteManager.class);

    private RouteServiceStub routeServiceStub;
    private Activity activity;

    public RouteManager(Activity activity) {
        super((BusinessContext) activity.getApplicationContext());
        this.activity = activity;
        final RestfulAPIFactory factory = new RestfulAPIFactory(activity);
        this.routeServiceStub = factory.create(RouteServiceStub.class, RestfulAPI.BASE_URL,
                RestfulAPI.getParams(activity));
    }

    /**
     * 获取城市
     *
     * @return List 城市里边
     * @throws BusinessException
     */
    public List<CityDTO> getRouteCities() throws BusinessException {
        try {
            final JSONObject result = this.routeServiceStub.getRouteCities();
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                final JSONArray cities = result.optJSONArray("result");
                final List<CityDTO> list = new ArrayList<>();

                for (int i = 0; i < cities.length(); i++) {
                    list.add(new CityDTO(cities.optJSONObject(i)));
                }

                return list;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }

    /**
     * 根据城市ID获取线路列表
     *
     * @param cityId 城市id
     * @return List    城市列表
     * @throws BusinessException
     */
    public List<RouteDTO> getRoutesByCityId(String cityId)
            throws BusinessException {
        try {
            final JSONObject result = this.routeServiceStub.getRoutesByCityId(cityId);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                final JSONArray routes = result.optJSONArray("result");
                final List<RouteDTO> list = new ArrayList<>();

                for (int i = 0; i < routes.length(); i++) {
                    list.add(new RouteDTO(routes.optJSONObject(i)));
                }

                return list;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }

    /**
     * 添加关注
     *
     * @param routeId 路线ID
     * @return int 关注数
     * @throws BusinessException
     */
    public int postRouteFollowerById(String routeId) throws BusinessException {
        try {
            final JSONObject result = this.routeServiceStub.postRouteFollower(routeId);
            if (null == result) {
                return -1;
            }

            int code = result.optInt("code");
            if (code == 0) {
                return result.optInt("result");
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return -1;
    }

    /**
     * 获取评论列表
     *
     * @param routeId 路线id
     * @param count   每页个数
     * @param page    页码
     * @return List 评论列表
     * @throws BusinessException
     */
    public List<RouteCommentDTO> getRouteCommentByRouteId(String routeId,
                                                          int count, int page) throws BusinessException {
        try {
            final JSONObject result = this.routeServiceStub.getRouteCommentsByRouteId(routeId,
                    page, count);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                final JSONObject obj = result.optJSONObject("result");
                final List<RouteCommentDTO> rcdList = new ArrayList<>();
                final JSONArray jsonArray = obj.optJSONArray("routeComments");
                RouteCommentDTO rcd;
                for (int i = 0; i < jsonArray.length(); i++) {
                    rcd = new RouteCommentDTO(jsonArray.optJSONObject(i));
                    rcd.setCommentCount(obj.optInt("routeCommentsCount"));
                    rcdList.add(rcd);
                }
                return rcdList;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }

    /**
     * 上传评论
     *
     * @param routeId  路线id
     * @param content  评论
     * @param parnetId 父评论者userid
     * @return int
     * @throws BusinessException
     */
    public int postRouteComment(String routeId, String content,
                                String parnetId) throws BusinessException {
        if (TextUtils.isEmpty(parnetId)) {
            parnetId = "";
        }

        try {
            final JSONObject result = this.routeServiceStub.postRouteComment(routeId, content, parnetId);
            if (null == result) {
                return -1;
            }

            int code = result.optInt("code");
            if (code == 0) {
                return result.optInt("count");
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return -1;
    }

    /**
     * 获取风景图
     *
     * @param routeId 路线id
     * @return List
     * @throws BusinessException
     */
    public List<String> getRoutePhotosByRouteId(final String routeId)
            throws BusinessException {
        try {
            final JSONObject result = this.routeServiceStub.getRoutePhotosByRouteId(routeId);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                List<String> list = new ArrayList<>();
                final JSONArray urls = result.optJSONArray("result");

                for (int i = 0; i < urls.length(); i++) {
                    list.add(urls.optJSONObject(i).getString("photoUrl"));
                }
                return list;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }

    /**
     * 获取个人制作路线的列表
     *
     * @return List
     * @throws BusinessException
     */
    public List<RouteDTO> getMyRoutes() throws BusinessException {
        try {
            final JSONObject result = this.routeServiceStub.getMyRoutes(1, 500);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                final JSONArray routes = result.optJSONArray("result");
                final List<RouteDTO> list = new ArrayList<>();

                for (int i = 0; i < routes.length(); i++) {
                    list.add(new RouteDTO(routes.optJSONObject(i)));
                }

                return list;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }

    /**
     * @param routeId 路线id
     * @return RouteDTO 路线详情
     * @throws BusinessException
     */
    public RouteDTO getRouteInfoByRouteId(String routeId) throws BusinessException {
        try {
            final JSONObject result = this.routeServiceStub.getRouteInfoByRouteId(routeId);
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                return new RouteDTO(result.optJSONObject("result"));
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }

    /**
     * 根据RouteId删除我的路线
     *
     * @param routeId 路线id
     * @return boolean
     * @throws BusinessException
     */
    public boolean deleteMyRouteById(final String routeId)
            throws BusinessException {
        try {
            final JSONObject result = this.routeServiceStub.deleteRouteByRouteId(routeId);
            if (null == result) {
                return false;
            }

            int code = result.optInt("code");
            if (code == 0) {
                Toasts.showOnUiThread(activity, activity.getResources().getString(R.string.deleteroutesuccess));
                return result.optBoolean("result");
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return false;
    }

    /**
     * 使用活动路线
     *
     * @param routeId 路线Id
     * @return JSONObject
     * @throws BusinessException
     */
    public JSONObject postFavoriteRoute(final String routeId)
            throws BusinessException {
        try {
            final JSONObject result = this.routeServiceStub.postFavoriteRoute(routeId);
            if (null == result) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            int code = result.optInt("code");
            if (code == 0) {
                return result.optJSONObject("result");
            }

        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }

    /**
     * 修改Route Name
     *
     * @param routeId   路线id
     * @param routeName 路线名称
     * @return boolean 是否成功
     * @throws BusinessException
     */
    public boolean updateRouteNameById(final String routeId,
                                       final String routeName) throws BusinessException {
        try {
            final JSONObject result = this.routeServiceStub.updateRoute(routeId, routeName);
            if (null == result) {
                return false;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            return result.optBoolean("result");
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 修改route
     *
     * @param route 路线ID
     * @param rnds  路线节点
     * @param mapUrl  路线图片
     * @return boolean
     * @throws BusinessException
     */
    public boolean updateRoute(RouteDTO route, List<RouteNodeDTO> rnds,
                               String mapUrl) throws BusinessException {
        StringBuilder originBuilder = new StringBuilder();
        originBuilder.append(String.valueOf(route.getOriginLongitude()))
                .append(",").append(String.valueOf(route.getOriginLatitude()))
                .append(",").append(String.valueOf(route.getOriginAltitude()));

        StringBuilder destinationBuilder = new StringBuilder();
        destinationBuilder
                .append(String.valueOf(route.getDestinationLongitude()))
                .append(",")
                .append(String.valueOf(route.getDestinationLatitude()))
                .append(",")
                .append(String.valueOf(route.getDestinationAltitude()));

        final JSONArray array = new JSONArray();

        for (RouteNodeDTO rnd : rnds) {
            final JSONObject obj = new JSONObject();
            try {
                obj.put(RemoteRouteNode.KEY_NODE, rnd.getKeyNode());
            } catch (JSONException e) {
                logger.error("Set route node key_node err", e);
            }

            try {
                obj.put(RemoteRouteNode.NAME, rnd.getName());
            } catch (JSONException e) {
                logger.error("Set route node name err", e);
            }

            try {
                obj.put("lng", rnd.getLongitude());
            } catch (JSONException e) {
                logger.error("Set route node longitude err", e);
            }

            try {
                obj.put("lat", rnd.getLatitude());
            } catch (JSONException e) {
                logger.error("Set route node latitude err", e);
            }

            try {
                obj.put(RemoteRouteNode.ORDINAL, rnd.getOrdinal());
            } catch (JSONException e) {
                logger.error("Set route node ordinal err", e);
            }

            try {
                obj.put(RemoteRouteNode.COORDINATE, rnd.getCoordinate());
            } catch (JSONException e) {
                logger.error("Set route node coordinate err", e);
            }

            try {
                obj.put(RemoteRouteNode.ALTITUDE, rnd.getAltitude());
            } catch (JSONException e) {
                logger.error("Set route node altitude err", e);
            }

            array.put(obj);
        }

        try {
            final JSONObject result = this.routeServiceStub.updateRoute(route.getId(),
                    route.getName(), originBuilder.toString(),
                    destinationBuilder.toString(), route.getTotalDistance(),
                    mapUrl, array.toString());
            if (null == result) {
                return false;
            }

            int code = result.optInt("code");
            if (code == 0) {
                return result.optBoolean("result");
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            return false;
        } catch (Exception e) {
            throw new BusinessException(e);
        }

    }

    /**
     * 上传路线
     *
     * @param route 路线
     * @param rnds  路线节点
     * @param mapUrl  路线图
     * @return boolean
     * @throws BusinessException
     */
    public boolean uploadRoute(RouteDTO route,
                               List<RouteNodeDTO> rnds, String mapUrl) throws BusinessException {
        StringBuilder originBuilder = new StringBuilder();
        originBuilder.append(String.valueOf(route.getOriginLongitude()))
                .append(",").append(String.valueOf(route.getOriginLatitude()))
                .append(",").append(String.valueOf(route.getOriginAltitude()));

        StringBuilder destinationBuilder = new StringBuilder();
        destinationBuilder
                .append(String.valueOf(route.getDestinationLongitude()))
                .append(",")
                .append(String.valueOf(route.getDestinationLatitude()))
                .append(",")
                .append(String.valueOf(route.getDestinationAltitude()));

//        String mapId = "";
//        final File routeMap = new File(path);
//        if (routeMap.exists() && routeMap.length() > 0) {
//            try {
//                AVFile map = AVFile.withFile(route.getName(), routeMap);
//                map.save();
//                mapId = map.getObjectId();
//            } catch (Exception e) {
//                logger.error("Set route map err", e);
//            }
//        }

        final JSONArray array = new JSONArray();

        for (RouteNodeDTO rnd : rnds) {
            final JSONObject obj = new JSONObject();
            try {
                obj.put(RemoteRouteNode.KEY_NODE, rnd.getKeyNode());
            } catch (JSONException e) {
                logger.error("Set route node key_node err", e);
            }

            try {
                obj.put(RemoteRouteNode.NAME, rnd.getName());
            } catch (JSONException e) {
                logger.error("Set route node name err", e);
            }

            try {
                obj.put("lng", rnd.getLongitude());
            } catch (JSONException e) {
                logger.error("Set route node longitude err", e);
            }

            try {
                obj.put("lat", rnd.getLatitude());
            } catch (JSONException e) {
                logger.error("Set route node latitude err", e);
            }

            try {
                obj.put(RemoteRouteNode.ORDINAL, rnd.getOrdinal());
            } catch (JSONException e) {
                logger.error("Set route node ordinal err", e);
            }

            try {
                obj.put(RemoteRouteNode.COORDINATE, rnd.getCoordinate());
            } catch (JSONException e) {
                logger.error("Set route node coordinate err", e);
            }

            try {
                obj.put(RemoteRouteNode.ALTITUDE, rnd.getAltitude());
            } catch (JSONException e) {
                logger.error("Set route node altitude err", e);
            }

            array.put(obj);
        }

        try {
            final JSONObject result = this.routeServiceStub.uploadRoute(route.getName(), originBuilder.toString(),
                    destinationBuilder.toString(), route.getTotalDistance(), mapUrl, array.toString());
            if (null == result) {
                return false;
            }
            Log.e("result", result.toString());
            String message = result.optString("message");
            int code = result.optInt("code");
            if (code == 0) {
                Toasts.showOnUiThread(activity, activity.getResources().getString(R.string.uploadmapsuccessed));
            } else {
                if (!TextUtils.isEmpty(message)) {
                    Toasts.showOnUiThread(activity, message);
                }
            }
            return result.optBoolean("result");
        } catch (Exception e) {
            throw new BusinessException(e);
        }

    }
}
