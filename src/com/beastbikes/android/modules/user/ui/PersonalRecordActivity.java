package com.beastbikes.android.modules.user.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.UserStatisticDTO;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;

@Alias("我的成绩")
@LayoutResource(R.layout.personal_record_activity)
public class PersonalRecordActivity extends SessionFragmentActivity {

    public static final String EXTRA_USER_ID = "user_id";

    @IdResource(R.id.personal_record_activity_header)
    private ViewGroup grpHeader;

    @IdResource(R.id.personal_record_activity_header_weekly_elapsed_time)
    private ViewGroup grpHeaderWeeklyElapsedTime;
    private TextView txtHeaderWeeklyElapsedTimeValue;
    private TextView txtHeaderWeeklyElapsedTimeUnit;

    @IdResource(R.id.personal_record_activity_header_weekly_distance)
    private ViewGroup grpHeaderWeeklyDistance;
    private TextView txtHeaderWeeklyDistanceValue;
    private TextView txtHeaderWeeklyDistanceUnit;

    @IdResource(R.id.personal_record_activity_header_weekly_activity_count)
    private ViewGroup grpHeaderWeeklyActivityCount;
    private TextView txtHeaderWeeklyActivityCountValue;
    private TextView txtHeaderWeeklyActivityCountUnit;

    @IdResource(R.id.personal_record_activity_header_weekly_calories)
    private ViewGroup grpHeaderWeeklyCalories;
    private TextView txtHeaderWeeklyCaloriesValue;
    private TextView txtHeaderWeeklyCaloriesUnit;

    @IdResource(R.id.personal_record_activity_statistic_weekly_distance)
    private ViewGroup grpWeeklyDistance;
    private TextView txtWeeklyDistanceLabel;
    private TextView txtWeeklyDistanceValue;
    private TextView txtWeeklyDistanceUnit;

    @IdResource(R.id.personal_record_activity_statistic_weekly_elapsed_time)
    private ViewGroup grpWeeklyElapsedTime;
    private TextView txtWeeklyElapsedTimeLabel;
    private TextView txtWeeklyElapsedTimeValue;
    private TextView txtWeeklyElapsedTimeUnit;

    @IdResource(R.id.personal_record_activity_statistic_weekly_calories)
    private ViewGroup grpWeeklyCalories;
    private TextView txtWeeklyCaloriesLabel;
    private TextView txtWeeklyCaloriesValue;
    private TextView txtWeeklyCaloriesUnit;

    @IdResource(R.id.personal_record_activity_statistic_weekly_risen_distance)
    private ViewGroup grpWeeklyRisenDistance;
    private TextView txtWeeklyRisenDistanceLabel;
    private TextView txtWeeklyRisenDistanceValue;
    private TextView txtWeeklyRisenDistanceUnit;

    @IdResource(R.id.personal_record_activity_statistic_weekly_activity_count)
    private ViewGroup grpWeeklyActivityCount;
    private TextView txtWeeklyActivityCountLabel;
    private TextView txtWeeklyActivityCountValue;
    private TextView txtWeeklyActivityCountUnit;

    @IdResource(R.id.personal_record_activity_statistic_monthly_distance)
    private ViewGroup grpMonthlyDistance;
    private TextView txtMonthlyDistanceLabel;
    private TextView txtMonthlyDistanceValue;
    private TextView txtMonthlyDistanceUnit;

    @IdResource(R.id.personal_record_activity_statistic_monthly_elapsed_time)
    private ViewGroup grpMonthlyElapsedTime;
    private TextView txtMonthlyElapsedTimeLabel;
    private TextView txtMonthlyElapsedTimeValue;
    private TextView txtMonthlyElapsedTimeUnit;

    @IdResource(R.id.personal_record_activity_statistic_monthly_calories)
    private ViewGroup grpMonthlyCalories;
    private TextView txtMonthlyCaloriesLabel;
    private TextView txtMonthlyCaloriesValue;
    private TextView txtMonthlyCaloriesUnit;

