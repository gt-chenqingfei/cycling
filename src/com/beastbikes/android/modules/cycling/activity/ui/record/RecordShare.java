package com.beastbikes.android.modules.cycling.activity.ui.record;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.utils.BitmapUtil;
import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenqingfei on 15/12/3.
 * <p>
 * 骑行报告分享临时生产的view
 */
public class RecordShare extends RecordBase<ActivityDTO> {

    public interface ShareBuildListener {
        void onShareBuild(Bitmap bitmap);
    }

    private TextView tvTitleDate;
    private TextView tvTitleTime;

    private TextView tvElevMaxValue;
    private TextView tvElevGainValue;
    private TextView tvElevDistanceValue;
    private TextView tvCalValue;
    private TextView tvCadenceValue;
    private TextView tvHeartRateValue;

    private TextView tvElevMaxUnit;
    private TextView tvElevGainUnit;
    private TextView tvElevDistanceUnit;
    private TextView tvCalUnit;
    private TextView tvCadenceUnit;
    private TextView tvHeartRateUnit;

    private ScrollView scrollView;
    private ImageView ivMap;
    private ImageView ivSummary;
    private DecimalFormat mDecimalFormat;

    public RecordShare(Context context) {
        super(context);
        mDecimalFormat = new DecimalFormat("0.0");
    }

    @Override
    public int getLayRes() {
        return R.layout.activity_cycling_completed_share_data_view;
    }

    @Override
    public void onCreateView() {
        super.onCreateView();
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/BebasNeue.otf");

        tvElevMaxValue = (TextView) findViewById(R.id.share_data_elev_max_value);
        tvElevGainValue = (TextView) findViewById(R.id.share_data_elev_gain_value);
        tvElevDistanceValue = (TextView) findViewById(R.id.share_data_elev_distance_value);
        tvCalValue = (TextView) findViewById(R.id.share_data_cal_value);
        tvCadenceValue = (TextView) findViewById(R.id.share_data_cadence_value);
        tvHeartRateValue = (TextView) findViewById(R.id.share_data_elev_heart_rate_value);

        tvElevMaxUnit = (TextView) findViewById(R.id.share_data_elev_max_unit);
        tvElevGainUnit = (TextView) findViewById(R.id.share_data_elev_gain_unit);
        tvElevDistanceUnit = (TextView) findViewById(R.id.share_data_elev_distance_unit);
        tvCalUnit = (TextView) findViewById(R.id.share_data_cal_unit);
        tvCadenceUnit = (TextView) findViewById(R.id.share_data_cadence_unit);
        tvHeartRateUnit = (TextView) findViewById(R.id.share_data_elev_heart_rate_unit);

        tvElevMaxValue.setTypeface(typeface);
        tvElevGainValue.setTypeface(typeface);
        tvElevDistanceValue.setTypeface(typeface);
        tvCalValue.setTypeface(typeface);
        tvCadenceValue.setTypeface(typeface);
        tvHeartRateValue.setTypeface(typeface);

        this.tvTitleDate = (TextView) findViewById(R.id.share_data_view_date);
        this.tvTitleTime = (TextView) findViewById(R.id.share_data_view_time);

        this.ivMap = (ImageView) findViewById(R.id.share_data_content_map);
        this.ivSummary = (ImageView) findViewById(R.id.share_data_content_summary);
        this.scrollView = (ScrollView) findViewById(R.id.cycling_completed_share_data_view);

    }

