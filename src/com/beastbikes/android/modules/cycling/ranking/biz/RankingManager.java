package com.beastbikes.android.modules.cycling.ranking.biz;

import android.app.Activity;
import android.text.TextUtils;

import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.modules.cycling.ranking.dto.RankDTO;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RankingManager extends AbstractBusinessObject {

    /**
     * 默认排行
     */
    public static final int NONE = 0;
    /**
     * 获取总排行
     */
    public static final int TOTAL_RANK = 0;
    /**
     * 获取月排行
     */
    public static final int MONTHLY_RANK = 1;
    /**
     * 获取周排行
     */
    public static final int WEEKLY_RANK = 2;

    //0总榜, 1 月榜, 2 周榜, 3 年榜
    public static final int RANK_TOTAL = 0;
    public static final int RANK_WEEKLY = 2;
    public static final int RANK_MONTHLY = 1;
    public static final int RANK_YEARLY = 3;

    private RankServiceStub rankServiceStub;
    private Activity activity;

    public RankingManager(Activity activity) {
        super((BusinessContext) activity.getApplicationContext());
        this.activity = activity;
        final RestfulAPIFactory factory = new RestfulAPIFactory(activity);
        this.rankServiceStub = factory.create(RankServiceStub.class,
                RestfulAPI.BASE_URL, RestfulAPI.getParams(activity));
    }

    /**
     * 新 获取排行榜列表
     * rankType	int	0总榜, 1 周榜, 2 月榜, 3 年榜
     */
    public List<RankDTO> getRankList(int rankType, String geoCode, int page, int count) throws BusinessException {
        try {
            final JSONObject result = this.rankServiceStub.getRankList(rankType,geoCode, page, count);
            if (result == null)
                return null;

            int code = result.optInt("code");
            if (code == 0) {
                final JSONArray results = result.optJSONArray("result");
                final int n = results.length();
                final List<RankDTO> list = new ArrayList<>();
                int ordinal = 1;
                for (int i = 0; i < n; i++) {
                    JSONObject o = results.optJSONObject(i);
                    if (!o.has("userId")) {
                        continue;
                    }
                    RankDTO rd = new RankDTO(o);
                    rd.setRankType(rankType);
                    rd.setOrdinal(ordinal);
                    list.add(rd);
                    ordinal++;
                }
                return list;
            }

            String message = result.optString("result");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public RankDTO getMyRank(int rankType, String geoCode)
            throws BusinessException {
        try {
            final JSONObject result = this.rankServiceStub.getMyRank(rankType , geoCode);
            if (null == result)
                return null;

            int code = result.optInt("code");
            if (code == 0) {
                JSONObject json = result.optJSONObject("result");
                RankDTO rd = new RankDTO(json);
                return rd;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty("message")) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }
    public JSONObject getGeoCode(String area){
        if (TextUtils.isEmpty(area)) {
            area = "北京";
        }

        try {
            return rankServiceStub.getGeoCode(area);
        } catch (Exception e) {
            return null;
        }
    }

}
