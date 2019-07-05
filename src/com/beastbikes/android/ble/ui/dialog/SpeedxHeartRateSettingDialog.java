package com.beastbikes.android.ble.ui.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.framework.android.utils.DimensionUtils;
import com.beastbikes.framework.ui.android.utils.Toasts;

/**
 * Created by icedan on 16/10/13.
 */

public class SpeedxHeartRateSettingDialog extends DialogFragment implements View.OnClickListener {

    public interface EditTextCommitListener {
        void onHeartRateValue(int value);
    }

    private EditText heartRateEt;
    private ImageView clearIv;

    private EditTextCommitListener commitListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCancelable(false);
        View view = inflater.inflate(R.layout.fragment_heart_rate_setting_dialog, null);
        int width = getResources().getDisplayMetrics().widthPixels -
                DimensionUtils.dip2px(getActivity(), 80);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,
                DimensionUtils.dip2px(getActivity(), 40));
        int margin = DimensionUtils.dip2px(getActivity(), 15);
        lp.setMargins(margin, margin, margin, 0);
        this.heartRateEt = (EditText) view.findViewById(R.id.dialog_heart_rate_setting_edittext);
        final ImageView closeIv = (ImageView) view.findViewById(R.id.dialog_heart_rate_setting_close);
        Button commitBtn = (Button) view.findViewById(R.id.dialog_heart_rate_setting_commit_btn);
        this.clearIv = (ImageView) view.findViewById(R.id.dialog_heart_rate_setting_clear);
        commitBtn.setLayoutParams(lp);
        TextView defaultTv = (TextView) view.findViewById(R.id.dialog_heart_rate_setting_default_tv);
        closeIv.setOnClickListener(this);
        commitBtn.setOnClickListener(this);
        defaultTv.setOnClickListener(this);
        this.clearIv.setOnClickListener(this);
        this.heartRateEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    clearIv.setVisibility(View.VISIBLE);
                } else {
                    clearIv.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_heart_rate_setting_close:// 关闭
                this.dismiss();
                break;
            case R.id.dialog_heart_rate_setting_commit_btn:// 确定
                String number = this.heartRateEt.getText().toString();
                if (TextUtils.isEmpty(number)) {
                    return;
                }
                int heartRate = Integer.valueOf(number);
                if (heartRate < 55 || heartRate > 249) {
                    Toasts.show(getActivity(), getString(R.string.label_heart_rate_setting_overrun));
                    return;
                }
                if (null != commitListener) {
                    commitListener.onHeartRateValue(heartRate);
                }
                this.dismiss();
                break;
            case R.id.dialog_heart_rate_setting_default_tv:// 使用推荐值
                if (null != commitListener) {
                    commitListener.onHeartRateValue(-1);
                }
                this.dismiss();
                break;
            case R.id.dialog_heart_rate_setting_clear:// 清空数值
                this.heartRateEt.setText("");
                break;
        }
    }

    public void setCommitListener(EditTextCommitListener listener) {
        this.commitListener = listener;
    }
}
