package com.beastbikes.android.ble.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.beastbikes.android.R;

public class BleActiveFailedDialog extends Dialog {

    private TextView btnOk, btnCancel;
    private String failedMsg;

    public BleActiveFailedDialog(Context context, String msg) {
        super(context, R.style.dialog_ble);
        this.failedMsg = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ble_active_failed);
        TextView msg = (TextView) findViewById(R.id.dialog_ble_active_failed_message);
        if (!TextUtils.isEmpty(failedMsg)) {
            msg.setText(failedMsg);
        }
    }
}
