package com.beastbikes.android.ble.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.user.ui.binding.BoundPhoneActivity;

public class BleBindDialog extends Dialog implements View.OnClickListener {

    private TextView btnOk, btnCancel;

    public BleBindDialog(Context context) {
        super(context, R.style.dialog_ble);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_ble_bind);
        this.initView();
    }

    private void initView() {
        this.btnOk = (TextView) findViewById(R.id.dialog_ble_bind_ok);
        this.btnCancel = (TextView) findViewById(R.id.dialog_ble_bind_cancel);

        this.btnOk.setOnClickListener(this);
        this.btnCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        this.dismiss();
        switch (v.getId()) {
            case R.id.dialog_ble_bind_ok: {
                getContext().startActivity(new Intent(getContext(), BoundPhoneActivity.class));
                break;
            }
        }
    }

}
