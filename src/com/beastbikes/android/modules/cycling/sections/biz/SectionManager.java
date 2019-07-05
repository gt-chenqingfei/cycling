package com.beastbikes.android.modules.cycling.sections.biz;

import android.app.Activity;
import android.text.TextUtils;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.modules.cycling.sections.dto.RecordSegmentDTO;
import com.beastbikes.android.modules.cycling.sections.dto.SectionDetailListDTO;
import com.beastbikes.android.modules.cycling.sections.dto.SectionListDTO;
import com.beastbikes.android.modules.cycling.sections.dto.SegmentRankDTO;
import com.beastbikes.android.modules.cycling.sections.dto.UserSegmentDTO;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 16/4/7.
 */
public class SectionManager extends AbstractBusinessObject implements Constants {

    private SectionServiceStub sectionServiceStub;
    private Activity activity;

    public SectionManager(Activity activity) {
        super((BusinessContext) activity.getApplicationContext());
        final RestfulAPIFactory factory = new RestfulAPIFactory(activity);
        this.activity = activity;
        this.sectionServiceStub = factory.create(SectionServiceStub.class,
                RestfulAPI.BASE_URL, RestfulAPI.getParams(activity));
    }

    public List<SectionListDTO> getSegmentList(double longitude, double latitude, float range, String difficult, String legRange,
                                               String altRange, String slopeRange, String orderby) throws BusinessException {
        try {
            final JSONObject obj = this.sectionServiceStub.getSegmentList(longitude, latitude, range, difficult, legRange, altRange, slopeRange, orderby);
            if (null == obj) {
                Toasts.showOnUiThread(activity, activity.getString(R.string.club_act_release_failure));
                return null;
            }
            if (obj.optInt("code") == 0) {
                JSONArray jsonArray = obj.optJSONArray("result");
                if (jsonArray == null || jsonArray.length() == 0)
                    return null;
                List<SectionListDTO> sectionListDTOs = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    SectionListDTO sectionListDTO = new SectionListDTO(jsonArray.optJSONObject(i));
                    sectionListDTOs.add(sectionListDTO);
                }
                return sectionListDTOs;
            }

            String message = obj.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SectionDetailListDTO getSegmentInfo(long segmentId, float longitude, float latitude) throws BusinessException {
        try {
            final JSONObject obj = this.sectionServiceStub.getSegmentInfo(segmentId, longitude, latitude);
            if (null == obj) {
                Toasts.showOnUiThread(activity, activity.getString(R.string.club_act_release_failure));
                return null;
            }
            if (obj.optInt("code") == 0) {
                SectionDetailListDTO sectionDetailListDTO = new SectionDetailListDTO(obj.optJSONObject("result"));
                return sectionDetailListDTO;
            }
            String message = obj.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<SegmentRankDTO> getSegmentRank(long segmentId, int page, int count) throws BusinessException {
        try {
            final JSONObject obj = this.sectionServiceStub.getSegmentRank(segmentId, page, count);
            if (null == obj) {
                Toasts.showOnUiThread(activity, activity.getString(R.string.club_act_release_failure));
                return null;
            }
            if (obj.optInt("code") == 0) {
                JSONArray jsonArray = obj.optJSONArray("result");
                if (jsonArray == null || jsonArray.length() == 0)
                    return null;
                List<SegmentRankDTO> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    SegmentRankDTO segmentRankDTO = new SegmentRankDTO(jsonArray.optJSONObject(i));
                    list.add(segmentRankDTO);
                }
                return list;
            }
            String message = obj.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int favorSegment(long segmentId) throws BusinessException {
        try {
            final JSONObject obj = this.sectionServiceStub.favorSegment(segmentId);
            if (null == obj) {
                Toasts.showOnUiThread(activity, activity.getString(R.string.club_act_release_failure));
                return -1;
            }
            if (obj.optInt("code") == 0) {
                return obj.optInt("result");
            }
            String message = obj.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<UserSegmentDTO> getUserSegmentList(String userId, int page, int count) throws BusinessException {
        try {
            final JSONObject obj = this.sectionServiceStub.getUserSegmentList(userId, page, count);
            if (null == obj) {
                Toasts.showOnUiThread(activity, activity.getString(R.string.club_act_release_failure));
                return null;
            }
            if (obj.optInt("code") == 0) {
                JSONArray jsonArray = obj.optJSONArray("result");
                if (jsonArray == null || jsonArray.length() == 0)
                    return null;
                List<UserSegmentDTO> listDTOs = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    listDTOs.add(new UserSegmentDTO(jsonArray.optJSONObject(i)));
                }
                return listDTOs;
            }
            String message = obj.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public RecordSegmentDTO getRecordSegmentList(String sportIdentify) throws BusinessException {
        try {
            final JSONObject obj = this.sectionServiceStub.getRecordSegmentList(sportIdentify);
            if (null == obj) {
                Toasts.showOnUiThread(activity, activity.getString(R.string.club_act_release_failure));
                return null;
            }
            if (obj.optInt("code") == 0) {
                JSONObject jsonObject = obj.optJSONObject("result");
                JSONArray jsonArray = jsonObject.optJSONArray("segmentList");
                if (jsonArray == null || jsonArray.length() == 0)
                    return null;
                List<UserSegmentDTO> listDTOs = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    listDTOs.add(new UserSegmentDTO(jsonArray.optJSONObject(i)));
                }
                boolean needWait = obj.optBoolean("needWait");
                RecordSegmentDTO recordSegmentDTO = new RecordSegmentDTO(needWait, listDTOs);
                return recordSegmentDTO;
            }
            String message = obj.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
