package com.beastbikes.android.ble.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.preferences.ui.SettingActivity;

public class BleDeviceActiveDialog extends Dialog implements View.OnClickListener {

    private TextView tvModel;
    private TextView tvNumber;
    private TextView btnActive, btnSwitch;
    private String model, number;
    public interface OnClickListener{
        public void onClickOk();
    }

    private OnClickListener clickListener;

    public BleDeviceActiveDialog(Context context, String model, String number,
                                  OnClickListener clickListener) {
        super(context, R.style.dialog_ble);
        this.clickListener = clickListener;
        this.model = model;
        this.number = number;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_ble_active);
        this.initView();

    }

    private void initView() {
        this.tvModel = (TextView) findViewById(R.id.dialog_ble_model);
        this.tvNumber = (TextView) findViewById(R.id.dialog_ble_number);

        this.btnActive = (TextView) findViewById(R.id.dialog_ble_to_active);
        this.btnSwitch = (TextView) findViewById(R.id.dialog_ble_switch);

        this.btnActive.setOnClickListener(this);
        this.btnSwitch.setOnClickListener(this);

        tvModel.setText(model);
        tvNumber.setText(number);
    }


    @Override
    public void onClick(View v) {
        this.dismiss();
        switch (v.getId()) {
            case R.id.dialog_ble_to_active: {
                if(clickListener != null){
                    clickListener.onClickOk();
                }
                break;
            }
            case R.id.dialog_ble_switch: {
                getContext().startActivity(new Intent(getContext(), SettingActivity.class));
                break;
            }
        }
    }

}
