package com.beastbikes.android.modules.cycling.sections.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.android.R;
import com.beastbikes.framework.android.res.annotation.MenuResource;

/**
 * Created by caoxiao on 16/4/5.
 */

@MenuResource(R.menu.activity_section_filters_menu)
@LayoutResource(R.layout.activity_section_filters)
public class SectionFiltersActivity extends SessionFragmentActivity implements View.OnClickListener {

    @IdResource(R.id.item_section_filter_difficulty1)
    private ViewGroup filterDifficultyVG1;
    private TextView filterDifficultyTV1;

    @IdResource(R.id.item_section_filter_difficulty2)
    private ViewGroup filterDifficultyVG2;
    private TextView filterDifficultyTV2;

    @IdResource(R.id.item_section_filter_difficulty3)
    private ViewGroup filterDifficultyVG3;
    private TextView filterDifficultyTV3;

    @IdResource(R.id.item_section_filter_difficulty4)
    private ViewGroup filterDifficultyVG4;
    private TextView filterDifficultyTV4;

    @IdResource(R.id.item_section_filter_difficulty5)
    private ViewGroup filterDifficultyVG5;
    private TextView filterDifficultyTV5;

    @IdResource(R.id.item_section_filter_difficulty6)
    private ViewGroup filterDifficultyVG6;
    private TextView filterDifficultyTV6;

    @IdResource(R.id.layout_activity_section_filter_seekbar1)
    private ViewGroup seekbarVG1;
    private TextView seekbarTitle1;
    private TextView seekbarUnit1;
    private TextView seekbarValue1;
    private ImageView seekbarIcon1;
    private TextView seekbarGraduation1;
    private SeekBar seekbar1;

    @IdResource(R.id.layout_activity_section_filter_seekbar2)
    private ViewGroup seekbarVG2;
    private TextView seekbarTitle2;
    private TextView seekbarUnit2;
    private TextView seekbarValue2;
    private ImageView seekbarIcon2;
    private TextView seekbarGraduation2;
    private SeekBar seekbar2;

    @IdResource(R.id.layout_activity_section_filter_seekbar3)
    private ViewGroup seekbarVG3;
    private TextView seekbarTitle3;
    private TextView seekbarUnit3;
    private TextView seekbarValue3;
    private ImageView seekbarIcon3;
    private TextView seekbarGraduation3;
    private SeekBar seekbar3;
    private SeekBarChangeListener1 seekBarChangeListener1;
    private SeekBarChangeListener2 seekBarChangeListener2;
    private SeekBarChangeListener3 seekBarChangeListener3;

    @IdResource(R.id.layout_activity_section_filter_submit)
    private TextView filterSubmit;

    public static final int EXTRA_FILTER_RESULT_CODE = 99;
    public static final String SECTION_DISTANCE = "section_distance";
    public static final String SECTION_ALTDIFF = "section_altdiff";
    public static final String SECTION_SLOPE = "section_slope";
    public static final String SECTION_DIFFICULT = "section_difficult";

    private String difficult = "";//enumerate value of {1, 2, 3, 4, 5}, 多选值用 , 隔开 exp: 1,3,4

    //超过500，上方显示500+,500内分段0-100-200-300-400-500
    private String distance = "";//最小里程和最大里程之间用 , 隔开 exp: 100,200 unit: km

    //超过300，上方显示300+,300内分段0-60-120-180-240-300
    private String altDiff = "";//海拔差 最小海拔差和最大海拔差之间用 , 隔开 exp: 0,500 unit: m

    //超过20，上方显示20+,20内分段0-5-10-15-20
    private String slope = "";//坡度 最小坡度和最大坡度之间用 , 隔开 exp: 0,20 unit: °

    private boolean isShowKilometre = true;

