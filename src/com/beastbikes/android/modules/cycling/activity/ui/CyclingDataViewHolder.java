package com.beastbikes.android.modules.cycling.activity.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.activity.dto.PreviewDto;
import com.beastbikes.android.widget.NumberTextView;
import com.beastbikes.android.widget.convenientbanner.holder.Holder;

/**
 * Created by icedan on 16/1/8.
 */
public class CyclingDataViewHolder implements Holder<PreviewDto>, View.OnClickListener {

    private Context context;
    private TextView label1;
    public NumberTextView value1;
    private TextView label2;
    public NumberTextView value2;
    private LinearLayout data1View;
    private LinearLayout data2View;
    private View line;
    private int position;

    @Override
    public View createView(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.cycling_fragment_viewpager_data, null);
        this.data1View = (LinearLayout) view.findViewById(R.id.cycling_fragment_viewpager_data_1);
        this.data2View = (LinearLayout) view.findViewById(R.id.cycling_fragment_viewpager_data_2);
        this.label1 = (TextView) view.findViewById(R.id.cycling_fragment_viewpager_data_1_label);
        this.label2 = (TextView) view.findViewById(R.id.cycling_fragment_viewpager_data_2_label);
        this.value1 = (NumberTextView) view.findViewById(R.id.cycling_fragment_viewpager_data_1_value);
        this.value2 = (NumberTextView) view.findViewById(R.id.cycling_fragment_viewpager_data_2_value);
        this.line = view.findViewById(R.id.cycling_fragment_viewpager_data_line);
        return view;
    }

    @Override
    public void UpdateUI(final Context context, final int position, PreviewDto data) {
        if (null == data) {
            return;
        }

        this.position = position;
        String label1 = data.getLabel1();
        if (TextUtils.isEmpty(label1)) {
            this.data1View.setVisibility(View.GONE);
        } else {
            this.data1View.setVisibility(View.VISIBLE);
            this.label1.setText(label1);
        }

        String value1 = data.getValue1();
        if (TextUtils.isEmpty(value1)) {
            this.data1View.setVisibility(View.GONE);
        } else {
            this.data1View.setVisibility(View.VISIBLE);
            this.value1.setText(value1);
        }

        String label2 = data.getLabel2();
        if (TextUtils.isEmpty(label2)) {
            this.data2View.setVisibility(View.GONE);
        } else {
            this.data2View.setVisibility(View.VISIBLE);
            this.label2.setText(label2);
        }

        String value2 = data.getValue2();
        if (TextUtils.isEmpty(value2)) {
            this.data2View.setVisibility(View.GONE);
        } else {
            this.data2View.setVisibility(View.VISIBLE);
            this.value2.setText(value2);
        }

        if (TextUtils.isEmpty(label1) || TextUtils.isEmpty(label2)) {
            this.line.setVisibility(View.GONE);
        }

//        if (this.data1View.getVisibility() == View.GONE) {
//            this.value2.setTextSize(60);
//        }
//
//        if (this.data2View.getVisibility() == View.GONE) {
//            this.value1.setTextSize(60);
//        }

        this.data1View.setOnClickListener(this);
        this.data2View.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cycling_fragment_viewpager_data_1:
            case R.id.cycling_fragment_viewpager_data_2:
                Intent settingIntent = new Intent(context, CyclingSettingPageActivity.class);
                settingIntent.putExtra(CyclingSettingPageActivity.EXTRA_SETTING_POSITION, this.position);
                context.startActivity(settingIntent);
                break;
        }
    }

}
