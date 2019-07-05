package com.beastbikes.android.modules.cycling.activity.ui.record;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by chenqingfei on 15/12/3.
 * <p>
 * 骑行报告摘要
 */
public class RecordSummary extends RecordBase<ActivityDTO> implements View.OnClickListener {

    public interface OnSummaryItemClickListener {
        void onSummaryItemClick(int id);
    }

    private CircleImageView ivUserAvatar;
    private TextView tvDistanceValue;
    private TextView tvDistanceLabel;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvName;
    private TextView tvMaxSpeed;
    private TextView tvAvgSpeed;
    private ImageView ivEdit;

    private TextView tvAvgSpeedUnit;
    private TextView tvTimeUnit;
    private TextView tvMaxSpeedUnit;

    private OnSummaryItemClickListener onSummaryItemClickListener;

    public RecordSummary(Context context) {
        super(context);
    }

    @Override
    public int getLayRes() {
        return R.layout.record_summary;
    }

    @Override
    public void onCreateView() {
        super.onCreateView();
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/BebasNeue.otf");
        tvDistanceValue = (TextView) findViewById(R.id.summary_distance);
        tvDistanceLabel = (TextView) findViewById(R.id.summary_distance_label);
        tvDate = (TextView) findViewById(R.id.summary_date);
        tvTime = (TextView) findViewById(R.id.summary_time);
        tvMaxSpeed = (TextView) findViewById(R.id.summary_max_speed);
        tvAvgSpeed = (TextView) findViewById(R.id.summary_avg_speed);
        ivUserAvatar = (CircleImageView) findViewById(R.id.summary_user_avatar);
        ivEdit = (ImageView) findViewById(R.id.summary_cycling_edit);
        tvName = (TextView) findViewById(R.id.summary_name);

        tvAvgSpeedUnit = (TextView) findViewById(R.id.summary_avg_speed_unit);
        tvTimeUnit = (TextView) findViewById(R.id.summary_time_unit);
        tvMaxSpeedUnit = (TextView) findViewById(R.id.summary_max_speed_unit);

        if (LocaleManager.isDisplayKM(mContext)) {
            tvAvgSpeedUnit.setText(mContext.getString(R.string.label_label_speed) + LocaleManager.LocaleString.activity_param_label_velocity);
            tvMaxSpeedUnit.setText(mContext.getString(R.string.label_max_speed) + LocaleManager.LocaleString.activity_param_label_velocity);
        } else {
            tvAvgSpeedUnit.setText(mContext.getString(R.string.label_label_speed) + LocaleManager.LocaleString.activity_param_label_velocity_mph);
            tvMaxSpeedUnit.setText(mContext.getString(R.string.label_max_speed) + LocaleManager.LocaleString.activity_param_label_velocity_mph);
        }
        tvTimeUnit.setText(mContext.getString(R.string.profile_fragment_statistic_item_total_elapsed_time));

        tvDistanceValue.setTypeface(typeface);
        tvDate.setTypeface(typeface);
        tvTime.setTypeface(typeface);
        tvMaxSpeed.setTypeface(typeface);
        tvAvgSpeed.setTypeface(typeface);
        ivEdit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (onSummaryItemClickListener != null) {
            onSummaryItemClickListener.onSummaryItemClick(view.getId());
        }
    }

    @Override
    public void onDataChanged(ActivityDTO dto) {
        super.onDataChanged(dto);
        if (dto == null)
            return;

        tvName.setText(dto.getNickname());
        Picasso.with(getContext()).load(dto.getAvatarUrl()).fit().error(R.drawable.ic_avatar).
                placeholder(R.drawable.ic_avatar).centerCrop().into(this.ivUserAvatar);

        String unitDistance = LocaleManager.isDisplayKM(getContext())
                ? LocaleManager.LocaleString.profile_fragment_statistic_item_total_distance
                : LocaleManager.LocaleString.profile_fragment_statistic_item_total_distance_mi;

        String distanceLabel = getContext().getString(R.string.activity_param_label_distance) + unitDistance;

        this.tvDistanceValue.setText(String.format("%.1f", dto.getTotalDistance()));
        this.tvDistanceLabel.setText(distanceLabel);
        this.tvAvgSpeed.setText(String.format("%.1f", dto.getVelocity()));
        this.tvMaxSpeed.setText(String.format("%.1f", dto.getMaxVelocity()));

        this.tvTime.setText(DateFormatUtil.dateFormat2StringHMS((long) dto.getElapsedTime()));
        this.tvDate.setText(dto.getTitle());

        if (dto.getUserId().equals(AVUser.getCurrentUser().getObjectId())) {
            ivEdit.setVisibility(View.VISIBLE);
        }
    }

    public void setOnSummaryItemClickListener(OnSummaryItemClickListener onSummaryItemClickListener) {
        this.onSummaryItemClickListener = onSummaryItemClickListener;
    }
}