    @IdResource(R.id.personal_record_activity_statistic_monthly_risen_distance)
    private ViewGroup grpMonthlyRisenDistance;
    private TextView txtMonthlyRisenDistanceLabel;
    private TextView txtMonthlyRisenDistanceValue;
    private TextView txtMonthlyRisenDistanceUnit;

    @IdResource(R.id.personal_record_activity_statistic_monthly_activity_count)
    private ViewGroup grpMonthlyActivityCount;
    private TextView txtMonthlyActivityCountLabel;
    private TextView txtMonthlyActivityCountValue;
    private TextView txtMonthlyActivityCountUnit;

    @IdResource(R.id.personal_record_activity_statistic_average_velocity)
    private ViewGroup grpAvgVelocity;
    private TextView txtAvgVelocityLabel;
    private TextView txtAvgVelocityValue;
    private TextView txtAvgVelocityUnit;

    @IdResource(R.id.personal_record_activity_statistic_max_distance)
    private ViewGroup grpMaxDistance;
    private TextView txtMaxDistanceLabel;
    private TextView txtMaxDistanceValue;
    private TextView txtMaxDistanceUnit;

    @IdResource(R.id.personal_record_activity_statistic_max_elapsed_time)
    private ViewGroup grpMaxElapsedTime;
    private TextView txtMaxElapsedTimeLabel;
    private TextView txtMaxElapsedTimeValue;
    private TextView txtMaxElapsedTimeUnit;

    @IdResource(R.id.personal_record_activity_statistic_max_velocity)
    private ViewGroup grpMaxVelocity;
    private TextView txtMaxVelocityLabel;
    private TextView txtMaxVelocityValue;
    private TextView txtMaxVelocityUnit;

    @IdResource(R.id.personal_record_activity_statistic_max_calories)
    private ViewGroup grpMaxCalories;
    private TextView txtMaxCaloriesLabel;
    private TextView txtMaxCaloriesValue;
    private TextView txtMaxCaloriesUnit;

    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (null == intent)
            return;

        String userId = intent.getStringExtra(EXTRA_USER_ID);
        AVUser user = AVUser.getCurrentUser();
        if (null != user && !userId.equals(user.getObjectId())) {
            setTitle(R.string.personal_record_activity_other_title);
        }

        this.userManager = new UserManager(this);