    public void build(final Bitmap bitmapSummary, final Bitmap bitmapMap,
                      ActivityDTO dto, final ShareBuildListener listener) {
        if (listener == null || dto == null) {
            Log.w("RecordShare", "build failed !!");
            return;
        }

        ChartDataProvider provider = ChartDataProvider.getInstatnce();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date startDate = new Date(dto.getStartTime());
        String time = sdf.format(startDate);

        sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(startDate);

        this.tvTitleDate.setText(date);
        this.tvTitleTime.setText(time);
        this.tvElevMaxValue.setText(mDecimalFormat.format(dto.getMaxAltitude()));
        this.tvElevGainValue.setText(mDecimalFormat.format(dto.getRiseTotal()));
        this.tvElevDistanceValue.setText(mDecimalFormat.format(dto.getUphillDistance()));
        this.tvCalValue.setText(mDecimalFormat.format(dto.getCalories()));
        this.tvCadenceValue.setText(mDecimalFormat.format(provider.getAverageCadence()));
        this.tvHeartRateValue.setText(mDecimalFormat.format(provider.getAverageHeartRate()));

        if (LocaleManager.isDisplayKM(mContext)) {
            String elevMaxUnit = mContext.getString(R.string.label_max_altitude) +
                    LocaleManager.LocaleString.profile_fragment_statistic_item_altitude_m;
            tvElevMaxUnit.setText(elevMaxUnit.toUpperCase());
            String elevGainUnit = mContext.getString(R.string.label_rise_distance) +
                    LocaleManager.LocaleString.profile_fragment_statistic_item_altitude_m;
            tvElevGainUnit.setText(elevGainUnit.toUpperCase());
            String elevDistanceUnit = mContext.getString(R.string.label_uphill_distance) +
                    LocaleManager.LocaleString.profile_fragment_statistic_item_altitude_m;
            tvElevDistanceUnit.setText(elevDistanceUnit.toUpperCase());
            String calUnit = mContext.getString(R.string.label_calorie) + "(" +
                    getContext().getString(R.string.label_calorie_unit) + ")";
            tvCalUnit.setText(calUnit.toUpperCase());
            String cadenceUnit = mContext.getString(R.string.label_average_cadence) + "(" +
                    getContext().getString(R.string.label_cadence_unit) + ")";
            tvCadenceUnit.setText(cadenceUnit.toUpperCase());
            String heartRateUnit = mContext.getString(R.string.label_heart_rate) +
                    getContext().getString(R.string.label_bpm);
            tvHeartRateUnit.setText(heartRateUnit.toUpperCase());
        } else {
            String elevMaxUnit = mContext.getString(R.string.label_max_altitude) +
                    LocaleManager.LocaleString.profile_fragment_statistic_item_altitude_feet;
            tvElevMaxUnit.setText(elevMaxUnit.toUpperCase());
            String elevGainUnit = mContext.getString(R.string.label_rise_distance) +
                    LocaleManager.LocaleString.profile_fragment_statistic_item_altitude_feet;
            tvElevGainUnit.setText(elevGainUnit.toUpperCase());
            String elevDistanceUnit = mContext.getString(R.string.label_uphill_distance) +
                    LocaleManager.LocaleString.profile_fragment_statistic_item_altitude_feet;
            tvElevDistanceUnit.setText(elevDistanceUnit.toUpperCase());
            String calUnit = mContext.getString(R.string.label_calorie) + "(" +
                    getContext().getString(R.string.label_calorie_unit) + ")";
            tvCalUnit.setText(calUnit.toUpperCase());
            String cadenceUnit = mContext.getString(R.string.label_average_cadence) + "(" +
                    getContext().getString(R.string.label_cadence_unit) + ")";
            tvCadenceUnit.setText(cadenceUnit.toUpperCase());
            String heartRateUnit = mContext.getString(R.string.label_heart_rate) +
                    getContext().getString(R.string.label_bpm);
            tvHeartRateUnit.setText(heartRateUnit);
        }

        this.ivMap.setImageBitmap(bitmapMap);
        this.ivSummary.setImageBitmap(bitmapSummary);

        this.scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {

                Bitmap bitmapMain = BitmapUtil.getBitmapByView(scrollView);
                listener.onShareBuild(bitmapMain);

                ivMap.setImageBitmap(null);
                ivSummary.setImageBitmap(null);

                if (bitmapMap != null && !bitmapMap.isRecycled()) {
                    bitmapMap.recycle();
                }

                if (bitmapSummary != null && !bitmapSummary.isRecycled()) {
                    bitmapSummary.recycle();
                }

                if (bitmapMain != null && !bitmapMain.isRecycled()) {
                    bitmapMain.recycle();
                }
                System.gc();
            }
        }, 10);
    }


}