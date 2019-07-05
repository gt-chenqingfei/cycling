package com.beastbikes.android.modules.cycling.activity.ui.record;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.utils.DateFormatUtil;

/**
 * Created by chenqingfei on 15/12/3.
 */

public class RecordAnalysis extends RecordBase<ActivityDTO> implements View.OnClickListener {

    private TextView mStartEndTime;
    private LinearLayout mLinear;

    public RecordAnalysis(Context context) {
        super(context);
    }

    @Override
    public void onCreateView() {
        super.onCreateView();
        mStartEndTime = (TextView) findViewById(R.id.record_analysis_time);
        mLinear = (LinearLayout) findViewById(R.id.record_analysis_linear);
        mLinear.setOnClickListener(this);
    }

    @Override
    public int getLayRes() {
        return R.layout.record_analysis;
    }

    @Override
    public void onClick(View view) {
        if (view == mLinear) {
            Intent intent = new Intent(mContext, RecordDataCompareActivity.class);
            mContext.startActivity(intent);
        }
    }

    @Override
    public void onDataChanged(ActivityDTO dto) {
        super.onDataChanged(dto);
        if (dto != null) {
            mStartEndTime.setText(DateFormatUtil.formatHMS(dto.getStartTime())
                    + " - " + DateFormatUtil.formatHMS(dto.getStopTime()));
        }
    }
}