    private ActionBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        isShowKilometre = LocaleManager.isDisplayKM(this);
        initView();
    }

    private void initView() {
        filterDifficultyTV1 = (TextView) filterDifficultyVG1.findViewById(R.id.item_section_filter_difficulty_tv);
        filterDifficultyTV2 = (TextView) filterDifficultyVG2.findViewById(R.id.item_section_filter_difficulty_tv);
        filterDifficultyTV3 = (TextView) filterDifficultyVG3.findViewById(R.id.item_section_filter_difficulty_tv);
        filterDifficultyTV4 = (TextView) filterDifficultyVG4.findViewById(R.id.item_section_filter_difficulty_tv);
        filterDifficultyTV5 = (TextView) filterDifficultyVG5.findViewById(R.id.item_section_filter_difficulty_tv);
        filterDifficultyTV6 = (TextView) filterDifficultyVG6.findViewById(R.id.item_section_filter_difficulty_tv);
        filterDifficultyTV1.setText(getResources().getString(R.string.speedx_one_star));
        filterDifficultyTV2.setText(getResources().getString(R.string.speedx_two_star));
        filterDifficultyTV3.setText(getResources().getString(R.string.speedx_three_star));
        filterDifficultyTV4.setText(getResources().getString(R.string.speedx_four_star));
        filterDifficultyTV5.setText(getResources().getString(R.string.speedx_five_star));
        filterDifficultyTV6.setText(getResources().getString(R.string.speedx_six_star));

        filterDifficultyTV1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllDifficultyNormal();
                filterDifficultyTV1.setSelected(true);
                filterDifficultyTV1.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                difficult = "1";

            }
        });
        filterDifficultyTV2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllDifficultyNormal();
                filterDifficultyTV2.setSelected(true);
                filterDifficultyTV2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                difficult = "2";
            }
        });
        filterDifficultyTV3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllDifficultyNormal();
                filterDifficultyTV3.setSelected(true);
                filterDifficultyTV3.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                difficult = "3";
            }
        });
        filterDifficultyTV4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllDifficultyNormal();
                filterDifficultyTV4.setSelected(true);
                filterDifficultyTV4.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                difficult = "4";
            }
        });
        filterDifficultyTV5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllDifficultyNormal();
                filterDifficultyTV5.setSelected(true);
                filterDifficultyTV5.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                difficult = "5";
            }
        });

        filterDifficultyVG6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAllDifficultyNormal();
                filterDifficultyTV6.setSelected(true);
                filterDifficultyTV6.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                difficult = "6";
            }
        });

        seekbarTitle1 = (TextView) seekbarVG1.findViewById(R.id.activity_section_filters_seekbar_title);
        seekbarTitle2 = (TextView) seekbarVG2.findViewById(R.id.activity_section_filters_seekbar_title);
        seekbarTitle3 = (TextView) seekbarVG3.findViewById(R.id.activity_section_filters_seekbar_title);
        seekbarUnit1 = (TextView) seekbarVG1.findViewById(R.id.activity_section_filters_seekbar_unit);
        seekbarUnit2 = (TextView) seekbarVG2.findViewById(R.id.activity_section_filters_seekbar_unit);
        seekbarUnit3 = (TextView) seekbarVG3.findViewById(R.id.activity_section_filters_seekbar_unit);
        seekbarValue1 = (TextView) seekbarVG1.findViewById(R.id.activity_section_filters_seekbar_value);
        seekbarValue2 = (TextView) seekbarVG2.findViewById(R.id.activity_section_filters_seekbar_value);
        seekbarValue3 = (TextView) seekbarVG3.findViewById(R.id.activity_section_filters_seekbar_value);
        seekbarIcon1 = (ImageView) seekbarVG1.findViewById(R.id.activity_section_filters_seekbar_icon);
        seekbarIcon2 = (ImageView) seekbarVG2.findViewById(R.id.activity_section_filters_seekbar_icon);
        seekbarIcon3 = (ImageView) seekbarVG3.findViewById(R.id.activity_section_filters_seekbar_icon);
        seekbarGraduation1 = (TextView) seekbarVG1.findViewById(R.id.section_filter_seekbar_graduation);
        seekbarGraduation2 = (TextView) seekbarVG2.findViewById(R.id.section_filter_seekbar_graduation);
        seekbarGraduation3 = (TextView) seekbarVG3.findViewById(R.id.section_filter_seekbar_graduation);
        seekbar1 = (SeekBar) seekbarVG1.findViewById(R.id.section_filter_seekbar);
        seekbar2 = (SeekBar) seekbarVG2.findViewById(R.id.section_filter_seekbar);
        seekbar3 = (SeekBar) seekbarVG3.findViewById(R.id.section_filter_seekbar);

        seekbarTitle1.setText(getResources().getString(R.string.activity_data_distance_label));
        seekbarTitle2.setText(getResources().getString(R.string.altitude_difference));
        seekbarTitle3.setText(getResources().getString(R.string.speedx_slopes));
        seekbarUnit1.setText(getResources().getString(R.string.kilometre));
        seekbarUnit2.setText(getResources().getString(R.string.metre));
        seekbarUnit3.setText(getResources().getString(R.string.degree));
        seekbarGraduation1.setText("500");
        seekbarGraduation2.setText("300");
        seekbarGraduation3.setText("20");
        seekbarIcon1.setImageResource(R.drawable.ic_section_filter_distance);
        seekbarIcon2.setImageResource(R.drawable.ic_section_filter_altitude_difference);
        seekbarIcon3.setImageResource(R.drawable.ic_section_filter_slopes);

        seekBarChangeListener1 = new SeekBarChangeListener1();
        seekBarChangeListener2 = new SeekBarChangeListener2();
        seekBarChangeListener3 = new SeekBarChangeListener3();

        seekbar1.setOnSeekBarChangeListener(seekBarChangeListener1);
        seekbar2.setOnSeekBarChangeListener(seekBarChangeListener2);
        seekbar3.setOnSeekBarChangeListener(seekBarChangeListener3);

        filterSubmit.setOnClickListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_activity_section_filter_submit:
                Intent intent = getIntent();
                intent.putExtra(SECTION_DIFFICULT, difficult);
                intent.putExtra(SECTION_DISTANCE, distance);
                intent.putExtra(SECTION_ALTDIFF, altDiff);
                intent.putExtra(SECTION_SLOPE, slope);
                this.setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.activity_section_filters_reset) {
            seekbar1.setProgress(0);
            seekbar2.setProgress(0);
            seekbar3.setProgress(0);
            setAllDifficultyNormal();
            difficult = "";
            distance = "";
            altDiff = "";
            slope = "";
            Intent intent = getIntent();
            intent.putExtra(SECTION_DIFFICULT, difficult);
            intent.putExtra(SECTION_DISTANCE, distance);
            intent.putExtra(SECTION_ALTDIFF, altDiff);
            intent.putExtra(SECTION_SLOPE, slope);
            this.setResult(RESULT_OK, intent);
            finish();
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void setAllDifficultyNormal() {
        filterDifficultyTV1.setSelected(false);
        filterDifficultyTV2.setSelected(false);
        filterDifficultyTV3.setSelected(false);
        filterDifficultyTV4.setSelected(false);
        filterDifficultyTV5.setSelected(false);
        filterDifficultyTV6.setSelected(false);
        filterDifficultyTV1.setTextColor(getResources().getColor(R.color.text_default));
        filterDifficultyTV2.setTextColor(getResources().getColor(R.color.text_default));
        filterDifficultyTV3.setTextColor(getResources().getColor(R.color.text_default));
        filterDifficultyTV4.setTextColor(getResources().getColor(R.color.text_default));
        filterDifficultyTV5.setTextColor(getResources().getColor(R.color.text_default));
        filterDifficultyTV6.setTextColor(getResources().getColor(R.color.text_default));
    }

    class SeekBarChangeListener1 implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            double index = i * 6.25;
            if (index > 500) {
                seekbarValue1.setText("500+");
            } else {
                seekbarValue1.setText("" + i * 625 / 100);
            }
            switch ((int) index / 100) {
                case 5:
                    if (isShowKilometre) {
                        distance = "500,1000";
                    } else {
                        distance = "804,1609";
                    }
                    break;
                case 4:
                    if (isShowKilometre) {
                        distance = "400,500";
                    } else {
                        distance = "643,804";
                    }
                    break;
                case 3:
                    if (isShowKilometre) {
                        distance = "300,400";
                    } else {
                        distance = "482,643";
                    }
                    break;
                case 2:
                    if (isShowKilometre) {
                        distance = "200,300";
                    } else {
                        distance = "321,482";
                    }
                    break;
                case 1:
                    if (isShowKilometre) {
                        distance = "100,200";
                    } else {
                        distance = "160,321";
                    }
                    break;
                case 0:
                    if (isShowKilometre) {
                        distance = "0,100";
                    } else {
                        distance = "0,160";
                    }
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    class SeekBarChangeListener2 implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            double index = i * 3.75;
            if (index > 300) {
                seekbarValue2.setText("300+");
            } else {
                seekbarValue2.setText("" + i * 375 / 100);
            }
            switch ((int) index / 60) {
                case 5:
                    if (isShowKilometre) {
                        altDiff = "300,2000";
                    } else {
                        altDiff = "91,2000";
                    }
                    break;
                case 4:
                    if (isShowKilometre) {
                        altDiff = "240,300";
                    } else {
                        altDiff = "73,91";
                    }
                    break;
                case 3:
                    if (isShowKilometre) {
                        altDiff = "180,240";
                    } else {
                        altDiff = "55,73";
                    }
                    break;
                case 2:
                    if (isShowKilometre) {
                        altDiff = "120,180";
                    } else {
                        altDiff = "36,55";
                    }
                    break;
                case 1:
                    if (isShowKilometre) {
                        altDiff = "60,120";
                    } else {
                        altDiff = "18,36";
                    }
                    break;
                case 0:
                    if (isShowKilometre) {
                        altDiff = "0,60";
                    } else {
                        altDiff = "0,18";
                    }
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    class SeekBarChangeListener3 implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            double index = i / 4;
            if (index > 20) {
                seekbarValue3.setText("20+");
            } else {
                seekbarValue3.setText("" + i * 25 / 100);
            }
            switch ((int) index / 5) {
                case 4:
                    slope = "20,180";
                    break;
                case 3:
                    slope = "15,20";
                    break;
                case 2:
                    slope = "10,15";
                    break;
                case 1:
                    slope = "5,10";
                    break;
                case 0:
                    slope = "0,5";
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
