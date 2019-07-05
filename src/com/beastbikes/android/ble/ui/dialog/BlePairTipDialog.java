package com.beastbikes.android.ble.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.user.ui.binding.BoundPhoneActivity;

public class BlePairTipDialog extends Dialog implements View.OnClickListener {

    public BlePairTipDialog(Context context) {
        super(context, R.style.dialog_ble);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_ble_pair);
        this.initView();
    }

    private void initView() {
        findViewById(R.id.dialog_ble_pair_ok)
                .setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
    }

}
