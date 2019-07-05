package com.beastbikes.android.modules.cycling.sections.dto;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by caoxiao on 16/4/23.
 */
public class RecordSegmentDTO {
    private boolean needWait;
    private List<UserSegmentDTO> userSegmentDTOs;

    public RecordSegmentDTO(boolean needWait, List<UserSegmentDTO> userSegmentDTOs) {
        this.needWait = needWait;
        this.userSegmentDTOs = userSegmentDTOs;
    }

    public boolean isNeedWait() {
        return needWait;
    }

    public void setNeedWait(boolean needWait) {
        this.needWait = needWait;
    }

    public List<UserSegmentDTO> getUserSegmentDTOs() {
        return userSegmentDTOs;
    }

    public void setUserSegmentDTOs(List<UserSegmentDTO> userSegmentDTOs) {
        this.userSegmentDTOs = userSegmentDTOs;
    }
}
