package com.beastbikes.android.modules.preferences.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.user.ui.binding.BoundPhoneActivity;

public class StravaUnBindDialog extends Dialog implements View.OnClickListener {

    public interface OnClickListener {
        public void onOkClick();
    }

    OnClickListener listener;

    private TextView btnOk, btnCancel;

    public StravaUnBindDialog(Context context, OnClickListener listener) {
        super(context, R.style.dialog_ble);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_strava_disconnect_tip);
        this.initView();
    }

    private void initView() {
        this.btnOk = (TextView) findViewById(R.id.dialog_ok);
        this.btnCancel = (TextView) findViewById(R.id.dialog_cancel);

        this.btnOk.setOnClickListener(this);
        this.btnCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        this.dismiss();
        switch (v.getId()) {
            case R.id.dialog_ok: {
                if (listener != null) {
                    listener.onOkClick();
                }
                break;
            }
        }
    }

}