        this.txtHeaderWeeklyActivityCountValue = (TextView) this.grpHeaderWeeklyActivityCount
                .findViewById(R.id.personal_record_activity_header_item_value);
        this.txtHeaderWeeklyActivityCountValue
                .setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.ic_personal_activity_count, 0, 0);
        this.txtHeaderWeeklyActivityCountUnit = (TextView) this.grpHeaderWeeklyActivityCount
                .findViewById(R.id.personal_record_activity_header_item_unit);
        this.txtHeaderWeeklyActivityCountUnit
                .setText(R.string.activity_param_label_count_unit);

        this.txtHeaderWeeklyCaloriesValue = (TextView) this.grpHeaderWeeklyCalories
                .findViewById(R.id.personal_record_activity_header_item_value);
        this.txtHeaderWeeklyCaloriesValue
                .setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.ic_personal_activity_calorie, 0, 0);
        this.txtHeaderWeeklyCaloriesUnit = (TextView) this.grpHeaderWeeklyCalories
                .findViewById(R.id.personal_record_activity_header_item_unit);
        this.txtHeaderWeeklyCaloriesUnit
                .setText(R.string.activity_param_label_calorie_unit);

        this.txtHeaderWeeklyDistanceValue = (TextView) this.grpHeaderWeeklyDistance
                .findViewById(R.id.personal_record_activity_header_item_value);
        this.txtHeaderWeeklyDistanceValue
                .setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.ic_personal_activity_distance, 0, 0);
        this.txtHeaderWeeklyDistanceUnit = (TextView) this.grpHeaderWeeklyDistance
                .findViewById(R.id.personal_record_activity_header_item_unit);

        this.txtHeaderWeeklyElapsedTimeValue = (TextView) this.grpHeaderWeeklyElapsedTime
                .findViewById(R.id.personal_record_activity_header_item_value);
        this.txtHeaderWeeklyElapsedTimeValue
                .setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.ic_personal_activity_elapsed_time, 0, 0);
        this.txtHeaderWeeklyElapsedTimeUnit = (TextView) this.grpHeaderWeeklyElapsedTime
                .findViewById(R.id.personal_record_activity_header_item_unit);
        this.txtHeaderWeeklyElapsedTimeUnit
                .setText(R.string.activity_param_label_elapsed_time_unit);

        this.txtWeeklyActivityCountLabel = (TextView) this.grpWeeklyActivityCount
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtWeeklyActivityCountLabel
                .setText(R.string.personal_record_activity_weekly_activity_count);
        this.txtWeeklyActivityCountValue = (TextView) this.grpWeeklyActivityCount
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtWeeklyActivityCountUnit = (TextView) this.grpWeeklyActivityCount
                .findViewById(R.id.personal_record_activity_statistic_item_unit);
        // this.txtWeeklyActivityCountUnit.setText(R.string.activity_param_label_count_unit);

        this.txtWeeklyCaloriesLabel = (TextView) this.grpWeeklyCalories
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtWeeklyCaloriesLabel
                .setText(R.string.personal_record_activity_weekly_calories);
        this.txtWeeklyCaloriesValue = (TextView) this.grpWeeklyCalories
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtWeeklyCaloriesUnit = (TextView) this.grpWeeklyCalories
                .findViewById(R.id.personal_record_activity_statistic_item_unit);
        this.txtWeeklyCaloriesUnit
                .setText(R.string.activity_param_label_calorie_unit);

        this.txtWeeklyDistanceLabel = (TextView) this.grpWeeklyDistance
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtWeeklyDistanceLabel
                .setText(R.string.personal_record_activity_weekly_distance);
        this.txtWeeklyDistanceValue = (TextView) this.grpWeeklyDistance
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtWeeklyDistanceUnit = (TextView) this.grpWeeklyDistance
                .findViewById(R.id.personal_record_activity_statistic_item_unit);

        this.txtWeeklyElapsedTimeLabel = (TextView) this.grpWeeklyElapsedTime
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtWeeklyElapsedTimeLabel
                .setText(R.string.personal_record_activity_weekly_elapsed_time);
        this.txtWeeklyElapsedTimeValue = (TextView) this.grpWeeklyElapsedTime
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtWeeklyElapsedTimeUnit = (TextView) this.grpWeeklyElapsedTime
                .findViewById(R.id.personal_record_activity_statistic_item_unit);
        // this.txtWeeklyElapsedTimeUnit.setText(R.string.activity_param_label_elapsed_time_unit);

        this.txtWeeklyRisenDistanceLabel = (TextView) this.grpWeeklyRisenDistance
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtWeeklyRisenDistanceLabel
                .setText(R.string.personal_record_activity_weekly_risen_distance);
        this.txtWeeklyRisenDistanceValue = (TextView) this.grpWeeklyRisenDistance
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtWeeklyRisenDistanceUnit = (TextView) this.grpWeeklyRisenDistance
                .findViewById(R.id.personal_record_activity_statistic_item_unit);

        this.txtMonthlyActivityCountLabel = (TextView) this.grpMonthlyActivityCount
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtMonthlyActivityCountLabel
                .setText(R.string.personal_record_activity_monthly_activity_count);
        this.txtMonthlyActivityCountValue = (TextView) this.grpMonthlyActivityCount
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtMonthlyActivityCountUnit = (TextView) this.grpMonthlyActivityCount
                .findViewById(R.id.personal_record_activity_statistic_item_unit);
        // this.txtMonthlyActivityCountUnit.setText(R.string.activity_param_label_count_unit);

        this.txtMonthlyCaloriesLabel = (TextView) this.grpMonthlyCalories
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtMonthlyCaloriesLabel
                .setText(R.string.personal_record_activity_monthly_calories);
        this.txtMonthlyCaloriesValue = (TextView) this.grpMonthlyCalories
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtMonthlyCaloriesUnit = (TextView) this.grpMonthlyCalories
                .findViewById(R.id.personal_record_activity_statistic_item_unit);
        this.txtMonthlyCaloriesUnit
                .setText(R.string.activity_param_label_calorie_unit);

        this.txtMonthlyDistanceLabel = (TextView) this.grpMonthlyDistance
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtMonthlyDistanceLabel
                .setText(R.string.personal_record_activity_monthly_distance);
        this.txtMonthlyDistanceValue = (TextView) this.grpMonthlyDistance
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtMonthlyDistanceUnit = (TextView) this.grpMonthlyDistance
                .findViewById(R.id.personal_record_activity_statistic_item_unit);

        this.txtMonthlyElapsedTimeLabel = (TextView) this.grpMonthlyElapsedTime
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtMonthlyElapsedTimeLabel
                .setText(R.string.personal_record_activity_monthly_elapsed_time);
        this.txtMonthlyElapsedTimeValue = (TextView) this.grpMonthlyElapsedTime
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtMonthlyElapsedTimeUnit = (TextView) this.grpMonthlyElapsedTime
                .findViewById(R.id.personal_record_activity_statistic_item_unit);
        // this.txtMonthlyElapsedTimeUnit.setText(R.string.activity_param_label_elapsed_time_unit);

        this.txtMonthlyRisenDistanceLabel = (TextView) this.grpMonthlyRisenDistance
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtMonthlyRisenDistanceLabel
                .setText(R.string.personal_record_activity_monthly_risen_distance);
        this.txtMonthlyRisenDistanceValue = (TextView) this.grpMonthlyRisenDistance
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtMonthlyRisenDistanceUnit = (TextView) this.grpMonthlyRisenDistance
                .findViewById(R.id.personal_record_activity_statistic_item_unit);

        this.txtAvgVelocityLabel = (TextView) this.grpAvgVelocity
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtAvgVelocityLabel
                .setText(R.string.personal_record_activity_avg_velocity);
        this.txtAvgVelocityValue = (TextView) this.grpAvgVelocity
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtAvgVelocityUnit = (TextView) this.grpAvgVelocity
                .findViewById(R.id.personal_record_activity_statistic_item_unit);

        this.txtMaxVelocityLabel = (TextView) this.grpMaxVelocity
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtMaxVelocityLabel
                .setText(R.string.personal_record_activity_max_velocity);
        this.txtMaxVelocityValue = (TextView) this.grpMaxVelocity
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtMaxVelocityUnit = (TextView) this.grpMaxVelocity
                .findViewById(R.id.personal_record_activity_statistic_item_unit);

        this.txtMaxCaloriesLabel = (TextView) this.grpMaxCalories
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtMaxCaloriesLabel
                .setText(R.string.personal_record_activity_max_calories);
        this.txtMaxCaloriesValue = (TextView) this.grpMaxCalories
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtMaxCaloriesUnit = (TextView) this.grpMaxCalories
                .findViewById(R.id.personal_record_activity_statistic_item_unit);
        this.txtMaxCaloriesUnit
                .setText(R.string.activity_param_label_calorie_unit);

        this.txtMaxDistanceLabel = (TextView) this.grpMaxDistance
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtMaxDistanceLabel
                .setText(R.string.personal_record_activity_max_distance);
        this.txtMaxDistanceValue = (TextView) this.grpMaxDistance
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtMaxDistanceUnit = (TextView) this.grpMaxDistance
                .findViewById(R.id.personal_record_activity_statistic_item_unit);

        this.txtMaxElapsedTimeLabel = (TextView) this.grpMaxElapsedTime
                .findViewById(R.id.personal_record_activity_statistic_item_label);
        this.txtMaxElapsedTimeLabel
                .setText(R.string.personal_record_activity_max_elapsed_time);
        this.txtMaxElapsedTimeValue = (TextView) this.grpMaxElapsedTime
                .findViewById(R.id.personal_record_activity_statistic_item_value);
        this.txtMaxElapsedTimeUnit = (TextView) this.grpMaxElapsedTime
                .findViewById(R.id.personal_record_activity_statistic_item_unit);
        // this.txtMaxElapsedTimeUnit.setText(R.string.activity_param_label_elapsed_time_unit);
        this.initLocale();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, UserStatisticDTO>() {

                    @Override
                    protected UserStatisticDTO doInBackground(String... params) {
                        final String userId = params[0];
                        try {
                            return userManager
                                    .getUserStatisticDataByUserId(userId);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(UserStatisticDTO dto) {
                        if (null == dto)
                            return;

                        updateUI(dto);
                    }

                }, getUserId());
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initLocale() {
        if (LocaleManager.isDisplayKM(PersonalRecordActivity.this)) {
            this.txtHeaderWeeklyDistanceUnit.setText(R.string.activity_param_label_distance_unit);
            this.txtWeeklyDistanceUnit.setText(R.string.activity_param_label_distance_unit);
            this.txtWeeklyRisenDistanceUnit.setText(R.string.label_uphill_distance_unit);
            this.txtMonthlyDistanceUnit.setText(R.string.activity_param_label_distance_unit);
            this.txtMonthlyRisenDistanceUnit.setText(R.string.label_uphill_distance_unit);
            this.txtMaxDistanceUnit.setText(R.string.activity_param_label_distance_unit);
            this.txtMaxVelocityUnit.setText(R.string.activity_param_label_velocity_unit);
            this.txtAvgVelocityUnit.setText(R.string.activity_param_label_velocity_unit);
        } else {
            txtHeaderWeeklyDistanceUnit.setText(LocaleManager.LocaleString.activity_param_label_distance_unit);
            txtWeeklyDistanceUnit.setText(LocaleManager.LocaleString.activity_param_label_distance_unit);
            txtWeeklyRisenDistanceUnit.setText(LocaleManager.LocaleString.activity_param_label_distance_unit);
            txtMonthlyDistanceUnit.setText(LocaleManager.LocaleString.activity_param_label_distance_unit);
            txtMonthlyRisenDistanceUnit.setText(LocaleManager.LocaleString.activity_param_label_distance_unit);
            txtMaxDistanceUnit.setText(LocaleManager.LocaleString.activity_param_label_distance_unit);
            txtMaxVelocityUnit.setText(LocaleManager.LocaleString.activity_param_label_distance_unit);
            txtAvgVelocityUnit.setText(LocaleManager.LocaleString.activity_param_label_distance_unit);
        }
    }

    private void updateUI(UserStatisticDTO dto) {
        final long wh, wm, ws, mh, mm, ms, th, tm, ts, yh, ym, ys;
        final long wet = dto.getWeeklyElapsedTime();
        final long met = dto.getMonthlyElapsedTime();
        final long yet = dto.getTotalMaxElapsedTime();
        final long tet = dto.getTotalTime();

        if (wet <= 0) {
            wh = wm = ws = 0;
        } else {
            wh = wet / 3600;
            wm = wet % 3600 / 60;
            ws = wet % 3600 % 60;
        }

        if (met <= 0) {
            mh = mm = ms = 0;
        } else {
            mh = met / 3600;
            mm = met % 3600 / 60;
            ms = met % 3600 % 60;
        }

        if (tet <= 0) {
            th = tm = ts = 0;
        } else {
            th = tet / 3600;
            tm = tet % 3600 / 60;
            ts = tet % 3600 % 60;
        }

        if (yet <= 0) {
            yh = ym = ys = 0;
        } else {
            yh = yet / 3600;
            ym = yet % 3600 / 60;
            ys = yet % 3600 % 60;
        }

        this.txtHeaderWeeklyActivityCountValue.setText(String.format("%d",
                dto.getTotalCount()));
        this.txtHeaderWeeklyCaloriesValue.setText(String.format("%d",
                Math.round(dto.getTotalCalories())));
        if (LocaleManager.isDisplayKM(PersonalRecordActivity.this)) {
            this.txtHeaderWeeklyDistanceValue.setText(String.format("%d",
                    Math.round(dto.getTotalDistance() / 1000f)));
            this.txtWeeklyDistanceValue.setText(String.format("%.1f",
                    dto.getWeeklyDistance() / 1000f));
            this.txtWeeklyRisenDistanceValue.setText(String.format("%.1f",
                    dto.getWeeklyRisenDistance()));
            this.txtMonthlyDistanceValue.setText(String.format("%.1f",
                    dto.getMonthlyDistance() / 1000f));
            this.txtMonthlyRisenDistanceValue.setText(String.format("%.1f",
                    dto.getMonthlyRisenDistance()));
            this.txtMaxDistanceValue.setText(String.format("%.1f",
                    dto.getTotalMaxDistance() / 1000f));
            this.txtMaxVelocityValue.setText(String.format("%.1f",
                    dto.getTotalMaxVelocity()));
            this.txtAvgVelocityValue.setText(String.format("%.1f",
                    dto.getTotalAverageVelocity()));
        } else {
            this.txtHeaderWeeklyDistanceValue.setText(String.format("%d",
                    Math.round(LocaleManager.kilometreToMile(dto.getTotalDistance() / 1000f))));
            this.txtWeeklyDistanceValue.setText(String.format("%.1f",
                    LocaleManager.kilometreToMile(dto.getWeeklyDistance() / 1000f)));
            this.txtWeeklyRisenDistanceValue.setText(String.format("%.1f",
                    LocaleManager.kilometreToMile(dto.getWeeklyRisenDistance())));
            this.txtMonthlyDistanceValue.setText(String.format("%.1f",
                    LocaleManager.kilometreToMile(dto.getMonthlyDistance() / 1000f)));
            this.txtMonthlyRisenDistanceValue.setText(String.format("%.1f",
                    LocaleManager.kilometreToMile(dto.getMonthlyRisenDistance() )));
            this.txtMaxDistanceValue.setText(String.format("%.1f",
                    LocaleManager.kilometreToMile(dto.getTotalMaxDistance() / 1000f)));
            this.txtMaxVelocityValue.setText(String.format("%.1f",
                    LocaleManager.kphToMph(dto.getTotalMaxVelocity())));
            this.txtAvgVelocityValue.setText(String.format("%.1f",
                    LocaleManager.kphToMph(dto.getTotalAverageVelocity())));
        }
        if (yet == 0) {
            this.txtHeaderWeeklyElapsedTimeValue.setText(String.format(
                    "%02d:%02d", tm, ts));
        } else {
            this.txtHeaderWeeklyElapsedTimeValue.setText(String.format(
                    "%02d:%02d", th, tm));
        }


        // this.txtWeeklyElapsedTimeValue.setText(String.format("%02d:%02d", wh,
        // wm));
        if (wh == 0) {
            this.txtWeeklyElapsedTimeValue.setText(String.format("%02d:%02d",
                    wm, ws));
        } else {
            this.txtWeeklyElapsedTimeValue.setText(String.format(
                    "%02d:%02d:%02d", wh, wm, ws));
        }

        // this.txtMonthlyElapsedTimeValue.setText(String.format("%02d:%02d",
        // mh, mm));
        if (mh == 0) {
            this.txtMonthlyElapsedTimeValue.setText(String.format("%02d:%02d",
                    mm, ms));
        } else {
            this.txtMonthlyElapsedTimeValue.setText(String.format(
                    "%02d:%02d:%02d", mh, mm, ms));
        }

        // this.txtMaxElapsedTimeValue.setText(String.format("%02d:%02d", th,
        // tm));
        if (th == 0) {
            this.txtMaxElapsedTimeValue.setText(String.format("%02d:%02d", ym,
                    ys));
        } else {
            this.txtMaxElapsedTimeValue.setText(String.format("%02d:%02d:%02d",
                    yh, ym, ys));
        }
        this.txtWeeklyCaloriesValue.setText(String.format("%.1f",
                dto.getWeeklyCalories()));
        this.txtWeeklyActivityCountValue.setText(String.format("%d",
                dto.getWeeklyActivityCount()));

        this.txtMonthlyCaloriesValue.setText(String.format("%.1f",
                dto.getMonthlyCalories()));

        this.txtMonthlyActivityCountValue.setText(String.format("%d",
                dto.getMonthlyActivityCount()));


        this.txtMaxCaloriesValue.setText(String.format("%.1f",
                dto.getTotalMaxCalories()));
    }
}
